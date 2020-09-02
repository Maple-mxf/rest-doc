package restdoc.remoting.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class RemotingSerializable {

    private final static Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static byte[] encode(final Object obj) {
        final String json = toJson(obj);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T decode(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, CHARSET_UTF8);
        return fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encode() {
        final String json = this.toJson();
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public String toJson() {
        return toJson(this);
    }
}
