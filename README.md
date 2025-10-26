# Assignment 3: Optimization of a City Transportation Network (Minimum Spanning Tree)

##  Project Overview
This project implements **Prim’s** and **Kruskal’s** algorithms to construct a **Minimum Spanning Tree (MST)** from a weighted undirected graph.  
The scenario represents a **city transportation network**, where each vertex is a district and edges represent possible roads between them, weighted by construction cost.  
The goal is to connect all districts with **the lowest possible total cost** while ensuring the network remains **connected** and **cycle-free**.

Both algorithms were tested on **28 graphs** of increasing complexity (from 5 to 2000 vertices) to evaluate their **correctness, efficiency, and scalability**.

---

##  Problem Statement
Given a weighted undirected graph:

- **Vertices (V)** → City districts  
- **Edges (E)** → Possible roads  
- **Edge weights (W)** → Construction costs  

Find a subset of edges that connects all vertices with **minimum total cost**, ensuring **no cycles** and **full connectivity** — the **Minimum Spanning Tree (MST)**.

---
##  Algorithm Implementation

### Prim’s Algorithm
- Uses a **priority queue** (min-heap) to grow the MST.
- Starts from an arbitrary vertex and repeatedly adds the **smallest edge** that connects a new vertex to the tree.
- Complexity: `O((V + E) log V)`

###  Kruskal’s Algorithm
- Sorts all edges by weight in ascending order.
- Uses a **Union-Find (Disjoint Set)** data structure to prevent cycles.
- Complexity: `O(E log E) + O(E α(V))`  
  (α(V) is the inverse Ackermann function — almost constant.)

Both algorithms recorded:
- MST **total cost**  
- **Operation count** (comparisons, unions, updates)  
- **Execution time (ms)**  

---

##  Input Data
A total of **28 graphs** were generated and divided into four categories:

| Category | Graph IDs | Vertices Range | Edges Range | Description |
|-----------|------------|----------------|--------------|--------------|
|  Small | 1–5 | 5–25 | 10–62 | For correctness & debugging |
|  Medium | 6–15 | 30–120 | 90–360 | Moderate-sized test cases |
|  Large | 16–25 | 150–600 | 480–1920 | Performance scaling |
|  Extra-Large | 26–28 | 1300–2000 | 5200–8000 | Stress test datasets |

All graphs were stored in `data/assign_3_input.json`.  
The program automatically produced:
- `data/assign_3_output.json` – detailed MST results  
- `results/summary.csv` – summarized performance  
- `figures/` – graph visualizations (bonus)

---

## 📈 Experimental Results

| Category | Avg Vertices | Avg Edges | Avg Prim Time (ms) | Avg Kruskal Time (ms) | Faster |
|-----------|---------------|------------|----------------------|-------------------------|---------|
| Small (1–5) | ~15 | ~37 | 0.38 | 0.28 | Kruskal |
| Medium (6–15) | ~70 | ~210 | 0.09 | 0.08 | Similar |
| Large (16–25) | ~400 | ~1280 | 0.34 | 0.25 | Similar |
| Extra-Large (26–28) | ~1633 | ~6533 | 1.40 | 1.10 | Prim |

Both algorithms produced **identical MST total costs** across all 28 test cases.  
Execution time remained below **2 milliseconds** even for the largest graphs.

---

##  Detailed Observations

| Graph | Vertices | Edges | Prim (ms) | Kruskal (ms) | MST Cost |
|--------|-----------|--------|-------------|----------------|------------|
| 1 | 5 | 10 | 1.75 | 0.23 | 49 |
| 10 | 70 | 210 | 0.08 | 0.08 | 769 |
| 17 | 200 | 640 | 0.21 | 0.21 | 1908 |
| 23 | 500 | 1600 | 0.44 | 0.26 | 4309 |
| 26 | 1300 | 5200 | 1.35 | 0.81 | 9959 |
| 27 | 1600 | 6400 | 1.77 | 1.21 | 12286 |
| 28 | 2000 | 8000 | **1.09** | **1.27** | **15775** |

**Correctness:**  
- Both algorithms always yielded the same MST cost and `V-1` edges.  
- No cycles or disconnections were detected.  

**Performance:**  
- Kruskal was slightly faster on the smallest graphs.  
- Prim caught up quickly and scaled better on medium-to-large graphs.  
- On extra-large graphs, Prim was consistently ~20–25% faster due to fewer global operations.

---

##  Analysis

### What Theory Predicts
| Algorithm | Time Complexity | Expected Advantage |
|------------|-----------------|--------------------|
| **Prim** | O((V + E) log V) | Works better on dense & large graphs |
| **Kruskal** | O(E log E) | Simple & efficient on small sparse graphs |

### What Happened in Practice
- **Small graphs (< 25 vertices):** Kruskal was faster.  
- **Medium (30–120 vertices):** Nearly identical results.  
- **Large (150–600 vertices):** Prim maintained consistent runtime.  
- **Extra-large (1300–2000 vertices):** Prim dominated with lower total execution time.

**Observation:**  
Kruskal’s sorting overhead grows with `E log E`, while Prim’s priority queue keeps operations localized and efficient.

---

##  Key Findings

| Criterion | Better Algorithm |
|------------|------------------|
|  Simplicity | Kruskal |
|  Scalability | **Prim** |
|  Dense Graphs | **Prim** |
|  Small Sparse Graphs | Kruskal |
|  Consistency | **Prim** |

 **Conclusion:**  
For real-world city-scale transportation networks, **Prim’s algorithm** is the preferred choice — it scales better, remains stable under load, and avoids sorting overhead on large datasets.

---

##  Output Files
| File | Description |
|------|--------------|
| `data/assign_3_input.json` | Input graph datasets |
| `data/assign_3_output.json` | MST results (Prim vs Kruskal) |
| `results/summary.csv` | Performance metrics |
| `figures/` | Visual graph representations (bonus section) |

---

##  References
1. R. Sedgewick, K. Wayne — *Algorithms, 4th Edition*, Addison-Wesley, 2011.  
   🔗 [https://algs4.cs.princeton.edu/home/](https://algs4.cs.princeton.edu/home/)  
2. T. H. Cormen, C. Leiserson, R. Rivest, C. Stein — *Introduction to Algorithms (3rd Edition)*, MIT Press, 2009.  
3. MIT OpenCourseWare — *6.046J Design and Analysis of Algorithms*  
   🔗 [https://ocw.mit.edu/courses/6-046j-design-and-analysis-of-algorithms-spring-2015/](https://ocw.mit.edu/courses/6-046j-design-and-analysis-of-algorithms-spring-2015/)  
4. GeeksForGeeks — *Difference Between Prim’s and Kruskal’s Algorithms*  
   🔗 [https://www.geeksforgeeks.org/difference-between-prims-and-kruskals-algorithm/](https://www.geeksforgeeks.org/difference-between-prims-and-kruskals-algorithm/)  
5. Visualgo — *MST Visualization*  
   🔗 [https://visualgo.net/en/mst](https://visualgo.net/en/mst)  
6. CP-Algorithms — *Prim’s and Kruskal’s Implementations*  
   🔗 [https://cp-algorithms.com/graph/mst_prim.html](https://cp-algorithms.com/graph/mst_prim.html)

---

## Author
**Zhanel A.**  
*Software Engineering — Astana IT University*  
 *Fall 2025*  
 *Assignment 3 — Optimization of a City Transportation Network (MST)*
