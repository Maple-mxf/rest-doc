package restdoc.rpc.client.common.model.http;

import restdoc.rpc.client.common.model.ApiDescriptor;

import java.util.*;


/**
 */
public class HttpApiDescriptor implements ApiDescriptor {

    /*Http Api interface default name*/
    private String name;

    /*RestDoc recommend request method*/
    private String method;

    /*URL path*/
    private String pattern;

    /*MethodName*/
    private String endpoint;

    /*Controller class package Name*/
    private String packageName;

    /*Http Api interface response type*/
    private String responseType;

    /**
     * @see HeaderExpression
     */
    private List<HeaderExpression> requestHeaderExpressions;

    private List<HeaderExpression> responseHeaderExpressions;

    private List<ParamExpression> paramExpressions;

    private Map<String, List<ParameterDescriptor>> requestHeaderParameters = new HashMap<>(4);

    private Set<ParameterDescriptor> queryParamParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> matrixVariableParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> pathVariableParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> requestBodyParameters = new HashSet<>(4);

    private ParameterDescriptor responseBodyDescriptor = null;

    private Map<String, List<ParameterDescriptor>> responseHeaderParameters = new HashMap<>(4);

    public Map<String, List<ParameterDescriptor>> getRequestHeaderParameters() {
        return requestHeaderParameters;
    }

    public Set<ParameterDescriptor> getQueryParamParameters() {
        return queryParamParameters;
    }

    public Set<ParameterDescriptor> getMatrixVariableParameters() {
        return matrixVariableParameters;
    }

    public Set<ParameterDescriptor> getPathVariableParameters() {
        return pathVariableParameters;
    }

    public Set<ParameterDescriptor> getRequestBodyParameters() {
        return requestBodyParameters;
    }

    public Map<String, List<ParameterDescriptor>> getResponseHeaderParameters() {
        return responseHeaderParameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParameterDescriptor getResponseBodyDescriptor() {
        return responseBodyDescriptor;
    }

    public void setResponseBodyDescriptor(ParameterDescriptor responseBodyDescriptor) {
        this.responseBodyDescriptor = responseBodyDescriptor;
    }

    /**
     * ParameterDescriptor
     */
    public static class ParameterDescriptor implements java.io.Serializable {

        private String name;

        private Boolean require = true;

        private Object defaultValue = null;

        private Object supplementary = null;

        /**
         * @see Class#getName()
         */
        private String type;

        public ParameterDescriptor() {
        }

        public ParameterDescriptor(String name) {
            this.name = name;
        }

        public ParameterDescriptor(String name, Boolean require) {
            this.name = name;
            this.require = require;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getRequire() {
            return require;
        }

        public void setRequire(Boolean require) {
            this.require = require;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getSupplementary() {
            return supplementary;
        }

        public void setSupplementary(Object supplementary) {
            this.supplementary = supplementary;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParameterDescriptor that = (ParameterDescriptor) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(require, that.require) &&
                    Objects.equals(defaultValue, that.defaultValue) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, require, defaultValue, type);
        }
    }

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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        if (packageName == null) {
            this.packageName = "NonPackage";
            return;
        }
        this.packageName = packageName;
    }

    public List<HeaderExpression> getRequestHeaderExpressions() {
        return requestHeaderExpressions;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRequestHeaderExpressions(List<HeaderExpression> requestHeaderExpressions) {
        this.requestHeaderExpressions = requestHeaderExpressions;
    }

    public List<HeaderExpression> getResponseHeaderExpressions() {
        return responseHeaderExpressions;
    }

    public void setResponseHeaderExpressions(List<HeaderExpression> responseHeaderExpressions) {
        this.responseHeaderExpressions = responseHeaderExpressions;
    }

    public List<ParamExpression> getParamExpressions() {
        return paramExpressions;
    }

    public void setParamExpressions(List<ParamExpression> paramExpressions) {
        this.paramExpressions = paramExpressions;
    }

    public void setRequestHeaderParameters(Map<String, List<ParameterDescriptor>> requestHeaderParameters) {
        this.requestHeaderParameters = requestHeaderParameters;
    }

    public void setQueryParamParameters(Set<ParameterDescriptor> queryParamParameters) {
        this.queryParamParameters = queryParamParameters;
    }

    public void setMatrixVariableParameters(Set<ParameterDescriptor> matrixVariableParameters) {
        this.matrixVariableParameters = matrixVariableParameters;
    }

    public void setPathVariableParameters(Set<ParameterDescriptor> pathVariableParameters) {
        this.pathVariableParameters = pathVariableParameters;
    }

    public void setRequestBodyParameters(Set<ParameterDescriptor> requestBodyParameters) {
        this.requestBodyParameters = requestBodyParameters;
    }

    public void setResponseHeaderParameters(Map<String, List<ParameterDescriptor>> responseHeaderParameters) {
        this.responseHeaderParameters = responseHeaderParameters;
    }

    @Override
    public String uniqueKey() {
        return controller + endpoint;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
}
