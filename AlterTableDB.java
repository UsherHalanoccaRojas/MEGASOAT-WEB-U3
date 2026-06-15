import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AlterTableDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://bn3ramkmisijqjhjjukm-mysql.services.clever-cloud.com:3306/bn3ramkmisijqjhjjukm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Lima&useUnicode=true&characterEncoding=UTF-8";
        String user = "u0oekml0dqi9glb4";
        String pass = "IytT4lhnstJTpmzUUtEM";
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            
            try {
                stmt.executeUpdate("ALTER TABLE usuarios ADD COLUMN avatar_url TEXT");
                System.out.println("Added avatar_url column");
            } catch (Exception e) {
                System.out.println("avatar_url might already exist: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("ALTER TABLE usuarios ADD COLUMN session_token TEXT");
                System.out.println("Added session_token column");
            } catch (Exception e) {
                System.out.println("session_token might already exist: " + e.getMessage());
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
