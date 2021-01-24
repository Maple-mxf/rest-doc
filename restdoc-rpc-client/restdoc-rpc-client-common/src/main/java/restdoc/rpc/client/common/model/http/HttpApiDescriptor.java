package restdoc.rpc.client.common.model.http;

import restdoc.rpc.client.common.model.ApiDescriptor;
import restdoc.rpc.client.common.model.FieldType;
import restdoc.rpc.client.common.util.MD5Util;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HttpApiDescriptor
 */
public class HttpApiDescriptor implements ApiDescriptor {

    private String id;

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

    /*Body is Require*/
    private boolean requireBody;

    /*File is Require*/
    private boolean requireFile;

    @Deprecated
    private Map<String, List<ParameterDescriptor>> requestHeaderParameters = new HashMap<>(4);

    private Set<KeyValuePair> requestHeaders = new HashSet<>();

    private Set<KeyValuePair> responseHeaders = new HashSet<>();

    private Set<ParameterDescriptor> queryParamParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> matrixVariableParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> pathVariableParameters = new HashSet<>(4);

    private Set<ParameterDescriptor> requestBodyParameters = new HashSet<>(4);

    private ParameterDescriptor responseBodyDescriptor = null;

    @Deprecated
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

    @Deprecated
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

    public static class KeyValuePair implements java.io.Serializable {
        private String name;
        private Boolean require = true;
        private Object defaultValue = null;

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

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    /**
     * ParameterDescriptor
     */
    public static class ParameterDescriptor implements java.io.Serializable {

        private String name;

        private Boolean require = true;

        private Object defaultValue = null;

        /**
         * TODO
         */
        private Object supplementary = null;

        /**
         * @see Class#getName()
         */
        private FieldType type;

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

        public FieldType getType() {
            return type;
        }

        public void setType(FieldType type) {
            this.type = type;
        }

        public Object getSupplementary() {
            return supplementary;
        }

        public void setSupplementary(Object supplementary) {
            this.supplementary = supplementary;
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
        this.id = MD5Util.MD5Encode(this.pattern, StandardCharsets.UTF_8.name());
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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


    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Override
    public String id() {
        return this.id;
    }

    public boolean isRequireBody() {
        return requireBody;
    }

    public void setRequireBody(boolean requireBody) {
        this.requireBody = requireBody;
    }

    public boolean isRequireFile() {
        return requireFile;
    }

    public void setRequireFile(boolean requireFile) {
        this.requireFile = requireFile;
    }

    public void addRequestHeader(KeyValuePair pair) {
        this.requestHeaders.add(pair);
    }

    public void addResponseHeader(KeyValuePair pair) {
        this.responseHeaders.add(pair);
    }

    public Set<KeyValuePair> getRequestHeaders() {
        return requestHeaders;
    }

    public Set<KeyValuePair> getResponseHeaders() {
        return responseHeaders;
    }
}
