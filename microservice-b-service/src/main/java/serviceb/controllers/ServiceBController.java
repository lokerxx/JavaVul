package serviceb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;



@RestController
public class ServiceBController {
    @Autowired
    private RestTemplate restTemplate;

    private static final String AES = "AES";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
    private static final Logger logger = LoggerFactory.getLogger(ServiceBController.class);



    @GetMapping("/some-endpoint")
    public String someEndpoint() {
        // 业务逻辑处理
        return "Response from Service B";
    }

    @PostMapping("/process")
    public void process(@RequestBody String data) {
        // 模拟数据处理逻辑
        String processedData = processData(data);

        // 发送数据到Service C 和 Service D
        restTemplate.postForObject("http://service-c/store", data, Void.class);
        restTemplate.postForObject("http://service-d/analyze", data, Void.class);
    }
    private String processData(String data) {
        try {
            // 使用AES加密数据
            String encryptedData = encryptAES(data, "yourEncryptionKey");
            logger.info("encryptedData:"+encryptedData);
            // ... 可以在这里添加其他处理逻辑

            // 使用AES解密数据
            String decryptedData = decryptAES(encryptedData, "yourEncryptionKey");
            logger.info("decryptedData:"+decryptedData);

            // 模拟数据转换
            String transformedData = basicDataTransform(decryptedData);
            logger.info("transformedData:"+transformedData);

            // 模拟计算操作
            String calculatedData = performSomeCalculations(transformedData);
            logger.info("calculatedData:"+calculatedData);

            // 返回处理后的数据
            return "Processed: " + calculatedData;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in processing data";
        }
    }

    private String encryptAES(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(CHARSET_NAME));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptAES(String encryptedData, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, CHARSET_NAME);
    }

    private SecretKeySpec getSecretKey(final String key) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        generator.init(128, secureRandom);
        SecretKey secretKey = generator.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), AES);
    }

    private String basicDataTransform(String data) {
        // 基本的数据转换，例如编码转换
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String performSomeCalculations(String data) {
        // 模拟一些计算，例如对数据进行哈希，或者其他计算操作
        return Integer.toHexString(data.hashCode());
    }

}