package restdoc.web.codesample;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import restdoc.web.model.doc.http.HttpDocument;
import restdoc.web.model.doc.http.URIVarDescriptor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Overman
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JavaCodeUnitTestCaseSampleTest {

    private static final VelocityEngine VE = new VelocityEngine();

    static {
        VE.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        VE.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        VE.init();
    }

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void gen() {

        HttpDocument document = mongoTemplate.findById("756136650580430848", HttpDocument.class);

        Template configTemplate = VE.getTemplate("codesample/JavaCodeUnitTestCaseSample.java.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("requestHeaders", document.getRequestHeaderDescriptor());

        Map<String, Object> uriVars = document.getUriVarDescriptors()
                .stream()
                .collect(Collectors.toMap(URIVarDescriptor::getField, URIVarDescriptor::getValue, (d1, d2) -> d2));

        ctx.put("uriVars", uriVars);
        ctx.put("url", document.getUrl());
        ctx.put("method", document.getMethod());
        ctx.put("since", DateFormatUtils.format(new Date(), "yyyy/MM/dd"));


        StringWriter writer = new StringWriter();
        configTemplate.merge(ctx, writer);

        try {
            writer.flush();
            writer.close();

            String res = writer.getBuffer().toString();
            System.err.println(res);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
