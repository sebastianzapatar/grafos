import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ============================================================================
 * SOLUCIONES DE LOS EJERCICIOS DE JUECES ONLINE (30 – 35) — DFS y BFS
 * ============================================================================
 *
 * Continuación de Soluciones.java (ejercicios 1–11) y SolucionesExtra.java
 * (ejercicios 12–29). Aquí los enunciados NO son inventados: son problemas
 * reales de CSES, Codeforces y HackerRank que se resuelven con DFS o BFS.
 *
 * Cada método resolverN_*() es la función que se enviaría al juez (recibe la
 * entrada ya parseada y devuelve la respuesta), y cada ejercicioN_*() la
 * ejecuta con el caso de ejemplo OFICIAL del enunciado y compara contra la
 * salida esperada. Si todo está bien, el programa imprime solo [OK].
 *
 * Compilar y ejecutar:
 *   javac SolucionesJueces.java
 *   java SolucionesJueces
 *
 * Contenido:
 *   --- DFS ---
 *   30. CSES 1192 · Counting Rooms      — flood fill en grilla (DFS iterativo)
 *   32. CF 977E   · Cyclic Components   — componente = ciclo ⟺ todo grado 2
 *   35. HR        · Roads and Libraries — componentes conexas + costo mínimo
 *
 *   --- BFS ---
 *   31. CSES 1193 · Labyrinth           — camino mínimo + reconstrucción
 *   33. CF 520B   · Two Buttons         — BFS sobre grafo implícito de estados
 *   34. HR        · BFS: Shortest Reach — distancias desde una fuente (×6)
 *
 * NOTA sobre entrada/salida: en los jueces hay que leer de stdin. Con
 * n, m ≤ 1000 (grillas de 10^6 celdas) o n ≤ 2·10^5, Scanner es demasiado
 * lento: se usa BufferedReader + StringBuilder (ver leerGrilla() al final).
 */
public class SolucionesJueces {

    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("   EJERCICIOS DE JUECES ONLINE (30 - 35)        ");
        System.out.println("   CSES · Codeforces · HackerRank               ");
        System.out.println("================================================");

        System.out.println("\n############  BLOQUE DFS  ############");
        ejercicio30_CountingRooms();
        ejercicio32_CyclicComponents();
        ejercicio35_RoadsAndLibraries();

        System.out.println("\n############  BLOQUE BFS  ############");
        ejercicio31_Labyrinth();
        ejercicio33_TwoButtons();
        ejercicio34_ShortestReach();

