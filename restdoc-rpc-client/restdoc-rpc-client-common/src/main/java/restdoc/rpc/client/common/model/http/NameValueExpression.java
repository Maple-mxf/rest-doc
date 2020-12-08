package restdoc.rpc.client.common.model.http;

public interface NameValueExpression<T> {

    String getName();

    T getValue();

    boolean isNegated();

}
