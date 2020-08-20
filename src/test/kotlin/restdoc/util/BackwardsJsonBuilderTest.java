package restdoc.util;

import org.junit.Test;
import restdoc.model.PathValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2.0
 */
public class BackwardsJsonBuilderTest {

    @Test
    public void buildJson() {
        List<PathValue> pathValues = new ArrayList<>();

        /*keyValues.add(new KeyValue("user.name", "jack"));
        keyValues.add(new KeyValue("user[].name", "jack array"));
        keyValues.add(new KeyValue("company.name", "alibabacloud"));*/

        pathValues.add(new PathValue("company.name[1]", "alibabacloud"));
        pathValues.add(new PathValue("company.idle[1]", "alibabacloud"));

        System.err.println(new BackwardsJsonBuilder(pathValues).getJsonTree());
    }
}
