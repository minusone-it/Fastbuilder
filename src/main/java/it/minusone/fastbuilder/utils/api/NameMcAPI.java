package it.minusone.fastbuilder.utils.api;

import it.minusone.fastbuilder.utils.utils.http.HTTPResponse;
import it.minusone.fastbuilder.utils.utils.http.HTTPUtils;

import java.util.UUID;

public class NameMcAPI {
    public static Boolean likedServer(UUID uuid, String ip) {
        return likedServer(uuid.toString(), ip);
    }

    public static Boolean likedServer(String uuid, String ip) {
        try {
            HTTPResponse http = HTTPUtils.get("https://api.namemc.com/server/" + ip + "/likes?profile=" + uuid);
            return Boolean.parseBoolean(http.getData());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
