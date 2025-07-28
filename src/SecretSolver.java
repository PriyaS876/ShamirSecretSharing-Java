import com.google.gson.*;
import java.io.*;
import java.util.*;

public class SecretSolver {

    static class Share {
        int x;
        int y;

        Share(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        String[] testFiles = { "input/testcase1.json", "input/testcase2.json" };

        for (String filePath : testFiles) {
            JsonObject json = loadJson(filePath);
            int k = json.getAsJsonObject("keys").get("k").getAsInt();

            List<Share> shares = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().equals("keys")) continue;

                int x = Integer.parseInt(entry.getKey());
                JsonObject valueObj = entry.getValue().getAsJsonObject();

                int base = Integer.parseInt(valueObj.get("base").getAsString());
                String value = valueObj.get("value").getAsString();

                int y = Integer.parseInt(value, base);
                shares.add(new Share(x, y));
            }

            int secret = interpolate(shares.subList(0, k));
            System.out.println("Secret from " + filePath + ": " + secret);
        }
    }

    public static JsonObject loadJson(String filePath) throws IOException {
        Reader reader = new FileReader(filePath);
        JsonParser parser = new JsonParser();
        return parser.parse(reader).getAsJsonObject();
    }

    public static int interpolate(List<Share> shares) {
        double secret = 0;

        for (int i = 0; i < shares.size(); i++) {
            double xi = shares.get(i).x;
            double yi = shares.get(i).y;

            double term = yi;

            for (int j = 0; j < shares.size(); j++) {
                if (i != j) {
                    double xj = shares.get(j).x;
                    term *= (0 - xj) / (xi - xj); // Lagrange basis
                }
            }
            secret += term;
        }

        return (int) Math.round(secret);
    }
}
