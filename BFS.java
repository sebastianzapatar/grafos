import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * ============================================================
 * ALGORITMO BFS (Breadth-First Search / Búsqueda en Anchura)
 * ============================================================
 * 
 * Descripción:
 *   Recorre el grafo visitando primero todos los vecinos del nodo
 *   actual antes de avanzar a los vecinos de los vecinos.
 *   Utiliza una COLA (Queue) como estructura auxiliar (FIFO).
 * 
 * Complejidad:
 *   - Tiempo:  O(V + E) donde V = vértices, E = aristas
 *   - Espacio: O(V) para el array visited y la cola
 * 
 * Aplicaciones:
 *   - Encontrar el camino más corto en grafos NO ponderados
 *   - Detección de componentes conexas
 *   - Verificar si un grafo es bipartito
 *   - Análisis de alcanzabilidad en autómatas (compiladores)
 * 
 * Esta clase también incluye un método DFS para comparar
 * ambos recorridos sobre el mismo grafo.
 */
public class BFS {

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia: adj[i] contiene los vecinos del nodo i */
    private List<List<Integer>> adj;

    /**
     * Constructor: crea un grafo con V vértices y sin aristas.
     * Inicializa la lista de adyacencia con V listas vacías.
     * 
     * @param V número de vértices del grafo
     */
    public BFS(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    /**
     * Agrega una arista dirigida de u hacia 'to'.
     * En un grafo no dirigido, se llamaría también addEdge(to, u).
     * 
     * @param u  nodo origen
     * @param to nodo destino
     */
    public void addEdge(int u, int to) {
        adj.get(u).add(to);
    }

    /**
     * ============================================================
     * RECORRIDO BFS (Búsqueda en Anchura)
     * ============================================================
     * 
     * Pasos del algoritmo:
     *   1. Marcar el nodo inicial como visitado y encolar
     *   2. Mientras la cola no esté vacía:
     *      a. Desencolar un nodo
     *      b. Procesar el nodo (imprimirlo)
     *      c. Para cada vecino no visitado:
     *         - Marcarlo como visitado
     *         - Actualizar su distancia
     *         - Encolarlo
     * 
     * @param start nodo desde donde inicia el recorrido
     */
    public void bfs(int start) {
        long startTime = System.nanoTime();

        // Array de visitados: evita ciclos infinitos y repeticiones
        boolean[] visited = new boolean[V];

        // Array de distancias: distance[i] = distancia mínima desde 'start' hasta i
        // (medida en número de aristas, no en peso)
        int[] distance = new int[V];

        // Cola FIFO: garantiza que los nodos se procesen nivel por nivel
        // Nivel 0: nodo start
        // Nivel 1: vecinos de start
        // Nivel 2: vecinos de los vecinos de start, etc.
        Queue<Integer> queue = new LinkedList<>();

        // Paso 1: Inicializar con el nodo de origen
        visited[start] = true;
        distance[start] = 0;
        queue.add(start);

        System.out.println("Recorrido BFS:");

        // Paso 2: Procesar la cola hasta que se vacíe
        while (!queue.isEmpty()) {
            // 2a. Desencolar el nodo al frente de la cola
            int node = queue.poll();

            // 2b. Procesar (imprimir) el nodo actual
            System.out.print(node + " ");

            // 2c. Explorar todos los vecinos del nodo actual
            for (int neighbor : adj.get(node)) {
                // Solo procesar vecinos NO visitados
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    // La distancia del vecino es la del nodo actual + 1
                    distance[neighbor] = distance[node] + 1;
                    // Agregar el vecino a la cola para procesarlo después
                    queue.add(neighbor);
                }
            }
        }

        // Medir tiempo de ejecución
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución BFS: %.3f ms\n", elapsedMs);

        // Mostrar las distancias calculadas
        // (si un nodo no fue visitado, es inalcanzable desde 'start')
        System.out.println("\n\nDistancia desde el nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("Nodo " + i + " -> " + (visited[i] ? distance[i] : "inalcanzable"));
        }
    }

    /**
     * ============================================================
     * RECORRIDO DFS (Búsqueda en Profundidad) - versión recursiva
     * ============================================================
     * 
     * Incluido aquí para comparar con BFS sobre el mismo grafo.
     * Utiliza recursión (pila implícita del sistema).
     * 
     * @param start nodo desde donde inicia el recorrido
     */
    public void dfs(int start) {
        long startTime = System.nanoTime();
        boolean[] visited = new boolean[V];
        System.out.println("\nRecorrido DFS (con profundidad):");
        // Llamada recursiva inicial con profundidad 0
        dfsRec(start, visited, 0);
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución DFS: %.3f ms\n", elapsedMs);
    }

    /**
     * Función auxiliar recursiva para DFS.
     * Cada llamada recursiva aumenta la profundidad en 1.
     * 
     * @param node    nodo actual a visitar
     * @param visited array de nodos visitados
     * @param depth   profundidad actual en el árbol DFS
     */
    public void dfsRec(int node, boolean[] visited, int depth) {
        // Marcar nodo como visitado
        visited[node] = true;
        System.out.println("Nodo " + node + " -> " + depth);

        // Explorar cada vecino no visitado (ir más profundo)
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                // Llamada recursiva: profundidad + 1 (backtracking implícito)
                dfsRec(neighbor, visited, depth + 1);
            }
        }
    }

    /**
     * Método principal con un grafo de ejemplo de 5 nodos.
     *
     * Aristas del grafo dirigido:
     *   0 → 1,  0 → 2
     *   1 → 3
     *   2 → 4
     *   3 → 4,  3 → 2
     *   4 → 1   (nótese que 4→1→3→4 forma un ciclo)
     */
    public static void main(String[] args) {
        BFS g = new BFS(5);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        g.addEdge(4, 1);
        g.addEdge(3, 2);

        g.bfs(0);   // Recorrido BFS desde el nodo 0
        g.dfs(0);   // Recorrido DFS desde el nodo 0 (comparación)
    }
}
