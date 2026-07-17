import java.util.*;

/**
 * ============================================================
 * ALGORITMO DFS (Depth-First Search / Búsqueda en Profundidad)
 * ============================================================
 * 
 * Descripción:
 *   Recorre el grafo explorando lo más profundo posible por cada
 *   rama antes de retroceder (backtracking). Utiliza una PILA
 *   (Stack) como estructura auxiliar, o de forma implícita mediante
 *   recursión (la pila de llamadas del sistema).
 * 
 * Complejidad:
 *   - Tiempo:  O(V + E) donde V = vértices, E = aristas
 *   - Espacio: O(V) para el array visited + O(V) para la pila de recursión
 *              En el peor caso (grafo lineal), la profundidad de recursión es V
 * 
 * Aplicaciones:
 *   - Detección de ciclos en grafos
 *   - Ordenamiento topológico (esencial en compiladores)
 *   - Encontrar componentes fuertemente conexas (Kosaraju, Tarjan)
 *   - Resolver laberintos y puzzles
 *   - Análisis de flujo en compiladores
 * 
 * Diferencia clave con BFS:
 *   - BFS usa COLA (FIFO) → explora por niveles (anchura)
 *   - DFS usa PILA (LIFO) → explora por ramas (profundidad)
 */
public class DFS {

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia: adj[i] contiene los vecinos del nodo i */
    private List<List<Integer>> adj;

    /**
     * Constructor: crea un grafo con V vértices y sin aristas.
     * 
     * @param V número de vértices del grafo
     */
    public DFS(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    /**
     * Agrega una arista dirigida de u hacia v.
     * 
     * @param u nodo origen
     * @param v nodo destino
     */
    public void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    /**
     * Inicia el recorrido DFS desde un nodo dado.
     * Crea el array de visitados y delega al método recursivo.
     * 
     * @param start nodo desde donde inicia el recorrido
     */
    public void dfs(int start) {
        // Array de visitados: fundamental para evitar ciclos infinitos
        // en grafos con ciclos. Sin esto, el algoritmo nunca terminaría.
        boolean[] visited = new boolean[V];
        dfsUtil(start, visited);
    }

    /**
     * ============================================================
     * MÉTODO RECURSIVO DE DFS
     * ============================================================
     * 
     * Pasos del algoritmo:
     *   1. Marcar el nodo actual como visitado
     *   2. Procesar el nodo (imprimirlo)
     *   3. Para cada vecino no visitado:
     *      - Llamar recursivamente a dfsUtil (ir más profundo)
     *   4. Cuando no hay más vecinos sin visitar, retornar
     *      (backtracking automático por la recursión)
     * 
     * Visualización del backtracking:
     *   Imaginemos el grafo: 0→1→3, 0→2→4
     * 
     *   dfsUtil(0)              ← Visitar 0
     *     └─ dfsUtil(1)         ← Visitar 1 (vecino de 0)
     *          └─ dfsUtil(3)    ← Visitar 3 (vecino de 1)
     *               (sin vecinos no visitados → BACKTRACK a 1)
     *          (sin más vecinos → BACKTRACK a 0)
     *     └─ dfsUtil(2)         ← Visitar 2 (siguiente vecino de 0)
     *          └─ dfsUtil(4)    ← Visitar 4 (vecino de 2)
     *               (sin vecinos no visitados → BACKTRACK a 2)
     *          (sin más vecinos → BACKTRACK a 0)
     *   FIN
     * 
     * @param node    nodo actual que se está visitando
     * @param visited array que registra qué nodos ya fueron visitados
     */
    private void dfsUtil(int node, boolean[] visited) {
        // Paso 1: Marcar el nodo como visitado para no volver a él
        visited[node] = true;

        // Paso 2: Procesar el nodo (en este caso, imprimirlo)
        System.out.print(node + " ");

        // Paso 3: Explorar TODOS los vecinos del nodo actual
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                // Llamada recursiva: "zambullirse" más profundo en el grafo
                // La pila de llamadas del sistema actúa como la pila de DFS
                dfsUtil(neighbor, visited);
                // Cuando esta llamada retorna, estamos haciendo BACKTRACKING
                // Es decir, regresamos al nodo actual para explorar otros vecinos
            }
        }
        // Paso 4: Backtracking implícito - al terminar el for, retornamos
        // al nodo padre en el árbol DFS
    }

    /**
     * Método principal con un grafo de ejemplo.
     * 
     * Grafo dirigido de 5 nodos:
     *   0 → 1
     *   0 → 2
     *   1 → 3
     *   2 → 4
     * 
     * Resultado esperado DFS desde 0: 0 1 3 2 4
     * (primero baja por la rama 0→1→3, luego backtrack a 0→2→4)
     */
    public static void main(String[] args) {
        DFS g = new DFS(5);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        System.out.print("DFS: ");
        g.dfs(0);
    }
}