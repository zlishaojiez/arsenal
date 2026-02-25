package cn.shaojiel.arsenal.uri;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public final class URIUtils {

    private URIUtils() {
    }

    public static Map<String, String> getQueryParams(final URI uri) {

        if (uri == null || StringUtils.isBlank(uri.getQuery())) {
            return Collections.emptyMap();
        }

        final Map<String, String> params = new LinkedHashMap<>();
        final StringTokenizer tokenizer = new StringTokenizer(uri.getQuery(), "&");
        while (tokenizer.hasMoreTokens()) {
            final String param = tokenizer.nextToken();
            final String[] keyValue = param.split("=", 2);
            final String key = keyValue.length > 0 ? keyValue[0].trim() : "";
            final String value = keyValue.length > 1 ? keyValue[1].trim() : "";
            if (!key.isEmpty()) {
                params.put(key, value);
            }
        }

        return Collections.unmodifiableMap(params);
    }
}
