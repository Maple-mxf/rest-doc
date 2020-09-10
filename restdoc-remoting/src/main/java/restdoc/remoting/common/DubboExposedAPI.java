package restdoc.remoting.common;

import java.util.List;

public class DubboExposedAPI implements ExposedAPI {

    private String name;
    private List<ExposedMethod> exposedMethods;
    private String uniqueKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExposedMethod> getExposedMethods() {
        return exposedMethods;
    }

    public void setExposedMethods(List<ExposedMethod> exposedMethods) {
        this.exposedMethods = exposedMethods;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Override
    public String uniqueKey() {
        return uniqueKey;
    }

    public static class ExposedMethod {
        private String paramDesc;
        private String[] compatibleParamSignatures;
        private String[] parameterClasses;
        private String returnClass;
        private String[] returnTypes;
        private String methodName;
        private boolean generic;
        private String[] parameterNames;

        public ExposedMethod() {
        }

        public ExposedMethod(String paramDesc, String[] compatibleParamSignatures,
                             String[] parameterClasses,String[] parameterNames, String returnClass,
                             String[] returnTypes, String methodName, boolean generic) {

            this.paramDesc = paramDesc;
            this.compatibleParamSignatures = compatibleParamSignatures;
            this.parameterClasses = parameterClasses;
            this.returnClass = returnClass;
            this.returnTypes = returnTypes;
            this.methodName = methodName;
            this.generic = generic;
            this.parameterNames =parameterNames;
        }

        public String getParamDesc() {
            return paramDesc;
        }

        public String[] getCompatibleParamSignatures() {
            return compatibleParamSignatures;
        }

        public void setParamDesc(String paramDesc) {
            this.paramDesc = paramDesc;
        }

        public void setCompatibleParamSignatures(String[] compatibleParamSignatures) {
            this.compatibleParamSignatures = compatibleParamSignatures;
        }

        public String[] getParameterClasses() {
            return parameterClasses;
        }

        public void setParameterClasses(String[] parameterClasses) {
            this.parameterClasses = parameterClasses;
        }

        public String getReturnClass() {
            return returnClass;
        }

        public void setReturnClass(String returnClass) {
            this.returnClass = returnClass;
        }

        public String[] getReturnTypes() {
            return returnTypes;
        }

        public void setReturnTypes(String[] returnTypes) {
            this.returnTypes = returnTypes;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public void setGeneric(boolean generic) {
            this.generic = generic;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isGeneric() {
            return generic;
        }

        public String[] getParameterNames() {
            return parameterNames;
        }

        public void setParameterNames(String[] parameterNames) {
            this.parameterNames = parameterNames;
        }
    }
}
