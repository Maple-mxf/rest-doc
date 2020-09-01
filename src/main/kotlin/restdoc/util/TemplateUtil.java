package restdoc.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

public class TemplateUtil {

    private static final VelocityEngine VE = new VelocityEngine();

    static {
        VE.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        VE.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        VE.init();
    }

    /**
     * @see java.io.Writer
     */
    public static String gen(Map<String, Object> map) {
        Template configTemplate = VE.getTemplate("tem.html");

        VelocityContext ctx = new VelocityContext();
        map.forEach(ctx::put);

        StringWriter writer = new StringWriter();
        configTemplate.merge(ctx, writer);

        try {
            writer.flush();
            writer.close();

            String res = writer.getBuffer().toString();
            System.err.println(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static InputStream mappedStream(String src) {
        if (StringUtils.isNotBlank(src))
            return new ByteArrayInputStream(src.getBytes());
        return null;
    }
}
