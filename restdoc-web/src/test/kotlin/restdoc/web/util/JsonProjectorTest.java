package restdoc.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * <p>说明</p>
 *
 * <p>作用</p>
 *
 * <ul>
 *   <li>作用1</li>
 * </ul>
 *
 * @since 2.0
 */
public class JsonProjectorTest {


    // users users[0] users[0][0] users[0][0].name = value
    @Test
    public void testResolve() throws JsonProcessingException {
        ObjectNode jsonTree = new JsonProjector(Lists.newArrayList(
                new PathValue("users[1][1].name", "value"),
                new PathValue("users[1][1].age", "value")
        )).project();

        System.err.println(jsonTree);
    }

}
