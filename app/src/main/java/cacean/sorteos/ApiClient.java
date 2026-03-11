package cacean.sorteos;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente REST para ApiDonBetoNode.
 * Reemplaza las llamadas SOAP por HTTP POST/GET con JSON.
 */
public class ApiClient {

    private static final String TAG = "ApiClient";

    /**
     * URL base de la API. Se configura en Main.onCreate desde strings.xml.
     * Emulador: http://10.0.2.2:3000/api
     * Dispositivo real: http://IP_PC:3000/api
     */
    public static String BASE_URL = "http://10.0.2.2:3000/api";

    /**
     * POST a /api/{endpoint} con body JSON.
     */
    public static JSONObject post(String endpoint, JSONObject body) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            if (body != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                return readResponse(conn);
            } else {
                Log.e(TAG, "HTTP " + code + " " + endpoint);
                return readResponse(conn);
            }
        } catch (Exception e) {
            Log.e(TAG, "post " + endpoint + ": " + e.getMessage());
            return null;
        }
    }

    private static JSONObject readResponse(HttpURLConnection conn) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String json = sb.toString();
            return json.isEmpty() ? new JSONObject() : new JSONObject(json);
        } catch (Exception e) {
            Log.e(TAG, "readResponse: " + e.getMessage());
            return null;
        }
    }

    /** Helper para crear JSON body */
    public static JSONObject json(String... keysAndValues) {
        JSONObject o = new JSONObject();
        try {
            for (int i = 0; i < keysAndValues.length - 1; i += 2) {
                o.put(keysAndValues[i], keysAndValues[i + 1] != null ? keysAndValues[i + 1] : "");
            }
        } catch (Exception e) {
            Log.e(TAG, "json: " + e.getMessage());
        }
        return o;
    }
}
