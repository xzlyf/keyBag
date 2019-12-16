package com.xz.utils.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerprintHelper {
    private Context mContext;
    private static final String TAG = "FingerprintHelper";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;
    private final String DEFAULT_KEY_NAME = "default_key";

    private static FingerprintHelper instance;
    private OnAuthResultListener mListener;

    private FingerprintHelper(Context context) {
        mContext = context;
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("获取密钥存储库实例失败", e);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = mContext.getSystemService(KeyguardManager.class);
            fingerprintManager = mContext.getSystemService(FingerprintManager.class);
            try {
                mKeyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new RuntimeException("获取密钥生成器实例失败", e);
            }
        }
    }

    /**
     * 获取实例
     *
     * @param context
     * @return
     */
    public static FingerprintHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (FingerprintHelper.class) {
                if (instance == null) {
                    instance = new FingerprintHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 设置结果回调
     *
     * @param listener
     */
    public void setOnAuthResultListener(OnAuthResultListener listener) {
        this.mListener = listener;
    }

    /**
     * 停止监听
     */
    public void stopListener() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    /**
     * 开始监听指纹传感器
     */
    public void startListening() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || fingerprintManager == null
                || keyguardManager == null
                || !fingerprintManager.isHardwareDetected()) {
            mListener.onDeviceNotSupport();
        } else if (!keyguardManager.isKeyguardSecure()) {
            mListener.onFailed("请先设置锁屏密码");
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            mListener.onFailed("请录入至少一个指纹");
        } else {
            Cipher defaultCipher;
            try {
                defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException("获取Cipher实例失败", e);
            }
            createKey(DEFAULT_KEY_NAME);
            doAuth(defaultCipher, DEFAULT_KEY_NAME);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createKey(String keyName) {
        try {
            mKeyStore.load(null);
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void doAuth(Cipher defaultCipher, String defaultKeyName) {
        if (initCipher(defaultCipher, defaultKeyName)) {
            Logger.w("模式A");
            mCancellationSignal = new CancellationSignal();
            FingerprintManager mFingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
            mFingerprintManager.authenticate(
                    new FingerprintManager.CryptoObject(defaultCipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            mListener.onFailed(errString.toString());
                        }

                        @Override
                        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                            super.onAuthenticationHelp(helpCode, helpString);
                            mListener.onHelper(helpString.toString());
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            mListener.onSuccess();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            //当指纹有效但无法识别
                            mListener.onAuthenticationFailed("指纹扫描失败，请重试");
                        }
                    }, null);
        } else {
            //This happens if the lock screen has been disabled or or a fingerprint got
            //enrolled. Thus show the dialog to authenticate with their password first
            //and ask the user if they want to authenticate with fingerprints in the
            //future
            Logger.w("模式B");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("密码初始化失败", e);
        }
    }


}
