import java.awt.*;
import java.util.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;

import javax.swing.*;

class NodeDistance implements Comparable<NodeDistance> {
    String node;
    int distance;

    public NodeDistance(String node, int distance) {
        this.node = node;
        this.distance = distance;
    }

    @Override
    public int compareTo(NodeDistance other) {
        return Integer.compare(this.distance, other.distance);
    }
}

class Graph {
    private Set<String> nodes = new HashSet<>(); // 新增节点集合
    private Map<String, Map<String, Integer>> adjacencyList;
    private Map<String, Set<String>> incomingEdges;

    public Graph() {
        adjacencyList = new HashMap<>();
        incomingEdges = new HashMap<>();
    }

    public void addEdge(String from, String to) {
        from = from.toLowerCase();
        to = to.toLowerCase();
        // 将from和to添加到节点集合
        nodes.add(from);
        nodes.add(to);
        adjacencyList.putIfAbsent(from, new HashMap<>());
        Map<String, Integer> edges = adjacencyList.get(from);
        edges.put(to, edges.getOrDefault(to, 0) + 1);

        incomingEdges.putIfAbsent(to, new HashSet<>());
        incomingEdges.get(to).add(from);
    }



    public void printAllNodes() {
        System.out.println("当前图包含节点：" + String.join(", ", adjacencyList.keySet()));
    }

    // 修改containsNode检查节点集合
    public boolean containsNode(String word) {
        return nodes.contains(word.toLowerCase());
    }

    public Map<String, Integer> getEdges(String node) {
        return adjacencyList.getOrDefault(node.toLowerCase(), new HashMap<>());
    }

    // 修改getNodes()返回节点集合
    public Set<String> getNodes() {
        return nodes;
    }

    public int getOutDegree(String node) {
        return adjacencyList.getOrDefault(node.toLowerCase(), new HashMap<>()).size();
    }

    public List<String> getIncomingNodes(String node) {
        return new ArrayList<>(incomingEdges.getOrDefault(node.toLowerCase(), new HashSet<>()));
    }
    private static void showDirectedGraph(Graph graph) {
        // 生成DOT图形描述
        StringBuilder dot = new StringBuilder("digraph G {\n    rankdir=LR;\n    node [shape=circle];\n");

        for (String node : graph.getNodes()) {
            Map<String, Integer> edges = graph.getEdges(node);
            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                String toNode = entry.getKey();
                int weight = entry.getValue();
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];\n",
                        node, toNode, weight));
            }
        }
        dot.append("}");

        try {
            // 写入临时文件
            File dotFile = File.createTempFile("graph", ".dot");
            Files.write(dotFile.toPath(), dot.toString().getBytes());

            // 生成PNG图像
            File pngFile = File.createTempFile("graph", ".png");
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng",
                    dotFile.getAbsolutePath(), "-o", pngFile.getAbsolutePath());

            int exitCode = pb.start().waitFor();

            if (exitCode == 0) {
                // 自动打开图像
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pngFile);
                } else {
                    System.out.println("Generated image: " + pngFile.getAbsolutePath());
                }
            } else {
                System.out.println("Graphviz failed. Ensure it's installed from https://graphviz.org/");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error generating graph: " + e.getMessage());
        }
    }

}

