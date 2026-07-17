import java.util.*;

/**
 * ============================================================
 * ALGORITMO DE BELLMAN-FORD
 * ============================================================
 * 
 * Descripción:
 *   Algoritmo de programación dinámica que encuentra el camino
 *   más corto desde un nodo origen a todos los demás nodos.
 *   A diferencia de Dijkstra, SÍ funciona con aristas de peso negativo
 *   y además puede DETECTAR ciclos de peso negativo.
 * 
 * Idea central:
 *   Relajar TODAS las aristas V-1 veces. Después de i iteraciones,
 *   tenemos las distancias mínimas usando como máximo i aristas.
 *   Como el camino más corto tiene a lo sumo V-1 aristas (sin ciclos),
 *   después de V-1 iteraciones tenemos las distancias óptimas.
 * 
 * ¿Por qué V-1 iteraciones?
 *   En un grafo con V vértices, el camino más largo sin repetir
 *   vértices tiene exactamente V-1 aristas. Cada iteración "propaga"
 *   la información de distancia una arista más lejos.
 * 
 * Complejidad:
 *   - Tiempo:  O(V × E) donde V = vértices, E = aristas
 *   - Espacio: O(V) para el array de distancias
 * 
 * Ventajas sobre Dijkstra:
 *   - Funciona con aristas de peso negativo
 *   - Detecta ciclos de peso negativo
 *   - Más simple de implementar (no necesita PriorityQueue)
 * 
 * Desventajas:
 *   - Más lento que Dijkstra: O(VE) vs O((V+E)logV)
 *   - No funciona si hay ciclos de peso negativo accesibles desde el origen
 */
public class BellmanFord {

    /**
     * Clase interna para representar una arista dirigida ponderada.
     * A diferencia de Dijkstra, aquí almacenamos explícitamente
     * el nodo origen, destino y peso de cada arista.
     */
    static class Edge {
        int source;  // Nodo origen de la arista
        int target;  // Nodo destino de la arista
        int weight;  // Peso/costo de la arista (puede ser negativo)

        Edge(int s, int t, int w) {
            source = s;
            target = t;
            weight = w;
        }
    }

    /** Número de vértices del grafo */
    private int V;

    /** Lista de TODAS las aristas del grafo (no usamos lista de adyacencia) */
    private List<Edge> edges;

    /**
     * Constructor: crea un grafo con V vértices y sin aristas.
     * 
     * @param V número de vértices del grafo
     */
    public BellmanFord(int V) {
        this.V = V;
        edges = new ArrayList<>();
    }

    /**
     * Agrega una arista dirigida ponderada al grafo.
     * 
     * @param u nodo origen
     * @param v nodo destino
     * @param w peso de la arista (puede ser negativo)
     */
    public void addEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
    }

    /**
     * ============================================================
     * ALGORITMO PRINCIPAL DE BELLMAN-FORD
     * ============================================================
     * 
     * Pasos del algoritmo:
     *   1. Inicializar dist[start] = 0, todo lo demás en infinito
     *   2. Repetir V-1 veces:
     *      - Para CADA arista (u, v, w):
     *        - Si dist[u] + w < dist[v], actualizar dist[v] (RELAJAR)
     *   3. (Opcional) Iteración extra para detectar ciclos negativos:
     *      - Si alguna arista aún puede relajarse, hay un ciclo negativo
     * 
     * Visualización de la propagación:
     *   Iteración 1: Conocemos distancias usando 1 arista
     *   Iteración 2: Conocemos distancias usando hasta 2 aristas
     *   ...
     *   Iteración V-1: Conocemos distancias usando hasta V-1 aristas
     *   (Este es el máximo posible sin ciclos)
     * 
     * @param start nodo origen desde donde calcular distancias
     */
    public void bellmanFord(int start) {
        // Paso 1: Inicializar distancias
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);  // Todo en infinito
        dist[start] = 0;                       // Origen en 0

        // Paso 2: Relajar TODAS las aristas V-1 veces
        // ¿Por qué V-1? Porque el camino más largo sin ciclos
        // tiene exactamente V-1 aristas en un grafo de V nodos
        for (int i = 1; i < V; i++) {
            // En cada iteración, intentar relajar CADA arista
            for (Edge e : edges) {
                // Verificar que el nodo origen sea alcanzable (no infinito)
                // para evitar overflow al sumar
                if (dist[e.source] != Integer.MAX_VALUE
                        && dist[e.source] + e.weight < dist[e.target]) {
                    // RELAJACIÓN: encontramos un camino más corto
                    // Actualizar la distancia al nodo destino
                    dist[e.target] = dist[e.source] + e.weight;
                }
            }
        }

        // Mostrar resultados
        System.out.println("Bellman-Ford desde nodo " + start + ":");
        for (int i = 0; i < V; i++) {
            System.out.println("A nodo " + i + ": " + dist[i]);
        }
    }

    /**
     * Método principal con un grafo de ejemplo con aristas negativas.
     * 
     * Grafo de 5 nodos (incluye peso negativo 0→1 = -1):
     *   0 → 1 (peso -1)
     *   0 → 2 (peso  4)
     *   1 → 2 (peso  3)
     *   1 → 3 (peso  2)
     *   1 → 4 (peso  2)
     *   3 → 2 (peso  5)
     *   3 → 1 (peso  1)
     *   4 → 3 (peso -3)
     * 
     * Resultado esperado desde nodo 0:
     *   A nodo 0: 0
     *   A nodo 1: -1  (0→1)
     *   A nodo 2: 2   (0→1→2, costo -1+3=2)
     *   A nodo 3: -2  (0→1→4→3, costo -1+2-3=-2)
     *   A nodo 4: 1   (0→1→4, costo -1+2=1)
     */
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