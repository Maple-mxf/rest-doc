package restdoc.client.web.model.vo;

public class Response {

    private String code;

    private Object data;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Response ok(){
        Response response = new Response();
        response.setCode("200");
        return response;
    }
}
