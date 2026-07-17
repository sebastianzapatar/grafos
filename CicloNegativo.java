import java.util.*;

/**
 * ============================================================
 * DETECCIÓN Y RECONSTRUCCIÓN DE CICLOS NEGATIVOS
 * (Bellman-Ford extendido)
 * ============================================================
 *
 * ¿QUÉ ES UN CICLO NEGATIVO?
 *   Es un ciclo cuya SUMA DE PESOS es menor que cero.
 *   Ejemplo: 1 → 2 → 3 → 1 con pesos 3, 2 y -7:
 *   suma = 3 + 2 + (-7) = -2 < 0 → ciclo negativo.
 *
 * ¿POR QUÉ SON UN PROBLEMA?
 *   Si un ciclo negativo es alcanzable desde el origen, el concepto
 *   de "camino más corto" DEJA DE EXISTIR: cada vuelta al ciclo
 *   reduce el costo total, por lo que la distancia tiende a -∞.
 *   Por eso Dijkstra no los soporta y Bellman-Ford los DETECTA
 *   en vez de dar una respuesta incorrecta.
 *
 * ¿CÓMO SE DETECTAN?
 *   Bellman-Ford garantiza que después de V-1 iteraciones todas
 *   las distancias son óptimas... SI no hay ciclos negativos.
 *   Entonces hacemos UNA ITERACIÓN EXTRA (la número V):
 *   - Si alguna arista todavía se puede relajar, la única
 *     explicación es que existe un ciclo negativo.
 *
 * ¿CÓMO SE RECONSTRUYE EL CICLO?
 *   1. Sea x el nodo destino de la arista que se relajó en la
 *      iteración extra.
 *   2. x puede no estar DENTRO del ciclo (solo ser alcanzado por él),
 *      así que retrocedemos V veces por el array parent[]: eso
 *      garantiza caer en un nodo que SÍ pertenece al ciclo.
 *   3. Desde ese nodo, seguimos parent[] hasta volver a él:
 *      esos nodos forman el ciclo negativo.
 *
 * Complejidad:
 *   - Tiempo:  O(V × E)  (igual que Bellman-Ford)
 *   - Espacio: O(V)
 */
public class CicloNegativo {

    /** Representa una arista dirigida ponderada u → v con peso w */
    static class Edge {
        int source, target, weight;

        Edge(int s, int t, int w) {
            source = s;
            target = t;
            weight = w;
        }
    }

    /** Número de vértices del grafo */
    private int V;

    /** Lista de todas las aristas del grafo */
    private List<Edge> edges;

