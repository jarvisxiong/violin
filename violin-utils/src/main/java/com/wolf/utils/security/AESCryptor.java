package com.wolf.utils.security;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Security;

/**
 * AES密码机。 统一编码UTF8。 128位密钥；ECB分组；PKCS7填充。 密钥不足128位，补填0。
 */
public class AESCryptor {

	private static final int KEY_BIT_SIZE = 128;

	private static final Charset CHAR_SET = Charset.forName("UTF-8");

	// AES，简单分组，填充7
	private static final String ALGORITHM = "AES/ECB/PKCS5PADDING";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 加密字符串。
	 * 
	 * @param target
	 *            原始字符串
	 * @param key
	 *            密钥字符串
	 * @return 加密结果字符串
	 */
	public static String encrypt(String target, String key) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, initKey(key));
			byte[] encryptResult = cipher.doFinal(target.getBytes(CHAR_SET));
			// 兼容安卓环境的1.2codec
			String unsafeStr = new String(Base64.encodeBase64(encryptResult,
					false), CHAR_SET);
			return unsafeStr.replace('+', '-').replace('/', '_');
		} catch (Exception e) {
			throw new RuntimeException("敏感数据加密错误", e);
		}
	}

	/**
	 * 解密字符串。
	 * 
	 * @param target
	 *            加密结果字符串
	 * @param key
	 *            密钥字符串
	 * @return 原始字符串
	 */
	public static String decrypt(String target, String key) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, initKey(key));
			String unsafeStr = target.replace('-', '+').replace('_', '/');
			byte[] decryptResult = cipher.doFinal(Base64.decodeBase64(unsafeStr
					.getBytes(CHAR_SET)));
			return new String(decryptResult, CHAR_SET);
		} catch (Exception e) {
			throw new RuntimeException("敏感数据解密错误", e);
		}
	}

	/**
	 * 生成密钥字节数组，原始密钥字符串不足128位，补填0.
	 * 
	 * @param originalKey
	 * @return
	 */
	private static SecretKeySpec initKey(String originalKey) {
		byte[] keys = originalKey.getBytes(CHAR_SET);

		byte[] bytes = new byte[KEY_BIT_SIZE / 8];
		for (int i = 0; i < bytes.length; i++) {
			if (keys.length > i) {
				bytes[i] = keys[i];
			} else {
				bytes[i] = 0;
			}
		}

		return new SecretKeySpec(bytes, "AES");
	}

}
