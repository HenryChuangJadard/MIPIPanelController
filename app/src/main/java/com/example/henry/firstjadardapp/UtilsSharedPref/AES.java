package com.example.henry.firstjadardapp.UtilsSharedPref;

import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

import com.example.henry.firstjadardapp.R;

/**
 * AES編解碼工具
 * @author Jonas
 *
 */
public class AES {

    public static String getAESKey(Context context)
    {
        String company=" "+context.getString(R.string.company_name)+" ";
        Calendar c = Calendar.getInstance();
        long unixTimeGMT = c.getTimeInMillis()
                - TimeZone.getDefault().getRawOffset();
        c.setTimeInMillis(unixTimeGMT);
        return company.substring(0, 2)+UtilsSharedPref.mSimpleDateFormat.format(c.getTime())+company.substring(2, company.length());
    }

    public static String getAESKey(Context context,long milliseconds)
    {
        String company=" "+context.getString(R.string.company_name)+" ";
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);
        return company.substring(0, 2)+UtilsSharedPref.mSimpleDateFormat.format(c.getTime())+company.substring(2, company.length());
    }

    public static String encode(String key,String mes) {
        try {
            String EncodeType = "AES";
            KeyGenerator kgen = KeyGenerator.getInstance(EncodeType);
            kgen.init(128); // 192 and 256 bits may not be available

            // Generate the secret key specs.
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();

            raw =key.getBytes();

            SecretKeySpec skeySpec = new SecretKeySpec(raw, EncodeType);

            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance(EncodeType);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(mes.getBytes());

            return asHex(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decode(String key,String mes) {
        try {
            String EncodeType = "AES";
            KeyGenerator kgen = KeyGenerator.getInstance(EncodeType);
            kgen.init(128); // 192 and 256 bits may not be available

            // Generate the secret key specs.
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();

            raw = key.getBytes();
            byte[] r = EncodingUtils.getBytes(key, "utf-8") ;

            SecretKeySpec skeySpec = new SecretKeySpec(raw, EncodeType);

            Cipher cipher = Cipher.getInstance(EncodeType);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] original = cipher.doFinal(hexToBytes(mes.toCharArray()));
            String s = new String(original);

            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    public static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127)
                value -= 256;
            raw[i] = (byte) value;
        }
        return raw;
    }
}
