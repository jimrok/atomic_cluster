package ewhine.tools;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Secure {
	final static private Logger LOG = LoggerFactory.getLogger(Secure.class
			.getName());
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	private static final String ENCODING = "UTF-8";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static int workload = 12;

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hex2Bytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] long2Bytes(long l) {
		byte[] data = new byte[8];
		for (int i = 0; i < 8; i++) {
			data[i] = (byte) ((l >> (i * 8)) & 0xFF);
		}

		return data;

	}

	public static byte[] int2Bytes(int l) {
		byte[] data = new byte[4];
		for (int i = 0; i < 4; i++) {
			data[i] = (byte) ((l >> (i * 8)) & 0xFF);
		}

		return data;
	}

	public static byte[] aes128Encrypt(byte[] data, String key, String iv) {

		try {

			//
			byte[] _key = new byte[16];
			System.arraycopy(key.getBytes("UTF-8"), 0, _key, 0, 16);
			SecretKeySpec seckey = new SecretKeySpec(_key, "AES");
			//
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(
					iv.getBytes("UTF-8"));
			//
			Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, seckey, paramSpec);

			return ecipher.doFinal(data);

		} catch (Exception e) {
			LOG.error("encrypt error", e);
		}
		return new byte[] {};
	}

	public static String randomHex(int length) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return bytesToHex(bytes);
	}

	public static byte[] randomBytes(int length) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return bytes;
	}

	public static int randomInteger(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	public static String uuid4() {
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();
		return uuidString;
	}
	
	public static String uuidHex() {
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString().replace("-", "");
		return uuidString;
	}
	
	public static String randomChars(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		
		return sb.toString();
	}
	
	public static String md5Hexdigest(String data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			if (LOG.isErrorEnabled())
				LOG.error("get digest algorithm exeception.", e);
			throw new RuntimeException(e);
		}
		return bytesToHex(md.digest(data.getBytes()));
	}

	public static String sha1Hexdigest(String data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			if (LOG.isErrorEnabled())
				LOG.error("get digest algorithm exeception.", e);
			throw new RuntimeException(e);
		}
		return bytesToHex(md.digest(data.getBytes()));
	}
	
	public static byte[] sha1Digest(String data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			if (LOG.isErrorEnabled())
				LOG.error("get digest algorithm exeception.", e);
			throw new RuntimeException(e);
		}
		return md.digest(data.getBytes());
	}
	
	public static byte[] sha1Digest(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			if (LOG.isErrorEnabled())
				LOG.error("get digest algorithm exeception.", e);
			throw new RuntimeException(e);
		}
		return md.digest(data);
	}

	public static String sha256Hexdigest(String data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			if (LOG.isErrorEnabled())
				LOG.error("get digest algorithm exeception.", e);
			throw new RuntimeException(e);
		}
		
		return bytesToHex(md.digest(data.getBytes()));
	}

	public static String hmacSHA1Hexdigest(String encryptText, String encryptKey)
			throws Exception {
		String result;
		// compute the hmac on input data bytes
		byte[] rawHmac = hmacSHA1Digest(encryptText, encryptKey);

		// base64-encode the hmac
		result = bytesToHex(rawHmac);

		return result;
	}

	public static byte[] hmacSHA1Digest(String encryptText, String encryptKey)
			throws Exception {

		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(encryptKey.getBytes(),
					HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(encryptText.getBytes());

			return rawHmac;

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : "
					+ e.getMessage());
		}

	}

	// See this: http://codahale.com/a-lesson-in-timing-attacks/
	public static boolean secureCompare(String encrypted_password,
			String digest_password) {
		// System.out.println("encr:" + encrypted_password + ",digest_password:"
		// + digest_password);

		char[] p = encrypted_password.toCharArray();
		char[] d = digest_password.toCharArray();

		boolean result = true;

		char[] one = null;
		char[] two = null;

		if (p.length < d.length) {
			one = p;
			two = d;
		} else {
			one = d;
			two = p;
		}

		for (int i = 0, n = p.length; i < n; i++) {
			if (one[i] != two[i]) {
				result = false;
			}
		}

		if (p.length != d.length) {
			result = false;
		}

		return result;
	}
	


	public static void main(String[] args) {
//		String data = "7ea1e0dae2cceabc3726db667eb760b4bf4ee66c";
//		MessageDigest md = null;
//		byte[] b1 = null;
//		try {
//			md = MessageDigest.getInstance("SHA-1");
//			b1 = md.digest(data.getBytes());
//		} catch (NoSuchAlgorithmException e) {
//			if (LOG.isErrorEnabled())
//				LOG.error("get digest algorithm exeception.", e);
//			throw new RuntimeException(e);
//		}
//		byte[] bytes = null;
//		try {
//			md = MessageDigest.getInstance("SHA-1");
//			bytes = md.digest(b1);
//		} catch (NoSuchAlgorithmException e) {
//			if (LOG.isErrorEnabled())
//				LOG.error("get digest algorithm exeception.", e);
//			throw new RuntimeException(e);
//		}
//		
//		char[] hexChars = new char[bytes.length * 2];
//		for (int j = 0; j < bytes.length; j++) {
//			int v = bytes[j] & 0xFF;
//			hexChars[j * 2] = hexArray[v >>> 4];
//			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//		}
//		
//		
//		System.out.println(new String(hexChars));
		
		System.out.println(Secure.sha1Hexdigest("33833:100:ORD20170310110509NZTMEF:14be937093dff08bc4b06ef2dba5c90e083934d1"));
	}

}
