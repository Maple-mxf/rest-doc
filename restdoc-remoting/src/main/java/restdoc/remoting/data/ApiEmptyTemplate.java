package restdoc.remoting.data;


import java.util.Arrays;

public class ApiEmptyTemplate {

    private String[] supportMethod;

    private String pattern;

    @Deprecated
    private boolean requireBody;

    private String function;

    private String[] consumer;

    private String[] produces;

    private String[] uriVarField;

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

    public boolean isRequireBody() {
        return requireBody;
    }

    public void setRequireBody(boolean requireBody) {
        this.requireBody = requireBody;
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

    public String[] getUriVarField() {
        return uriVarField;
    }

    public void setUriVarField(String[] uriVarField) {
        this.uriVarField = uriVarField;
    }

    @Override
    public String toString() {
        return "ApiEmptyTemplate{" +
                "supportMethod=" + Arrays.toString(supportMethod) +
                ", pattern='" + pattern + '\'' +
                ", requireBody=" + requireBody +
                ", function='" + function + '\'' +
                ", consumer=" + Arrays.toString(consumer) +
                ", produces=" + Arrays.toString(produces) +
                ", uriVarField=" + Arrays.toString(uriVarField) +
                ", controller='" + controller + '\'' +
                '}';
    }
}
