package com.anushiya.totp.service;

import com.anushiya.totp.dto.RegisterRequest;
import com.anushiya.totp.entity.AppUser;
import com.anushiya.totp.repository.UserRepository;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();

    @Value("${encryption.key}")
    private String encryptionKey;

    public void register(RegisterRequest request) throws Exception {
        logger.info("Creating new user: {}", request.getUsername());

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate TOTP secret and encrypt
        String secret = secretGenerator.generate();
        String encryptedSecret = encrypt(secret);
        user.setSecret(encryptedSecret);

        userRepository.save(user);
        logger.info("User [{}] registered and secret encrypted", request.getUsername());
    }

    private String encrypt(String input) throws Exception {
        logger.debug("Encrypting TOTP secret");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
