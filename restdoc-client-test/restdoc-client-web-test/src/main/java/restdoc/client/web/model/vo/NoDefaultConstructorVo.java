package restdoc.client.web.model.vo;

public class NoDefaultConstructorVo {

    private String field;

    public NoDefaultConstructorVo() {
    }

    public NoDefaultConstructorVo(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
