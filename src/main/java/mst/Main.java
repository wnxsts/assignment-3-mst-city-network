package mst;

import com.google.gson.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        String inputPath = "data/assign_3_input.json";
        String outputPath = "data/assign_3_output.json";
        String csvPath = "results/summary.csv";
        String figuresDir = "figures";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<JsonObject> allResults = new ArrayList<>();
        StringBuilder csv = new StringBuilder(
                "graph_id,vertices,edges,prim_cost,kruskal_cost,prim_time_ms,kruskal_time_ms,prim_ops,kruskal_ops\n"
        );

        try {
            // читаем вход
            String jsonText = Files.readString(Paths.get(inputPath));
            JsonObject root = JsonParser.parseString(jsonText).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("graphs");

            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();

                int id = getIntSafe(obj, "id", -1);
                // Парсим граф в нашем внутреннем формате Graph
                Graph g = parseGraphUniversal(obj);

                if (id < 0) {
                    // если id нет, попробуем пронумеровать автоматически
                    id = allResults.size() + 1;
                }

                System.out.println("▶ Processing Graph ID " + id +
                        " (V=" + g.vertices() + ", E=" + g.numEdges() + ")");

                // Запуск алгоритмов
                MSTResult prim = Prim.run(g);
                MSTResult kruskal = Kruskal.run(g);

                // JSON-запись результата по этому графу
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

                // CSV
                csv.append(id).append(",")
                        .append(g.vertices()).append(",")
                        .append(g.numEdges()).append(",")
                        .append(prim.totalCost).append(",")
                        .append(kruskal.totalCost).append(",")
                        .append(String.format(Locale.US, "%.3f", prim.timeMs)).append(",")
                        .append(String.format(Locale.US, "%.3f", kruskal.timeMs)).append(",")
                        .append(prim.operations).append(",")
                        .append(kruskal.operations).append("\n");

                // Рисуем граф (бонус)
                try {
                    Files.createDirectories(Paths.get(figuresDir));
                    GraphDrawer.draw(g, id, figuresDir);
                } catch (Exception ex) {
                    System.err.println(" Could not draw graph " + id + ": " + ex.getMessage());
                }
            }

            // Пишем output.json
            JsonObject out = new JsonObject();
            out.add("results", gson.toJsonTree(allResults));
            Files.createDirectories(Paths.get("data"));
            Files.writeString(Paths.get(outputPath), gson.toJson(out));

            // Пишем summary.csv
            Files.createDirectories(Paths.get("results"));
            Files.writeString(Paths.get(csvPath), csv.toString());

            System.out.println("\n Completed successfully!");
            System.out.println("→ JSON: " + outputPath);
            System.out.println("→ CSV:  " + csvPath);
            System.out.println("→ Figures: " + figuresDir);

        } catch (IOException e) {
            System.err.println(" Error reading input file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Универсальный парсер графа: понимает оба формата входа. */
    private static Graph parseGraphUniversal(JsonObject obj) {
        // Формат A: num_vertices + edges[{u,v,w}]
        if (obj.has("num_vertices")) {
            int n = obj.get("num_vertices").getAsInt();
            Graph g = new Graph(n);
            JsonArray es = obj.getAsJsonArray("edges");
            if (es != null) {
                for (JsonElement eEl : es) {
                    JsonObject e = eEl.getAsJsonObject();
                    int u = getIntSafe(e, "u", -1);
                    int v = getIntSafe(e, "v", -1);
                    int w = getIntSafe(e, "w", getIntSafe(e, "weight", 1)); // подстрахуемся
                    if (u >= 0 && v >= 0) g.addEdge(u, v, w);
                }
            }
            return g;
        }

        // Формат B: nodes + edges[{from,to,weight}]
        if (obj.has("nodes")) {
            JsonArray nodes = obj.getAsJsonArray("nodes");
            int n = nodes.size();
            Graph g = new Graph(n);

            // Карту имён -> индексов
            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String name = nodes.get(i).getAsString();
                idx.put(name, i);
            }
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

        // Если формат неизвестен — кинем понятную ошибку
        throw new IllegalArgumentException("Unknown graph format: expected {num_vertices,edges} or {nodes,edges}");
    }

    private static int getIntSafe(JsonObject o, String key, int def) {
        JsonElement x = o.get(key);
        return x == null || x.isJsonNull() ? def : x.getAsInt();
    }
    private static String getStringSafe(JsonObject o, String key, String def) {
        JsonElement x = o.get(key);
        return x == null || x.isJsonNull() ? def : x.getAsString();
    }
}