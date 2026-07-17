import java.util.*;

/**
 * ============================================================================
 * SOLUCIONES DE LOS 11 EJERCICIOS DE LA PRESENTACIÓN DE GRAFOS
 * ============================================================================
 *
 * Cada método ejercicioN_*() es autocontenido: construye su propio grafo,
 * ejecuta el algoritmo correspondiente e imprime el resultado, que coincide
 * con la solución mostrada en la slide del ejercicio.
 *
 * Compilar y ejecutar:
 *   javac Soluciones.java
 *   java Soluciones
 *
 * Contenido:
 *   1. BFS               — orden de visita y distancia en aristas
 *   2. DFS               — componentes conexas
 *   3. Dijkstra          — distancias mínimas desde un origen
 *   4. Bellman-Ford      — detección de ciclo negativo
 *   5. Floyd-Warshall    — matriz de distancias entre todos los pares
 *   6. Orden Topológico  — orden de compilación (DFS + pila)
 *   7. Dijkstra + prev[] — reconstrucción del camino más corto
 *   8. Dijkstra vs FW    — todos los pares, comparación práctica
 *   9. Union Naive       — traza de parent[] sin rank
 *  10. Union by Rank     — traza de parent[] y rank[]
 *  11. Kruskal           — MST usando Union-Find
 */
public class Soluciones {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   SOLUCIONES DE EJERCICIOS DE GRAFOS     ");
        System.out.println("==========================================");

