package com.xz.utils;

import java.util.HashMap;

/**
 * 摩斯密码 加密解密
 */
public class MorseCodeUtil {
    private HashMap<String, String> hm;

    public MorseCodeUtil() {
        hm = new HashMap<>();
        //密文
        hm.put(".-", "A");
        hm.put("-...", "B");
        hm.put("-.-.", "C");
        hm.put("-..", "D");
        hm.put(".", "E");
        hm.put("..-.", "F");
        hm.put("--.", "G");
        hm.put("....", "H");
        hm.put("..", "I");
        hm.put(".---", "J");
        hm.put("-.-", "K");
        hm.put(".-..", "L");
        hm.put("--", "M");
        hm.put("-.", "N");
        hm.put("---", "O");
        hm.put(".--.", "P");
        hm.put("--.-", "Q");
        hm.put(".-.", "R");
        hm.put("...", "S");
        hm.put("-", "T");
        hm.put("..-", "U");
        hm.put("...-", "V");
        hm.put(".--", "W");
        hm.put("-..-", "X");
        hm.put("-.--", "Y");
        hm.put("-----", "0");
        hm.put(".----", "1");
        hm.put("..---", "2");
        hm.put("...--", "3");
        hm.put("....-", "4");
        hm.put(".....", "5");
        hm.put("-....", "6");
        hm.put("--...", "7");
        hm.put("---..", "8");
        hm.put("----.", "9");
        //明文
        hm.put("A", ".-");
        hm.put("B", "-...");
        hm.put("C", "-.-.");
        hm.put("D", "-..");
        hm.put("E", ".");
        hm.put("F", "..-.");
        hm.put("G", "--.");
        hm.put("H", "....");
        hm.put("I", "..");
        hm.put("J", ".---");
        hm.put("K", "-.-");
        hm.put("L", ".-..");
        hm.put("M", "--");
        hm.put("N", "-.");
        hm.put("O", "---");
        hm.put("P", ".--.");
        hm.put("Q", "--.-");
        hm.put("R", ".-.");
        hm.put("S", "...");
        hm.put("T", "-");
        hm.put("U", "..-");
        hm.put("V", "...-");
        hm.put("W", ".--");
        hm.put("X", "-..-");
        hm.put("Y", "-.--");
        hm.put("Z", "--..");
        hm.put("0", "-----");
        hm.put("1", ".----");
        hm.put("2", "..---");
        hm.put("3", "...--");
        hm.put("4", "....-");
        hm.put("5", ".....");
        hm.put("6", "-....");
        hm.put("7", "--...");
        hm.put("8", "---..");
        hm.put("9", "----.");
    }

    /**
     * 解密
     *
     * @param s
     */
    public String decode(String s)//执行摩斯码转换字母
    {
        StringBuffer sb = new StringBuffer();
        String[] st = s.split("/");

        for (int x = 0; x < st.length; x++) {
//            Log.d("xz", "method_1: " + hm.get(st[x]));
            sb.append(hm.get(st[x]));
        }
        return sb.toString();
    }

    /**
     * 加密
     *
     * @param s
     */
    public String encryp(String s)//执行字母转换摩斯码
    {
        StringBuffer sb = new StringBuffer();

        char[] buf = s.toUpperCase().toCharArray();

        for (int i = 0; i < buf.length; i++) {
//            Log.d("xz", "method_1: " + hm.get(buf[i] + ""));
//            Log.d("xz", "method_1: /");
            sb.append(hm.get(buf[i] + "") + "/");

        }
        return sb.toString();
    }

}
