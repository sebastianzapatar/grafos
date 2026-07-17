import java.util.*;

/**
 * ============================================================
 * ORDEN TOPOLÓGICO (Algoritmo de Kahn)
 * ============================================================
 *
 * Descripción:
 *   Dado un grafo dirigido ACÍCLICO (DAG), un orden topológico es
 *   una secuencia de los vértices tal que para toda arista u → v,
 *   u aparece ANTES que v en la secuencia.
 *
 *   Intuición: si u → v significa "u debe hacerse antes que v"
 *   (dependencias), el orden topológico es un orden válido de
 *   ejecución de todas las tareas.
 *
 * IMPORTANTE: solo existe si el grafo NO tiene ciclos.
 *   Si A depende de B y B depende de A, no hay orden posible.
 *   Por eso este algoritmo también sirve como DETECTOR DE CICLOS:
 *   si no logra ordenar todos los nodos, hay un ciclo.
 *
 * Algoritmo de Kahn (BFS con grados de entrada):
 *   1. Calcular el grado de entrada (in-degree) de cada nodo
 *      (cuántas aristas le llegan)
 *   2. Encolar todos los nodos con grado de entrada 0
 *      (no dependen de nadie → pueden ir primero)
 *   3. Mientras la cola no esté vacía:
 *      a. Desencolar un nodo y agregarlo al orden
 *      b. "Eliminar" sus aristas: restar 1 al grado de entrada
 *         de cada vecino
 *      c. Si algún vecino queda con grado 0, encolarlo
 *   4. Si el orden tiene menos de V nodos → HAY CICLO
 *
 * Complejidad:
 *   - Tiempo:  O(V + E)
 *   - Espacio: O(V)
 *
 * Aplicaciones en compiladores:
 *   - Orden de compilación de módulos según sus dependencias
 *   - Orden de evaluación de atributos en gramáticas
 *   - Planificación de instrucciones (instruction scheduling)
 *   - Resolución de dependencias (Makefiles, Maven, Gradle)
 */
public class OrdenTopologico {

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia: adj[i] contiene los vecinos del nodo i */
    private List<List<Integer>> adj;

    /**
     * Constructor: crea un grafo dirigido con V vértices.
     *
     * @param V número de vértices del grafo
     */
    public OrdenTopologico(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    }

    /**
     * Agrega una arista dirigida u → v ("u debe ir antes que v").
     *
     * @param u nodo origen (prerequisito)
     * @param v nodo destino (dependiente)
     */
    public void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    /**
     * ============================================================
     * ALGORITMO DE KAHN
     * ============================================================
     *
     * @return lista con el orden topológico, o lista vacía si el
     *         grafo tiene un ciclo (no existe orden válido)
     */
    public List<Integer> ordenTopologico() {
        // Paso 1: calcular el grado de entrada de cada nodo
        int[] gradoEntrada = new int[V];
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                gradoEntrada[v]++;
            }
        }

        // Paso 2: encolar los nodos sin dependencias (grado 0)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < V; i++) {
            if (gradoEntrada[i] == 0) queue.add(i);
        }

        // Paso 3: procesar la cola
        List<Integer> orden = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            orden.add(u);  // u ya no tiene dependencias pendientes

            // "Eliminar" las aristas que salen de u
            for (int v : adj.get(u)) {
                gradoEntrada[v]--;
                // Si v ya no tiene dependencias pendientes, puede procesarse
                if (gradoEntrada[v] == 0) queue.add(v);
            }
        }

        // Paso 4: si no ordenamos todos los nodos, hay un ciclo
        if (orden.size() < V) {
            System.out.println("⚠ El grafo tiene un ciclo: no existe orden topológico.");
            return new ArrayList<>();
        }
        return orden;
    }

    /**
     * Método principal con un ejemplo de dependencias de compilación.
     *
     * Imaginemos 6 módulos donde u → v significa "u se compila antes que v":
     *   5 → 2, 5 → 0     (el módulo 5 no depende de nadie)
     *   4 → 0, 4 → 1
     *   2 → 3
     *   3 → 1
     *
     * Un orden válido: 4 5 2 0 3 1 (puede haber varios órdenes válidos)
     */
    public static void main(String[] args) {
        System.out.println("=== Grafo 1: DAG de dependencias ===");
        OrdenTopologico g = new OrdenTopologico(6);
        g.addEdge(5, 2);
        g.addEdge(5, 0);
        g.addEdge(4, 0);
        g.addEdge(4, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        List<Integer> orden = g.ordenTopologico();
        System.out.println("Orden topológico: " + orden);

        // Ejemplo con ciclo: 0 → 1 → 2 → 0 (dependencia circular)
        System.out.println("\n=== Grafo 2: con dependencia circular ===");
        OrdenTopologico g2 = new OrdenTopologico(3);
        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        g2.addEdge(2, 0);
        g2.ordenTopologico();
    }
}
