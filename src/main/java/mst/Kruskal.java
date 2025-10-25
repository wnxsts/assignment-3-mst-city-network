package mst;

import java.util.*;

public class Kruskal {
    public static MSTResult run(Graph g) {
        MSTResult res = new MSTResult();
        Timer t = new Timer();
        t.start();

        List<Edge> edges = new ArrayList<>(g.edges());
        Collections.sort(edges);
        UnionFind uf = new UnionFind(g.vertices());
        int ops = 0, cost = 0;

        for (Edge e : edges) {
            ops++;
            if (uf.union(e.u, e.v)) {
                res.mstEdges.add(e);
                cost += e.w;
            }
        }

        res.totalCost = cost;
        res.operations = ops;
        res.timeMs = t.stop();
        return res;
    }
}