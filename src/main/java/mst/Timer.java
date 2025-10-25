package mst;

public class Timer {
    private long start;
    public void start() { start = System.nanoTime(); }
    public double stop() { return (System.nanoTime() - start) / 1_000_000.0; }
}