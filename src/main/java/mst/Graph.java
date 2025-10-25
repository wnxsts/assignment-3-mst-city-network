package mst;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<int[]>> adj;
    private final List<Edge> edges;

    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        this.edges = new ArrayList<>();
    }

    public int vertices() { return n; }
    public List<Edge> edges() { return edges; }
    public List<List<int[]>> adjacency() { return adj; }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new int[]{v, w});
        adj.get(v).add(new int[]{u, w});
        edges.add(new Edge(u, v, w));
    }

    public int numEdges() { return edges.size(); }

    @SuppressWarnings("unchecked")
    public static Graph fromJson(Map<String, Object> map) {
        int n = ((Number) map.get("num_vertices")).intValue();
        Graph g = new Graph(n);
        List<Map<String, Object>> es = (List<Map<String, Object>>) map.get("edges");
        for (Map<String, Object> e : es) {
            int u = ((Number) e.get("u")).intValue();
            int v = ((Number) e.get("v")).intValue();
            int w = ((Number) e.get("w")).intValue();
            g.addEdge(u, v, w);
        }
        return g;
    }
}