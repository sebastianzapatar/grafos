import java.util.*;

public class BellmanFord {
    static class Edge {
        int source, target, weight;
        Edge(int s, int t, int w) {
            source = s; target = t; weight = w;
        }
    }

    private int V;
    private List<Edge> edges;

    public BellmanFord(int V) {
        this.V = V;
        edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
    }

    public void bellmanFord(int start) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        for (int i = 1; i < V; i++) {
            for (Edge e : edges) {
                if (dist[e.source] != Integer.MAX_VALUE && dist[e.source] + e.weight < dist[e.target]) {
                    dist[e.target] = dist[e.source] + e.weight;
                }
            }
        }

        System.out.println("Bellman-Ford desde nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("A nodo " + i + ": " + dist[i]);
        }
    }

    public static void main(String[] args) {
        BellmanFord g = new BellmanFord(5);
        g.addEdge(0, 1, -1);
        g.addEdge(0, 2, 4);
        g.addEdge(1, 2, 3);
        g.addEdge(1, 3, 2);
        g.addEdge(1, 4, 2);
        g.addEdge(3, 2, 5);
        g.addEdge(3, 1, 1);
        g.addEdge(4, 3, -3);
        g.bellmanFord(0);
    }
}