package mst;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InputOutput {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<Graph> readGraphs(String path) throws IOException {
        String json = Files.readString(Paths.get(path));
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        List<Graph> graphs = new ArrayList<>();
        for (JsonElement el : root.getAsJsonArray("graphs")) {
            Map<String, Object> map = gson.fromJson(el, Map.class);
            graphs.add(Graph.fromJson(map));
        }
        return graphs;
    }

    public static void writeResults(String outJson, List<JsonObject> results) throws IOException {
        JsonObject root = new JsonObject();
        root.add("results", gson.toJsonTree(results));
        Files.writeString(Paths.get(outJson), gson.toJson(root));
    }
}