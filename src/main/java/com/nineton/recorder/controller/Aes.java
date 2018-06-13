package com.nineton.recorder.controller;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Aes {

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return bytesToHexString(encrypted);
//        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            byte[] encrypted1 = hexStringToByte(sSrc);//先用base64解密
            
            
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "362DA87FA3E89A95";
        // 需要加密的字串
        String cSrc = "www.gowhere.so";
        System.out.println(cSrc);
        // 加密
        String enString = Aes.Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);

        enString = "e92846c402e13ffcd6a374e47a1f60230d141bbbf3661f99535571cde613988d918bb15a08998dbca2304448c45bbf4b5045cc33ac529545118749f1d117c554cb8e28ee76ae765300f621f0e4e8a34b5c6ce54b75ee2f8d279c779acb02e9299820359ba3737687786bd35e8aa583ee79e20934c801a3657309f6d5c3d10ffd1fbed2a4eb5c2e689985f9543ed3eab124b4bff3a56eace3eb2bd8120baae061e9e989d39f129d0ee80a065763bb09d9ba7b13872d8ad0318e3ed8ebb597b7f1a4e15949164c258b1296a09b42ff1c851c96c513c014fb61563d84b13382d6c6990178a42c2712ed1d8749220e7072b6bd740d0ab48abf16fc7305e31ca5a2e02d3d27bd580842ec65e62be396077da889b9b0e0e0ae1c1e87c04488ff90dde330d521f9a749e5b08cf6f90836df225fb90b0f770abb4a50262d93e74d9a815f8e7611791744ab6732c93950d0106540845661609e868e57c6806dee7b0318ef63d5855f96864c5a7a96ffb18a6ccac1119b8caa11308a26b2f6702f5e0df420d41dc795765fd91a8d700e89bd130817abfd225bc18dc575deb585eaab8540648a44c4d4d005274a7344b51cd08d4de91419786e1f8f3adaa2e8ed17096baea0212f445d5873a2d543d51586a9ae97fd11721b525bf07fa02cae17e7180648198c969d8a0ace9866bd9f47b1b79f78af0916448ee60c29ae44ab9644e2892952d8b4bdc9d3541a9de111d853ea1fd2aca5537da2d2fdc1ca0f762ce5b5cd1d0d17d2d0da6b06d35e1e1e6762df6ffd51b5ad93454e5aa90d09fcc8c7940344589de504a967de56c02fb20d396e7b5f105261b5a75eaa7f6ee2e3e9186b9456177a045ae6ce3ef5e25a3c458b921b04762c9ff0f78baa08eabb766a9981c5c5b0138323420b746b683b6f87ccb2ab70619ef9598d7740e8116c7e0cbf95d7044a8339c8366794c491c4d267ffe32ac016b7c4384d34029f064316bb0949c6e0ba0cebc1370114a8e7dd90d0265df4e13d91e8db4bd9add04b3361e3febeb76bfdd380218b1f9084f0150d9d7199744d6ae0499c240698370f7a40eec4599a73fe30a2bd881f9bbbfeef28c031ed62a49cc1549835e04d256f8b20f60a712d05132d06f952019fe62ba8485a22c6357b1b41e7007f28d6ed3c95980339b05cad8736a87749214cf652ed3e4f11eebee9fba0e06cba79a2af26fab6c84245770769415aa516e5bfd33c86632d310be4d5d65fd9ac0f7bbcb9bc0a18c0b8028855df3c02a9855e666986ca62783f035229309af1977de12e703833ce7362611d72b4";
        // 解密
        String DeString = Aes.Decrypt(enString, cKey);
        System.out.println("解密后的字串是：" + DeString);
    }
    
    public static String bytesToHexString(byte[] src){  
        StringBuilder stringBuilder = new StringBuilder();  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
      
//            stringBuilder.append(i + ":");  
      
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
//            stringBuilder.append(hv + ";");  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }
    
    public static byte[] hexStringToByte(String hex) {  
        int len = (hex.length() / 2);  
        byte[] result = new byte[len];  
        char[] achar = hex.toCharArray();  
        for (int i = 0; i < len; i++) {  
            int pos = i * 2;  
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));  
        }  
        return result;  
    }  
    
    private static byte toByte(char c) {  
        byte b = (byte) "0123456789abcdef".indexOf(c);  
        return b;  
    }
}