import java.util.*;

/**
 * ============================================================
 * ALGORITMO DE FLOYD-WARSHALL
 * ============================================================
 * 
 * Descripción:
 *   Algoritmo de programación dinámica que encuentra las distancias
 *   más cortas entre TODOS los pares de vértices en un grafo ponderado.
 *   A diferencia de Dijkstra (un origen) o Bellman-Ford (un origen),
 *   Floyd-Warshall resuelve el problema "All-Pairs Shortest Path".
 * 
 * Idea central:
 *   Para cada par (i, j), verificar si pasar por un vértice intermedio k
 *   mejora la distancia actual: dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
 * 
 * Complejidad:
 *   - Tiempo:  O(V³) donde V = número de vértices
 *   - Espacio: O(V²) para la matriz de distancias
 * 
 * Ventajas:
 *   - Encuentra caminos mínimos entre TODOS los pares de nodos
 *   - Funciona con aristas de peso negativo
 *   - Detecta ciclos de peso negativo
 *   - Implementación muy sencilla (triple for anidado)
 * 
 * Desventajas:
 *   - O(V³) puede ser lento para grafos muy grandes
 *   - No es eficiente si solo necesitamos un origen (usar Dijkstra)
 * 
 * Aplicaciones en compiladores:
 *   - Análisis de dependencias entre módulos
 *   - Optimización de grafos de flujo de control
 *   - Alcanzabilidad en autómatas finitos
 */
public class FloydWarshall {

    /** Constante que representa infinito (no hay camino directo) */
    private static final int INF = 99999;

    /** Número de vértices del grafo */
    private int V;

    /**
     * Matriz de distancias del grafo.
     * dist[i][j] = peso de la arista de i a j (INF si no existe)
     */
    private int[][] dist;

    /**
     * Matriz de predecesores para reconstruir caminos.
     * next[i][j] = siguiente nodo en el camino más corto de i a j
     */
    private int[][] next;

