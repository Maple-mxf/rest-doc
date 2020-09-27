package restdoc.remoting.common;


import java.util.Arrays;

public class RestWebExposedAPI implements ExposedAPI {

    @Deprecated
    private String[] supportMethod;

    private String pattern;

    private String function;

    private String[] consumer;

    private String[] produces;

    private String[] uriVarFields;

    /**
     * Bean type name
     */
    private String controller;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String[] getSupportMethod() {
        return supportMethod;
    }

    public void setSupportMethod(String[] supportMethod) {
        this.supportMethod = supportMethod;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public String[] getConsumer() {
        return consumer;
    }

    public void setConsumer(String[] consumer) {
        this.consumer = consumer;
    }

    public String[] getUriVarFields() {
        return uriVarFields;
    }

    public void setUriVarFields(String[] uriVarFields) {
        this.uriVarFields = uriVarFields;
    }

    @Override
    public String toString() {
        return "ApiEmptyTemplate{" +
                "supportMethod=" + Arrays.toString(supportMethod) +
                ", pattern='" + pattern + '\'' +
                ", function='" + function + '\'' +
                ", consumer=" + Arrays.toString(consumer) +
                ", produces=" + Arrays.toString(produces) +
                ", uriVarField=" + Arrays.toString(uriVarFields) +
                ", controller='" + controller + '\'' +
                '}';
    }

    @Override
    public String uniqueKey() {
        return controller + function;
    }
}
