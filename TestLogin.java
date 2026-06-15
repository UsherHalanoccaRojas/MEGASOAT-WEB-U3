import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestLogin {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:8080/api/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"email\":\"admin@megasoat.com\", \"password\":\"123456\", \"captchaId\":\"dummy\", \"captchaAnswer\":\"dummy\"}";
            
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