    /**
     * Constructor: crea un grafo con V vértices y sin aristas.
     *
     * @param V número de vértices del grafo
     */
    public CicloNegativo(int V) {
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
     * BUSCAR Y RECONSTRUIR UN CICLO NEGATIVO
     * ============================================================
     *
     * Pasos:
     *   1. Ejecutar Bellman-Ford V iteraciones (una más de lo normal)
     *      guardando en parent[v] el nodo desde el que se relajó v.
     *   2. Si en la iteración V (la extra) se relajó alguna arista,
     *      guardar su nodo destino en 'x' → hay ciclo negativo.
     *   3. Retroceder V veces desde x usando parent[] para garantizar
     *      estar DENTRO del ciclo.
     *   4. Recorrer parent[] desde ahí hasta cerrar el ciclo y
     *      devolver la lista de nodos.
     *
     * NOTA: aquí inicializamos dist[] en 0 para todos los nodos
     * (en lugar de ∞ desde un origen). Esto equivale a agregar un
     * origen virtual conectado a todos los nodos con peso 0, y
     * permite detectar ciclos negativos en CUALQUIER parte del
     * grafo, no solo los alcanzables desde un origen específico.
     *
     * @return lista de nodos que forman el ciclo negativo
     *         (el primero y el último son el mismo nodo),
     *         o lista vacía si no hay ciclo negativo
     */
    public List<Integer> encontrarCicloNegativo() {
        int[] dist = new int[V];      // Inicia en 0: detecta ciclos en todo el grafo
        int[] parent = new int[V];
        Arrays.fill(parent, -1);

        int x = -1;  // Último nodo relajado en la iteración extra

        // Paso 1 y 2: V iteraciones completas de relajación
        for (int i = 0; i < V; i++) {
            x = -1;
            for (Edge e : edges) {
                if (dist[e.source] + e.weight < dist[e.target]) {
                    dist[e.target] = dist[e.source] + e.weight;
                    parent[e.target] = e.source;
                    x = e.target;  // Se relajó algo en esta iteración
                }
            }
            // Si en una iteración no se relajó nada, ya no habrá cambios
            if (x == -1) break;
        }

        // Si la iteración V (índice V-1) no relajó nada → sin ciclo negativo
        if (x == -1) {
            return new ArrayList<>();
        }

        // Paso 3: retroceder V veces para caer dentro del ciclo.
        // x puede estar "colgando" del ciclo sin pertenecer a él,
        // pero después de V pasos hacia atrás seguro estamos dentro.
        for (int i = 0; i < V; i++) {
            x = parent[x];
        }

        // Paso 4: recorrer el ciclo hasta volver al nodo inicial
        List<Integer> ciclo = new ArrayList<>();
        for (int actual = x; ; actual = parent[actual]) {
            ciclo.add(actual);
            if (actual == x && ciclo.size() > 1) break;
        }
        Collections.reverse(ciclo);  // parent[] apunta hacia atrás: invertir
        return ciclo;
    }

    /**
     * Ejecuta la búsqueda e imprime el resultado con el peso total del ciclo.
     */
    public void reportar() {
        List<Integer> ciclo = encontrarCicloNegativo();

        if (ciclo.isEmpty()) {
            System.out.println("✓ No hay ciclos negativos en el grafo.");
            return;
        }

        // Imprimir el ciclo: n0 → n1 → ... → n0
        System.out.print("⚠ Ciclo negativo encontrado: ");
        for (int i = 0; i < ciclo.size(); i++) {
            System.out.print(ciclo.get(i) + (i < ciclo.size() - 1 ? " → " : "\n"));
        }

        // Calcular el peso total del ciclo sumando sus aristas
        int pesoTotal = 0;
        for (int i = 0; i < ciclo.size() - 1; i++) {
            for (Edge e : edges) {
                if (e.source == ciclo.get(i) && e.target == ciclo.get(i + 1)) {
                    pesoTotal += e.weight;
                    break;
                }
            }
        }
        System.out.println("   Peso total del ciclo: " + pesoTotal);
    }

    /**
     * Método principal con dos ejemplos.
     *
     * Grafo 1 (CON ciclo negativo):
     *   0 → 1 (peso  4)
     *   1 → 2 (peso  3)
     *   2 → 3 (peso  2)
     *   3 → 1 (peso -7)   ← cierra el ciclo 1→2→3→1
     *   Peso del ciclo: 3 + 2 + (-7) = -2 < 0
     *
     * Grafo 2 (SIN ciclo negativo, aunque tiene pesos negativos):
     *   0 → 1 (peso -1)
     *   1 → 2 (peso  3)
     *   2 → 0 (peso  1)
     *   Peso del ciclo 0→1→2→0: -1 + 3 + 1 = 3 ≥ 0 → no es negativo
     */
    public static void main(String[] args) {
        System.out.println("=== Grafo 1: con ciclo negativo ===");
        CicloNegativo g1 = new CicloNegativo(4);
        g1.addEdge(0, 1, 4);
        g1.addEdge(1, 2, 3);
        g1.addEdge(2, 3, 2);
        g1.addEdge(3, 1, -7);
        g1.reportar();

        System.out.println("\n=== Grafo 2: pesos negativos pero SIN ciclo negativo ===");
        CicloNegativo g2 = new CicloNegativo(3);
        g2.addEdge(0, 1, -1);
        g2.addEdge(1, 2, 3);
        g2.addEdge(2, 0, 1);
        g2.reportar();
    }
}
