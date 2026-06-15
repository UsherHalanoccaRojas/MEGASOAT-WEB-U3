import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLogin5 {
    public static void main(String[] args) {
        try {
            // Get captcha
            URL captchaUrl = new URL("http://localhost:8080/api/auth/captcha");
            HttpURLConnection captchaConn = (HttpURLConnection) captchaUrl.openConnection();
            captchaConn.setRequestMethod("GET");
            InputStream is = captchaConn.getInputStream();
            Scanner scanner = new Scanner(is);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();
            
            System.out.println("Captcha Body: " + responseBody);
            
            Matcher idMatcher = Pattern.compile("\"id\":\"(.*?)\"").matcher(responseBody);
            Matcher questionMatcher = Pattern.compile("\"question\":\"(.*?)\"").matcher(responseBody);
            idMatcher.find();
            questionMatcher.find();
            String id = idMatcher.group(1);
            String question = questionMatcher.group(1);
            
            // Login
            URL loginUrl = new URL("http://localhost:8080/api/auth/login");
            HttpURLConnection loginConn = (HttpURLConnection) loginUrl.openConnection();
            loginConn.setRequestMethod("POST");
            loginConn.setRequestProperty("Content-Type", "application/json");
            loginConn.setDoOutput(true);

            String jsonInputString = "{\"email\":\"superadmin@megasoat.com\", \"password\":\"123456\", \"captchaId\":\"" + id + "\", \"captchaAnswer\":\"" + question + "\"}";
            
            try(OutputStream os = loginConn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }

            int code = loginConn.getResponseCode();
            System.out.println("Login Response Code: " + code);
            
            InputStream loginIs = (code >= 200 && code < 300) ? loginConn.getInputStream() : loginConn.getErrorStream();
            if (loginIs != null) {
                Scanner loginScanner = new Scanner(loginIs);
                String loginBody = loginScanner.hasNext() ? loginScanner.useDelimiter("\\A").next() : "";
                System.out.println("Login Response Body: " + loginBody);
                loginScanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
