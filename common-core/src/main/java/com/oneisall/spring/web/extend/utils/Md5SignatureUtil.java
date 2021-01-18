package com.oneisall.spring.web.extend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Md5 签名
 *
 * @author liuzhicong
 **/
public class Md5SignatureUtil {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String buildSignature(String secretKey, List<String> params) {
        // 为不影响参数列表,复制一份.确保证数列表的正确复制,切勿修改下面代码.
        List<String> copyList = new ArrayList<String>(Arrays
                .asList(new String[params.size()]));
        Collections.copy(copyList, params);
        // 避免null值,导致sort时异常
        for (int i = 0; i < copyList.size(); i++) {
            String p = copyList.get(i);
            if (p == null) {
                copyList.set(i, "");
            }
        }
        Collections.sort(copyList);
        StringBuilder code = new StringBuilder();
        for (String p : copyList) {
            code.append(p);
        }
        return sign(code.toString(), secretKey);
    }

    public static String sign(String text, String key) {
        if (text == null) {
            throw new IllegalArgumentException("text can't be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        String s = md5(key);
        byte[] textData = text.getBytes();
        int len = textData.length;
        int n = (len + 15) / 16;
        byte[] tempData = new byte[n * 16];
        for (int i = len; i < n * 16; i++) {
            tempData[i] = 0;
        }
        System.arraycopy(textData, 0, tempData, 0, len);
        textData = tempData;
        String[] c = new String[n];
        for (int i = 0; i < n; i++) {
            c[i] = new String(textData, 16 * i, 16);
        }
        String[] b = new String[n];
        String temp = s;
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < n; i++) {
            b[i] = md5(temp + c[i]);
            temp = b[i];
            target.append(b[i]);
        }
        return md5(target.toString());
    }

    public static String md5(String content) {
        byte[] data = getMd5digest().digest(content.getBytes());
        char[] chars = encodeHex(data);
        return new String(chars);
    }

    private static MessageDigest getMd5digest() {
        try {
            MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
            md5MessageDigest.reset();
            return md5MessageDigest;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(
                    "Could not access MD5 algorithm, fatal error");
        }
    }

    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }
}

