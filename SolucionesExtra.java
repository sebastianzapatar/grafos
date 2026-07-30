import java.util.*;

/**
 * ============================================================================
 * SOLUCIONES DE LOS EJERCICIOS EXTRA (12 – 29) — DFS, BFS y DIJKSTRA
 * ============================================================================
 *
 * Continuación de Soluciones.java (ejercicios 1–11). Aquí se profundiza SOLO
 * en los tres algoritmos centrales del curso, con variantes que aparecen una
 * y otra vez en compiladores y en entrevistas técnicas.
 *
 * Cada método ejercicioN_*() es autocontenido: construye su propio grafo,
 * ejecuta el algoritmo e imprime el resultado que aparece en la solución
 * de la slide / del archivo EJERCICIOS_EXTRA.md.
 *
 * Compilar y ejecutar:
 *   javac SolucionesExtra.java
 *   java SolucionesExtra
 *
 * Contenido:
 *   --- DFS ---
 *   12. Tiempos de descubrimiento/finalización y clasificación de aristas
 *   13. Detección de imports circulares (DFS con 3 colores) + reconstruir ciclo
 *   14. Conteo de islas en una grilla (grafo implícito)
 *   15. ¿Es bipartito? (2-coloreo con DFS)
 *   16. Puntos de articulación y puentes (Tarjan, low-link)
 *   17. Contar TODOS los caminos simples (DFS con backtracking real)
 *
 *   --- BFS ---
 *   18. Camino más corto en un laberinto + reconstrucción de la ruta
 *   19. BFS multi-fuente (propagación de un incendio)
 *   20. Saltos mínimos del caballo de ajedrez (grafo implícito)
 *   21. 0-1 BFS con Deque (peajes gratis / peajes de costo 1)
 *   22. BFS sobre estados numéricos (mínimo de operaciones)
 *   23. Por qué BFS falla con pesos, y cómo "arreglarlo" subdividiendo aristas
 *
 *   --- DIJKSTRA ---
 *   24. Traza completa paso a paso (tabla dist[] / visitado[])
 *   25. Contar cuántos caminos mínimos distintos existen
 *   26. Contraejemplo con peso negativo: Dijkstra vs Bellman-Ford
 *   27. Dijkstra sobre una grilla con costo de terreno
 *   28. Estado extendido: una arista gratis (grafo por capas)
 *   29. Camino de máxima capacidad / cuello de botella (widest path)
 */
public class SolucionesExtra {

    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("   SOLUCIONES EJERCICIOS EXTRA (12 - 29)        ");
        System.out.println("   DFS · BFS · Dijkstra                          ");
        System.out.println("================================================");

        System.out.println("\n############  BLOQUE DFS  ############");
        ejercicio12_TiemposYAristas();
        ejercicio13_ImportsCirculares();
        ejercicio14_Islas();
        ejercicio15_Bipartito();
        ejercicio16_PuntosArticulacion();
        ejercicio17_ContarCaminos();

        System.out.println("\n############  BLOQUE BFS  ############");
        ejercicio18_Laberinto();
        ejercicio19_Incendio();
        ejercicio20_Caballo();
        ejercicio21_Bfs01();
        ejercicio22_EstadosNumericos();
        ejercicio23_BfsConPesos();

