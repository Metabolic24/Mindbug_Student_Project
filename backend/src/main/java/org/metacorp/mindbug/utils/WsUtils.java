package org.metacorp.mindbug.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;

public final class WsUtils {

    public static final String PLAYER_ID_KEY = "playerId";
    public static final String PLAYER_NAME_KEY = "playerName";

    /**
     * Retrieve the param value from the WebSocket query
     * @param param the query parameter key
     * @param query the whole WebSocket query
     * @return the parameter value if any, null otherwise
     */
    public static String getValueFromQueryParam(String param, String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        return Arrays.stream(query.split("&"))
                .map(WsUtils::splitQueryParameter)
                .filter(entry -> entry.getKey().equals(param))
                .map(AbstractMap.SimpleImmutableEntry::getValue)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Missing playerId argument"));
    }

    private static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        if(idx == -1) {
            throw new IllegalArgumentException();
        }
        final String key = idx > 0 ? it.substring(0, idx) : "";
        final String value = (idx > 0 && it.length() > idx + 1) || (it.indexOf('=') == it.length()-1) || (it.indexOf('=') == 0)  ? it.substring(idx + 1) : it.substring(idx);
        return new AbstractMap.SimpleImmutableEntry<>(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
    }

    private WsUtils(){
        // Not to be used
    }
}
