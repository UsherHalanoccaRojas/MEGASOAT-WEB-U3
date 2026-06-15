import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://bn3ramkmisijqjhjjukm-mysql.services.clever-cloud.com:3306/bn3ramkmisijqjhjjukm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Lima&useUnicode=true&characterEncoding=UTF-8";
        String user = "u0oekml0dqi9glb4";
        String pass = "IytT4lhnstJTpmzUUtEM";
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE usuarios SET password = '123456'");
            System.out.println("Updated all passwords to 123456");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
