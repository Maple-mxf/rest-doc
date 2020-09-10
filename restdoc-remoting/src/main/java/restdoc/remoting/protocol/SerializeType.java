package restdoc.remoting.protocol;

/**
 * @author ubuntu-m
 */
public enum SerializeType {

    /**
     * Only support json
     * <p>
     * Other format in future will be support
     */
    JSON((byte) 0),
    ;

    private byte code;

    SerializeType(byte code) {
        this.code = code;
    }

    public static SerializeType valueOf(byte code) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getCode() == code) {
                return serializeType;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