public class main {
    public static void main(String[] args) {
        /*
        if (args.length == 0) {
            System.out.println("Please provide the file path as an argument.");
            return;
        }
        */

        //String filePath = args[0];
        String filePath = "./res/Easy Test2.txt";
        //String filePath = "./res/Cursed Be The Treasure.txt";
        Graph graph = buildGraphFromFile(filePath);
        if (graph == null) {
            System.out.println("Error reading file.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose function:");
            System.out.println("1. Show directed graph");
            System.out.println("2. Query bridge words");
            System.out.println("3. Generate new text");
            System.out.println("4. Calculate shortest path");
            System.out.println("5. Calculate PageRank");
            System.out.println("6. Random walk");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    showDirectedGraph(graph);
                    break;
                case 2:
                    System.out.print("Enter word1 and word2: ");
                    String[] words = scanner.nextLine().split("\\s+");
                    if (words.length != 2) {
                        System.out.println("Invalid input.");
                        break;
                    }
                    System.out.println(queryBridgeWords(graph, words[0], words[1]));
                    break;
                case 3:
                    System.out.print("Enter text: ");
                    String inputText = scanner.nextLine();
                    System.out.println(generateNewText(graph, inputText));
                    break;
                case 4:
                    System.out.print("Enter word1 and word2: ");
                    String[] pathWords = scanner.nextLine().split("\\s+");
                    if (pathWords.length != 2) {
                        System.out.println("Invalid input.");
                        break;
                    }
                    System.out.println(calcShortestPath(graph, pathWords[0], pathWords[1]));
                    break;
                case 5:
                    System.out.print("Enter word: ");
                    String word = scanner.nextLine().trim().toLowerCase();
                    System.out.println("PageRank: " + String.format("%.4f", calPageRank(graph, word)));
                    break;
                case 6:
                    System.out.println("Random walk: " + randomWalk(graph));
                    break;
                case 0:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static Graph buildGraphFromFile(String filePath) {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder fullContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fullContent.append(line).append(" ");
            }

            // 改进后的正则表达式处理
            String processedText = fullContent.toString()
                    .replaceAll("[^a-zA-Z' ]", " ")  // 保留字母和单引号
                    .toLowerCase()
                    .replaceAll("'+", "'")           // 处理多个单引号
                    .replaceAll("\\s+", " ");

            // 调试输出文件内容
            System.out.println("Processed Text: " + processedText);

            // 改进的单词分割
            String[] words = processedText.split("[^a-zA-Z']+");

            List<String> prevWords = new ArrayList<>();
            for (String word : words) {
                if (!word.isEmpty() && !word.equals("'")) { // 过滤空单词和单引号
                    String cleanWord = word.replaceAll("^'+|'+$", ""); // 去除首尾单引号
                    if (!cleanWord.isEmpty()) {
                        if (!prevWords.isEmpty()) {
                            String prevWord = prevWords.get(prevWords.size() - 1);
                            graph.addEdge(prevWord, cleanWord);
                        }
                        prevWords.add(cleanWord);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }

        // 调试输出所有节点
        System.out.println("Final nodes in graph: " + String.join(", ", graph.getNodes()));
        return graph;
    }

    public static void showDirectedGraph(Graph graph) {
        // 生成DOT图形描述，确保显示所有节点
        StringBuilder dot = new StringBuilder("digraph G {\n    rankdir=LR;\n    node [shape=circle];\n");
        // 显式声明所有节点，确保孤立节点也显示
        for (String node : graph.getNodes()) {
            dot.append(String.format("    \"%s\";\n", node));
        }
        // 添加所有边
        for (String node : graph.getNodes()) {
            Map<String, Integer> edges = graph.getEdges(node);
            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                String toNode = entry.getKey();
                int weight = entry.getValue();
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];\n", node, toNode, weight));
            }
        }
        dot.append("}");

        try {
            // 写入临时文件
            File dotFile = File.createTempFile("graph", ".dot");
            Files.write(dotFile.toPath(), dot.toString().getBytes());

            // 生成PNG图像
            File pngFile = File.createTempFile("graph", ".png");
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile.getAbsolutePath(), "-o", pngFile.getAbsolutePath());

            int exitCode = pb.start().waitFor();

            if (exitCode == 0) {
                // 自动打开图像
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pngFile);
                } else {
                    System.out.println("Generated image: " + pngFile.getAbsolutePath());
                }
            } else {
                System.out.println("Graphviz failed. Ensure it's installed from https://graphviz.org/");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error generating graph: " + e.getMessage());
        }
        /*
        for (String node : graph.getNodes()) {
            System.out.print(node + " -> ");
            Map<String, Integer> edges = graph.getEdges(node);
            List<String> edgeList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                edgeList.add(entry.getKey() + "(" + entry.getValue() + ")");
            }
            System.out.println(String.join(", ", edgeList));
        }*/
    }

    public static String queryBridgeWords(Graph graph, String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!graph.containsNode(word1) || !graph.containsNode(word2)) {
            if (!graph.containsNode(word1) && !graph.containsNode(word2)) {
                return "No " + word1 + " and " + word2 + " in the graph!";
            } else if (!graph.containsNode(word1)) {
                return "No " + word1 + " in the graph!";
            } else {
                return "No " + word2 + " in the graph!";
            }
        }

        List<String> bridges = new ArrayList<>();
        Map<String, Integer> word1Edges = graph.getEdges(word1);
        for (String word3 : word1Edges.keySet()) {
            if (graph.getEdges(word3).containsKey(word2)) {
                bridges.add(word3);
            }
        }

        if (bridges.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            StringJoiner sj = new StringJoiner("\", \"", "\"", "\"");
            bridges.forEach(sj::add);
            String output = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + sj.toString() + ".";
            return bridges.size() == 1 ? output.replace("are", "is") : output;
        }
    }

    public static String generateNewText(Graph graph, String inputText) {
        String[] words = inputText.split("[^a-zA-Z]+");
        List<String> originalWords = Arrays.stream(words).filter(w -> !w.isEmpty()).collect(Collectors.toList());
        if (originalWords.size() < 2) {
            return inputText;
        }

        List<String> newText = new ArrayList<>();
        newText.add(originalWords.get(0));
        Random rand = new Random();
        for (int i = 0; i < originalWords.size() - 1; i++) {
            String word1 = originalWords.get(i).toLowerCase();
            String word2 = originalWords.get(i + 1).toLowerCase();
            List<String> bridges = new ArrayList<>();
            if (graph.containsNode(word1) && graph.containsNode(word2)) {
                Map<String, Integer> word1Edges = graph.getEdges(word1);
                for (String word3 : word1Edges.keySet()) {
                    if (graph.getEdges(word3).containsKey(word2)) {
                        bridges.add(word3);
                    }
                }
            }
            if (!bridges.isEmpty()) {
                newText.add(bridges.get(rand.nextInt(bridges.size())));
            }
            newText.add(originalWords.get(i + 1));
        }
        return String.join(" ", newText);
    }

    public static String calcShortestPath(Graph graph, String start, String end) {
        start = start.toLowerCase();
        end = end.toLowerCase();
        if (!graph.containsNode(start) || !graph.containsNode(end)) {
            return "One or both words not in graph.";
        }

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        for (String node : graph.getNodes()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        pq.add(new NodeDistance(start, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            if (current.node.equals(end)) break;
            if (current.distance > dist.get(current.node)) continue;

            for (Map.Entry<String, Integer> edge : graph.getEdges(current.node).entrySet()) {
                String neighbor = edge.getKey();
                int weight = edge.getValue();
                int alt = dist.get(current.node) + weight;
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    prev.put(neighbor, current.node);
                    pq.add(new NodeDistance(neighbor, alt));
                }
            }
        }

        if (dist.get(end) == Integer.MAX_VALUE) {
            return "No path from " + start + " to " + end + ".";
        }

        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
        }
        return "Shortest path: " + String.join(" → ", path) + " (length " + dist.get(end) + ")";
    }

    public static double calPageRank(Graph graph, String word) {
        graph.printAllNodes(); // 新增调试语句
        word = word.toLowerCase();
        if (!graph.containsNode(word)) return 0.0;

        Map<String, Double> pr = new HashMap<>();
        int n = graph.getNodes().size();
        if (n == 0) return 0.0; // 防止除以0
        double d = 0.85;
        int maxIterations = 100;
        double tolerance = 0.0001;

        // 初始化PR值
        for (String node : graph.getNodes()) {
            pr.put(node.toLowerCase(), 1.0 / n); // 确保节点名小写匹配
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> newPr = new HashMap<>();
            // 计算所有悬挂节点的总PR值（出度为0的节点）
            double danglingPR = 0.0;
            for (String node : graph.getNodes()) {
                String lowerNode = node.toLowerCase();
                int outDegree = graph.getOutDegree(lowerNode); // 确保出度查询使用小写
                System.out.printf("节点 %s 出度: %d, PR: %.4f%n", lowerNode, outDegree, pr.get(lowerNode));
                if (graph.getOutDegree(node) == 0) {
                    danglingPR += pr.getOrDefault(lowerNode, 0.0);
                }
            }
            System.out.printf("迭代 %d: 悬挂总PR=%.4f%n", iter, danglingPR);
            // 计算每个节点的新PR值
            for (String node : graph.getNodes()) {
                String lowerNode = node.toLowerCase();
                double contribution = 0.0;
                // 入边贡献计算（统一小写）
                for (String incoming : graph.getIncomingNodes(lowerNode)) {
                    String lowerIncoming = incoming.toLowerCase();
                    int outDegree = graph.getOutDegree(lowerIncoming);
                    if (outDegree > 0) {
                        contribution += pr.getOrDefault(lowerIncoming, 0.0) / outDegree;
                    }
                }

                // 修正悬挂贡献计算
                double totalContribution = contribution + (danglingPR / n);
                newPr.put(lowerNode, (1 - d)/n + d * totalContribution);
            }

            // 收敛检查（小写键匹配）
            boolean converged = true;
            for (String node : graph.getNodes()) {
                String lowerNode = node.toLowerCase();
                double oldVal = pr.getOrDefault(lowerNode, 0.0);
                double newVal = newPr.getOrDefault(lowerNode, 0.0);
                if (Math.abs(newVal - oldVal) > tolerance) {
                    converged = false;
                    break;
                }
            }
            if (converged) break;

            pr = newPr;
        }

        return pr.getOrDefault(word, 0.0);
    }

    public static String randomWalk(Graph graph) {
        List<String> nodes = new ArrayList<>(graph.getNodes());
        if (nodes.isEmpty()) return "";
        Random rand = new Random();
        String current = nodes.get(rand.nextInt(nodes.size()));
        List<String> path = new ArrayList<>();
        Set<String> visitedEdges = new HashSet<>();
        path.add(current);

        while (true) {
            Map<String, Integer> edges = graph.getEdges(current);
            if (edges.isEmpty()) break;
            List<String> candidates = new ArrayList<>(edges.keySet());
            String next = candidates.get(rand.nextInt(candidates.size()));
            String edge = current + "->" + next;
            if (visitedEdges.contains(edge)) break;
            visitedEdges.add(edge);
            path.add(next);
            current = next;
        }
        return String.join(" ", path);
    }
}
//做一点修改
//随便写一点修改