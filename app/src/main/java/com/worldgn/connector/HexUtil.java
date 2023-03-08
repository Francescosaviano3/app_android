package com.worldgn.connector;

import java.math.BigInteger;

class HexUtil {

	/**
	 * Convert byte[] to hex string.
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {  
            int ch = (int) s.charAt(i);  
            String s4 = Integer.toHexString(ch);
            str = str + s4;  
        }  
        return "0x" + str;//0x表示十六进制  
    }  

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16)
				| ((src[offset + 3] & 0xFF) << 24);
		return value;
	}
	
	
    /* byte[]->int */  
    public final static int getInt(byte[] buf, boolean asc) {  
      if (buf == null) {  
        throw new IllegalArgumentException("byte array is null!");
      }  
      if (buf.length > 4) {  
        throw new IllegalArgumentException("byte array size > 4 !");
      }  
      int r = 0;  
      if (asc)  
        for (int i = buf.length - 1; i >= 0; i--) {  
          r <<= 8;  
          r |= (buf[i] & 0x000000ff);  
        }  
      else  
        for (int i = 0; i < buf.length; i++) {  
          r <<= 8;  
          r |= (buf[i] & 0x000000ff);  
        }  
      return r;  
    }  

    /* byte to  int */  
    public final static int getInt(byte[] buf, boolean asc, int len) {  
        if (buf == null) {  
          throw new IllegalArgumentException("byte array is null!");
        }  
        if (len > 4) {  
          throw new IllegalArgumentException("byte array size > 4 !");
        }  
        int r = 0;  
        if (asc)  
          for (int i = len - 1; i >= 0; i--) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        else  
          for (int i = 0; i < len; i++) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        return r;  
    }

    public final static int parseIrNir(byte[] array) {

		return  array[0] | (array[1] >> 8) | (array[2] >> 16) | (array[3] >> 24);

	}

	public final static int getInteger(String hex) {
    	return (int) Long.parseLong(hex, 16);
	}

	public final static long getLong(String hex) {
    	return Long.parseLong(hex, 16);
	}


}
