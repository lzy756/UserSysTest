package com.example.customer.config;

import com.example.customer.entity.User;
import com.example.customer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

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

        logger.info("尝试认证用户: {}", username);

        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            logger.error("用户不存在: {}", username);
            throw new BadCredentialsException("用户名或密码错误");
        }

        User user = userOptional.get();
        logger.info("找到用户: {}, 角色数量: {}", username, user.getRoles().size());
        logger.info("用户角色: {}", user.getRoles());

        // 验证密码 - 直接使用BCrypt验证明文密码
        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("密码验证成功");

            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());

            logger.info("授予的权限: {}", authorities);

            // 如果没有角色，给一个默认角色
            if (authorities.isEmpty()) {
                logger.warn("用户没有角色，授予默认USER角色");
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return new UsernamePasswordAuthenticationToken(
                username,
                password,
                authorities
            );
        }

        logger.error("密码验证失败");
        throw new BadCredentialsException("用户名或密码错误");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
