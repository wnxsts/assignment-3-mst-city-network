package mst;

import com.google.gson.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Main runner:
 * - Reads graphs from data/assign_3_input.json (format A or B)
 *   A) { id, num_vertices, edges:[{u,v,w}] }
 *   B) { id, nodes:[...], edges:[{from,to,weight}] }
 * - Runs Prim & Kruskal
 * - Writes data/assign_3_output.json and results/summary.csv
 * - Draws figures/graph_XX.png (if GraphDrawer exists)
 */
public class Main {

    public static void main(String[] args) {
        final String inputPath   = "data/assign_3_input.json";
        final String outputPath  = "data/assign_3_output.json";
        final String csvPath     = "results/summary.csv";
        final String figuresDir  = "figures";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<JsonObject> allResults = new ArrayList<>();

        StringBuilder csv = new StringBuilder(
                "graph_id,vertices,edges,algorithm,total_cost,operations_count,execution_time_ms\n"
        );

        try {
            String jsonText = Files.readString(Paths.get(inputPath));
            JsonObject root = JsonParser.parseString(jsonText).getAsJsonObject();
            JsonArray graphsJson = root.getAsJsonArray("graphs");

            for (JsonElement el : graphsJson) {
                JsonObject obj = el.getAsJsonObject();

                int id = getIntSafe(obj, "id", -1);
                Graph g = parseGraphUniversal(obj);
                if (id <= 0) id = allResults.size() + 1;

                System.out.println("▶ Processing Graph ID " + id +
                        " (V=" + g.vertices() + ", E=" + g.numEdges() + ")");

                MSTResult prim    = Prim.run(g);
                MSTResult kruskal = Kruskal.run(g);

                JsonObject record = new JsonObject();
                record.addProperty("graph_id", id);

                JsonObject stats = new JsonObject();
                stats.addProperty("vertices", g.vertices());
                stats.addProperty("edges", g.numEdges());
                record.add("input_stats", stats);

                JsonObject primJson = new JsonObject();
                primJson.add("mst_edges", gson.toJsonTree(prim.mstEdges));
                primJson.addProperty("total_cost", prim.totalCost);
                primJson.addProperty("operations_count", prim.operations);
                primJson.addProperty("execution_time_ms", prim.timeMs);

                JsonObject kruskalJson = new JsonObject();
                kruskalJson.add("mst_edges", gson.toJsonTree(kruskal.mstEdges));
                kruskalJson.addProperty("total_cost", kruskal.totalCost);
                kruskalJson.addProperty("operations_count", kruskal.operations);
                kruskalJson.addProperty("execution_time_ms", kruskal.timeMs);

                record.add("prim", primJson);
                record.add("kruskal", kruskalJson);
                allResults.add(record);

                csv.append(id).append(",")
                        .append(g.vertices()).append(",")
                        .append(g.numEdges()).append(",Prim,")
                        .append(String.format(Locale.US, "%d", prim.totalCost)).append(",")
                        .append(prim.operations).append(",")
                        .append(String.format(Locale.US, "%.2f", prim.timeMs)).append("\n");

                csv.append(id).append(",")
                        .append(g.vertices()).append(",")
                        .append(g.numEdges()).append(",Kruskal,")
                        .append(String.format(Locale.US, "%d", kruskal.totalCost)).append(",")
                        .append(kruskal.operations).append(",")
                        .append(String.format(Locale.US, "%.2f", kruskal.timeMs)).append("\n");

                try {
                    Files.createDirectories(Paths.get(figuresDir));
                    GraphDrawer.draw(g, id, figuresDir);
                } catch (Throwable t) {
                    System.err.println("Could not draw graph " + id + ": " + t.getMessage());
                }
            }

            JsonObject out = new JsonObject();
            out.add("results", gson.toJsonTree(allResults));
            Files.createDirectories(Paths.get("data"));
            Files.writeString(Paths.get(outputPath), gson.toJson(out));

            Files.createDirectories(Paths.get("results"));
            Files.writeString(Paths.get(csvPath), csv.toString());

            System.out.println("\nCompleted successfully!");
            System.out.println("→ JSON: " + outputPath);
            System.out.println("→ CSV:  " + csvPath);
            System.out.println("→ Figures: " + figuresDir);

        } catch (IOException io) {
            System.err.println("Error: " + io.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Graph parseGraphUniversal(JsonObject obj) {
        if (obj.has("num_vertices")) {
            int n = obj.get("num_vertices").getAsInt();
            Graph g = new Graph(n);
            JsonArray es = obj.getAsJsonArray("edges");
            if (es != null) {
                for (JsonElement eEl : es) {
                    JsonObject e = eEl.getAsJsonObject();
                    int u = getIntSafe(e, "u", -1);
                    int v = getIntSafe(e, "v", -1);
                    int w = getIntSafe(e, "w", getIntSafe(e, "weight", 1));
                    if (u >= 0 && v >= 0) g.addEdge(u, v, w);
                }
            }
            return g;
        }

        if (obj.has("nodes")) {
            JsonArray nodes = obj.getAsJsonArray("nodes");
            int n = nodes.size();
            Graph g = new Graph(n);

            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < n; i++) idx.put(nodes.get(i).getAsString(), i);

            JsonArray es = obj.getAsJsonArray("edges");
            if (es != null) {
                for (JsonElement eEl : es) {
                    JsonObject e = eEl.getAsJsonObject();
                    String sf = getStringSafe(e, "from", null);
                    String st = getStringSafe(e, "to", null);
                    int w = getIntSafe(e, "weight", getIntSafe(e, "w", 1));
                    if (sf != null && st != null && idx.containsKey(sf) && idx.containsKey(st)) {
                        g.addEdge(idx.get(sf), idx.get(st), w);
                    }
                }
            }
            return g;
        }

        throw new IllegalArgumentException("Unknown graph format: expected {num_vertices,edges} or {nodes,edges}");
    }

    private static int getIntSafe(JsonObject o, String key, int def) {
        JsonElement x = o.get(key);
        return (x == null || x.isJsonNull()) ? def : x.getAsInt();
    }

    private static String getStringSafe(JsonObject o, String key, String def) {
        JsonElement x = o.get(key);
        return (x == null || x.isJsonNull()) ? def : x.getAsString();
    }
}