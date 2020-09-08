package restdoc.remoting.common.body;

import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;
import java.util.Map;

/**
 * ReportClientExportInterfacesBody
 */
public class ClientExportInterfacesBody extends RemotingSerializable {

    private Map<String, ExportInterface> exportInterfaces;

    public Map<String, ExportInterface> getExportInterfaces() {
        return exportInterfaces;
    }

    @Override
    public String toString() {
        return "ReportClientExportInterfacesBody{" +
                "exportInterfaces=" + exportInterfaces +
                '}';
    }

    public void setExportInterfaces(Map<String, ExportInterface> exportInterfaces) {
        this.exportInterfaces = exportInterfaces;
    }

    public static class ExportInterface {
        private String name;
        private List<ExportMethod> exportMethods;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ExportMethod> getExportMethods() {
            return exportMethods;
        }

        public void setExportMethods(List<ExportMethod> exportMethods) {
            this.exportMethods = exportMethods;
        }
    }

    public static class ExportMethod {
        private String name;
        private List<Parameter> parameters;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(List<Parameter> parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "ExportMethod{" +
                    "name='" + name + '\'' +
                    ", parameters=" + parameters +
                    '}';
        }
    }


    public static class Parameter {
        private Class<?> type;
        private String name;

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
