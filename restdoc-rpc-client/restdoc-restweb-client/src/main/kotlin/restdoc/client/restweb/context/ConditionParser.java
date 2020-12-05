package restdoc.client.restweb.context;

import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.remoting.common.RestWebApiDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionParser {

    public void parse(RequestMappingInfo requestMappingInfo, RestWebApiDescriptor emptyDescriptor) {

        ParamsRequestCondition paramsCondition = requestMappingInfo.getParamsCondition();
        ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
        ProducesRequestCondition producesCondition = requestMappingInfo.getProducesCondition();
        HeadersRequestCondition headersCondition = requestMappingInfo.getHeadersCondition();
        RequestCondition<?> customCondition = requestMappingInfo.getCustomCondition();

        // consumesCondition
        List<RestWebApiDescriptor.ParameterDescriptor> acceptableContentTypes
                = emptyDescriptor.getRequestHeaderParameters().getOrDefault("Content-Type", new ArrayList<>());
        acceptableContentTypes.addAll(
                consumesCondition.getConsumableMediaTypes()
                        .stream()
                        .map(t -> {
                            RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(null, true);
                            pd.setRequireEqualsValue(t.toString());
                            return pd;
                        })
                        .collect(Collectors.toList())
        );
        emptyDescriptor.getRequestHeaderParameters().put("Content-Type", acceptableContentTypes);

        // headersCondition
        headersCondition.getExpressions()
                .stream()
                .filter(NameValueExpression::isNegated)
                .map(t -> {
                    RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(t.getName(), true);
                    pd.setRequireEqualsValue(t.getValue());
                    return pd;
                })
                .collect(Collectors.groupingBy(RestWebApiDescriptor.ParameterDescriptor::getName))
                .forEach((key, value) -> {
                    List<RestWebApiDescriptor.ParameterDescriptor> pds
                            = emptyDescriptor.getRequestHeaderParameters().getOrDefault(key, new ArrayList<>());
                    pds.addAll(value);
                    emptyDescriptor.getRequestHeaderParameters().put(key, pds);
                });

        // producesCondition
        List<RestWebApiDescriptor.ParameterDescriptor> produceContentTypes = emptyDescriptor.getResponseHeaderParameters()
                .getOrDefault("Content-Type", new ArrayList<>());
        produceContentTypes.addAll(
                producesCondition.getProducibleMediaTypes()
                        .stream()
                        .map(t -> {
                            RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(null, true);
                            pd.setRequireEqualsValue(t.toString());
                            return pd;
                        })
                        .collect(Collectors.toList()));
        emptyDescriptor.getResponseHeaderParameters().put("Content-Type", produceContentTypes);

        // paramsCondition
        // If method is "GET" query string
        // If method is "POST" form data
//        paramsCondition.getMatchingCondition().
    }
}
