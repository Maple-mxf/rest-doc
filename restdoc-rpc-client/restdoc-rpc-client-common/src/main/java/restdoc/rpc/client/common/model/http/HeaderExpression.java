package restdoc.rpc.client.common.model.http;

public class HeaderExpression extends AbstractNameValueExpression<String> {

    public HeaderExpression(String expression) {
        super(expression);
    }

    @Override
    protected boolean isCaseSensitiveName() {
        return false;
    }

    @Override
    protected String parseValue(String valueExpression) {
        return valueExpression;
    }
}