    /**
     * Constructor: inicializa el grafo con V vértices.
     * - La diagonal se pone en 0 (distancia de un nodo a sí mismo)
     * - Todas las demás distancias se inicializan en INF (no hay arista)
     * - La matriz next se inicializa en -1 (no hay camino conocido)
     * 
     * @param V número de vértices del grafo
     */
    public FloydWarshall(int V) {
        this.V = V;
        dist = new int[V][V];
        next = new int[V][V];

        // Inicializar matrices
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) {
                    dist[i][j] = 0;       // Distancia a sí mismo = 0
                } else {
                    dist[i][j] = INF;      // Sin arista = infinito
                }
                next[i][j] = -1;           // Sin camino conocido aún
            }
        }
    }

    /**
     * Agrega una arista dirigida con peso al grafo.
     * Actualiza tanto la matriz de distancias como la de predecesores.
     * 
     * @param u nodo origen
     * @param v nodo destino
     * @param w peso de la arista (puede ser negativo)
     */
    public void addEdge(int u, int v, int w) {
        dist[u][v] = w;
        next[u][v] = v;  // Desde u, el siguiente nodo hacia v es v directamente
    }

    /**
     * ============================================================
     * ALGORITMO PRINCIPAL DE FLOYD-WARSHALL
     * ============================================================
     * 
     * Funciona con tres bucles anidados:
     *   - k: el vértice intermedio que estamos considerando
     *   - i: el vértice origen del camino
     *   - j: el vértice destino del camino
     * 
     * En cada iteración preguntamos:
     *   "¿Es más corto ir de i a j pasando por k?"
     *   Si dist[i][k] + dist[k][j] < dist[i][j], entonces SÍ,
     *   y actualizamos la distancia.
     * 
     * IMPORTANTE: El bucle de k DEBE ser el más externo.
     * Esto garantiza que al considerar k, ya tenemos los caminos
     * óptimos usando solo los vértices {0, 1, ..., k-1} como intermedios.
     */
    public void floydWarshall() {
        System.out.println("=== Ejecutando Floyd-Warshall ===\n");
        System.out.println("Matriz de distancias inicial:");
        printMatrix(dist);

        // --- TRIPLE BUCLE: corazón del algoritmo ---
        // k = vértice intermedio candidato
        for (int k = 0; k < V; k++) {
            // i = vértice origen
            for (int i = 0; i < V; i++) {
                // j = vértice destino
                for (int j = 0; j < V; j++) {
                    // Verificar que existan los caminos parciales (no sean INF)
                    // para evitar overflow en la suma
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        // ¿Es mejor ir de i→k→j que el camino actual i→j?
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            // ¡Sí! Actualizamos la distancia mínima
                            dist[i][j] = dist[i][k] + dist[k][j];
                            // Actualizamos el predecesor para reconstruir el camino
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
            System.out.println("Después de considerar vértice intermedio k=" + k + ":");
            printMatrix(dist);
        }

        // --- DETECCIÓN DE CICLOS NEGATIVOS ---
        // Si dist[i][i] < 0, significa que hay un ciclo negativo
        // que pasa por el vértice i
        boolean hasNegativeCycle = false;
        for (int i = 0; i < V; i++) {
            if (dist[i][i] < 0) {
                hasNegativeCycle = true;
                System.out.println("⚠ Ciclo negativo detectado en el vértice " + i);
            }
        }

        if (!hasNegativeCycle) {
            System.out.println("✓ No se detectaron ciclos negativos.\n");
        }

        // Mostrar resultado final
        System.out.println("=== Matriz de distancias mínimas (resultado final) ===");
        printMatrix(dist);

        // Mostrar algunos caminos reconstruidos
        System.out.println("=== Caminos más cortos reconstruidos ===");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i != j && dist[i][j] != INF) {
                    System.out.print("Camino " + i + " → " + j + 
                                     " (costo: " + dist[i][j] + "): ");
                    printPath(i, j);
                    System.out.println();
                }
            }
        }
    }

    /**
     * Reconstruye e imprime el camino más corto de u a v
     * usando la matriz de predecesores 'next'.
     * 
     * @param u nodo origen
     * @param v nodo destino
     */
    public void printPath(int u, int v) {
        if (next[u][v] == -1) {
            System.out.print("No hay camino");
            return;
        }
        System.out.print(u);
        while (u != v) {
            u = next[u][v];
            System.out.print(" → " + u);
        }
    }

    /**
     * Imprime la matriz de distancias formateada.
     * INF se muestra como "∞" para mayor claridad.
     * 
     * @param matrix matriz a imprimir
     */
    public void printMatrix(int[][] matrix) {
        System.out.print("     ");
        for (int j = 0; j < V; j++) {
            System.out.printf("%6d", j);
        }
        System.out.println();
        System.out.print("     ");
        for (int j = 0; j < V; j++) {
            System.out.print("------");
        }
        System.out.println();

        for (int i = 0; i < V; i++) {
            System.out.printf("%3d |", i);
            for (int j = 0; j < V; j++) {
                if (matrix[i][j] == INF) {
                    System.out.printf("%6s", "∞");
                } else {
                    System.out.printf("%6d", matrix[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Método principal con un ejemplo demostrativo.
     * 
     * Grafo de ejemplo (4 vértices):
     * 
     *     (0)---3--->(1)
     *      |          |
     *      8          2
     *      |          |
     *      v          v
     *     (2)<--1----(3)
     *      |
     *      +--------->(1) peso -4 (arista negativa)
     */
    public static void main(String[] args) {
        FloydWarshall g = new FloydWarshall(4);

        // Agregar aristas del grafo
        g.addEdge(0, 1, 3);    // 0 → 1 con peso 3
        g.addEdge(0, 2, 8);    // 0 → 2 con peso 8
        g.addEdge(1, 3, 2);    // 1 → 3 con peso 2
        g.addEdge(2, 1, -4);   // 2 → 1 con peso -4 (negativo)
        g.addEdge(3, 2, 1);    // 3 → 2 con peso 1

        // Ejecutar Floyd-Warshall
        g.floydWarshall();
    }
}
