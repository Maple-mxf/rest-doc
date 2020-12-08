package restdoc.rpc.client.common.model.http;

import java.util.HashSet;
import java.util.Set;

public class ParamExpression extends AbstractNameValueExpression<String> {

    private static final String[] SUBMIT_IMAGE_SUFFIXES = {".x", ".y"};

    private final Set<String> namesToMatch = new HashSet<>(SUBMIT_IMAGE_SUFFIXES.length + 1);

    public ParamExpression(String expression) {
        super(expression);
        this.namesToMatch.add(getName());
        for (String suffix : SUBMIT_IMAGE_SUFFIXES) {
            this.namesToMatch.add(getName() + suffix);
        }
    }

    @Override
    protected boolean isCaseSensitiveName() {
        return true;
    }

    @Override
    protected String parseValue(String valueExpression) {
        return valueExpression;
    }
}