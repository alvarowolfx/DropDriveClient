package br.agiratec.dropdrive.client.util;

public class HexByteUtil {

	
	public static byte[] getByteArrayFromHexString(String md5Hash){
		
		int len = md5Hash.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(md5Hash.charAt(i), 16) << 4)
	                             + Character.digit(md5Hash.charAt(i+1), 16));
	    }
	    
	    return data;
		
	}
	public static String getHexStringFromByteArray(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
