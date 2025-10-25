package mst;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.Random;

public class GraphDrawer {
    public static void draw(Graph g, int id, String folder) throws Exception {
        int size = 800;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, size, size);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        int n = g.vertices();
        double angleStep = 2 * Math.PI / n;
        int radius = 300;
        Point[] points = new Point[n];
        int cx = size/2, cy = size/2;

        for (int i=0;i<n;i++){
            int x = (int)(cx + radius * Math.cos(i * angleStep));
            int y = (int)(cy + radius * Math.sin(i * angleStep));
            points[i] = new Point(x,y);
        }

        // Draw edges
        g2.setColor(new Color(120,120,120));
        for (Edge e : g.edges()){
            Point p1 = points[e.u];
            Point p2 = points[e.v];
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            int tx = (p1.x + p2.x)/2;
            int ty = (p1.y + p2.y)/2;
            g2.drawString(String.valueOf(e.w), tx, ty);
        }

        // Draw nodes
        g2.setColor(new Color(0,100,200));
        for (int i=0;i<n;i++){
            Point p = points[i];
            g2.fillOval(p.x-8, p.y-8, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(i), p.x-4, p.y-10);
            g2.setColor(new Color(0,100,200));
        }

        g2.dispose();
        File out = new File(folder + "/graph_" + String.format("%02d", id) + ".png");
        ImageIO.write(img, "png", out);
    }
}