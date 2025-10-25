package mst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AlgorithmTests {

    private Graph smallConnected() {
        Graph g = new Graph(4);
        g.addEdge(0,1,1);
        g.addEdge(1,2,2);
        g.addEdge(2,3,3);
        g.addEdge(0,3,4);
        g.addEdge(0,2,5);
        return g;
    }

    private Graph disconnected() {
        Graph g = new Graph(4);
        g.addEdge(0,1,1);
        g.addEdge(2,3,2);
        return g;
    }

    @Test
    public void costEquality() {
        Graph g = smallConnected();
        MSTResult p = Prim.run(g);
        MSTResult k = Kruskal.run(g);
        assertEquals(p.totalCost, k.totalCost, "Prim and Kruskal must have the same cost");
    }

    @Test
    public void edgesCountIsVminus1() {
        Graph g = smallConnected();
        MSTResult p = Prim.run(g);
        MSTResult k = Kruskal.run(g);
        assertEquals(g.vertices()-1, p.mstEdges.size());
        assertEquals(g.vertices()-1, k.mstEdges.size());
    }

    @Test
    public void mstIsAcyclic() {
        Graph g = smallConnected();
        assertTrue(isAcyclic(g.vertices(), Prim.run(g).mstEdges));
        assertTrue(isAcyclic(g.vertices(), Kruskal.run(g).mstEdges));
    }

    @Test
    public void disconnectedHandled() {
        Graph g = disconnected();
        // на несвязном графе MST не сможет иметь V-1 рёбер
        assertTrue(Prim.run(g).mstEdges.size() < g.vertices()-1);
        assertTrue(Kruskal.run(g).mstEdges.size() < g.vertices()-1);
    }

    @Test
    public void metricsNonNegative() {
        Graph g = smallConnected();
        MSTResult p = Prim.run(g);
        MSTResult k = Kruskal.run(g);
        assertTrue(p.timeMs >= 0 && k.timeMs >= 0);
        assertTrue(p.operations >= 0 && k.operations >= 0);
    }

    @Test
    public void reproducible() {
        Graph g = smallConnected();
        assertEquals(Prim.run(g).totalCost, Prim.run(g).totalCost);
        assertEquals(Kruskal.run(g).totalCost, Kruskal.run(g).totalCost);
    }

    private boolean isAcyclic(int n, List<Edge> edges) {
        UnionFind uf = new UnionFind(n);
        for (Edge e : edges) {
            if (!uf.union(e.u, e.v)) return false;
        }
        return true;
    }
}