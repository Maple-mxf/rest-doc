package smartdoc.dashboard.base;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.function.Function;

public class GraphTest {

    public static void main(String[] args) {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.addNode("A");
    }

    Function<String, Object> function = t -> t.toLowerCase();
}


