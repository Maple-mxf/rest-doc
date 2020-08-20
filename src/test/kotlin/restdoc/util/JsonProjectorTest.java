package restdoc.util;

import com.google.common.collect.Lists;
import org.junit.Test;
import restdoc.model.PathValue;

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


    @Test
    public void testResolve() {
        new JsonProjector(Lists.newArrayList(new PathValue("users[0][1][2].name", "value")));
    }

}
