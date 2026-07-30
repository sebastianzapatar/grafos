package algoritmos;

import java.util.*;

public class BFS {
    private int V;
    private List<List<Integer>> adj = new ArrayList<>();
    public BFS(int V) {
        this.V = V;
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }
    //u origen to destino
    public void addEdge(int u, int to) {
        adj.get(u).add(to);
    }
    //s es el nodo origen, puede empezar en cualquiera
    public int[] bfs(int s) {
        long startTime = System.nanoTime();
        int[] distances= new int[V];
        boolean[] visited = new boolean[V];
        Arrays.fill(distances, -1);
        Arrays.fill(visited, false);
        //Vamos a colocar los hijos
        //Expandimos el vecino,
        //Los hijos del vecino...
        Queue<Integer> q = new LinkedList<>();

        distances[s] = 0;
        visited[s] = true;
        q.offer(s);
        while (!q.isEmpty()) {
            int node = q.poll();
           //todo draw path to node s to anyone
            for(int i: adj.get(node)) {
                if(!visited[i]) {
                    visited[i] = true;
                    distances[i] = distances[node] + 1;
                    q.add(i);

                }
            }
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000.0;
        IO.println("Tiempo de ejecucion ms/n "+duration);
        return distances;
    }
    public int[] dfs(int s) {
        long startTime = System.nanoTime();
        int[] distances= new int[V];
        boolean[] visited = new boolean[V];
        Arrays.fill(distances, -1);
        Arrays.fill(visited, false);
        dfsRec(s,visited,distances,0);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000.0;
        IO.println("Tiempo de ejecucion ms/n "+duration);
        return distances;
    }

    public void dfsRec(int node, boolean[] visited
    ,int[] distances,int depht) {
        visited[node] = true;
        distances[node] = depht;
        for(int i: adj.get(node)) {
            if(!visited[i]) {
                dfsRec(i,visited,distances,depht+1);
            }
        }
    }

    //misma idea que dfs pero con pila explicita, sin recursion
    public int[] dfsPila(int s) {
        long startTime = System.nanoTime();
        int[] distances= new int[V];
        boolean[] visited = new boolean[V];
        Arrays.fill(distances, -1);
        Arrays.fill(visited, false);
        //cada entrada de la pila es {nodo, profundidad}
        Deque<int[]> stack = new ArrayDeque<>();

        stack.push(new int[]{s, 0});
        while (!stack.isEmpty()) {
            int[] top = stack.pop();
            int node = top[0];
            int depht = top[1];
            //un nodo puede entrar varias veces, solo cuenta la primera que sale
            if (visited[node]) {
                continue;
            }
            visited[node] = true;
            distances[node] = depht;
            List<Integer> vecinos = adj.get(node);
            //al reves para que salgan en el mismo orden que la version recursiva
            for (int i = vecinos.size() - 1; i >= 0; i--) {
                int vecino = vecinos.get(i);
                if (!visited[vecino]) {
                    stack.push(new int[]{vecino, depht + 1});
                }
            }
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000.0;
        IO.println("Tiempo de ejecucion ms/n "+duration);
        return distances;
    }

    static void main() {
        BFS g=new BFS(6);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        g.addEdge(4, 1);
        g.addEdge(3, 2);
        int[] distances= g.bfs(0);
        IO.println(Arrays.toString(distances));
        int[] distancesDfs= g.dfs(0);
        IO.println(Arrays.toString(distancesDfs));
        int[] distancesDfsPila= g.dfsPila(0);
        IO.println(Arrays.toString(distancesDfsPila));
    }
}
