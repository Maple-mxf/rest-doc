package restdoc.util;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Deprecated
public class JsonParserTest {

    @Test
    public void extractString() {
        Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
        Matcher matcher = pattern.matcher("users[1][2]");
        boolean isFind = matcher.find();

        System.err.println(isFind);
        System.err.println(matcher.group(1));

        Pattern compile = Pattern.compile("(\\[\\d+\\])+");
        Matcher matcher1 = compile.matcher("ph[1][2]");
        System.err.println(matcher1.find());
        System.err.println(matcher1.group(0));
    }


    final Pattern fieldNamePattern = Pattern.compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    @Test
    public void extractField(){
        Matcher matcher = fieldNamePattern.matcher("ph[1][2]");

        if (matcher.find()){
            System.err.println(matcher.group(0));
        }
    }

  /*  @Test
    public void testParseJson(){

        new BodyFieldDescriptor("")

        List<BodyFieldDescriptor> descriptors = new ArrayList<>();
        descriptors.add()

        new JsonParser()
    }*/
}
