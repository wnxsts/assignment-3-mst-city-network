package mst;

import java.util.*;

public class Prim {
    public static MSTResult run(Graph g) {
        MSTResult res = new MSTResult();
        Timer t = new Timer();
        t.start();

        int n = g.vertices();
        boolean[] visited = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        int ops = 0, cost = 0;

        visited[0] = true;
        for (int[] e : g.adjacency().get(0)) pq.offer(new int[]{e[0], e[1], 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int v = cur[0], w = cur[1], u = cur[2];
            ops++;
            if (visited[v]) continue;
            visited[v] = true;
            res.mstEdges.add(new Edge(u, v, w));
            cost += w;
            for (int[] nxt : g.adjacency().get(v))
                if (!visited[nxt[0]]) pq.offer(new int[]{nxt[0], nxt[1], v});
        }

        res.totalCost = cost;
        res.operations = ops;
        res.timeMs = t.stop();
        return res;
    }
}