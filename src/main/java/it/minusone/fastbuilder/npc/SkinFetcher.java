package it.minusone.fastbuilder.npc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinFetcher {
    private static final String MOJANG_SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public static String[] getSkin(String uuid) {
        try {
            URL url = new URL(MOJANG_SESSION_URL + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
            JSONArray properties = (JSONArray) jsonResponse.get("properties");
            JSONObject property = (JSONObject) properties.get(0);

            String value = (String) property.get("value");
            String signature = (String) property.get("signature");

            return new String[]{value, signature};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
