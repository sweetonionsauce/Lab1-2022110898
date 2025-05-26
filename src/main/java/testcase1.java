import org.junit.Test;
import static org.junit.Assert.*;

public class testcase1 {

    // 测试用例1: 两个单词都不在图中
    @Test
    public void testBothWordsNotPresent() {
        Graph graph = new Graph();
        // 确保图中没有"apple"和"moon"
        String result = main.queryBridgeWords(graph, "Apple", "moon");
        assertEquals("No apple and moon in the graph!", result);
    }

    // 测试用例2: 存在桥接词"explore"
    @Test
    public void testSingleBridgeWordExists() {
        Graph graph = new Graph();
        // 构建测试图: to -> explore -> strange
        graph.addEdge("to", "explore");
        graph.addEdge("explore", "strange");
        // 添加其他必要节点（如果测试数据需要）
        graph.addEdge("to", "other"); // 无关边

        String result = main.queryBridgeWords(graph, "To", "Strange");
        assertEquals("The bridge words from \"to\" to \"strange\" is: \"explore\".", result);
    }

    // 测试用例3: 节点存在但没有桥接词
    @Test
    public void testNoBridgeWordsBetweenExistingNodes() {
        Graph graph = new Graph();
        // 构建测试图: seek -> other 和 out <- another
        graph.addEdge("seek", "other");
        graph.addEdge("another", "out");

        String result = main.queryBridgeWords(graph, "seek", "out");
        assertEquals("No bridge words from seek to out!", result);
    }

    // 测试无效输入（主程序处理，此处验证方法参数校验）
    @Test
    public void testInvalidInputHandling() {
        Graph graph = new Graph();
        // 空输入测试（方法本身不处理，此处演示参数校验）
        String result = main.queryBridgeWords(graph, "", "");
        assertEquals("No  and  in the graph!", result); // 根据实际逻辑调整
    }
}