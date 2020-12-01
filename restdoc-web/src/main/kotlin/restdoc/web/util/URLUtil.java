package restdoc.web.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class URLUtil {

    /**
     * @see URL#getQuery()
     */
    public static Map<String, Object> parseQueryParam(String urlString) throws MalformedURLException {
        if (!urlString.startsWith("http") && !urlString.startsWith("https"))
            urlString = "http://127.0.0.1" + urlString;
        URL url = new URL(urlString);
        String query = url.getQuery();
        return query == null ?
                new HashMap<>() :
                Arrays.stream(query.split("&"))
                        .map(s -> {
                            String[] arr = s.split("=");
                            return arr.length > 1 ? new AbstractMap.SimpleEntry<String, Object>(arr[0], arr[1]) :
                                    new AbstractMap.SimpleEntry<String, Object>(arr[0], "");
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
