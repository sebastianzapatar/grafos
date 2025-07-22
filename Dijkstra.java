import java.util.*;

public class Dijkstra {
    static class Edge {
        int target, weight;
        Edge(int t, int w) {
            target = t; weight = w;
        }
    }

    private int V;
    private List<List<Edge>> adj;

    public Dijkstra(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }

    public void dijkstra(int start) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];

            if (d > dist[u]) continue;

            for (Edge e : adj.get(u)) {
                if (dist[u] + e.weight < dist[e.target]) {
                    dist[e.target] = dist[u] + e.weight;
                    pq.add(new int[]{e.target, dist[e.target]});
                }
            }
        }

        System.out.println("Dijkstra desde nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("A nodo " + i + ": " + dist[i]);
        }
    }

    public static void main(String[] args) {
        Dijkstra g = new Dijkstra(5);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 4);
        g.addEdge(2, 3, 2);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 3, 1);
        g.dijkstra(0);
    }
}