        System.out.println("\n================================================");
        System.out.println(fallos == 0
                ? "   TODOS LOS CASOS DE EJEMPLO PASARON [OK]"
                : "   HAY " + fallos + " CASO(S) QUE FALLAN [ERROR]");
        System.out.println("================================================");
    }

    // =========================================================================
    // =========================  BLOQUE DFS  ==================================
    // =========================================================================

    // =========================================================================
    // Ejercicio 30 — CSES 1192 "Counting Rooms"
    // https://cses.fi/problemset/task/1192
    //
    // Enunciado: mapa de un edificio de n × m casillas, '.' = piso y '#' = pared.
    // Desde una casilla de piso se puede mover arriba/abajo/izquierda/derecha.
    // ¿Cuántas habitaciones (regiones de piso conectadas) hay?
    // Restricciones: 1 ≤ n, m ≤ 1000  →  hasta 10^6 casillas.
    //
    // Idea: la grilla ES un grafo implícito (nodo = casilla de piso, arista =
    // casillas vecinas). Contar habitaciones = contar componentes conexas:
    // recorrer todas las casillas y, al encontrar piso sin visitar, lanzar un
    // flood fill que marca toda su componente.
    //
    // ⚠️ Trampa clásica de Java: un DFS RECURSIVO sobre 10^6 celdas desborda la
    // pila (StackOverflowError) porque una habitación en zigzag puede tener
    // profundidad 10^6. Por eso aquí el DFS usa una pila EXPLÍCITA (ArrayDeque).
    // Un BFS con cola funcionaría igual de bien: el orden no importa, solo
    // importa "tocar" toda la componente.
    //
    // Complejidad: O(n·m) tiempo y O(n·m) memoria.
    // =========================================================================
    public static void ejercicio30_CountingRooms() {
        System.out.println("\n--- Ej 30 · CSES 1192 Counting Rooms (DFS flood fill) ---");
        String[] mapa = {
                "########",
                "#..#...#",
                "####.#.#",
                "#..#...#",
                "########"
        };
        int obtenido = resolver30_CountingRooms(aGrilla(mapa));
        verificar("CSES 1192 (ejemplo oficial)", "3", String.valueOf(obtenido));
    }

    /** Devuelve el número de componentes conexas de casillas '.' en la grilla. */
    public static int resolver30_CountingRooms(char[][] g) {
        int n = g.length, m = g[0].length;
        boolean[][] visitado = new boolean[n][m];
        // Movimientos: abajo, arriba, derecha, izquierda
        int[] di = {1, -1, 0, 0};
        int[] dj = {0, 0, 1, -1};

        int habitaciones = 0;
        Deque<int[]> pila = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (g[i][j] == '#' || visitado[i][j]) continue;

                // Casilla de piso nunca vista → empieza una habitación nueva
                habitaciones++;
                visitado[i][j] = true;
                pila.push(new int[]{i, j});

                // DFS iterativo: consume toda la componente antes de seguir
                while (!pila.isEmpty()) {
                    int[] actual = pila.pop();
                    for (int d = 0; d < 4; d++) {
                        int ni = actual[0] + di[d];
                        int nj = actual[1] + dj[d];
                        if (ni < 0 || ni >= n || nj < 0 || nj >= m) continue;
                        if (visitado[ni][nj] || g[ni][nj] == '#') continue;
                        // Se marca AL APILAR (no al desapilar): evita que una
                        // misma casilla entre varias veces a la pila.
                        visitado[ni][nj] = true;
                        pila.push(new int[]{ni, nj});
                    }
                }
            }
        }
        return habitaciones;
    }

    // =========================================================================
    // Ejercicio 32 — Codeforces 977E "Cyclic Components"  (rating 1500)
    // https://codeforces.com/problemset/problem/977/E
    //
    // Enunciado: grafo no dirigido de n vértices y m aristas (sin bucles ni
    // aristas repetidas). Contar cuántas componentes conexas son exactamente
    // un ciclo: v1—v2—...—vk—v1, sin ninguna arista extra, con k ≥ 3.
    // Restricciones: 1 ≤ n ≤ 2·10^5, 0 ≤ m ≤ 2·10^5.
    //
    // Idea (la clave del problema): NO hay que buscar el ciclo. Una componente
    // conexa es un ciclo simple ⟺ TODOS sus vértices tienen grado exactamente 2.
    //   · grado 1 → hay una "cola" colgando, no es ciclo
    //   · grado ≥ 3 → hay una rama o una cuerda, no es ciclo
    //   · si todos tienen grado 2 y la componente es conexa, necesariamente
    //     es un único ciclo (aristas = vértices, cada uno con dos vecinos).
    // Entonces: un DFS por componente + verificar el grado de cada vértice.
    //
    // Complejidad: O(n + m).
    // =========================================================================
    public static void ejercicio32_CyclicComponents() {
        System.out.println("\n--- Ej 32 · CF 977E Cyclic Components (DFS + grados) ---");

        int[][] aristas1 = {{1, 2}, {3, 4}, {5, 4}, {3, 5}};
        verificar("CF 977E (ejemplo 1)", "1",
                String.valueOf(resolver32_CyclicComponents(5, aristas1)));

        int[][] aristas2 = {
                {1, 8}, {1, 12}, {5, 11}, {11, 9}, {9, 15}, {15, 5}, {4, 13},
                {3, 13}, {4, 3}, {10, 16}, {7, 10}, {16, 7}, {14, 3}, {14, 4}, {17, 6}
        };
        verificar("CF 977E (ejemplo 2)", "2",
                String.valueOf(resolver32_CyclicComponents(17, aristas2)));
    }

    /** Cuenta las componentes conexas que son exactamente un ciclo simple. */
    public static int resolver32_CyclicComponents(int n, int[][] aristas) {
        List<List<Integer>> adj = nuevaAdj(n + 1);   // vértices 1..n
        int[] grado = new int[n + 1];
        for (int[] e : aristas) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
            grado[e[0]]++;
            grado[e[1]]++;
        }

        boolean[] visitado = new boolean[n + 1];
        int ciclos = 0;

        for (int inicio = 1; inicio <= n; inicio++) {
            if (visitado[inicio]) continue;

            // DFS iterativo sobre la componente de 'inicio'
            boolean esCiclo = true;
            Deque<Integer> pila = new ArrayDeque<>();
            visitado[inicio] = true;
            pila.push(inicio);

            while (!pila.isEmpty()) {
                int u = pila.pop();
                if (grado[u] != 2) esCiclo = false;   // basta UNO para descartar
                for (int v : adj.get(u)) {
                    if (!visitado[v]) {
                        visitado[v] = true;
                        pila.push(v);
                    }
                }
            }
            if (esCiclo) ciclos++;
        }
        return ciclos;
    }

    // =========================================================================
    // Ejercicio 35 — HackerRank "Roads and Libraries"
    // https://www.hackerrank.com/challenges/torque-and-development/problem
    //
    // Enunciado: n ciudades sin conexiones. Se puede construir una biblioteca
    // en una ciudad (costo c_lib) y carreteras bidireccionales entre los pares
    // de ciudades de la lista (costo c_road cada una). Todo ciudadano debe
    // poder llegar a una biblioteca. Costo mínimo total.
    // Restricciones típicas: 1 ≤ n ≤ 10^5, m ≤ 10^5, costos ≤ 10^5.
    //
    // Idea: primero el caso trivial — si c_lib ≤ c_road nunca conviene una
    // carretera: una biblioteca por ciudad, costo n·c_lib.
    // Si no, dentro de cada componente conexa basta 1 biblioteca y conectar
    // sus k vértices cuesta (k−1) carreteras (un árbol de expansión; el grafo
    // no es ponderado, así que cualquier árbol sirve — no hace falta Kruskal).
    // Con C componentes y n vértices en total:
    //        costo = C·c_lib + (n − C)·c_road
    // Las componentes se cuentan con un DFS por vértice no visitado.
    //
    // ⚠️ Usar long: 10^5 ciudades × 10^5 de costo = 10^10 desborda un int.
    //
    // Complejidad: O(n + m).
    // =========================================================================
    public static void ejercicio35_RoadsAndLibraries() {
        System.out.println("\n--- Ej 35 · HR Roads and Libraries (componentes DFS) ---");

        int[][] ciudades1 = {{1, 2}, {3, 1}, {2, 3}};
        verificar("HR Roads and Libraries (consulta 1)", "4",
                String.valueOf(resolver35_RoadsAndLibraries(3, 2, 1, ciudades1)));

        int[][] ciudades2 = {{1, 3}, {3, 4}, {2, 4}, {1, 2}, {2, 3}, {5, 6}};
        verificar("HR Roads and Libraries (consulta 2)", "12",
                String.valueOf(resolver35_RoadsAndLibraries(6, 2, 5, ciudades2)));
    }

    /** Costo mínimo para que toda ciudad tenga acceso a una biblioteca. */
    public static long resolver35_RoadsAndLibraries(int n, long cLib, long cRoad, int[][] ciudades) {
        // Caso 1: la biblioteca cuesta lo mismo o menos que la carretera
        if (cLib <= cRoad) return (long) n * cLib;

        // Caso 2: conviene conectar → contar componentes conexas con DFS
        List<List<Integer>> adj = nuevaAdj(n + 1);
        for (int[] c : ciudades) {
            adj.get(c[0]).add(c[1]);
            adj.get(c[1]).add(c[0]);
        }

        boolean[] visitado = new boolean[n + 1];
        long componentes = 0;

        for (int inicio = 1; inicio <= n; inicio++) {
            if (visitado[inicio]) continue;
            componentes++;
            Deque<Integer> pila = new ArrayDeque<>();
            visitado[inicio] = true;
            pila.push(inicio);
            while (!pila.isEmpty()) {
                int u = pila.pop();
                for (int v : adj.get(u)) {
                    if (!visitado[v]) {
                        visitado[v] = true;
                        pila.push(v);
                    }
                }
            }
        }
        // 1 biblioteca por componente + (n − C) carreteras para conectarlas
        return componentes * cLib + (n - componentes) * cRoad;
    }

    // =========================================================================
    // =========================  BLOQUE BFS  ==================================
    // =========================================================================

    // =========================================================================
    // Ejercicio 31 — CSES 1193 "Labyrinth"
    // https://cses.fi/problemset/task/1193
    //
    // Enunciado: laberinto de n × m con '#' pared, '.' piso, 'A' inicio y 'B'
    // final. Encontrar un camino más corto de A a B moviéndose en las 4
    // direcciones. Imprimir "YES", su longitud y la secuencia de movimientos
    // (L/R/U/D); si no existe, "NO".
    // Restricciones: 1 ≤ n, m ≤ 1000.
    //
    // Idea: grafo implícito NO ponderado (cada movimiento cuesta 1) → BFS da la
    // distancia mínima. Lo nuevo frente a Counting Rooms es RECONSTRUIR el
    // camino: al visitar una celda se guarda de dónde se llegó (padre) y con
    // qué letra de movimiento. Al final se sube desde B hasta A y se invierte.
    //
    // ⚠️ Aquí NO sirve DFS: encuentra "un" camino, pero no el más corto.
    // ⚠️ El juez acepta CUALQUIER camino de longitud mínima; la letra concreta
    //    depende del orden en que se exploran los vecinos.
    //
    // Complejidad: O(n·m).
    // =========================================================================
    public static void ejercicio31_Labyrinth() {
        System.out.println("\n--- Ej 31 · CSES 1193 Labyrinth (BFS + reconstrucción) ---");
        String[] mapa = {
                "########",
                "#.A#...#",
                "#.##.#B#",
                "#......#",
                "########"
        };
        String[] r = resolver31_Labyrinth(aGrilla(mapa));
        System.out.println("    Salida del programa: " + String.join(" | ", r));
        System.out.println("    (el enunciado muestra YES / 9 / LDDRRRRRU; el juez");
        System.out.println("     acepta cualquier camino de longitud mínima)");
        verificar("CSES 1193 · existe camino", "YES", r[0]);
        verificar("CSES 1193 · longitud mínima", "9", r[1]);
        verificar("CSES 1193 · el camino es válido", "válido",
                validarCamino(aGrilla(mapa), r[2]) ? "válido" : "INVÁLIDO");
    }

    /** Devuelve {"NO"} o {"YES", longitud, movimientos}. */
    public static String[] resolver31_Labyrinth(char[][] g) {
        int n = g.length, m = g[0].length;
        int inicio = -1, fin = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (g[i][j] == 'A') inicio = i * m + j;   // celda → id de nodo
                if (g[i][j] == 'B') fin = i * m + j;
            }
        }

        int[] di = {1, -1, 0, 0};
        int[] dj = {0, 0, 1, -1};
        char[] letra = {'D', 'U', 'R', 'L'};   // misma posición que di/dj

        int[] padre = new int[n * m];          // de qué celda vengo
        char[] movimiento = new char[n * m];   // con qué letra llegué
        boolean[] visitado = new boolean[n * m];
        Arrays.fill(padre, -1);

        Queue<Integer> cola = new ArrayDeque<>();
        visitado[inicio] = true;
        cola.add(inicio);

        while (!cola.isEmpty()) {
            int u = cola.poll();
            if (u == fin) break;               // BFS: la primera vez que se saca B ya es óptimo
            int i = u / m, j = u % m;
            for (int d = 0; d < 4; d++) {
                int ni = i + di[d], nj = j + dj[d];
                if (ni < 0 || ni >= n || nj < 0 || nj >= m) continue;
                if (g[ni][nj] == '#') continue;
                int v = ni * m + nj;
                if (visitado[v]) continue;
                visitado[v] = true;
                padre[v] = u;
                movimiento[v] = letra[d];
                cola.add(v);
            }
        }

        if (!visitado[fin]) return new String[]{"NO"};

        // Reconstrucción: de B hacia A siguiendo padre[], y se invierte
        StringBuilder camino = new StringBuilder();
        for (int actual = fin; actual != inicio; actual = padre[actual]) {
            camino.append(movimiento[actual]);
        }
        camino.reverse();
        return new String[]{"YES", String.valueOf(camino.length()), camino.toString()};
    }

    // =========================================================================
    // Ejercicio 33 — Codeforces 520B "Two Buttons"  (rating 1400)
    // https://codeforces.com/problemset/problem/520/B
    //
    // Enunciado: la pantalla muestra n. El botón rojo multiplica por 2, el azul
    // resta 1 (si el número deja de ser positivo el aparato se rompe).
    // Mínimo número de pulsaciones para llegar de n a m.
    // Restricciones: 1 ≤ n, m ≤ 10^4.
    //
    // Idea: aquí NO hay un grafo dado — hay que verlo. Nodo = número en la
    // pantalla, arista = una pulsación (x → 2x, x → x−1). Todas las aristas
    // cuestan 1 ⇒ BFS da el mínimo de pulsaciones.
    // La única sutileza es acotar el universo de estados: pasarse de 2m nunca
    // ayuda (desde ahí solo se puede restar de a 1, y bajar desde 2m hasta m
    // cuesta lo mismo que haber duplicado antes). Con la cota 2m el grafo tiene
    // O(m) nodos.
    //
    // Complejidad: O(m). Abajo se incluye además la solución greedy inversa,
    // O(log m), que es la que se suele publicar en el editorial.
    // =========================================================================
    public static void ejercicio33_TwoButtons() {
        System.out.println("\n--- Ej 33 · CF 520B Two Buttons (BFS sobre estados) ---");

        verificar("CF 520B (ejemplo 1: n=4, m=6) BFS", "2",
                String.valueOf(resolver33_TwoButtonsBfs(4, 6)));
        verificar("CF 520B (ejemplo 2: n=10, m=1) BFS", "9",
                String.valueOf(resolver33_TwoButtonsBfs(10, 1)));

        // El greedy inverso debe coincidir con el BFS en TODO el rango legal
        boolean coinciden = true;
        for (int a = 1; a <= 300 && coinciden; a++) {
            for (int b = 1; b <= 300; b++) {
                if (a == b) continue;   // el enunciado garantiza n ≠ m
                if (resolver33_TwoButtonsBfs(a, b) != resolver33_TwoButtonsGreedy(a, b)) {
                    coinciden = false;
                    System.out.println("    discrepancia en n=" + a + " m=" + b);
                    break;
                }
            }
        }
        verificar("CF 520B · BFS == greedy en 1..300", "true", String.valueOf(coinciden));
    }

    /** BFS sobre el grafo implícito de estados. Devuelve el mínimo de pulsaciones. */
    public static int resolver33_TwoButtonsBfs(int n, int m) {
        // Pasarse de 2m nunca sirve → ese es el tamaño del grafo
        int limite = Math.max(n, 2 * m);
        int[] dist = new int[limite + 1];
        Arrays.fill(dist, -1);

        Queue<Integer> cola = new ArrayDeque<>();
        dist[n] = 0;
        cola.add(n);

        while (!cola.isEmpty()) {
            int x = cola.poll();
            if (x == m) return dist[x];

            // Botón rojo: x → 2x
            if (2L * x <= limite && dist[2 * x] == -1) {
                dist[2 * x] = dist[x] + 1;
                cola.add(2 * x);
            }
            // Botón azul: x → x−1 (el número debe seguir siendo positivo)
            if (x - 1 >= 1 && dist[x - 1] == -1) {
                dist[x - 1] = dist[x] + 1;
                cola.add(x - 1);
            }
        }
        return dist[m];
    }

    /**
     * Misma respuesta en O(log m): se va de m HACIA n deshaciendo pulsaciones.
     * Si m > n: si m es impar la última pulsación tuvo que ser azul (m+1),
     * si es par lo más barato es deshacer una duplicación (m/2).
     * Cuando m ≤ n solo quedan restas: n − m pulsaciones.
     */
    public static int resolver33_TwoButtonsGreedy(int n, int m) {
        int pasos = 0;
        while (m > n) {
            if (m % 2 == 1) m++;      // deshacer un "restar 1"
            else m /= 2;              // deshacer un "multiplicar por 2"
            pasos++;
        }
        return pasos + (n - m);
    }

    // =========================================================================
    // Ejercicio 34 — HackerRank "BFS: Shortest Reach in a Graph"
    // https://www.hackerrank.com/challenges/bfsshortreach/problem
    //
    // Enunciado: grafo no dirigido donde CADA arista pesa 6. Dado un nodo s,
    // devolver la distancia mínima de s a cada uno de los otros n−1 nodos, en
    // orden de número de nodo, con −1 para los inalcanzables.
    // Restricciones: q ≤ 10 consultas, 2 ≤ n ≤ 1000.
    //
    // Idea: como TODAS las aristas pesan lo mismo, no hace falta Dijkstra:
    // BFS cuenta el número mínimo de aristas y al final se multiplica por 6.
    // Es exactamente el argumento de por qué BFS resuelve el camino mínimo en
    // grafos no ponderados (y también en grafos de peso uniforme).
    //
    // ⚠️ El vector de salida NO incluye al nodo origen.
    //
    // Complejidad: O(n + m) por consulta.
    // =========================================================================
    public static void ejercicio34_ShortestReach() {
        System.out.println("\n--- Ej 34 · HR BFS: Shortest Reach in a Graph ---");

        int[][] aristas1 = {{1, 2}, {1, 3}};
        verificar("HR Shortest Reach (consulta 1)", "6 6 -1",
                unir(resolver34_ShortestReach(4, aristas1, 1)));

        int[][] aristas2 = {{2, 3}};
        verificar("HR Shortest Reach (consulta 2)", "-1 6",
                unir(resolver34_ShortestReach(3, aristas2, 2)));
    }

    /** Distancias (×6) desde s al resto de nodos, en orden, sin incluir s. */
    public static int[] resolver34_ShortestReach(int n, int[][] aristas, int s) {
        final int PESO = 6;
        List<List<Integer>> adj = nuevaAdj(n + 1);   // nodos 1..n
        for (int[] e : aristas) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, -1);
        Queue<Integer> cola = new ArrayDeque<>();
        dist[s] = 0;
        cola.add(s);

        while (!cola.isEmpty()) {
            int u = cola.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;   // se cuentan ARISTAS, no peso
                    cola.add(v);
                }
            }
        }

        // Se arma la respuesta saltando el origen y escalando por el peso
        int[] respuesta = new int[n - 1];
        int k = 0;
        for (int v = 1; v <= n; v++) {
            if (v == s) continue;
            respuesta[k++] = (dist[v] == -1) ? -1 : dist[v] * PESO;
        }
        return respuesta;
    }

    // =========================================================================
    // ===========================  UTILIDADES  ================================
    // =========================================================================

    private static int fallos = 0;

    /** Compara la salida obtenida con la salida oficial del enunciado. */
    private static void verificar(String caso, String esperado, String obtenido) {
        boolean ok = esperado.equals(obtenido);
        if (!ok) fallos++;
        System.out.printf("    [%s] %-42s esperado=%s  obtenido=%s%n",
                ok ? "OK" : "ERROR", caso, esperado, obtenido);
    }

    private static List<List<Integer>> nuevaAdj(int n) {
        List<List<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        return adj;
    }

    private static char[][] aGrilla(String[] filas) {
        char[][] g = new char[filas.length][];
        for (int i = 0; i < filas.length; i++) g[i] = filas[i].toCharArray();
        return g;
    }

    private static String unir(int[] v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(v[i]);
        }
        return sb.toString();
    }

    /** Comprueba que la secuencia de movimientos lleva de A a B sin cruzar paredes. */
    private static boolean validarCamino(char[][] g, String movimientos) {
        int n = g.length, m = g[0].length, i = -1, j = -1;
        for (int a = 0; a < n; a++)
            for (int b = 0; b < m; b++)
                if (g[a][b] == 'A') { i = a; j = b; }

        for (char c : movimientos.toCharArray()) {
            switch (c) {
                case 'D' -> i++;
                case 'U' -> i--;
                case 'R' -> j++;
                case 'L' -> j--;
                default -> { return false; }
            }
            if (i < 0 || i >= n || j < 0 || j >= m || g[i][j] == '#') return false;
        }
        return g[i][j] == 'B';
    }

    /**
     * Lectura rápida de una grilla para enviar a CSES (Scanner es demasiado
     * lento con 10^6 caracteres). Solo se usa al enviar al juez, no en el main.
     */
    @SuppressWarnings("unused")
    private static char[][] leerGrilla() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        char[][] g = new char[n][m];
        for (int i = 0; i < n; i++) g[i] = br.readLine().toCharArray();
        return g;
    }
}
