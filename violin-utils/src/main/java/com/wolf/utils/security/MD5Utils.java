package com.wolf.utils.security;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;


public class MD5Utils {

    public static String encode(String targetStr) {
        byte[] md5Result = DigestUtils.md5(targetStr.getBytes(Charset.forName("UTF-8")));
        if (md5Result.length != 16) {
            throw new IllegalArgumentException("MD5加密结果字节数组错误");
        }
        Integer first = Math.abs(bytesToInt(md5Result, 0));
        Integer second = Math.abs(bytesToInt(md5Result, 4));
        Integer third = Math.abs(bytesToInt(md5Result, 8));
        Integer fourth = Math.abs(bytesToInt(md5Result, 12));
        return first.toString() + second.toString() + third.toString() + fourth.toString();
    }

    /**
     * 高位前，低位后，字节数组转INT
     * @param src
     * @param offset
     * @return
     */
    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }
    
}