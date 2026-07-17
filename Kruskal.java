import java.util.*;

/**
 * ============================================================
 * ALGORITMO DE KRUSKAL (Árbol de Expansión Mínima / MST)
 * ============================================================
 *
 * Descripción:
 *   Dado un grafo NO dirigido, conexo y ponderado, el Árbol de
 *   Expansión Mínima (Minimum Spanning Tree) es el subconjunto de
 *   aristas que conecta TODOS los vértices con el MENOR costo total
 *   y SIN formar ciclos (un árbol con V-1 aristas).
 *
 * Idea del algoritmo (greedy):
 *   1. Ordenar todas las aristas por peso (menor a mayor)
 *   2. Recorrerlas en orden y agregar cada arista al MST
 *      SOLO si no forma un ciclo con las ya elegidas
 *   3. Terminar cuando tengamos V-1 aristas
 *
 * ¿Cómo saber si una arista forma un ciclo?
 *   Con la estructura UNION-FIND (Disjoint Set Union):
 *   - find(x): devuelve el "representante" del conjunto de x
 *   - union(x, y): fusiona los conjuntos de x e y
 *   Si dos nodos ya tienen el mismo representante, ya están
 *   conectados → agregar la arista formaría un CICLO.
 *   (Union-Find es otra forma de detectar ciclos en grafos
 *   no dirigidos, además del DFS.)
 *
 * Complejidad:
 *   - Tiempo:  O(E log E) por el ordenamiento de aristas
 *              (las operaciones de Union-Find son casi O(1))
 *   - Espacio: O(V + E)
 *
 * Aplicaciones:
 *   - Diseño de redes (eléctricas, de fibra óptica) con costo mínimo
 *   - Clustering (agrupamiento de datos)
 *   - Aproximaciones para el problema del viajante (TSP)
 */
public class Kruskal {

    /** Representa una arista no dirigida ponderada u — v con peso w */
    static class Edge implements Comparable<Edge> {
        int u, v, weight;

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.weight = w;
        }

        /** Permite ordenar las aristas por peso con Collections.sort */
        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    /**
     * ============================================================
     * ESTRUCTURA UNION-FIND (Disjoint Set Union)
     * ============================================================
     * Mantiene conjuntos disjuntos de nodos y permite:
     *   - find(x): ¿a qué conjunto pertenece x?  (con path compression)
     *   - union(x,y): fusionar dos conjuntos     (con union by rank)
     * Ambas operaciones quedan en tiempo casi constante.
     */
    static class UnionFind {
        int[] parent;  // parent[i] = padre de i en el árbol del conjunto
        int[] rank;    // rank[i] = altura aproximada del árbol de i

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            // Al inicio cada nodo es su propio conjunto
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        /**
         * Encuentra el representante (raíz) del conjunto de x.
         * PATH COMPRESSION: al buscar, colgamos cada nodo directamente
         * de la raíz para acelerar búsquedas futuras.
         */
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Compresión de camino
            }
            return parent[x];
        }

        /**
         * Une los conjuntos de x e y.
         * UNION BY RANK: el árbol más bajo se cuelga del más alto
         * para mantener los árboles planos.
         *
         * @return false si ya estaban en el mismo conjunto (¡ciclo!)
         */
        boolean union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return false;  // Mismo conjunto → formaría ciclo

            if (rank[rx] < rank[ry]) {
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rank[rx]++;
            }
            return true;
        }
    }

    /** Número de vértices del grafo */
    private int V;

    /** Lista de todas las aristas del grafo */
    private List<Edge> edges;

    /**
     * Constructor: crea un grafo no dirigido con V vértices.
     *
     * @param V número de vértices del grafo
     */
    public Kruskal(int V) {
        this.V = V;
        edges = new ArrayList<>();
    }

    /**
     * Agrega una arista NO dirigida ponderada al grafo.
     *
     * @param u un extremo de la arista
     * @param v el otro extremo
     * @param w peso de la arista
     */
    public void addEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
    }

    /**
     * ============================================================
     * ALGORITMO PRINCIPAL DE KRUSKAL
     * ============================================================
     *
     * Pasos:
     *   1. Ordenar aristas por peso ascendente
     *   2. Para cada arista (u, v):
     *      - Si u y v están en conjuntos distintos → agregarla al MST
     *        y unir los conjuntos
     *      - Si están en el mismo conjunto → descartarla (haría ciclo)
     *   3. Parar al reunir V-1 aristas
     */
    public void kruskal() {
        // Paso 1: ordenar las aristas por peso (usa compareTo de Edge)
        Collections.sort(edges);

        UnionFind uf = new UnionFind(V);
        List<Edge> mst = new ArrayList<>();
        int costoTotal = 0;

        // Paso 2: recorrer aristas de menor a mayor peso
        for (Edge e : edges) {
            // union() devuelve false si u y v ya están conectados
            if (uf.union(e.u, e.v)) {
                mst.add(e);
                costoTotal += e.weight;
                // Paso 3: un árbol de V nodos tiene exactamente V-1 aristas
                if (mst.size() == V - 1) break;
            } else {
                System.out.println("   Descartada " + e.u + "—" + e.v +
                                   " (peso " + e.weight + "): formaría un ciclo");
            }
        }

        // Mostrar resultado
        System.out.println("\nAristas del Árbol de Expansión Mínima:");
        for (Edge e : mst) {
            System.out.println("   " + e.u + " — " + e.v + "  (peso " + e.weight + ")");
        }
        System.out.println("Costo total del MST: " + costoTotal);
    }

    /**
     * Método principal con un grafo clásico de ejemplo.
     *
     * Grafo no dirigido de 4 nodos:
     *   0 —10— 1
     *   0 — 6— 2
     *   0 — 5— 3
     *   1 —15— 3
     *   2 — 4— 3
     *
     * MST esperado: 2—3 (4), 0—3 (5), 0—1 (10) → costo total 19
     */
    public static void main(String[] args) {
        Kruskal g = new Kruskal(4);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 6);
        g.addEdge(0, 3, 5);
        g.addEdge(1, 3, 15);
        g.addEdge(2, 3, 4);

        System.out.println("=== Ejecutando Kruskal ===");
        g.kruskal();
    }
}
