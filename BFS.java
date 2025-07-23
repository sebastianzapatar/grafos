import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {
    private int V;
    private List<List<Integer>> adj;

    public BFS(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int to) {
        adj.get(u).add(to);
    }

    public void bfs(int start) {
        long startTime = System.nanoTime();
        boolean[] visited = new boolean[V];
        int[] distance = new int[V];
        Queue<Integer> queue = new LinkedList<>();

        visited[start] = true;
        distance[start] = 0;
        queue.add(start);

        System.out.println("Recorrido BFS:");
        while (!queue.isEmpty()) {
            int node = queue.poll();
            System.out.print(node + " ");
            for (int neighbor : adj.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[node] + 1;
                    queue.add(neighbor);
                }
            }
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución BFS: %.3f ms\n", elapsedMs);
        System.out.println("\n\nDistancia desde el nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("Nodo " + i + " -> " + distance[i]);
        }
    }

    public void dfs(int start) {
        long startTime = System.nanoTime();
        boolean[] visited = new boolean[V];
        System.out.println("\nRecorrido DFS (con profundidad):");
        dfsRec(start, visited, 0);
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución DFS: %.3f ms\n", elapsedMs);
    }

    public void dfsRec(int node, boolean[] visited, int depth) {
        visited[node] = true;
        System.out.println("Nodo " + node + " -> " + depth);
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                dfsRec(neighbor, visited, depth + 1);
            }
        }
    }

    public static void main(String[] args) {
        BFS g = new BFS(5);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        g.addEdge(4, 1);
        g.addEdge(3, 2);

        g.bfs(0);
        g.dfs(0);
    }
}
