import java.util.*;

/**
 * ============================================================
 * DETECCIÓN DE CICLOS EN GRAFOS
 * ============================================================
 *
 * ¿QUÉ ES UN CICLO?
 *   Un ciclo es un camino que comienza y termina en el MISMO
 *   vértice, sin repetir aristas. Por ejemplo: 1 → 2 → 3 → 1.
 *   Un grafo sin ciclos dirigidos se llama DAG (Directed Acyclic
 *   Graph), estructura fundamental en compiladores (dependencias,
 *   orden de evaluación, grafos de flujo).
 *
 * ESTRATEGIA PARA GRAFOS DIRIGIDOS: DFS con 3 colores
 *   - BLANCO (0): nodo no visitado todavía
 *   - GRIS   (1): nodo en proceso (está en la pila de recursión actual)
 *   - NEGRO  (2): nodo terminado (ya exploramos todos sus descendientes)
 *
 *   Si durante el DFS encontramos una arista hacia un nodo GRIS,
 *   significa que regresamos a un nodo que todavía está "abierto"
 *   en el camino actual → ¡HAY UN CICLO! (se llama "back edge").
 *
 *   ¿Por qué no basta con "visitado sí/no"? Porque un nodo puede
 *   ser alcanzado por dos caminos distintos sin que exista ciclo
 *   (ej: 0→1→3 y 0→2→3). El color GRIS distingue "está en MI camino
 *   actual" de "ya fue explorado por otro camino".
 *
 * ESTRATEGIA PARA GRAFOS NO DIRIGIDOS: DFS con padre
 *   Si encontramos un vecino ya visitado que NO es el padre del
 *   nodo actual, hay un ciclo. (La arista hacia el padre no cuenta
 *   porque en un grafo no dirigido u—v se recorre en ambos sentidos.)
 *
 * Complejidad:
 *   - Tiempo:  O(V + E)
 *   - Espacio: O(V)
 *
 * Aplicaciones en compiladores:
 *   - Detectar dependencias circulares entre módulos/imports
 *   - Validar que un grafo de tareas admite orden topológico
 *   - Detectar recursión en gramáticas
 */
public class DeteccionCiclos {

    /** Colores para el DFS en grafos dirigidos */
    private static final int BLANCO = 0;  // No visitado
    private static final int GRIS   = 1;  // En la pila de recursión
    private static final int NEGRO  = 2;  // Completamente explorado

    /** Número de vértices del grafo */
    private int V;

    /** Lista de adyacencia: adj[i] contiene los vecinos del nodo i */
    private List<List<Integer>> adj;

    /** Array de padres para reconstruir el ciclo encontrado */
    private int[] parent;

