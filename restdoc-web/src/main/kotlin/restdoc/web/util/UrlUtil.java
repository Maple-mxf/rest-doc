package restdoc.web.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtil {

    public static Map<String, Object> parseQueryParam(String url) {
        int si = url.lastIndexOf("?");
        if (si != -1) {
            return Arrays.stream(url.substring(si + 1).split("&"))
                    .map(t -> {
                        String[] arr = t.split("=");
                        return arr.length > 1 ?
                                new AbstractMap.SimpleEntry<String, Object>(arr[0], arr[1]) :
                                new AbstractMap.SimpleEntry<String, Object>(t, "");
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            return new HashMap<>();
        }
    }
}
