package restdoc.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2.0
 */
public class BackwardsJsonBuilderTest {

    @Test
    public void buildJson() {
        List<KeyValue> keyValues = new ArrayList<>();

        /*keyValues.add(new KeyValue("user.name", "jack"));
        keyValues.add(new KeyValue("user[].name", "jack array"));
        keyValues.add(new KeyValue("company.name", "alibabacloud"));*/

        /**/
        keyValues.add(new KeyValue("company.name[1]", "alibabacloud"));

        System.err.println(new BackwardsJsonBuilder(keyValues).getJsonTree());
    }
}
