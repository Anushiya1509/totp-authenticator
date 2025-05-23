package com.anushiya.totp.service;

import com.anushiya.totp.entity.AppUser;
import com.anushiya.totp.repository.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class TotpService {

    private static final Logger logger = LoggerFactory.getLogger(TotpService.class);

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA512, 6);

    @Autowired
    private UserRepository userRepository;

    @Value("${encryption.key}")
    private String encryptionKey;

    public String generateTotp(String username) throws Exception {
        logger.info("Generating TOTP for user: {}", username);
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        if (user.getSecret() == null) {
            logger.warn("TOTP secret not set for user: {}", username);
            throw new IllegalStateException("TOTP secret not configured for user");
        }

        String decryptedSecret = decrypt(user.getSecret());
        long currentTimeSeconds = Instant.now().getEpochSecond();
        String code = codeGenerator.generate(decryptedSecret, currentTimeSeconds / 30);
        logger.debug("Generated TOTP code [{}] for user [{}]", code, username);
        return code;
    }

    public boolean verifyTotp(String username, String code) throws Exception {
        logger.info("Verifying TOTP code for user: {}", username);
        String expected = generateTotp(username);
        boolean result = expected.equals(code);
        logger.debug("Expected code: {}, Received code: {}, Match: {}", expected, code, result);
        return result;
    }

    private String decrypt(String encrypted) throws Exception {
        logger.debug("Decrypting secret");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decrypted);
    }
}
