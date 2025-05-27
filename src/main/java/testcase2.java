import org.junit.Test;
import static org.junit.Assert.*;

public class testcase2 {

    // 测试用例1: 正常多跳路径存在
    @Test
    public void testMultiHopPathExists() {
        Graph graph = new Graph();
        // 构建路径: new → worlds → to → explore → strange
        graph.addEdge("new", "worlds");
        graph.addEdge("worlds", "to");
        graph.addEdge("to", "explore");
        graph.addEdge("explore", "strange");
        String result = main.calcShortestPath(graph, "new", "strange");
        assertEquals("Shortest path: new → worlds → to → explore → strange (length 4)", result);
    }

    // 测试用例2: 节点不存在图空
    @Test
    public void testBothWordsNotInEmptyGraph() {
        Graph graph = new Graph();
        String result = main.calcShortestPath(graph, "apple", "moon");
        assertEquals("One or both words not in graph.", result);
    }

    // 测试用例3: 路径不存在但节点存在
    @Test
    public void testNoPathBetweenExistingNodes() {
        Graph graph = new Graph();
        // 构建孤立子图
        graph.addEdge("civilization", "frontier");
        graph.addEdge("to", "explore");
        String result = main.calcShortestPath(graph, "civilization", "to");
        assertEquals("No path from civilization to to.", result);
    }

    // 测试用例4: 直接边存在
    @Test
    public void testDirectEdgeExists() {
        Graph graph = new Graph();
        graph.addEdge("new", "civilization");
        String result = main.calcShortestPath(graph, "new", "civilization");
        assertEquals("Shortest path: new → civilization (length 1)", result);
    }

    // 测试用例5: 多路径中的最短路径
    @Test
    public void testMultiplePathsSelectShortest() {
        Graph graph = new Graph();
        // 主路径: new → worlds → to → seek → out (length=4)
        graph.addEdge("new", "worlds");
        graph.addEdge("worlds", "to");
        graph.addEdge("to", "seek");
        graph.addEdge("seek", "out");

        // 干扰路径: new → test → out (length=2)
        graph.addEdge("new", "test");
        graph.addEdge("test", "out");

        String result = main.calcShortestPath(graph, "new", "out");
        assertEquals("Shortest path: new → test → out (length 2)", result);
    }

    // 测试用例6: 反向路径检测
    @Test
    public void testReversePathDetection() {
        Graph graph = new Graph();
        // 构建路径: to → explore → strange → new
        graph.addEdge("to", "explore");
        graph.addEdge("explore", "strange");
        graph.addEdge("strange", "new");
        String result = main.calcShortestPath(graph, "to", "new");
        assertEquals("Shortest path: to → explore → strange → new (length 3)", result);
    }

    // 边界测试: 相同节点
    @Test
    public void testSameNodePath() {
        Graph graph = new Graph();
        graph.addEdge("node", "node");  // 自环边
        String result = main.calcShortestPath(graph, "node", "node");
        assertTrue(result.contains("node → node (length 1)") || result.contains("length 0"));
    }
}