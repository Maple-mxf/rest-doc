package restdoc.remoting.data;


public class ApiEmptyTemplate {

    private String[] supportMethod;

    private String pattern;

    @Deprecated
    private boolean requireBody;

    private String function;


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
}