        System.out.println("\n############  BLOQUE DIJKSTRA  ############");
        ejercicio24_TrazaDijkstra();
        ejercicio25_ContarCaminosMinimos();
        ejercicio26_PesoNegativo();
        ejercicio27_DijkstraEnGrilla();
        ejercicio28_UnaAristaGratis();
        ejercicio29_CuelloDeBotella();
    }

    // =========================================================================
    // =========================  BLOQUE DFS  ==================================
    // =========================================================================

    // =========================================================================
    // Ejercicio 12: Tiempos de descubrimiento/finalización y tipos de arista
    //
    // Enunciado: grafo dirigido con aristas
    //   0→1, 0→2, 0→3, 1→3, 2→4, 3→0, 4→1, 4→5
    // Recorrer con DFS desde 0 visitando los vecinos en orden ascendente.
    // Anotar d[u] (cuándo se descubre) y f[u] (cuándo se termina), y
    // clasificar cada arista como árbol / retroceso / avance / cruce.
    //
    // Idea: con un reloj que avanza en cada evento y el color del nodo destino:
    //   BLANCO           → arista de ÁRBOL   (se recurre)
    //   GRIS             → arista de RETROCESO (back edge) ⇒ ¡HAY CICLO!
    //   NEGRO y d[u]<d[v]→ arista de AVANCE  (forward)
    //   NEGRO y d[u]>d[v]→ arista de CRUCE   (cross)
    //
    // Resultado esperado:
    //   d/f → 0:(1,12) 1:(2,5) 2:(6,11) 3:(3,4) 4:(7,10) 5:(8,9)
    //   árbol: 0→1, 1→3, 0→2, 2→4, 4→5 | retroceso: 3→0
    //   avance: 0→3 | cruce: 4→1
    // =========================================================================
    private static final int BLANCO = 0, GRIS = 1, NEGRO = 2;

    public static void ejercicio12_TiemposYAristas() {
        System.out.println("\n--- Ejercicio 12: Tiempos DFS y clasificación de aristas ---");
        int V = 6;
        List<List<Integer>> adj = nuevaAdj(V);
        int[][] aristas = {{0,1},{0,2},{0,3},{1,3},{2,4},{3,0},{4,1},{4,5}};
        for (int[] e : aristas) adj.get(e[0]).add(e[1]);
        // Orden ascendente de vecinos: hace que la traza sea REPRODUCIBLE
        for (List<Integer> l : adj) Collections.sort(l);

        int[] d = new int[V];        // instante de descubrimiento (se pinta GRIS)
        int[] f = new int[V];        // instante de finalización  (se pinta NEGRO)
        int[] color = new int[V];    // BLANCO / GRIS / NEGRO
        int[] reloj = {0};           // arreglo de 1 celda = "variable por referencia"
        List<String> clasificacion = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            if (color[i] == BLANCO) dfsTiempos(i, adj, color, d, f, reloj, clasificacion);
        }

        System.out.println("Nodo :  d   f");
        for (int i = 0; i < V; i++) {
            System.out.printf("  %d  : %2d  %2d%n", i, d[i], f[i]);
        }
        System.out.println("Clasificación de aristas:");
        for (String s : clasificacion) System.out.println("   " + s);
        System.out.println("Como existe al menos una arista de RETROCESO → el grafo TIENE ciclo.");
    }

    private static void dfsTiempos(int u, List<List<Integer>> adj, int[] color,
                                   int[] d, int[] f, int[] reloj, List<String> out) {
        color[u] = GRIS;             // u entra a la pila de recursión
        d[u] = ++reloj[0];

        for (int v : adj.get(u)) {
            if (color[v] == BLANCO) {
                out.add(u + "→" + v + "  ÁRBOL");
                dfsTiempos(v, adj, color, d, f, reloj, out);
            } else if (color[v] == GRIS) {
                // v es ancestro nuestro y sigue abierto → volvimos sobre el camino
                out.add(u + "→" + v + "  RETROCESO (back)  ⇒ ciclo");
            } else if (d[u] < d[v]) {
                // v ya terminó pero lo descubrimos DESPUÉS de u → es descendiente
                out.add(u + "→" + v + "  AVANCE (forward)");
            } else {
                // v ya terminó y se descubrió ANTES que u → otra rama
                out.add(u + "→" + v + "  CRUCE (cross)");
            }
        }

        color[u] = NEGRO;            // u sale de la pila de recursión
        f[u] = ++reloj[0];
    }

    // =========================================================================
    // Ejercicio 13: Imports circulares (DFS con 3 colores) + reconstruir ciclo
    //
    // Enunciado: un compilador procesa estos módulos, donde u→v = "u importa a v":
    //   main→parser, parser→lexer, parser→ast, lexer→symtab,
    //   ast→symtab, symtab→semantic, semantic→parser
    // ¿Hay una dependencia circular? ¿Cuál es exactamente el ciclo?
    //
    // Idea: DFS con 3 colores. Si una arista apunta a un nodo GRIS, ese nodo
    // sigue abierto en la pila de recursión → cerramos un ciclo. Con parent[]
    // se retrocede desde el nodo actual hasta el gris para imprimirlo.
    //
    // Resultado esperado: SÍ hay ciclo →
    //   parser → lexer → symtab → semantic → parser
    // =========================================================================
    public static void ejercicio13_ImportsCirculares() {
        System.out.println("\n--- Ejercicio 13: Imports circulares (DFS 3 colores) ---");
        String[] mods = {"main", "parser", "lexer", "ast", "symtab", "semantic"};
        Map<String, Integer> id = new HashMap<>();
        for (int i = 0; i < mods.length; i++) id.put(mods[i], i);

        List<List<Integer>> adj = nuevaAdj(mods.length);
        String[][] imports = {
            {"main", "parser"}, {"parser", "lexer"}, {"parser", "ast"},
            {"lexer", "symtab"}, {"ast", "symtab"},
            {"symtab", "semantic"}, {"semantic", "parser"}
        };
        for (String[] e : imports) adj.get(id.get(e[0])).add(id.get(e[1]));

        int[] color = new int[mods.length];
        int[] padre = new int[mods.length];
        Arrays.fill(padre, -1);
        List<String> ciclo = new ArrayList<>();

        boolean hayCiclo = false;
        for (int i = 0; i < mods.length && !hayCiclo; i++) {
            if (color[i] == BLANCO) hayCiclo = dfsCiclo(i, adj, color, padre, mods, ciclo);
        }

        System.out.println("¿Dependencia circular?: " + (hayCiclo ? "SÍ" : "NO"));
        if (hayCiclo) System.out.println("Ciclo: " + String.join(" → ", ciclo));
    }

    private static boolean dfsCiclo(int u, List<List<Integer>> adj, int[] color,
                                    int[] padre, String[] nombres, List<String> ciclo) {
        color[u] = GRIS;
        for (int v : adj.get(u)) {
            if (color[v] == GRIS) {
                // ¡Back edge! El ciclo son los nodos de la pila desde v hasta u
                List<String> tmp = new ArrayList<>();
                int actual = u;
                while (actual != v) {
                    tmp.add(nombres[actual]);
                    actual = padre[actual];
                }
                tmp.add(nombres[v]);
                Collections.reverse(tmp);
                tmp.add(nombres[v]);      // cerrar el ciclo visualmente
                ciclo.addAll(tmp);
                return true;
            }
            if (color[v] == BLANCO) {
                padre[v] = u;
                if (dfsCiclo(v, adj, color, padre, nombres, ciclo)) return true;
            }
            // color[v] == NEGRO → ya terminó, no puede cerrar ciclo con nosotros
        }
        color[u] = NEGRO;
        return false;
    }

    // =========================================================================
    // Ejercicio 14: Conteo de islas (DFS sobre un grafo IMPLÍCITO)
    //
    // Enunciado: en la grilla, 1 = tierra y 0 = agua. Dos celdas de tierra
    // pertenecen a la misma isla si son vecinas en horizontal o vertical
    // (NO en diagonal). ¿Cuántas islas hay y cuál es la más grande?
    //
    // Idea: es exactamente "componentes conexas", pero el grafo no está en una
    // lista de adyacencia: los vecinos de (i,j) se CALCULAN con los 4
    // desplazamientos. Cada DFS que se lanza sobre tierra sin visitar "hunde"
    // una isla completa.
    //
    // Resultado esperado: 5 islas; la mayor tiene 5 celdas.
    // =========================================================================
    public static void ejercicio14_Islas() {
        System.out.println("\n--- Ejercicio 14: Conteo de islas (DFS en grilla) ---");
        int[][] grid = {
            {1, 1, 0, 0, 1},
            {1, 1, 0, 0, 1},
            {1, 0, 1, 0, 0},
            {0, 0, 0, 1, 1},
            {1, 0, 0, 1, 1}
        };
        int filas = grid.length, cols = grid[0].length;
        boolean[][] visitado = new boolean[filas][cols];

        int islas = 0, mayor = 0;
        List<Integer> tamanos = new ArrayList<>();

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1 && !visitado[i][j]) {
                    islas++;
                    int tam = dfsIsla(grid, visitado, i, j);
                    tamanos.add(tam);
                    mayor = Math.max(mayor, tam);
                }
            }
        }

        System.out.println("Número de islas: " + islas);
        System.out.println("Tamaños (en orden de descubrimiento): " + tamanos);
        System.out.println("Isla más grande: " + mayor + " celdas");
    }

    /** DFS 4-direccional; devuelve cuántas celdas tiene la isla. */
    private static int dfsIsla(int[][] grid, boolean[][] vis, int i, int j) {
        // Corte: fuera de la grilla, agua, o ya visitada
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length) return 0;
        if (grid[i][j] == 0 || vis[i][j]) return 0;

        vis[i][j] = true;
        // 1 (esta celda) + lo que aporten las 4 direcciones
        return 1 + dfsIsla(grid, vis, i - 1, j)
                 + dfsIsla(grid, vis, i + 1, j)
                 + dfsIsla(grid, vis, i, j - 1)
                 + dfsIsla(grid, vis, i, j + 1);
    }

    // =========================================================================
    // Ejercicio 15: ¿Es bipartito? (2-coloreo con DFS)
    //
    // Enunciado: dado el grafo NO dirigido con el ciclo 0-1-2-3-4-5-0,
    // ¿se pueden pintar los nodos con 2 colores sin que dos vecinos compartan
    // color? ¿Y si agregamos la arista 0-2?
    //
    // Idea: DFS pintando cada vecino con el color OPUESTO. Si encontramos un
    // vecino ya pintado del MISMO color, hay conflicto → no es bipartito.
    // Teorema: un grafo es bipartito ⟺ no tiene ciclos de longitud IMPAR.
    //
    // Resultado esperado:
    //   Grafo A (ciclo de 6): SÍ es bipartito → {0,2,4} y {1,3,5}
    //   Grafo B (+ arista 0-2): NO, aparece el triángulo 0-1-2 (ciclo impar)
    // =========================================================================
    public static void ejercicio15_Bipartito() {
        System.out.println("\n--- Ejercicio 15: ¿Es bipartito? (2-coloreo DFS) ---");

        int[][] cicloPar = {{0,1},{1,2},{2,3},{3,4},{4,5},{5,0}};
        probarBipartito("Grafo A (ciclo de longitud 6)", 6, cicloPar);

        int[][] conTriangulo = {{0,1},{1,2},{2,3},{3,4},{4,5},{5,0},{0,2}};
        probarBipartito("Grafo B (mismo + arista 0-2)", 6, conTriangulo);
    }

    private static void probarBipartito(String titulo, int V, int[][] aristas) {
        List<List<Integer>> adj = nuevaAdj(V);
        for (int[] e : aristas) {           // grafo NO dirigido → ambos sentidos
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        int[] colorNodo = new int[V];
        Arrays.fill(colorNodo, -1);         // -1 = sin pintar

        boolean esBipartito = true;
        for (int i = 0; i < V && esBipartito; i++) {
            if (colorNodo[i] == -1) {
                colorNodo[i] = 0;
                esBipartito = dfsColorear(i, adj, colorNodo);
            }
        }

        System.out.println(titulo + " → " + (esBipartito ? "SÍ es bipartito" : "NO es bipartito"));
        if (esBipartito) {
            List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
            for (int i = 0; i < V; i++) (colorNodo[i] == 0 ? a : b).add(i);
            System.out.println("   Conjunto 0: " + a + "   Conjunto 1: " + b);
        }
    }

    /** Devuelve false apenas encuentra dos vecinos del mismo color. */
    private static boolean dfsColorear(int u, List<List<Integer>> adj, int[] color) {
        for (int v : adj.get(u)) {
            if (color[v] == -1) {
                color[v] = 1 - color[u];    // color opuesto al de u
                if (!dfsColorear(v, adj, color)) return false;
            } else if (color[v] == color[u]) {
                return false;               // conflicto → ciclo impar
            }
        }
        return true;
    }

    // =========================================================================
    // Ejercicio 16: Puntos de articulación y puentes (Tarjan / low-link)
    //
    // Enunciado: red de servidores (grafo NO dirigido):
    //   0-1, 1-2, 2-0, 1-3, 3-4, 4-5, 5-3, 3-6
    // ¿Qué servidores, al caerse, DESCONECTAN la red? ¿Qué cables son críticos?
    //
    // Idea: en el árbol DFS, low[u] = el instante de descubrimiento más
    // pequeño alcanzable desde el subárbol de u usando a lo sumo una arista de
    // retroceso. Para un hijo v de u:
    //   low[v] >= disc[u] → u es PUNTO DE ARTICULACIÓN (v no puede "saltarse" u)
    //   low[v] >  disc[u] → la arista u-v es un PUENTE
    // La raíz es articulación solo si tiene MÁS DE UN hijo en el árbol DFS.
    //
    // Resultado esperado: articulación = {1, 3}; puentes = {1-3, 3-6}
    // =========================================================================
    public static void ejercicio16_PuntosArticulacion() {
        System.out.println("\n--- Ejercicio 16: Puntos de articulación y puentes ---");
        int V = 7;
        int[][] aristas = {{0,1},{1,2},{2,0},{1,3},{3,4},{4,5},{5,3},{3,6}};
        List<List<Integer>> adj = nuevaAdj(V);
        for (int[] e : aristas) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        int[] disc = new int[V];     // 0 = aún sin descubrir
        int[] low = new int[V];
        boolean[] esArticulacion = new boolean[V];
        List<String> puentes = new ArrayList<>();
        int[] reloj = {0};

        for (int i = 0; i < V; i++) {
            if (disc[i] == 0) dfsAP(i, -1, adj, disc, low, esArticulacion, puentes, reloj);
        }

        List<Integer> aps = new ArrayList<>();
        for (int i = 0; i < V; i++) if (esArticulacion[i]) aps.add(i);

        System.out.println("disc[] = " + Arrays.toString(disc));
        System.out.println("low[]  = " + Arrays.toString(low));
        System.out.println("Puntos de articulación: " + aps);
        System.out.println("Puentes (aristas críticas): " + puentes);
    }

    private static void dfsAP(int u, int padre, List<List<Integer>> adj, int[] disc, int[] low,
                              boolean[] esAP, List<String> puentes, int[] reloj) {
        disc[u] = low[u] = ++reloj[0];
        int hijos = 0;

        for (int v : adj.get(u)) {
            if (v == padre) continue;                 // no volver por donde vinimos
            if (disc[v] != 0) {
                // v ya descubierto → arista de retroceso: puedo "subir" hasta disc[v]
                low[u] = Math.min(low[u], disc[v]);
            } else {
                hijos++;
                dfsAP(v, u, adj, disc, low, esAP, puentes, reloj);
                low[u] = Math.min(low[u], low[v]);    // heredo lo que alcanzó mi hijo

                if (padre != -1 && low[v] >= disc[u]) esAP[u] = true;
                if (low[v] > disc[u]) puentes.add(u + "-" + v);
            }
        }
        // La raíz es articulación solo si el DFS tuvo que arrancar 2+ ramas
        if (padre == -1 && hijos > 1) esAP[u] = true;
    }

    // =========================================================================
    // Ejercicio 17: Contar TODOS los caminos simples (backtracking real)
    //
    // Enunciado: grafo dirigido 0→1, 0→2, 1→2, 1→3, 2→3, 2→4, 3→4.
    // ¿Cuántos caminos distintos hay de 0 a 4? Listarlos.
    //
    // Idea CLAVE: en el DFS "normal" un nodo se marca visitado y NUNCA se
    // desmarca (queremos recorrer, no repetir). Para enumerar caminos hay que
    // DESMARCARLO al retroceder, porque el mismo nodo puede aparecer en otro
    // camino distinto. Eso es backtracking de verdad.
    //
    // Ojo con la complejidad: enumerar caminos es EXPONENCIAL, no O(V+E).
    //
    // Resultado esperado: 5 caminos.
    // =========================================================================
    public static void ejercicio17_ContarCaminos() {
        System.out.println("\n--- Ejercicio 17: Contar todos los caminos simples ---");
        int V = 5;
        int[][] aristas = {{0,1},{0,2},{1,2},{1,3},{2,3},{2,4},{3,4}};
        List<List<Integer>> adj = nuevaAdj(V);
        for (int[] e : aristas) adj.get(e[0]).add(e[1]);

        boolean[] enCamino = new boolean[V];
        List<Integer> camino = new ArrayList<>();
        List<String> encontrados = new ArrayList<>();

        enCamino[0] = true;
        camino.add(0);
        dfsCaminos(0, 4, adj, enCamino, camino, encontrados);

        System.out.println("Caminos de 0 a 4:");
        for (String c : encontrados) System.out.println("   " + c);
        System.out.println("Total: " + encontrados.size() + " caminos");
    }

    private static void dfsCaminos(int u, int destino, List<List<Integer>> adj,
                                   boolean[] enCamino, List<Integer> camino, List<String> out) {
        if (u == destino) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < camino.size(); i++) {
                if (i > 0) sb.append(" → ");
                sb.append(camino.get(i));
            }
            out.add(sb.toString());
            return;
        }
        for (int v : adj.get(u)) {
            if (!enCamino[v]) {
                enCamino[v] = true;   // entrar
                camino.add(v);

                dfsCaminos(v, destino, adj, enCamino, camino, out);

                camino.remove(camino.size() - 1);
                enCamino[v] = false;  // ¡DESMARCAR! aquí está el backtracking
            }
        }
    }

    // =========================================================================
    // =========================  BLOQUE BFS  ==================================
    // =========================================================================

    // =========================================================================
    // Ejercicio 18: Camino más corto en un laberinto + reconstrucción
    //
    // Enunciado: '.' es libre, '#' es muro, S el inicio y E la salida.
    // Movimientos en 4 direcciones. ¿Cuál es el mínimo de pasos y cuál es la ruta?
    //
    // Idea: BFS sobre la grilla. Como TODOS los movimientos cuestan lo mismo
    // (1 paso), la cola FIFO garantiza que la primera vez que se toca una celda
    // es por el camino más corto. Se guarda prev[][] para reconstruir la ruta.
    //
    // Resultado esperado: 14 pasos.
    // =========================================================================
    public static void ejercicio18_Laberinto() {
        System.out.println("\n--- Ejercicio 18: Laberinto (BFS + reconstrucción) ---");
        String[] mapa = {
            "S..#...",
            ".#.#.#.",
            ".#...#.",
            ".###.#.",
            "....#.E"
        };
        int filas = mapa.length, cols = mapa[0].length();
        char[][] g = new char[filas][];
        for (int i = 0; i < filas; i++) g[i] = mapa[i].toCharArray();

        int[] inicio = null, fin = null;
        for (int i = 0; i < filas; i++)
            for (int j = 0; j < cols; j++) {
                if (g[i][j] == 'S') inicio = new int[]{i, j};
                if (g[i][j] == 'E') fin = new int[]{i, j};
            }

        int[][] dist = new int[filas][cols];
        int[][] prev = new int[filas][cols];   // guardamos el índice plano i*cols+j
        for (int[] fila : dist) Arrays.fill(fila, -1);
        for (int[] fila : prev) Arrays.fill(fila, -1);

        int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
        Queue<int[]> q = new LinkedList<>();
        q.add(inicio);
        dist[inicio[0]][inicio[1]] = 0;

        while (!q.isEmpty()) {
            int[] c = q.poll();
            int x = c[0], y = c[1];
            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k], ny = y + dy[k];
                if (nx < 0 || nx >= filas || ny < 0 || ny >= cols) continue;  // fuera
                if (g[nx][ny] == '#' || dist[nx][ny] != -1) continue;         // muro/visitada
                dist[nx][ny] = dist[x][y] + 1;
                prev[nx][ny] = x * cols + y;
                q.add(new int[]{nx, ny});
            }
        }

        int pasos = dist[fin[0]][fin[1]];
        System.out.println("Mínimo de pasos: " + pasos);

        // Reconstrucción: retroceder desde E hasta S usando prev[][]
        List<String> ruta = new ArrayList<>();
        int cur = fin[0] * cols + fin[1];
        while (cur != -1) {
            int x = cur / cols, y = cur % cols;
            ruta.add("(" + x + "," + y + ")");
            if (g[x][y] != 'S' && g[x][y] != 'E') g[x][y] = '*';  // marcar la ruta
            cur = prev[x][y];
        }
        Collections.reverse(ruta);
        System.out.println("Ruta: " + String.join(" → ", ruta));
        System.out.println("Laberinto con la ruta marcada:");
        for (char[] fila : g) System.out.println("   " + new String(fila));
    }

    // =========================================================================
    // Ejercicio 19: BFS multi-fuente (incendio forestal)
    //
    // Enunciado: en la grilla, 1 = árbol sano, 0 = terreno pelado, 2 = árbol ya
    // en llamas. Cada minuto el fuego pasa a los árboles vecinos (4 direcciones).
    // ¿Cuántos minutos hasta que no quede ningún árbol sano? ¿Sobrevive alguno?
    //
    // Idea: BFS con VARIOS orígenes: se encolan TODOS los focos iniciales con
    // distancia 0. El BFS los expande en paralelo, así que dist[celda] = minuto
    // en que se quema. El resultado es el máximo de esas distancias.
    //
    // Trampa clásica: procesar los focos uno por uno da respuestas mal; deben
    // entrar todos a la cola ANTES de empezar el while.
    //
    // Resultado esperado: 6 minutos, y queda 1 árbol sano — el de la esquina
    // inferior izquierda (3,0), rodeado de terreno pelado.
    // =========================================================================
    public static void ejercicio19_Incendio() {
        System.out.println("\n--- Ejercicio 19: BFS multi-fuente (incendio) ---");
        int[][] bosque = {
            {2, 1, 1, 0, 1},
            {1, 1, 0, 1, 1},
            {0, 1, 1, 1, 0},
            {1, 0, 1, 1, 2}
        };
        int filas = bosque.length, cols = bosque[0].length;

        Queue<int[]> q = new LinkedList<>();
        int sanos = 0;
        // Paso 1: encolar TODOS los focos a la vez (multi-fuente)
        for (int i = 0; i < filas; i++)
            for (int j = 0; j < cols; j++) {
                if (bosque[i][j] == 2) q.add(new int[]{i, j});
                else if (bosque[i][j] == 1) sanos++;
            }

        int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
        int minutos = 0;

        // Paso 2: BFS por NIVELES → cada nivel es un minuto
        while (!q.isEmpty() && sanos > 0) {
            int tamNivel = q.size();     // cuántas celdas arden en este minuto
            for (int n = 0; n < tamNivel; n++) {
                int[] c = q.poll();
                for (int k = 0; k < 4; k++) {
                    int nx = c[0] + dx[k], ny = c[1] + dy[k];
                    if (nx < 0 || nx >= filas || ny < 0 || ny >= cols) continue;
                    if (bosque[nx][ny] != 1) continue;   // pelado o ya quemado
                    bosque[nx][ny] = 2;                  // se quema
                    sanos--;
                    q.add(new int[]{nx, ny});
                }
            }
            minutos++;                   // terminó un minuto completo
        }

        System.out.println("Minutos hasta que deja de propagarse: " + minutos);
        System.out.println("Árboles que NUNCA se queman: " + sanos);
        System.out.println("Estado final del bosque:");
        for (int[] fila : bosque) System.out.println("   " + Arrays.toString(fila));
    }

    // =========================================================================
    // Ejercicio 20: Saltos mínimos del caballo (grafo implícito)
    //
    // Enunciado: en un tablero 8×8, ¿cuántos saltos mínimos necesita un caballo
    // para ir de a1 (0,0) a h8 (7,7)?
    //
    // Idea: el grafo tiene 64 nodos y las aristas son los 8 movimientos en "L".
    // No hay que construir la lista de adyacencia: se genera al vuelo. Como
    // todos los saltos cuestan 1, BFS da el mínimo.
    //
    // Resultado esperado: 6 saltos.
    // =========================================================================
    public static void ejercicio20_Caballo() {
        System.out.println("\n--- Ejercicio 20: Saltos mínimos del caballo ---");
        int N = 8;
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};

        int[][] dist = new int[N][N];
        for (int[] f : dist) Arrays.fill(f, -1);

        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{0, 0});
        dist[0][0] = 0;

        while (!q.isEmpty()) {
            int[] c = q.poll();
            for (int k = 0; k < 8; k++) {
                int nx = c[0] + dx[k], ny = c[1] + dy[k];
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
                if (dist[nx][ny] != -1) continue;
                dist[nx][ny] = dist[c[0]][c[1]] + 1;
                q.add(new int[]{nx, ny});
            }
        }

        System.out.println("Saltos de a1 (0,0) a h8 (7,7): " + dist[7][7]);
        System.out.println("Tabla de distancias desde (0,0):");
        for (int i = N - 1; i >= 0; i--) {          // se imprime como un tablero
            StringBuilder sb = new StringBuilder("   ");
            for (int j = 0; j < N; j++) sb.append(String.format("%2d ", dist[i][j]));
            System.out.println(sb);
        }
    }

    // =========================================================================
    // Ejercicio 21: 0-1 BFS con Deque
    //
    // Enunciado: red de calles donde cada tramo es gratis (peso 0) o cuesta un
    // peaje (peso 1). Aristas dirigidas:
    //   0→1(0) 0→2(1) 1→2(0) 1→3(1) 2→3(0) 2→4(1) 3→4(1) 3→5(1) 4→5(0)
    // ¿Cuál es el peaje mínimo de 0 a 5?
    //
    // Idea: cuando los pesos son SOLO 0 y 1, no hace falta un heap: basta una
    // cola doble (Deque). Al relajar una arista de peso 0 se inserta AL FRENTE
    // (mismo "nivel" de costo) y con peso 1 se inserta AL FINAL (nivel siguiente).
    // Resultado: O(V+E) en vez de O((V+E) log V).
    //
    // Resultado esperado: costo mínimo 0→5 = 1
    // =========================================================================
    public static void ejercicio21_Bfs01() {
        System.out.println("\n--- Ejercicio 21: 0-1 BFS con Deque ---");
        int V = 6;
        int[][] aristas = {
            {0,1,0}, {0,2,1}, {1,2,0}, {1,3,1},
            {2,3,0}, {2,4,1}, {3,4,1}, {3,5,1}, {4,5,0}
        };
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;

        Deque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(0);

        while (!dq.isEmpty()) {
            int u = dq.pollFirst();
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    // peso 0 → mismo nivel → al FRENTE; peso 1 → al FINAL
                    if (w == 0) dq.addFirst(v);
                    else dq.addLast(v);
                }
            }
        }

        System.out.println("Peajes mínimos desde 0: " + Arrays.toString(dist));
        System.out.println("Costo mínimo 0 → 5: " + dist[5] + " peaje(s)");
        System.out.println("Camino de costo 1: 0 →(0) 1 →(0) 2 →(0) 3 →(1) 5");
    }

    // =========================================================================
    // Ejercicio 22: BFS sobre estados numéricos (grafo implícito e infinito)
    //
    // Enunciado: partiendo del número 3 y usando solo dos operaciones,
    //   ×2   y   −1
    // ¿cuál es el mínimo de operaciones para llegar a 10?
    //
    // Idea: cada NÚMERO es un nodo y cada OPERACIÓN es una arista de peso 1.
    // Como todas las operaciones cuestan lo mismo → BFS. El espacio de estados
    // es infinito, así que hay que ACOTARLO (aquí 0..2·objetivo) para que el
    // algoritmo termine.
    //
    // Resultado esperado: 3 operaciones → 3 ×2→ 6 −1→ 5 ×2→ 10
    // =========================================================================
    public static void ejercicio22_EstadosNumericos() {
        System.out.println("\n--- Ejercicio 22: BFS sobre estados numéricos ---");
        int inicio = 3, objetivo = 10;
        int LIMITE = objetivo * 2;                 // cota del espacio de estados

        int[] dist = new int[LIMITE + 1];
        int[] prev = new int[LIMITE + 1];
        Arrays.fill(dist, -1);
        Arrays.fill(prev, -1);

        Queue<Integer> q = new LinkedList<>();
        q.add(inicio);
        dist[inicio] = 0;

        while (!q.isEmpty()) {
            int x = q.poll();
            if (x == objetivo) break;

            // Vecinos = resultados de aplicar cada operación
            int[] vecinos = {x * 2, x - 1};
            for (int y : vecinos) {
                if (y < 0 || y > LIMITE) continue;  // fuera del espacio acotado
                if (dist[y] != -1) continue;        // ya alcanzado con menos pasos
                dist[y] = dist[x] + 1;
                prev[y] = x;
                q.add(y);
            }
        }

        System.out.println("Mínimo de operaciones de " + inicio + " a " + objetivo + ": " + dist[objetivo]);
        List<Integer> ruta = new ArrayList<>();
        for (int cur = objetivo; cur != -1; cur = prev[cur]) ruta.add(cur);
        Collections.reverse(ruta);
        System.out.println("Secuencia: " + ruta);
    }

    // =========================================================================
    // Ejercicio 23: ¿Por qué BFS NO sirve con pesos? (contraejemplo + arreglo)
    //
    // Enunciado: grafo dirigido con pesos
    //   S→T (10),  S→A (1),  A→B (1),  B→T (1)
    // BFS dice que el camino más corto S→T tiene 1 arista. ¿Es el más BARATO?
    //
    // Idea: BFS minimiza NÚMERO DE ARISTAS, no COSTO. Con pesos hay que usar
    // Dijkstra. Truco didáctico: si los pesos son enteros pequeños, se puede
    // SUBDIVIDIR cada arista de peso w en w aristas de peso 1 (con nodos
    // ficticios); en ese grafo BFS sí da la respuesta correcta, pero el grafo
    // crece a O(suma de pesos) → impracticable si los pesos son grandes.
    //
    // Resultado esperado: BFS → 1 arista (costo real 10);
    //                     costo mínimo verdadero = 3 (S→A→B→T)
    // =========================================================================
    public static void ejercicio23_BfsConPesos() {
        System.out.println("\n--- Ejercicio 23: BFS vs pesos (contraejemplo) ---");
        // S=0, A=1, B=2, T=3
        int V = 4;
        int[][] aristas = {{0,3,10}, {0,1,1}, {1,2,1}, {2,3,1}};

        // (a) BFS ignorando pesos → cuenta ARISTAS
        List<List<Integer>> adj = nuevaAdj(V);
        for (int[] e : aristas) adj.get(e[0]).add(e[1]);
        int[] saltos = new int[V];
        Arrays.fill(saltos, -1);
        Queue<Integer> q = new LinkedList<>();
        q.add(0);
        saltos[0] = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : adj.get(u)) if (saltos[v] == -1) { saltos[v] = saltos[u] + 1; q.add(v); }
        }
        System.out.println("BFS (mínimo de ARISTAS) S→T = " + saltos[3] + "  → ruta S→T, costo real 10");

        // (b) Dijkstra respetando pesos → cuenta COSTO
        List<List<int[]>> adjW = new ArrayList<>();
        for (int i = 0; i < V; i++) adjW.add(new ArrayList<>());
        for (int[] e : aristas) adjW.get(e[0]).add(new int[]{e[1], e[2]});
        int[] dist = dijkstraSimple(V, adjW, 0);
        System.out.println("Dijkstra (mínimo COSTO)  S→T = " + dist[3] + "  → ruta S→A→B→T");

        // (c) El "arreglo": subdividir cada arista de peso w en w aristas de peso 1
        int nodosFicticios = 0;
        for (int[] e : aristas) nodosFicticios += e[2] - 1;
        int Vexp = V + nodosFicticios;
        List<List<Integer>> adjExp = nuevaAdj(Vexp);
        int siguiente = V;
        for (int[] e : aristas) {
            int u = e[0], w = e[2];
            for (int k = 1; k < w; k++) {          // cadena de nodos intermedios
                adjExp.get(u).add(siguiente);
                u = siguiente++;
            }
            adjExp.get(u).add(e[1]);
        }
        int[] saltosExp = new int[Vexp];
        Arrays.fill(saltosExp, -1);
        Queue<Integer> q2 = new LinkedList<>();
        q2.add(0);
        saltosExp[0] = 0;
        while (!q2.isEmpty()) {
            int u = q2.poll();
            for (int v : adjExp.get(u)) if (saltosExp[v] == -1) { saltosExp[v] = saltosExp[u] + 1; q2.add(v); }
        }
        System.out.println("BFS sobre el grafo SUBDIVIDIDO (" + Vexp + " nodos) S→T = " + saltosExp[3]
                + "  ✓ coincide con Dijkstra");
        System.out.println("Conclusión: BFS minimiza ARISTAS; para minimizar COSTO se usa Dijkstra.");
    }

    // =========================================================================
    // =======================  BLOQUE DIJKSTRA  ===============================
    // =========================================================================

    // =========================================================================
    // Ejercicio 24: Traza completa de Dijkstra paso a paso
    //
    // Enunciado: grafo dirigido con A=0 … F=5:
    //   A→B(7) A→C(9) A→F(14) B→C(10) B→D(15) C→D(11) C→F(2) D→E(6) F→E(9)
    // Llenar la tabla: en cada iteración, qué nodo se extrae y cómo queda dist[].
    //
    // Idea: en cada paso se saca del heap el nodo NO finalizado con menor
    // distancia tentativa; ese valor ya no cambiará (pesos ≥ 0). Luego se
    // relajan sus aristas salientes.
    //
    // Resultado esperado: A=0 B=7 C=9 D=20 E=20 F=11
    // Camino a E: A→C→F→E (9+2+9 = 20), más barato que A→C→D→E (9+11+6 = 26).
    // =========================================================================
    public static void ejercicio24_TrazaDijkstra() {
        System.out.println("\n--- Ejercicio 24: Traza paso a paso de Dijkstra ---");
        String[] nom = {"A", "B", "C", "D", "E", "F"};
        int V = 6;
        int[][] aristas = {
            {0,1,7}, {0,2,9}, {0,5,14}, {1,2,10},
            {1,3,15}, {2,3,11}, {2,5,2}, {3,4,6}, {5,4,9}
        };
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dist = new int[V];
        int[] prev = new int[V];
        boolean[] listo = new boolean[V];       // "finalizado" / fuera del heap
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{0, 0});

        System.out.println("Paso | Extrae | dist[A B C D E F]");
        int paso = 0;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[0];
            if (listo[u]) continue;             // entrada obsoleta del heap
            listo[u] = true;

            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (!listo[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    pq.add(new int[]{v, dist[v]});
                }
            }
            System.out.printf("  %d  |   %s    | %s%n", ++paso, nom[u], mostrar(dist));
        }

        System.out.println("Distancias finales desde A:");
        for (int i = 0; i < V; i++) System.out.println("   A → " + nom[i] + " = " + dist[i]);
        System.out.println("Camino óptimo hasta E: " + reconstruir(prev, 4, nom));
    }

    // =========================================================================
    // Ejercicio 25: ¿Cuántos caminos mínimos distintos hay?
    //
    // Enunciado: aristas 0→1(1) 0→2(1) 0→3(2) 1→3(1) 2→3(1) 3→4(1) 3→5(2) 4→5(1)
    // Además de la distancia mínima de 0 a 5, ¿cuántos caminos distintos la logran?
    //
    // Idea: se lleva un contador cnt[v]:
    //   - si encontramos una distancia MEJOR a v  → cnt[v] = cnt[u]  (reinicia)
    //   - si encontramos una distancia IGUAL a v  → cnt[v] += cnt[u] (acumula)
    // Es la misma relajación de Dijkstra con un caso extra: el empate.
    //
    // Resultado esperado: dist[5] = 4 con 6 caminos mínimos distintos.
    // =========================================================================
    public static void ejercicio25_ContarCaminosMinimos() {
        System.out.println("\n--- Ejercicio 25: Contar caminos mínimos ---");
        int V = 6;
        int[][] aristas = {{0,1,1},{0,2,1},{0,3,2},{1,3,1},{2,3,1},{3,4,1},{3,5,2},{4,5,1}};
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dist = new int[V];
        long[] cnt = new long[V];               // cuántos caminos mínimos llegan a v
        boolean[] listo = new boolean[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;
        cnt[0] = 1;                             // hay 1 forma de "estar" en el origen

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{0, 0});

        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (listo[u]) continue;
            listo[u] = true;

            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    cnt[v] = cnt[u];            // camino mejor → se reinicia el conteo
                    pq.add(new int[]{v, dist[v]});
                } else if (dist[u] + w == dist[v]) {
                    cnt[v] += cnt[u];           // EMPATE → se suman las formas
                }
            }
        }

        System.out.println("dist[] = " + Arrays.toString(dist));
        System.out.println("cnt[]  = " + Arrays.toString(cnt));
        System.out.println("Distancia mínima 0 → 5: " + dist[5]);
        System.out.println("Cantidad de caminos mínimos 0 → 5: " + cnt[5]);
    }

    // =========================================================================
    // Ejercicio 26: Contraejemplo con peso negativo (Dijkstra vs Bellman-Ford)
    //
    // Enunciado: A→B(2), A→C(3), C→B(−2), B→D(1). No hay ciclo negativo.
    // ¿Qué responde Dijkstra (versión con conjunto de finalizados) y qué
    // responde Bellman-Ford?
    //
    // Idea: Dijkstra da por DEFINITIVA la distancia del nodo que extrae. Aquí
    // extrae B con 2 y lo cierra; después descubre que por C se llegaba con 1,
    // pero ya es tarde: B (y su descendiente D) quedan mal.
    //
    // Matiz importante (¡buena discusión de clase!): la versión "perezosa" de
    // Dijkstra.java, sin arreglo de finalizados, sí corregiría este caso porque
    // vuelve a encolar B. Pero deja de tener garantía: en grafos con muchas
    // aristas negativas puede reprocesar nodos un número exponencial de veces.
    // La garantía la da Bellman-Ford, con O(V·E).
    //
    // Resultado esperado: Dijkstra → B=2, D=3 (INCORRECTO)
    //                     Bellman-Ford → B=1, D=2 (correcto)
    // =========================================================================
    public static void ejercicio26_PesoNegativo() {
        System.out.println("\n--- Ejercicio 26: Peso negativo, Dijkstra vs Bellman-Ford ---");
        String[] nom = {"A", "B", "C", "D"};
        int V = 4;
        int[][] aristas = {{0,1,2}, {0,2,3}, {2,1,-2}, {1,3,1}};

        // (a) Dijkstra CON conjunto de finalizados (la versión clásica del libro)
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dj = new int[V];
        boolean[] listo = new boolean[V];
        Arrays.fill(dj, 999999);
        dj[0] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{0, 0});
        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (listo[u]) continue;
            listo[u] = true;                    // ← aquí se "congela" la distancia
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (!listo[v] && dj[u] + w < dj[v]) {
                    dj[v] = dj[u] + w;
                    pq.add(new int[]{v, dj[v]});
                }
            }
        }

        // (b) Bellman-Ford: V-1 rondas relajando TODAS las aristas
        int[] bf = new int[V];
        Arrays.fill(bf, 999999);
        bf[0] = 0;
        for (int i = 1; i < V; i++)
            for (int[] e : aristas)
                if (bf[e[0]] + e[2] < bf[e[1]]) bf[e[1]] = bf[e[0]] + e[2];

        System.out.println("Nodo | Dijkstra | Bellman-Ford");
        for (int i = 0; i < V; i++) {
            String marca = (dj[i] != bf[i]) ? "   ← ¡DIFERENTE!" : "";
            System.out.printf("  %s  |    %2d    |     %2d%s%n", nom[i], dj[i], bf[i], marca);
        }
        System.out.println("Dijkstra cierra B en 2 y nunca revisa que por C se llega en 1.");
    }

    // =========================================================================
    // Ejercicio 27: Dijkstra sobre una grilla con costo de terreno
    //
    // Enunciado: cada celda tiene un costo de ATRAVESARLA. Ir de (0,0) a (3,3)
    // moviéndose en 4 direcciones, minimizando la suma de los costos de las
    // celdas pisadas (incluidas la inicial y la final).
    //
    //   1 3 1 2
    //   2 8 4 1
    //   5 1 1 3
    //   2 2 1 1
    //
    // Idea: es Dijkstra puro; el "peso de la arista u→v" es el costo de ENTRAR
    // a la celda v. Nótese que BFS aquí NO sirve: los movimientos no cuestan
    // todos lo mismo.
    //
    // Resultado esperado: costo 12 bajando por la columna izquierda
    // (0,0)→(1,0)→(2,0)→(2,1)→(2,2)→(3,2)→(3,3) = 1+2+5+1+1+1+1.
    // Ojo: la ruta por el borde superior también cuesta 12 (hay empate);
    // lo caro es cortar por el centro pisando la celda de costo 8 → 16.
    // =========================================================================
    public static void ejercicio27_DijkstraEnGrilla() {
        System.out.println("\n--- Ejercicio 27: Dijkstra en grilla con terreno ---");
        int[][] costo = {
            {1, 3, 1, 2},
            {2, 8, 4, 1},
            {5, 1, 1, 3},
            {2, 2, 1, 1}
        };
        int n = costo.length, m = costo[0].length;

        int[][] dist = new int[n][m];
        int[][] prev = new int[n][m];
        for (int[] f : dist) Arrays.fill(f, Integer.MAX_VALUE);
        for (int[] f : prev) Arrays.fill(f, -1);
        dist[0][0] = costo[0][0];               // pisar la celda inicial ya cuesta

        // {fila, columna, distancia}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.add(new int[]{0, 0, dist[0][0]});
        int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};

        while (!pq.isEmpty()) {
            int[] c = pq.poll();
            int x = c[0], y = c[1], d = c[2];
            if (d > dist[x][y]) continue;       // entrada obsoleta

            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k], ny = y + dy[k];
                if (nx < 0 || nx >= n || ny < 0 || ny >= m) continue;
                int nd = d + costo[nx][ny];     // el peso es el costo de la celda destino
                if (nd < dist[nx][ny]) {
                    dist[nx][ny] = nd;
                    prev[nx][ny] = x * m + y;
                    pq.add(new int[]{nx, ny, nd});
                }
            }
        }

        System.out.println("Costo mínimo de (0,0) a (3,3): " + dist[n-1][m-1]);
        List<String> ruta = new ArrayList<>();
        for (int cur = (n-1) * m + (m-1); cur != -1; ) {
            int x = cur / m, y = cur % m;
            ruta.add("(" + x + "," + y + ")=" + costo[x][y]);
            cur = prev[x][y];
        }
        Collections.reverse(ruta);
        System.out.println("Ruta: " + String.join(" → ", ruta));
        System.out.println("Matriz de costos acumulados:");
        for (int[] f : dist) System.out.println("   " + Arrays.toString(f));
    }

    // =========================================================================
    // Ejercicio 28: Estado extendido — una arista GRATIS (grafo por capas)
    //
    // Enunciado: aristas 0→1(5) 0→2(2) 2→1(1) 1→3(1) 2→3(9) 3→4(4) 1→4(10).
    // Se puede usar UN cupón que vuelve gratis una sola arista del camino.
    // ¿Cuál es el costo mínimo de 0 a 4 con y sin cupón?
    //
    // Idea (patrón muy reutilizable): el nodo del grafo ya no es "el lugar",
    // sino el PAR (lugar, cupón usado). Se duplica el grafo en 2 capas:
    //   capa 0 = cupón disponible, capa 1 = cupón gastado
    //   arista normal: (u,k) → (v,k) con peso w
    //   usar cupón:    (u,0) → (v,1) con peso 0
    // Sobre ese grafo de 2V nodos se corre Dijkstra tal cual.
    //
    // Resultado esperado: sin cupón = 8 (0→2→1→3→4);
    //                     con cupón = 3 (0→2→1 y el cupón en la arista 1→4).
    // Es contraintuitivo: conviene ir a 1 (costo 3) y quemar el cupón en la
    // arista de peso 10, en vez de seguir la ruta barata y ahorrar solo 4.
    // =========================================================================
    public static void ejercicio28_UnaAristaGratis() {
        System.out.println("\n--- Ejercicio 28: Una arista gratis (grafo por capas) ---");
        int V = 5;
        int[][] aristas = {{0,1,5},{0,2,2},{2,1,1},{1,3,1},{2,3,9},{3,4,4},{1,4,10}};
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] sinCupon = dijkstraSimple(V, adj, 0);

        // dist[nodo][cupónUsado]
        int[][] dist = new int[V][2];
        for (int[] f : dist) Arrays.fill(f, Integer.MAX_VALUE);
        dist[0][0] = 0;

        // {nodo, cupónUsado, distancia}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.add(new int[]{0, 0, 0});

        while (!pq.isEmpty()) {
            int[] c = pq.poll();
            int u = c[0], k = c[1], d = c[2];
            if (d > dist[u][k]) continue;

            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];

                // (1) pagar la arista → me quedo en la misma capa
                if (d + w < dist[v][k]) {
                    dist[v][k] = d + w;
                    pq.add(new int[]{v, k, dist[v][k]});
                }
                // (2) gastar el cupón → salto a la capa 1 con costo 0
                if (k == 0 && d < dist[v][1]) {
                    dist[v][1] = d;
                    pq.add(new int[]{v, 1, dist[v][1]});
                }
            }
        }

        int conCupon = Math.min(dist[4][0], dist[4][1]);
        System.out.println("Costo mínimo 0 → 4 SIN cupón: " + sinCupon[4]);
        System.out.println("Costo mínimo 0 → 4 CON cupón: " + conCupon);
        System.out.println("dist[nodo][0] (cupón intacto) = " + mostrarCapa(dist, 0));
        System.out.println("dist[nodo][1] (cupón gastado) = " + mostrarCapa(dist, 1));
        System.out.println("Mejor jugada: 0→2(2) 2→1(1) y gastar el cupón en 1→4 (ahorra 10).");
    }

    // =========================================================================
    // Ejercicio 29: Camino de máxima capacidad (widest path / cuello de botella)
    //
    // Enunciado: red de tuberías NO dirigida con capacidades
    //   0-1(10) 0-2(6) 1-2(8) 1-3(3) 2-3(7) 3-4(5) 2-4(4)
    // Queremos la ruta de 0 a 4 que MAXIMICE el mínimo de sus capacidades
    // (el "cuello de botella" del camino).
    //
    // Idea: Dijkstra con dos cambios:
    //   - la cola de prioridad es un MAX-heap
    //   - la relajación deja de ser suma y pasa a ser:
    //         nueva = min(cap[u], peso(u,v))     y se acepta si nueva > cap[v]
    // Todo lo demás (el argumento greedy) es idéntico.
    //
    // Resultado esperado: capacidad máxima 0→4 = 5.
    // La ruta que encuentra el algoritmo es 0-1-2-3-4 → min(10,8,7,5) = 5.
    // (0-2-3-4 también da 5; hay empate y cualquiera de las dos sirve.)
    // =========================================================================
    public static void ejercicio29_CuelloDeBotella() {
        System.out.println("\n--- Ejercicio 29: Camino de máxima capacidad ---");
        int V = 5;
        int[][] aristas = {{0,1,10},{0,2,6},{1,2,8},{1,3,3},{2,3,7},{3,4,5},{2,4,4}};
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : aristas) {                       // NO dirigido
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }

        int[] cap = new int[V];
        int[] prev = new int[V];
        Arrays.fill(cap, 0);                            // 0 = aún inalcanzable
        Arrays.fill(prev, -1);
        cap[0] = Integer.MAX_VALUE;                     // desde el origen, sin límite

        // MAX-heap: primero el nodo con MAYOR capacidad garantizada
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[1] - a[1]);
        pq.add(new int[]{0, Integer.MAX_VALUE});

        while (!pq.isEmpty()) {
            int[] c = pq.poll();
            int u = c[0];
            if (c[1] < cap[u]) continue;                // entrada obsoleta

            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                int nueva = Math.min(cap[u], w);        // el cuello de botella manda
                if (nueva > cap[v]) {
                    cap[v] = nueva;
                    prev[v] = u;
                    pq.add(new int[]{v, nueva});
                }
            }
        }

        System.out.println("Capacidad máxima garantizada hasta cada nodo:");
        for (int i = 0; i < V; i++)
            System.out.println("   0 → " + i + " : " + (cap[i] == Integer.MAX_VALUE ? "∞" : cap[i]));
        System.out.println("Cuello de botella máximo 0 → 4 = " + cap[4]);
        List<Integer> ruta = new ArrayList<>();
        for (int cur = 4; cur != -1; cur = prev[cur]) ruta.add(cur);
        Collections.reverse(ruta);
        System.out.println("Ruta: " + ruta);
    }

    // =========================================================================
    // ============================  UTILIDADES  ===============================
    // =========================================================================

    /** Crea una lista de adyacencia vacía con V listas. */
    private static List<List<Integer>> nuevaAdj(int V) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        return adj;
    }

    /** Dijkstra clásico (versión perezosa) que solo devuelve el arreglo dist[]. */
    private static int[] dijkstraSimple(int V, List<List<int[]>> adj, int origen) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[origen] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{origen, 0});
        while (!pq.isEmpty()) {
            int[] c = pq.poll();
            int u = c[0], d = c[1];
            if (d > dist[u]) continue;
            for (int[] e : adj.get(u)) {
                if (dist[u] + e[1] < dist[e[0]]) {
                    dist[e[0]] = dist[u] + e[1];
                    pq.add(new int[]{e[0], dist[e[0]]});
                }
            }
        }
        return dist;
    }

    /** Imprime dist[] usando ∞ en vez de Integer.MAX_VALUE. */
    private static String mostrar(int[] dist) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dist.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(dist[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(dist[i]));
        }
        return sb.append("]").toString();
    }

    /** Imprime una capa de la matriz dist[nodo][capa]. */
    private static String mostrarCapa(int[][] dist, int capa) {
        int[] fila = new int[dist.length];
        for (int i = 0; i < dist.length; i++) fila[i] = dist[i][capa];
        return mostrar(fila);
    }

    /** Reconstruye el camino origen→destino siguiendo prev[] hacia atrás. */
    private static String reconstruir(int[] prev, int destino, String[] nombres) {
        List<String> camino = new ArrayList<>();
        for (int cur = destino; cur != -1; cur = prev[cur]) camino.add(nombres[cur]);
        Collections.reverse(camino);
        return String.join(" → ", camino);
    }
}
