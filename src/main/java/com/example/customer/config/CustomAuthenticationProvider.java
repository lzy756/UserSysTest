package com.example.customer.config;

import com.example.customer.entity.User;
import com.example.customer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        User user = userOptional.get();

        // 验证密码
        // 前端传来的是SHA-256加密后的密码，需要与数据库中的BCrypt密码比对
        // 方法：先对前端传来的SHA-256哈希再进行BCrypt验证

        // 如果传入的密码是64位十六进制（SHA-256的特征），说明是前端加密过的
        if (password.matches("^[a-f0-9]{64}$")) {
            // 前端发送的是SHA-256哈希值
            // 我们需要验证：SHA-256(原始密码) == 传入的password
            // 然后验证：BCrypt(SHA-256(原始密码)) == 数据库中的密码

            // 由于我们无法从SHA-256反推原始密码，我们需要改变策略：
            // 在数据库中存储 BCrypt(SHA-256(原始密码))
            // 验证时：比对 BCrypt.matches(前端传来的SHA-256, 数据库中的BCrypt)

            if (passwordEncoder.matches(password, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority("USER"))
                );
            }
        } else {
            // 如果是明文（向后兼容或调试用），直接BCrypt验证
            if (passwordEncoder.matches(password, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority("USER"))
                );
            }
        }

        throw new BadCredentialsException("用户名或密码错误");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * 工具方法：计算字符串的SHA-256哈希值
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }
}
