package com.dtcomg.system.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void generateEncryptedPassword() {
        // 创建密码编码器
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 要加密的原始密码
        String rawPassword = "admin123"; // 修改为你想要的密码

        // 生成加密后的密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后的密码: " + encodedPassword);

        // 验证密码是否正确
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("密码验证结果: " + matches);
    }
}
