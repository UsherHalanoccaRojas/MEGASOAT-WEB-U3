import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class DescTableDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://bn3ramkmisijqjhjjukm-mysql.services.clever-cloud.com:3306/bn3ramkmisijqjhjjukm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Lima&useUnicode=true&characterEncoding=UTF-8";
        String user = "u0oekml0dqi9glb4";
        String pass = "IytT4lhnstJTpmzUUtEM";
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            try {
                ResultSet rs = stmt.executeQuery("SELECT * FROM audit_logs LIMIT 1");
                ResultSetMetaData rsmd = rs.getMetaData();
                System.out.println("Columns in audit_logs:");
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.println(rsmd.getColumnName(i));
                }
            } catch (Exception e) {
                System.out.println("Error querying audit_logs: " + e.getMessage());
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
