package com.a99live.zhibo.live.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class EncodeUtils {

    // 编码方式
    private static final String CONTENT_CHARSET = "UTF-8";

    // HMAC算法
    private static final String HMAC_ALGORITHM = "HmacSHA1";


    private static final char[] base64_legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();


    public static String makeSignPlainText(
            TreeMap<String, Object> requestParams, String requestMethod,
            String url) {

        String retStr = "";
        retStr += requestMethod;
        retStr += url;
        retStr += _buildParamStr(requestParams);

        return retStr;
    }

    protected static String _buildParamStr(TreeMap<String, Object> requestParams) {
        return _buildParamStr(requestParams, "GET");
    }

    protected static String _buildParamStr(
            TreeMap<String, Object> requestParams, String requestMethod) {

        String retStr = "";
        for (String key : requestParams.keySet()) {
//            if (key.equals(XgoSignature.SIGNATURE)) {
//                continue;
//            }
            // 过滤文件，不需要验证
            Object object = requestParams.get(key);
            if ((File.class == object.getClass())) {
                continue;
            }

            if (retStr.isEmpty()) {
                retStr += '?';
            } else {
                retStr += '&';
            }

            retStr += key + '=' + object.toString();
        }
        return retStr;
    }


    /**
     * @param signStr 被加密串
     * @param secret  加密密钥
     * @return
     * @brief 签名
     * @author gavinyao@tencent.com
     * @date 2014-08-13 21:07:27
     */
    public static String sign(String signStr, String secret)
            throws NoSuchAlgorithmException, UnsupportedEncodingException,
            InvalidKeyException {

        String sign;
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(CONTENT_CHARSET), mac.getAlgorithm());

        mac.init(secretKey);
        byte[] hash = mac.doFinal(signStr.getBytes(CONTENT_CHARSET));

        // base64
        sign = new String(base64_encode(hash).getBytes());

        return sign;
    }


    /**
     * MD5 加密
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();

    }

    /**
     * Base64 加密
     */
    public static String base64_encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;
        while (i <= end) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);
            buf.append(base64_legalChars[(d >> 18) & 63]);
            buf.append(base64_legalChars[(d >> 12) & 63]);
            buf.append(base64_legalChars[(d >> 6) & 63]);
            buf.append(base64_legalChars[d & 63]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }
        if (i == start + len - 2) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);
            buf.append(base64_legalChars[(d >> 18) & 63]);
            buf.append(base64_legalChars[(d >> 12) & 63]);
            buf.append(base64_legalChars[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;
            buf.append(base64_legalChars[(d >> 18) & 63]);
            buf.append(base64_legalChars[(d >> 12) & 63]);
            buf.append("==");
        }
        return buf.toString();
    }

    /**
     * Base64 加密
     */
    public static byte[] base64_decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();
        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;
            if (i == len)
                break;
            int tri = (decode(s.charAt(i)) << 18)
                    + (decode(s.charAt(i + 1)) << 12)
                    + (decode(s.charAt(i + 2)) << 6)
                    + (decode(s.charAt(i + 3)));
            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);
            i += 4;
        }
    }

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
    }

    /**
     * 对文本进行编码
     * @param s  文本
     * @param bm 编码格式 utf-8
     *
    1. +  URL 中+号表示空格 %2B

    2. 空格 URL中的空格可以用+号或者编码 %20

    3. /  分隔目录和子目录 %2F

    4. ?  分隔实际的 URL 和参数 %3F

    5. % 指定特殊字符 %25

    6. # 表示书签 %23

    7. & URL 中指定的参数间的分隔符 %26

    8. = URL 中指定参数的值 %3D
     */
    public static String encode(String s, String bm) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<s.length();i++){
            char c = s.charAt(i);
            switch (c){
                case '+':
                    sb.append("%2B");
                    break;
                case ' ':
                    sb.append("%20");
                    break;
                case '/':
                    sb.append("%2F");
                    break;
                case '?':
                    sb.append("%3F");
                    break;
                case '%':
                    sb.append("%25");
                    break;
                case '#':
                    sb.append("%23");
                    break;
//                case '&':
//                    sb.append("%26");
//                    break;
                case '=':
                    sb.append("%3D");
                    break;
                case '$':
                    sb.append("%24");
                    break;
                case '@':
                    sb.append("%40");
                    break;
                case '*':
                    sb.append("%2A");
                    break;
                case '~':
                    sb.append("~");
                    break;
                default:
                    try {
                        sb.append(URLEncoder.encode(String.valueOf(c),bm));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;

            }
//            if ("+".equals(c)){
//                sb.append("%2B");
//            }else if(" ".equals(c)){
//                sb.append("%20");
//            }else if("/".equals(c)){
//                sb.append("%2F");
//            }else if("?".equals(c)){
//                sb.append("%3F");
//            }else if("%".equals(c)){
//                sb.append("%25");
//            }else if("#".equals(c)){
//                sb.append("%23");
//            }else if("&".equals(c)){
//                sb.append("%26");
//            }else if("=".equals(c)){
//                sb.append("%3D");
//            }else if("$".equals(c)){
//                sb.append("%24");
//            }else if("*".equals(c)){
//                sb.append("%2A");
//            }else if("@".equals(c)){
//                sb.append("%40");
//            }else if("~".equals(c)){
//                sb.append("~");
//            }else{
//                try {
//                    sb.append(URLEncoder.encode(c,bm));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return sb.toString();
    }
}
