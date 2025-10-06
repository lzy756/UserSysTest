import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成admin123的BCrypt哈希
        String password = "admin123";
        String encoded = encoder.encode(password);

        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + encoded);
        System.out.println();

        // 验证
        boolean matches = encoder.matches(password, encoded);
        System.out.println("验证结果: " + matches);
    }
}
