package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class UserEncoder {
	//
	static String strAESKey = "+82 10-3006-4474";
	//
	// default mode is ECB (with Java)
	public static byte[] encryptAES(String to_encrypt, String strKey)
	{
		try {
			Cipher cipher = Cipher.getInstance("AES");
			//Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			//SecretKeySpec skeySpec = new SecretKeySpec(strKey.getBytes(), "AES");
			SecretKeySpec skeySpec = new SecretKeySpec(strKey.getBytes("UTF-8"), "AES");
		    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		    return cipher.doFinal(to_encrypt.getBytes());
		    
		} catch ( Exception ex ) {
			ex.printStackTrace();
			return null;
		}
	}
	
	//
	public static String decryptAES(byte[] to_decrypt, String strKey)
	{
		try {
			Cipher cipher = Cipher.getInstance("AES");
			//Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec skeySpec = new SecretKeySpec(strKey.getBytes("UTF-8"), "AES");
		    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		    byte[] byResult = cipher.doFinal(to_decrypt);
		    return new String(byResult);
		    
		} catch ( Exception ex ) {
			ex.printStackTrace();
			return null;
		}
	}
	
	//
	public static String userEncrypt(String to_encrypt)
	{
		byte[] byEncrypt = encryptAES(to_encrypt, strAESKey);
		String strEncrypt = Base64.encode(byEncrypt);
		return strEncrypt;
	}
	
	public static String userDecrypt(String to_decrypt)
	{
		byte[] byDecrypt = Base64.decode(to_decrypt);
		String strDecrypt = decryptAES(byDecrypt, strAESKey);
		return strDecrypt;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// 현재 사용하지 않는 암호화 로직
	public static byte[] encryptBlowfish( String to_encrypt, String strkey )
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec( strkey.getBytes(), "Blowfish" );
			Cipher cipher = Cipher.getInstance( "Blowfish" );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			return cipher.doFinal( to_encrypt.getBytes() );
		}
		catch ( Exception e )
		{
			return null;
		}
	}
	
	public static String decryptBlowfish( byte[] to_decrypt, String strkey )
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec( strkey.getBytes(), "Blowfish" );
			Cipher cipher = Cipher.getInstance( "Blowfish" );
			cipher.init( Cipher.DECRYPT_MODE, key );
			byte[] decrypted = cipher.doFinal( to_decrypt );
			return new String( decrypted );
		}
		catch ( Exception e )
		{
			return null;
		}
	}
}
