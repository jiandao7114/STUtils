package com.st.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.content.Context;

/**
 * RSAUtil
 */
public class RSAUtils {

    private static final String KEY_ALGORITHM       = "RSA";
    /**
     * 注意不要使用默认的算法，java,android默认不一样 <br/>
     * java RSA/ECB/PKCS1Padding <br/>
     * android RSA/ECB/NoPadding
     */
    private static final String KEY_ALGORITHM_ARG   = "RSA/ECB/PKCS1Padding";
    private static final String PUBLIC_KEY          = "RSAPublicKey";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String SHA1PRNG            = "SHA1PRNG";

    private static String       seed                = "aHByz0jdqibLwAc=Qrcp&>$!g";

    /**
     * 随机生成密钥对
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Key> initKey() throws NoSuchAlgorithmException {
        Map<String, Key> keyMap = new HashMap<String, Key>(2);

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        keyMap.put(PUBLIC_KEY, publicKey);

        return keyMap;
    }

    /**
     * 通过seed来生成密钥对(确定密钥对)
     * 
     * @param seed
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Key> initKey(String seed) throws NoSuchAlgorithmException {
        Map<String, Key> keyMap = new HashMap<String, Key>(2);

        if (seed != null) {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
            secureRandom.setSeed(seed.getBytes());
            keyPairGen.initialize(1024, secureRandom);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            keyMap.put(PUBLIC_KEY, publicKey);
        }

        return keyMap;
    }

    public static String getDefaultPublicKeyByPemFile(Context context, int public_key_raw_resource)
            throws IOException, CertificateException {
        return getPemFileData(context, public_key_raw_resource);
    }

    private static String getPemFileData(Context context, int public_key_raw_resource)
            throws FileNotFoundException, IOException {
        StringBuffer key = new StringBuffer();

        BufferedReader br = null;
        InputStream in = null;
        try {
            in = context.getResources().openRawResource(public_key_raw_resource);
            br = new BufferedReader(new InputStreamReader(in));

            String line = null;
            while ((line = br.readLine()) != null) {
                if (!(line.startsWith("--") && line.endsWith("--"))) {
                    key.append(line);
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return key.toString();
    }

    /**
     * 得到默认公钥
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getDefaultPublicKey() throws NoSuchAlgorithmException {
        Key publicKey = initKey(seed).get(PUBLIC_KEY);
        if (publicKey != null) {
            return encryptBASE64(publicKey.getEncoded());
        }
        return "";
    }

    /**
     * 得到公钥
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getPublicKey(Map<String, Key> initKey) throws NoSuchAlgorithmException {
        if (initKey != null) {
            Key publicKey = initKey.get(PUBLIC_KEY);
            if (publicKey != null) {
                return encryptBASE64(publicKey.getEncoded());
            }
        }
        return "";
    }

    public static byte[] decryptBASE64(String key) throws IOException {
        return Base64.decode(key);
    }

    public static String encryptBASE64(byte[] key) {
        return Base64.encode(key);
    }

    /********************** 数据加解密 ******************************/
    /**
     * @param data 明文数据
     * @param strPublicKey 公开密钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     * @throws CertificateException
     */
    public static byte[] encryptByPublicKey(byte[] data, String strPublicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException,
            CertificateException {
        // 对公钥转码
        byte[] keyBytes = decryptBASE64(strPublicKey);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_ARG);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /********************** 数据加解密end ******************************/

    /********************** 数字签名认证 **********************************/
    /**
     * 校验数字签名
     * 
     * @param encyptData 加密数据
     * @param strPublicKey 公钥
     * @param sign 数字签名
     * @return 校验成功返回true 失败返回false
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public static boolean verify(byte[] encyptData, String strPublicKey, String sign)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, IOException {

        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(strPublicKey);
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(encyptData);
        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 公开密钥解密
     * 
     * @param encyptData 密文
     * @param strPublicKey 公开密钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public static byte[] decryptByPublicKey(byte[] encyptData, String strPublicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        // 对公开密钥转码
        byte[] keyBytes = decryptBASE64(strPublicKey);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_ARG);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encyptData);
    }
    /********************** 数字签名end *******************************/
}