    /**
     * Constructor: crea un grafo con V vértices y sin aristas.
     *
     * @param V número de vértices del grafo
     */
    public DeteccionCiclos(int V) {
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
     * ============================================================
     * DETECCIÓN DE CICLOS EN GRAFO DIRIGIDO (DFS con 3 colores)
     * ============================================================
     *
     * Pasos del algoritmo:
     *   1. Pintar todos los nodos de BLANCO
     *   2. Para cada nodo BLANCO, iniciar un DFS
     *   3. Al entrar a un nodo: pintarlo de GRIS
     *   4. Al explorar una arista u → v:
     *      - Si v es GRIS  → ¡CICLO ENCONTRADO! (back edge)
     *      - Si v es BLANCO → seguir el DFS recursivamente
     *      - Si v es NEGRO → ignorar (ya explorado, sin ciclo por ahí)
     *   5. Al terminar con un nodo: pintarlo de NEGRO
     *
     * @return true si el grafo tiene al menos un ciclo
     */
    public boolean tieneCicloDirigido() {
        int[] color = new int[V];       // Todos inician en BLANCO (0)
        parent = new int[V];
        Arrays.fill(parent, -1);

        // El grafo puede no ser conexo: intentar desde cada nodo blanco
        for (int i = 0; i < V; i++) {
            if (color[i] == BLANCO && dfsCiclo(i, color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DFS recursivo que busca back edges (aristas hacia nodos GRISES).
     * Cuando encuentra el ciclo, lo reconstruye e imprime usando parent[].
     *
     * @param u     nodo actual
     * @param color estado de cada nodo (BLANCO/GRIS/NEGRO)
     * @return true si se encontró un ciclo desde este nodo
     */
    private boolean dfsCiclo(int u, int[] color) {
        // Paso 3: el nodo entra a la pila de recursión
        color[u] = GRIS;

        for (int v : adj.get(u)) {
            if (color[v] == GRIS) {
                // Paso 4: ¡BACK EDGE! v está en nuestro camino actual
                imprimirCiclo(u, v);
                return true;
            }
            if (color[v] == BLANCO) {
                parent[v] = u;  // Recordar por dónde llegamos (para reconstruir)
                if (dfsCiclo(v, color)) return true;
            }
            // Si v es NEGRO no hacemos nada: ya fue explorado sin ciclos
        }

        // Paso 5: el nodo sale de la pila de recursión
        color[u] = NEGRO;
        return false;
    }

    /**
     * Reconstruye e imprime el ciclo encontrado.
     * El ciclo es: inicio → ... → fin → inicio
     * Se recorre la cadena de padres desde 'fin' hasta 'inicio'.
     *
     * @param fin    nodo donde se detectó la back edge (extremo final)
     * @param inicio nodo GRIS al que apunta la back edge (inicio del ciclo)
     */
    private void imprimirCiclo(int fin, int inicio) {
        List<Integer> ciclo = new ArrayList<>();
        // Retroceder desde 'fin' hasta llegar a 'inicio'
        for (int x = fin; x != inicio; x = parent[x]) {
            ciclo.add(x);
        }
        ciclo.add(inicio);
        Collections.reverse(ciclo);   // Ordenar del inicio al fin
        ciclo.add(inicio);            // Cerrar el ciclo

        System.out.print("   Ciclo encontrado: ");
        for (int i = 0; i < ciclo.size(); i++) {
            System.out.print(ciclo.get(i) + (i < ciclo.size() - 1 ? " → " : "\n"));
        }
    }

    /**
     * ============================================================
     * DETECCIÓN DE CICLOS EN GRAFO NO DIRIGIDO (DFS con padre)
     * ============================================================
     *
     * NOTA: este método asume que las aristas se agregaron en ambos
     * sentidos (addEdge(u,v) y addEdge(v,u)).
     *
     * Idea: al hacer DFS, si un vecino ya visitado NO es el padre
     * del nodo actual, entonces existe otra forma de llegar a él
     * → hay un ciclo.
     *
     * @return true si el grafo no dirigido tiene al menos un ciclo
     */
    public boolean tieneCicloNoDirigido() {
        boolean[] visited = new boolean[V];
        for (int i = 0; i < V; i++) {
            if (!visited[i] && dfsNoDirigido(i, -1, visited)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DFS para grafos no dirigidos.
     *
     * @param u       nodo actual
     * @param padre   nodo desde el que llegamos a u (-1 si es la raíz)
     * @param visited array de nodos visitados
     * @return true si se encontró un ciclo
     */
    private boolean dfsNoDirigido(int u, int padre, boolean[] visited) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (dfsNoDirigido(v, u, visited)) return true;
            } else if (v != padre) {
                // Vecino visitado que NO es el padre → ciclo
                System.out.println("   Ciclo detectado al ver la arista " + u + " — " + v);
                return true;
            }
        }
        return false;
    }

    /**
     * Método principal con dos ejemplos: uno CON ciclo y uno SIN ciclo.
     *
     * Grafo 1 (dirigido, CON ciclo):
     *   0 → 1 → 2 → 3 → 1   (el ciclo es 1 → 2 → 3 → 1)
     *
     * Grafo 2 (dirigido, SIN ciclo — es un DAG):
     *   0 → 1 → 3
     *   0 → 2 → 3
     *   (el nodo 3 se alcanza por dos caminos, pero NO hay ciclo)
     */
    public static void main(String[] args) {
        System.out.println("=== Grafo 1: dirigido con ciclo 1→2→3→1 ===");
        DeteccionCiclos g1 = new DeteccionCiclos(4);
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 1);
        System.out.println("¿Tiene ciclo? " + g1.tieneCicloDirigido());

        System.out.println("\n=== Grafo 2: DAG (dos caminos a 3, sin ciclo) ===");
        DeteccionCiclos g2 = new DeteccionCiclos(4);
        g2.addEdge(0, 1);
        g2.addEdge(0, 2);
        g2.addEdge(1, 3);
        g2.addEdge(2, 3);
        System.out.println("¿Tiene ciclo? " + g2.tieneCicloDirigido());

        System.out.println("\n=== Grafo 3: no dirigido con ciclo 0—1—2—0 ===");
        DeteccionCiclos g3 = new DeteccionCiclos(4);
        // Aristas en ambos sentidos (grafo no dirigido)
        g3.addEdge(0, 1); g3.addEdge(1, 0);
        g3.addEdge(1, 2); g3.addEdge(2, 1);
        g3.addEdge(2, 0); g3.addEdge(0, 2);
        g3.addEdge(2, 3); g3.addEdge(3, 2);
        System.out.println("¿Tiene ciclo? " + g3.tieneCicloNoDirigido());
    }
}