        ejercicio1_BFS();
        ejercicio2_DFS();
        ejercicio3_Dijkstra();
        ejercicio4_BellmanFord();
        ejercicio5_FloydWarshall();
        ejercicio6_OrdenTopologico();
        ejercicio7_DijkstraPredecesores();
        ejercicio8_DijkstraVsFW();
        ejercicio9_UnionNaive();
        ejercicio10_UnionByRank();
        ejercicio11_Kruskal();
    }

    // =========================================================================
    // Ejercicio 1: BFS
    //
    // Enunciado: grafo dirigido con aristas 0→1, 0→2, 1→3, 1→4, 2→4, 2→5,
    // 3→5, 4→5. Determinar el orden de visita BFS desde 0 y la distancia
    // (en número de aristas) de 0 a 5.
    //
    // Idea: BFS usa una COLA (FIFO), por eso explora "por niveles": primero
    // todos los nodos a distancia 1, luego los de distancia 2, etc. Eso
    // garantiza que la primera vez que se alcanza un nodo, es por el camino
    // con MENOS aristas.
    //
    // Resultado esperado: orden 0,1,2,3,4,5 y distancia 0→5 = 2 (vía 0→2→5).
    // =========================================================================
    public static void ejercicio1_BFS() {
        System.out.println("\n--- Ejercicio 1: BFS ---");

        // Lista de adyacencia: adj.get(u) = vecinos de u
        int V = 6;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        adj.get(0).addAll(Arrays.asList(1, 2));
        adj.get(1).addAll(Arrays.asList(3, 4));
        adj.get(2).addAll(Arrays.asList(4, 5));
        adj.get(3).addAll(Arrays.asList(5));
        adj.get(4).addAll(Arrays.asList(5));

        int start = 0, target = 5;
        Queue<Integer> q = new LinkedList<>();   // cola FIFO del BFS
        boolean[] visitados = new boolean[V];    // evita procesar un nodo 2 veces
        int[] dist = new int[V];                 // distancia en ARISTAS desde start

        // Inicialización: el origen entra a la cola con distancia 0
        q.add(start);
        visitados[start] = true;
        dist[start] = 0;

        List<Integer> ordenVisita = new ArrayList<>();

        while (!q.isEmpty()) {
            int u = q.poll();          // sacar el FRENTE de la cola
            ordenVisita.add(u);

            for (int v : adj.get(u)) {
                if (!visitados[v]) {
                    // Se marca visitado AL ENCOLAR (no al desencolar) para
                    // que un mismo nodo no entre dos veces a la cola
                    visitados[v] = true;
                    dist[v] = dist[u] + 1;   // un nivel más profundo
                    q.add(v);
                }
            }
        }

        System.out.println("Orden de visita BFS: " + ordenVisita);
        System.out.println("Distancia de " + start + " a " + target + ": " + dist[target] + " aristas");
    }

    // =========================================================================
    // Ejercicio 2: DFS — Componentes Conexas
    //
    // Enunciado: grafo NO dirigido con aristas 0-1, 0-2, 1-2, 3-4, 5-6, 5-7,
    // 6-7. ¿Cuántas componentes conexas hay y cuáles son sus nodos?
    //
    // Idea: recorrer todos los nodos; cada vez que se encuentra uno sin
    // visitar, se lanza un DFS desde él. Todo lo que ese DFS alcance forma
    // UNA componente. El número de DFS lanzados = número de componentes.
    //
    // Resultado esperado: 3 componentes → {0,1,2}, {3,4}, {5,6,7}.
    // =========================================================================
    public static void ejercicio2_DFS() {
        System.out.println("\n--- Ejercicio 2: DFS Componentes Conexas ---");
        int V = 8;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());

        // Grafo NO dirigido: cada arista se agrega en AMBOS sentidos
        int[][] aristas = {{0,1}, {0,2}, {1,2}, {3,4}, {5,6}, {5,7}, {6,7}};
        for (int[] e : aristas) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        boolean[] visitados = new boolean[V];
        int componentes = 0;
        List<List<Integer>> grupos = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            if (!visitados[i]) {
                // Nodo sin visitar → arranca una componente nueva
                componentes++;
                List<Integer> grupoActual = new ArrayList<>();
                dfsEj2(i, adj, visitados, grupoActual);
                grupos.add(grupoActual);
            }
        }

        System.out.println("Número de componentes conexas: " + componentes);
        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("Componente " + (i + 1) + ": " + grupos.get(i));
        }
    }

    /** DFS recursivo: marca u y visita en profundidad a sus vecinos. */
    private static void dfsEj2(int u, List<List<Integer>> adj, boolean[] visitados, List<Integer> grupo) {
        visitados[u] = true;
        grupo.add(u);
        for (int v : adj.get(u)) {
            if (!visitados[v]) {
                dfsEj2(v, adj, visitados, grupo);   // profundizar antes de seguir
            }
        }
    }

    // =========================================================================
    // Ejercicio 3: Dijkstra
    //
    // Enunciado: grafo ponderado A→B(4), A→C(2), B→D(2), B→E(1), C→E(5),
    // D→F(6), E→D(3), E→F(1). Distancias mínimas desde A.
    //
    // Idea: greedy con min-heap. Siempre se procesa el nodo NO finalizado con
    // menor distancia tentativa; al procesarlo se "relajan" sus aristas
    // (¿mejora pasar por u para llegar a v?). Requiere pesos >= 0.
    //
    // Resultado esperado: A=0, B=4, C=2, D=6, E=5, F=6 (A→B→E→F).
    // =========================================================================
    public static void ejercicio3_Dijkstra() {
        System.out.println("\n--- Ejercicio 3: Dijkstra ---");
        int V = 6; // A=0, B=1, C=2, D=3, E=4, F=5
        String[] nodos = {"A", "B", "C", "D", "E", "F"};
        // adj.get(u) = lista de aristas {destino, peso}
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());

        adj.get(0).add(new int[]{1, 4}); // A->B (4)
        adj.get(0).add(new int[]{2, 2}); // A->C (2)
        adj.get(1).add(new int[]{3, 2}); // B->D (2)
        adj.get(1).add(new int[]{4, 1}); // B->E (1)
        adj.get(2).add(new int[]{4, 5}); // C->E (5)
        adj.get(3).add(new int[]{5, 6}); // D->F (6)
        adj.get(4).add(new int[]{3, 3}); // E->D (3)
        adj.get(4).add(new int[]{5, 1}); // E->F (1)

        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);  // "infinito": aún sin camino conocido
        dist[0] = 0;

        // Min-heap de pares {nodo, distancia tentativa}, ordenado por distancia
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{0, 0});

        while (!pq.isEmpty()) {
            int[] actual = pq.poll();
            int u = actual[0], d = actual[1];
            // Entrada OBSOLETA: ya se encontró un camino mejor hacia u antes
            // de sacar esta entrada del heap → ignorarla
            if (d > dist[u]) continue;

            // RELAJACIÓN: ¿llegar a v pasando por u es más barato?
            for (int[] e : adj.get(u)) {
                int v = e[0], peso = e[1];
                if (dist[u] + peso < dist[v]) {
                    dist[v] = dist[u] + peso;
                    pq.add(new int[]{v, dist[v]});   // nueva mejor distancia al heap
                }
            }
        }

        System.out.println("Distancias mínimas desde A:");
        for (int i = 0; i < V; i++) {
            System.out.println("A -> " + nodos[i] + ": " + dist[i]);
        }
    }

    // =========================================================================
    // Ejercicio 4: Bellman-Ford — Detección de Ciclo Negativo
    //
    // Enunciado: aristas 0→1(1), 1→2(3), 2→3(2), 3→1(-7). ¿Hay ciclo negativo?
    // El ciclo 1→2→3→1 pesa 3 + 2 + (-7) = -2 → SÍ.
    //
    // Idea: un camino simple tiene a lo sumo V-1 aristas, así que V-1 rondas
    // de relajación bastan para estabilizar las distancias. Si en una ronda
    // EXTRA (la V-ésima) alguna arista todavía mejora, es porque un ciclo
    // negativo sigue "bajando" las distancias hacia -infinito.
    // =========================================================================
    public static void ejercicio4_BellmanFord() {
        System.out.println("\n--- Ejercicio 4: Bellman-Ford (Ciclo Negativo) ---");
        int V = 4;
        // Cada arista es {origen, destino, peso}
        int[][] edges = {
            {0, 1, 1},
            {1, 2, 3},
            {2, 3, 2},
            {3, 1, -7}
        };

        int[] dist = new int[V];
        Arrays.fill(dist, 999999);   // "infinito" seguro (evita overflow al sumar)
        dist[0] = 0;

        // Fase 1: V-1 rondas relajando TODAS las aristas
        for (int i = 1; i < V; i++) {
            for (int[] e : edges) {
                if (dist[e[0]] + e[2] < dist[e[1]]) {
                    dist[e[1]] = dist[e[0]] + e[2];
                }
            }
        }

        // Fase 2: ronda extra — si algo aún se relaja, hay ciclo negativo
        boolean cicloNegativo = false;
        for (int[] e : edges) {
            if (dist[e[0]] + e[2] < dist[e[1]]) {
                cicloNegativo = true;
                break;
            }
        }
        System.out.println("¿Existe ciclo negativo?: " + (cicloNegativo ? "SÍ" : "NO"));
    }

    // =========================================================================
    // Ejercicio 5: Floyd-Warshall
    //
    // Enunciado: aristas 0→1(5), 0→3(10), 1→2(3), 2→3(1). Calcular la matriz
    // de distancias mínimas entre TODOS los pares.
    //
    // Idea (programación dinámica): para cada nodo intermedio k, y cada par
    // (i, j), probar si pasar por k mejora: dist[i][j] = min(dist[i][j],
    // dist[i][k] + dist[k][j]). El bucle de k DEBE ser el externo.
    //
    // Resultado esperado: dist[0][3] = 9 por el camino 0→1→2→3 (5+3+1),
    // mejor que la arista directa 0→3 de peso 10.
    // =========================================================================
    public static void ejercicio5_FloydWarshall() {
        System.out.println("\n--- Ejercicio 5: Floyd-Warshall ---");
        int V = 4;
        int INF = 999999;
        // Matriz inicial: pesos directos, 0 en la diagonal, INF si no hay arista
        int[][] dist = {
            {0, 5, INF, 10},
            {INF, 0, 3, INF},
            {INF, INF, 0, 1},
            {INF, INF, INF, 0}
        };

        // k = nodo intermedio permitido; i = origen; j = destino
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];   // mejora vía k
                    }
                }
            }
        }

        System.out.println("Matriz de distancias final:");
        for (int i = 0; i < V; i++) {
            System.out.println(Arrays.toString(dist[i]).replace("999999", "∞"));
        }
        System.out.println("Distancia 0 -> 3 = " + dist[0][3]);
    }

    // =========================================================================
    // Ejercicio 6: Orden Topológico (DFS + pila)
    //
    // Enunciado: módulos de un compilador donde A→B significa "A depende de
    // B". Encontrar un orden de compilación válido (cada módulo se compila
    // DESPUÉS de sus dependencias).
    //
    // Idea: DFS post-orden. Un nodo se apila cuando TODOS sus descendientes
    // (dependencias) ya terminaron. Al vaciar la pila se obtiene el orden:
    // primero los módulos sin dependencias.
    //
    // Resultado esperado: utils → lexer → ast → parser → codegen → main
    // (u otro orden topológico válido).
    // =========================================================================
    public static void ejercicio6_OrdenTopologico() {
        System.out.println("\n--- Ejercicio 6: Orden Topológico ---");
        String[] mods = {"main", "parser", "lexer", "ast", "codegen", "utils"};
        Map<String, Integer> id = new HashMap<>();
        for(int i=0; i<mods.length; i++) id.put(mods[i], i);

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < mods.length; i++) adj.add(new ArrayList<>());

        // Arista u→v = "u depende de v"
        adj.get(id.get("main")).add(id.get("parser"));
        adj.get(id.get("main")).add(id.get("codegen"));
        adj.get(id.get("parser")).add(id.get("lexer"));
        adj.get(id.get("parser")).add(id.get("ast"));
        adj.get(id.get("codegen")).add(id.get("ast"));
        adj.get(id.get("lexer")).add(id.get("utils"));
        adj.get(id.get("ast")).add(id.get("utils"));

        boolean[] visitados = new boolean[mods.length];
        Stack<Integer> pila = new Stack<>();

        // Lanzar DFS desde cada nodo aún no visitado (cubre grafos no conexos)
        for (int i = 0; i < mods.length; i++) {
            if (!visitados[i]) dfsTopo(i, adj, visitados, pila);
        }

        // La pila tiene main arriba y utils abajo; al hacer pop sale main
        // primero, PERO como aquí u→v = "u depende de v", el orden de
        // compilación correcto es el INVERSO del pop → vaciamos y damos vuelta
        System.out.print("Orden de compilación: ");
        List<String> orden = new ArrayList<>();
        while (!pila.isEmpty()) orden.add(mods[pila.pop()]);
        Collections.reverse(orden);   // dependencias primero
        System.out.println(String.join(" -> ", orden));
    }

    /** DFS post-orden: apila u cuando todas sus dependencias terminaron. */
    private static void dfsTopo(int u, List<List<Integer>> adj, boolean[] vis, Stack<Integer> pila) {
        vis[u] = true;
        for (int v : adj.get(u)) {
            if (!vis[v]) dfsTopo(v, adj, vis, pila);
        }
        pila.push(u);   // ¡clave! se apila al TERMINAR, no al entrar
    }

    // =========================================================================
    // Ejercicio 7: Dijkstra con Predecesores
    //
    // Enunciado: aristas 0→1(2), 0→2(6), 1→3(5), 2→3(1), 3→4(2), 2→4(8).
    // Encontrar no solo la distancia mínima 0→4 sino el CAMINO.
    //
    // Idea: igual que Dijkstra, pero al relajar u→v se guarda prev[v] = u
    // ("llegué a v desde u"). El camino se reconstruye retrocediendo desde
    // el destino con prev[] y luego invirtiendo la lista.
    //
    // Resultado esperado: dist 0→4 = 9, camino 0 → 2 → 3 → 4.
    // =========================================================================
    public static void ejercicio7_DijkstraPredecesores() {
        System.out.println("\n--- Ejercicio 7: Dijkstra con Predecesores ---");
        int V = 5;
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());

        adj.get(0).add(new int[]{1, 2});
        adj.get(0).add(new int[]{2, 6});
        adj.get(1).add(new int[]{3, 5});
        adj.get(2).add(new int[]{3, 1});
        adj.get(3).add(new int[]{4, 2});
        adj.get(2).add(new int[]{4, 8});

        int[] dist = new int[V];
        int[] prev = new int[V];   // prev[v] = nodo anterior en el mejor camino a v
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);     // -1 = sin predecesor (marca el inicio)
        dist[0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{0, 0});

        while (!pq.isEmpty()) {
            int[] actual = pq.poll();
            int u = actual[0], d = actual[1];
            if (d > dist[u]) continue;   // entrada obsoleta del heap

            for (int[] e : adj.get(u)) {
                int v = e[0], peso = e[1];
                if (dist[u] + peso < dist[v]) {
                    dist[v] = dist[u] + peso;
                    prev[v] = u;              // registrar por dónde llegamos
                    pq.add(new int[]{v, dist[v]});
                }
            }
        }

        // Reconstrucción: retroceder 4 ← prev[4] ← prev[prev[4]] ... hasta -1
        List<Integer> camino = new ArrayList<>();
        int actual = 4;
        while (actual != -1) {
            camino.add(actual);
            actual = prev[actual];
        }
        Collections.reverse(camino);   // quedó al revés (destino→origen)

        System.out.println("Distancia 0 -> 4: " + dist[4]);
        System.out.println("Camino: " + camino);
    }

    // =========================================================================
    // Ejercicio 8: Dijkstra vs Floyd-Warshall (todos los pares)
    //
    // Enunciado: aristas 0→1(3), 0→2(8), 1→2(2), 2→0(5). Obtener la matriz
    // de distancias entre TODOS los pares.
    //
    // Análisis (ver slide): con V nodos y E aristas,
    //   - Dijkstra desde cada nodo: O(V · (V+E) log V) → gana en grafos DISPERSOS
    //   - Floyd-Warshall:           O(V³)              → gana en grafos DENSOS
    //
    // Resultado esperado:
    //   [0, 3, 5]   (0→2 mejora vía 1: 3+2=5 < 8)
    //   [7, 0, 2]   (1→0 vía 2: 2+5=7)
    //   [5, 8, 0]   (2→1 vía 0: 5+3=8)
    // =========================================================================
    public static void ejercicio8_DijkstraVsFW() {
        System.out.println("\n--- Ejercicio 8: Dijkstra vs Floyd-Warshall ---");
        int V = 3;
        int INF = 99999;
        int[][] adj = {
            {0, 3, 8},
            {INF, 0, 2},
            {5, INF, 0}
        };

        // Aquí usamos Floyd-Warshall (una sola pasada resuelve todos los pares)
        int[][] dist = new int[V][V];
        for(int i=0; i<V; i++) System.arraycopy(adj[i], 0, dist[i], 0, V);

        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        System.out.println("Matriz final (todos los pares):");
        for (int i = 0; i < V; i++) {
            System.out.println(Arrays.toString(dist[i]));
        }
    }

    // =========================================================================
    // Ejercicio 9: Union Naive
    //
    // Enunciado: 6 elementos {0..5}. Ejecutar union(1,0), union(3,2),
    // union(5,4), union(3,0), union(5,0) con union NAIVE:
    // la raíz de x SIEMPRE se cuelga de la raíz de y, sin mirar alturas.
    //
    // Traza esperada de parent[]:
    //   [0,1,2,3,4,5] → [0,0,2,3,4,5] → [0,0,2,2,4,5] → [0,0,2,2,4,4]
    //   → [0,0,0,2,4,4] → [0,0,0,2,0,4]
    // Árbol final: 0←1, 0←2←3, 0←4←5 (cadenas de largo 2).
    // find(5) sin path compression: 5→4→0 = 2 saltos.
    // =========================================================================
    public static void ejercicio9_UnionNaive() {
        System.out.println("\n--- Ejercicio 9: Union Naive ---");
        int n = 6;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;   // cada uno es su propia raíz

        // Regla naive: parent[find(x)] = find(y)
        unionNaive(parent, 1, 0);
        unionNaive(parent, 3, 2);
        unionNaive(parent, 5, 4);
        unionNaive(parent, 3, 0);
        unionNaive(parent, 5, 0);

        System.out.println("Arreglo parent final (Naive): " + Arrays.toString(parent));

        // Contar los saltos que necesita find(5) SIN path compression:
        // seguir parent[] hasta llegar a un nodo que es su propio padre (raíz)
        int saltos = 0;
        int current = 5;
        while(parent[current] != current) {
            current = parent[current];
            saltos++;
        }
        System.out.println("Saltos necesarios para find(5): " + saltos);
    }

    /** find SIN path compression (para que la traza coincida con el papel). */
    private static int findNaive(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    /** Union naive: cuelga la raíz de x debajo de la raíz de y, sin mirar alturas. */
    private static void unionNaive(int[] parent, int x, int y) {
        int rootX = findNaive(parent, x);
        int rootY = findNaive(parent, y);
        if (rootX != rootY) {
            parent[rootX] = rootY;   // puede crear cadenas largas (peor caso O(n))
        }
    }

    // =========================================================================
    // Ejercicio 10: Union by Rank
    //
    // Enunciado: mismas 5 operaciones del ejercicio 9, ahora con union by
    // rank: el árbol de MENOR rank (altura aproximada) se cuelga del de
    // MAYOR rank; si empatan, gana uno y su rank sube en 1.
    //
    // Traza esperada:
    //   union(1,0): ranks 0==0 → parent[0]=1, rank[1]=1
    //   union(3,2): ranks 0==0 → parent[2]=3, rank[3]=1
    //   union(5,4): ranks 0==0 → parent[4]=5, rank[5]=1
    //   union(3,0): raíces 3 y 1, ranks 1==1 → parent[1]=3, rank[3]=2
    //   union(5,0): raíces 5(r=1) y 3(r=2)  → parent[5]=3 (el bajo cuelga del alto)
    // Final: parent = [1,3,3,3,5,3], rank = [0,1,0,2,0,1].
    // El árbol queda ANCHO y PLANO (altura ≤ log n garantizada).
    // =========================================================================
    public static void ejercicio10_UnionByRank() {
        System.out.println("\n--- Ejercicio 10: Union by Rank ---");
        int n = 6;
        int[] parent = new int[n];
        int[] rank = new int[n];     // rank[i] solo es válido si i es raíz
        for (int i = 0; i < n; i++) parent[i] = i;

        unionRank(parent, rank, 1, 0);
        unionRank(parent, rank, 3, 2);
        unionRank(parent, rank, 5, 4);
        unionRank(parent, rank, 3, 0);
        unionRank(parent, rank, 5, 0);

        System.out.println("Arreglo parent final (Rank): " + Arrays.toString(parent));
        System.out.println("Arreglo rank final:          " + Arrays.toString(rank));
    }

    /** find SIN path compression, para mostrar la traza tal cual el papel. */
    private static int findRank(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    /** Union by rank: el árbol más bajo se cuelga del más alto. */
    private static void unionRank(int[] parent, int[] rank, int x, int y) {
        int rootX = findRank(parent, x);
        int rootY = findRank(parent, y);

        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;   // rootX es más bajo → cuelga de rootY
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;   // rootY es más bajo → cuelga de rootX
            } else {
                parent[rootY] = rootX;   // misma altura: gana rootX...
                rank[rootX]++;           // ...y su altura crece en 1
            }
        }
    }

    // =========================================================================
    // Ejercicio 11: Kruskal con Union-Find
    //
    // Enunciado: grafo NO dirigido con aristas 2-3(4), 0-3(5), 0-2(6),
    // 0-1(10), 1-3(15). Construir el MST trazando parent[] en cada paso.
    //
    // Idea: ordenar las aristas por peso ascendente y tomarlas en orden;
    // una arista u-v se ACEPTA solo si find(u) != find(v) (une dos
    // componentes distintas). Si find(u) == find(v), agregarla cerraría un
    // ciclo → se descarta. Parar al tener V-1 aristas.
    //
    // Resultado esperado: MST = {2-3, 0-3, 0-1}, costo total 19.
    // La arista 0-2 se descarta (ciclo 0-3-2-0) y 1-3 ni se evalúa.
    // =========================================================================
    public static void ejercicio11_Kruskal() {
        System.out.println("\n--- Ejercicio 11: Kruskal con Union-Find ---");
        int V = 4;
        // Cada arista es {u, v, peso}
        int[][] edges = {
            {2, 3, 4},
            {0, 3, 5},
            {0, 2, 6},
            {0, 1, 10},
            {1, 3, 15}
        };

        // Paso 1: ordenar aristas por peso ascendente (aquí ya vienen
        // ordenadas, pero se ordena igual por claridad)
        Arrays.sort(edges, Comparator.comparingInt(e -> e[2]));

        // Union-Find naive para detectar ciclos
        int[] parent = new int[V];
        for (int i = 0; i < V; i++) parent[i] = i;

        List<String> mst = new ArrayList<>();
        int costoTotal = 0;

        // Paso 2: procesar aristas de menor a mayor peso
        for (int[] e : edges) {
            int u = e[0], v = e[1], peso = e[2];
            int ru = findNaive(parent, u);
            int rv = findNaive(parent, v);

            if (ru == rv) {
                // Misma raíz → u y v ya están conectados → la arista
                // formaría un CICLO y se descarta
                System.out.println("Arista " + u + "-" + v + " (" + peso + "): DESCARTADA (formaría ciclo)");
                continue;
            }

            // Raíces distintas → unir componentes y aceptar la arista
            parent[ru] = rv;
            mst.add(u + "-" + v);
            costoTotal += peso;
            System.out.println("Arista " + u + "-" + v + " (" + peso + "): AÑADIDA  → parent = " + Arrays.toString(parent));

            // Paso 3: un árbol de expansión tiene exactamente V-1 aristas
            if (mst.size() == V - 1) break;
        }

        System.out.println("MST: " + mst + " | Costo total: " + costoTotal);
    }
}
