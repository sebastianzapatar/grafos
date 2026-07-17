import java.util.*;

/**
 * ============================================================
 * ALGORITMO DE PRIM (Árbol de Expansión Mínima / MST)
 * ============================================================
 *
 * Descripción:
 *   Igual que Kruskal, encuentra el Árbol de Expansión Mínima de
 *   un grafo NO dirigido, conexo y ponderado, pero con una
 *   estrategia diferente:
 *
 *   - KRUSKAL trabaja por ARISTAS: las ordena y va tomando las más
 *     baratas que no formen ciclo (el bosque se va uniendo).
 *   - PRIM trabaja por NODOS: hace CRECER un solo árbol desde un
 *     nodo inicial, agregando siempre la arista más barata que
 *     conecte el árbol con un nodo de afuera.
 *
 * Idea del algoritmo (greedy, muy similar a Dijkstra):
 *   1. Empezar con un nodo cualquiera dentro del árbol
 *   2. De todas las aristas que cruzan del árbol hacia afuera,
 *      elegir la de MENOR peso (usando una PriorityQueue)
 *   3. Agregar ese nodo y esa arista al árbol
 *   4. Repetir hasta incluir los V nodos
 *
 * Diferencia clave con Dijkstra:
 *   - Dijkstra minimiza la DISTANCIA ACUMULADA desde el origen
 *   - Prim minimiza solo el PESO DE LA ARISTA que conecta el
 *     nuevo nodo al árbol (no importa la distancia acumulada)
 *
 * Complejidad (con PriorityQueue):
 *   - Tiempo:  O(E log V)
 *   - Espacio: O(V + E)
 *
 * ¿Cuándo usar Prim vs Kruskal?
 *   - Prim: mejor en grafos DENSOS (muchas aristas)
 *   - Kruskal: mejor en grafos DISPERSOS y más simple con Union-Find
 */
public class Prim {

    /** Representa una arista ponderada hacia el nodo target */
    static class Edge {
        int target, weight;

        Edge(int t, int w) {
            target = t;
            weight = w;
        }
    }

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia ponderada (no dirigida: aristas en ambos sentidos) */
    private List<List<Edge>> adj;

    /**
     * Constructor: crea un grafo no dirigido con V vértices.
     *
     * @param V número de vértices del grafo
     */
    public Prim(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    /**
     * Agrega una arista NO dirigida ponderada (en ambos sentidos).
     *
     * @param u un extremo de la arista
     * @param v el otro extremo
     * @param w peso de la arista
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }

    /**
     * ============================================================
     * ALGORITMO PRINCIPAL DE PRIM
     * ============================================================
     *
     * Pasos:
     *   1. minCost[i] = costo mínimo conocido para conectar i al árbol
     *      (inicia en ∞, excepto el nodo inicial en 0)
     *   2. Extraer de la PriorityQueue el nodo más barato de conectar
     *   3. Marcarlo como dentro del árbol y sumar su costo
     *   4. Relajar: para cada vecino fuera del árbol, si esta arista
     *      es más barata que su minCost actual, actualizarla
     *   5. Repetir hasta procesar todos los nodos
     *
     * @param start nodo desde donde empieza a crecer el árbol
     */
    public void prim(int start) {
        boolean[] enArbol = new boolean[V];   // ¿El nodo ya está en el MST?
        int[] minCost = new int[V];           // Costo mínimo de conexión al árbol
        int[] parent = new int[V];            // Con qué nodo se conecta al árbol
        Arrays.fill(minCost, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        // Paso 1: el nodo inicial se conecta "gratis"
        minCost[start] = 0;

        // PriorityQueue de pares [nodo, costo], ordenada por costo
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        int costoTotal = 0;

        while (!pq.isEmpty()) {
            // Paso 2: nodo más barato de conectar al árbol
            int[] current = pq.poll();
            int u = current[0];

            // Ignorar entradas obsoletas (el nodo ya entró al árbol antes)
            if (enArbol[u]) continue;

            // Paso 3: u entra al árbol
            enArbol[u] = true;
            costoTotal += minCost[u];

            // Paso 4: actualizar los costos de conexión de los vecinos
            for (Edge e : adj.get(u)) {
                if (!enArbol[e.target] && e.weight < minCost[e.target]) {
                    minCost[e.target] = e.weight;
                    parent[e.target] = u;   // e.target se conectaría vía u
                    pq.add(new int[]{e.target, e.weight});
                }
            }
        }

        // Mostrar resultado
        System.out.println("Aristas del Árbol de Expansión Mínima (Prim desde " + start + "):");
        for (int i = 0; i < V; i++) {
            if (parent[i] != -1) {
                System.out.println("   " + parent[i] + " — " + i + "  (peso " + minCost[i] + ")");
            }
        }
        System.out.println("Costo total del MST: " + costoTotal);
    }

    /**
     * Método principal con el mismo grafo del ejemplo de Kruskal,
     * para comprobar que ambos llegan al mismo costo total.
     *
     * Grafo no dirigido de 4 nodos:
     *   0 —10— 1
     *   0 — 6— 2
     *   0 — 5— 3
     *   1 —15— 3
     *   2 — 4— 3
     *
     * MST esperado: costo total 19 (igual que con Kruskal)
     */
    public static void main(String[] args) {
        Prim g = new Prim(4);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 6);
        g.addEdge(0, 3, 5);
        g.addEdge(1, 3, 15);
        g.addEdge(2, 3, 4);

        System.out.println("=== Ejecutando Prim ===");
        g.prim(0);
    }
}
