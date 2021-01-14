package restdoc.web.base;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public class GraphTest {

    public static void main(String[] args) {
        MutableGraph<String> graph = GraphBuilder.directed().build();

        graph.addNode("A");

    }
}
