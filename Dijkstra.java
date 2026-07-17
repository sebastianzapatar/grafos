import java.util.*;

/**
 * ============================================================
 * ALGORITMO DE DIJKSTRA
 * ============================================================
 * 
 * Descripción:
 *   Algoritmo greedy que encuentra el camino más corto desde
 *   un nodo origen a todos los demás nodos en un grafo ponderado
 *   con pesos NO negativos.
 * 
 * Idea central (Relajación de aristas):
 *   Si la distancia actual a un nodo v es mayor que la distancia
 *   a un nodo u más el peso de la arista u→v, entonces actualizamos:
 *   dist[v] = dist[u] + peso(u,v)
 * 
 * Complejidad (con PriorityQueue / Min-Heap):
 *   - Tiempo:  O((V + E) log V)
 *   - Espacio: O(V + E) para las listas de adyacencia y la cola
 * 
 * Limitación importante:
 *   NO funciona correctamente con aristas de peso negativo.
 *   Para esos casos, usar Bellman-Ford o Floyd-Warshall.
 * 
 * Aplicaciones:
 *   - GPS y navegación (ruta más corta)
 *   - Enrutamiento en redes (protocolo OSPF)
 *   - Optimización de flujo en compiladores
 */
public class Dijkstra {

    /**
     * Clase interna para representar una arista ponderada.
     * Almacena el nodo destino y el peso de la arista.
     */
    static class Edge {
        int target;  // Nodo destino de la arista
        int weight;  // Peso/costo de la arista

        Edge(int t, int w) {
            target = t;
            weight = w;
        }
    }

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia ponderada: adj[i] contiene las aristas que salen del nodo i */
    private List<List<Edge>> adj;

    /**
     * Constructor: crea un grafo ponderado con V vértices.
     * 
     * @param V número de vértices del grafo
     */
    public Dijkstra(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    /**
     * Agrega una arista dirigida ponderada de u hacia v con peso w.
     * 
     * @param u nodo origen
     * @param v nodo destino
     * @param w peso de la arista (debe ser ≥ 0)
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }

    /**
     * ============================================================
     * ALGORITMO PRINCIPAL DE DIJKSTRA
     * ============================================================
     * 
     * Pasos del algoritmo:
     *   1. Inicializar todas las distancias como infinito (∞)
     *   2. Poner la distancia del nodo origen en 0
     *   3. Insertar (origen, 0) en la cola de prioridad (min-heap)
     *   4. Mientras la cola no esté vacía:
     *      a. Extraer el nodo con menor distancia (greedy)
     *      b. Si la distancia extraída es mayor que la actual, ignorar
     *         (ya encontramos un camino mejor antes)
     *      c. Para cada arista del nodo extraído:
     *         - Calcular nueva distancia = dist[u] + peso(u,v)
     *         - Si nueva distancia < dist[v], actualizar (RELAJAR)
     *         - Insertar (v, nueva_distancia) en la cola
     * 
     * ¿Por qué funciona?
     *   La PriorityQueue siempre nos da el nodo con menor distancia
     *   acumulada. Como todos los pesos son ≥ 0, una vez que extraemos
     *   un nodo, su distancia es definitivamente la mínima.
     * 
     * @param start nodo origen desde donde calcular distancias
     */
    public void dijkstra(int start) {
        // Paso 1: Inicializar distancias en infinito
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);

        // Paso 2: Distancia del origen a sí mismo es 0
        dist[start] = 0;

        // Paso 3: Cola de prioridad (min-heap) ordenada por distancia
        // Cada elemento es un par [nodo, distancia]
        // Comparator: ordena por el segundo elemento (distancia) de menor a mayor
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        // Paso 4: Procesar nodos en orden de distancia creciente
        while (!pq.isEmpty()) {
            // 4a. Extraer el nodo con MENOR distancia acumulada
            int[] current = pq.poll();
            int u = current[0];  // Nodo actual
            int d = current[1];  // Distancia cuando se encoló

            // 4b. Optimización: si ya encontramos un camino más corto,
            // este elemento de la cola está obsoleto → ignorar
            if (d > dist[u]) continue;

            // 4c. RELAJACIÓN: explorar todas las aristas del nodo u
            for (Edge e : adj.get(u)) {
                // Calcular la distancia tentativa pasando por u
                int newDist = dist[u] + e.weight;

                // ¿Esta ruta es mejor que la conocida?
                if (newDist < dist[e.target]) {
                    // ¡Sí! Actualizar la distancia mínima (RELAJAR la arista)
                    dist[e.target] = newDist;
                    // Agregar a la cola para explorar desde este nodo
                    pq.add(new int[]{e.target, dist[e.target]});
                }
            }
        }

        // Mostrar resultados
        System.out.println("Dijkstra desde nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("A nodo " + i + ": " + dist[i]);
        }
    }

    /**
     * Método principal con un grafo de ejemplo de 5 nodos.
     * 
     * Grafo ponderado:
     *   0 --10--> 1
     *   0 ---3--> 2
     *   1 ---1--> 2
     *   2 ---4--> 1
     *   2 ---2--> 3
     *   3 ---2--> 4
     *   4 ---1--> 3
     * 
     * Resultado esperado desde nodo 0:
     *   A nodo 0: 0
     *   A nodo 1: 7  (0→2→1, costo 3+4=7, mejor que 0→1 directo con costo 10)
     *   A nodo 2: 3  (0→2)
     *   A nodo 3: 5  (0→2→3, costo 3+2=5)
     *   A nodo 4: 7  (0→2→3→4, costo 3+2+2=7)
     */
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