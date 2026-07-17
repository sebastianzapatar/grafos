import java.util.*;

/**
 * ============================================================
 * UNION-FIND (Disjoint Set Union / DSU)
 * ============================================================
 *
 * ¿QUÉ ES?
 *   Estructura de datos que mantiene una colección de conjuntos
 *   DISJUNTOS (sin elementos en común). Soporta dos operaciones:
 *
 *   - find(x):    ¿A qué conjunto pertenece x? (devuelve el representante)
 *   - union(x,y): Fusionar los conjuntos de x e y en uno solo
 *
 * ¿PARA QUÉ SIRVE EN KRUSKAL?
 *   Kruskal necesita saber si dos nodos ya están conectados.
 *   Si lo están, agregar la arista formaría un CICLO.
 *   Union-Find resuelve esto en tiempo casi constante.
 *
 * ============================================================
 * DOS VERSIONES: UNION NAIVE vs UNION BY RANK
 * ============================================================
 *
 * UNION NAIVE (simple):
 *   - Al unir dos conjuntos, simplemente colgamos la raíz de
 *     uno debajo de la raíz del otro, sin importar alturas.
 *   - Problema: los árboles pueden degenerar en CADENAS lineales.
 *   - Peor caso: find() tarda O(n) si todos se encadenan.
 *
 *   Ejemplo de degeneración (union naive):
 *     union(1,0) → 0←1
 *     union(2,0) → 0←1, 0←2     (todos cuelgan de 0)
 *     union(3,0) → 0←1, 0←2, 0←3
 *     PERO si siempre colgamos el primero del segundo:
 *     union(0,1) → 0→1
 *     union(0,2) → 0→1→2        (¡cadena!)
 *     union(0,3) → 0→1→2→3      (¡cadena aún peor!)
 *
 * UNION BY RANK (optimizada):
 *   - Cada raíz tiene un "rank" (altura aproximada del árbol).
 *   - Al unir, el árbol MÁS BAJO se cuelga del MÁS ALTO.
 *   - Así los árboles se mantienen PLANOS (altura ≤ log n).
 *   - Con path compression: tiempo amortizado O(α(n)) ≈ O(1).
 *     (α es la función inversa de Ackermann, crece ultra lento)
 *
 * PATH COMPRESSION (se usa en ambas versiones):
 *   Cuando hacemos find(x), recorremos el camino de x hasta
 *   la raíz. Aprovechamos para colgar CADA nodo del camino
 *   directamente de la raíz. Así la próxima búsqueda es O(1).
 *
 *   Ejemplo: si el árbol es 0←1←2←3 y hacemos find(3):
 *     Recorremos 3→2→1→0 (raíz)
 *     Después: 0←1, 0←2, 0←3  (todos cuelgan directo de 0)
 *
 * ============================================================
 * RESUMEN DE COMPLEJIDADES
 * ============================================================
 *
 *   | Operación     | Union Naive     | Union by Rank       |
 *   |---------------|-----------------|---------------------|
 *   | find(x)       | O(n) peor caso  | O(α(n)) amortizado  |
 *   | union(x,y)    | O(n) peor caso  | O(α(n)) amortizado  |
 *   | Espacio       | O(n)            | O(n)                |
 *
 *   Nota: con path compression, union naive también mejora en
 *   la práctica, pero la garantía teórica solo se logra
 *   combinando path compression + union by rank.
 */
public class UnionFind {

    // ============================================================
    //  VERSIÓN 1: UNION NAIVE (sin rank)
    // ============================================================

    /**
     * Union-Find con union NAIVE (sin optimización de rank).
     *
     * - find(): usa path compression (sí es eficiente)
     * - union(): simplemente cuelga raíz de x debajo de raíz de y,
     *            sin considerar cuál árbol es más alto.
     *
     * Problema: si siempre unimos en la misma dirección, el árbol
     * puede degenerar en una cadena lineal de altura O(n).
     */
    static class UnionFindNaive {
        int[] parent;  // parent[i] = padre de i (i es raíz si parent[i] == i)

        /**
         * Inicializa n conjuntos, cada elemento es su propia raíz.
         * parent = [0, 1, 2, ..., n-1]
         */
        UnionFindNaive(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;  // Cada nodo es su propio conjunto
            }
        }

        /**
         * Encuentra la raíz (representante) del conjunto de x.
         * Usa PATH COMPRESSION: cada nodo visitado se cuelga
         * directamente de la raíz para acelerar futuras búsquedas.
         *
         * Ejemplo: si parent = [0, 0, 1, 2] (cadena 3→2→1→0)
         *   find(3): recorre 3→2→1→0
         *   Después: parent = [0, 0, 0, 0] (todos apuntan a 0)
         */
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Compresión de camino
            }
            return parent[x];
        }

        /**
         * UNION NAIVE: une los conjuntos de x e y.
         * Simplemente cuelga la raíz de x debajo de la raíz de y.
         * NO considera alturas → puede crear árboles desbalanceados.
         *
         * @return false si ya estaban en el mismo conjunto (ciclo)
         */
        boolean union(int x, int y) {
            int rx = find(x);  // Raíz del conjunto de x
            int ry = find(y);  // Raíz del conjunto de y

            if (rx == ry) return false;  // Ya están conectados → ¡ciclo!

            // NAIVE: siempre colgamos rx debajo de ry
            // (no importa cuál sea más alto)
            parent[rx] = ry;
            return true;
        }
    }

    // ============================================================
    //  VERSIÓN 2: UNION BY RANK (optimizada)
    // ============================================================

    /**
     * Union-Find con UNION BY RANK (optimización completa).
     *
     * - find(): usa path compression
     * - union(): compara rank (altura) de ambos árboles y cuelga
     *            el más bajo del más alto → árboles balanceados.
     *
     * Garantía: con ambas optimizaciones, cada operación tarda
     * O(α(n)) amortizado, donde α es la inversa de Ackermann.
     * En la práctica esto es O(1) para cualquier valor razonable de n.
     */
    static class UnionFindRank {
        int[] parent;  // parent[i] = padre de i
        int[] rank;    // rank[i] = altura APROXIMADA del subárbol de i

        /**
         * Inicializa n conjuntos.
         * parent = [0, 1, 2, ..., n-1]  (cada uno es su raíz)
         * rank   = [0, 0, 0, ..., 0]    (árboles de altura 0)
         */
        UnionFindRank(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;  // Cada nodo es su propio conjunto
                // rank[i] = 0 (por defecto en Java)
            }
        }

        /**
         * Encuentra la raíz del conjunto de x.
         * Idéntico a la versión naive: usa path compression.
         */
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Compresión de camino
            }
            return parent[x];
        }

        /**
         * UNION BY RANK: une los conjuntos de x e y.
         *
         * Regla: el árbol con MENOR rank se cuelga del de MAYOR rank.
         * Si tienen el MISMO rank, uno se cuelga del otro y el rank
         * del ganador incrementa en 1.
         *
         * ¿Por qué funciona?
         *   - Si rank[rx] < rank[ry]: colgar rx de ry NO cambia
         *     la altura de ry (rx es más bajo).
         *   - Si rank[rx] > rank[ry]: colgar ry de rx NO cambia
         *     la altura de rx.
         *   - Si rank[rx] == rank[ry]: la altura crece en 1,
         *     pero esto pasa pocas veces → altura máxima O(log n).
         *
         * @return false si ya estaban en el mismo conjunto (ciclo)
         */
        boolean union(int x, int y) {
            int rx = find(x);  // Raíz del conjunto de x
            int ry = find(y);  // Raíz del conjunto de y

            if (rx == ry) return false;  // Ya están conectados → ¡ciclo!

            // UNION BY RANK: el árbol más bajo se cuelga del más alto
            if (rank[rx] < rank[ry]) {
                // rx es más bajo → cuelga de ry
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                // ry es más bajo → cuelga de rx
                parent[ry] = rx;
            } else {
                // Misma altura → elegimos uno (ry cuelga de rx)
                // y aumentamos el rank del ganador
                parent[ry] = rx;
                rank[rx]++;
            }
            return true;
        }
    }

    // ============================================================
    //  DEMOSTRACIÓN: paso a paso de ambas versiones
    // ============================================================

    /**
     * Imprime el estado actual de parent[] y rank[] (si existe).
     */
    static void printState(String label, int[] parent, int[] rank) {
        System.out.println("\n   " + label);
        System.out.print("   parent = [");
        for (int i = 0; i < parent.length; i++) {
            System.out.print(parent[i]);
            if (i < parent.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        if (rank != null) {
            System.out.print("   rank   = [");
            for (int i = 0; i < rank.length; i++) {
                System.out.print(rank[i]);
                if (i < rank.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    public static void main(String[] args) {
        int n = 6;  // Elementos: {0, 1, 2, 3, 4, 5}

        // Secuencia de uniones a realizar
        int[][] ops = {{1,0}, {3,2}, {5,4}, {3,0}, {5,0}};

        // ============================================================
        //  DEMO 1: UNION NAIVE
        // ============================================================
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  UNION NAIVE (sin rank)");
        System.out.println("═══════════════════════════════════════════════");

        UnionFindNaive naive = new UnionFindNaive(n);
        printState("Estado inicial:", naive.parent, null);

        for (int[] op : ops) {
            int x = op[0], y = op[1];
            // Capturar las raíces ANTES de unir (después ya estarían fusionadas)
            int rx = naive.find(x), ry = naive.find(y);
            boolean merged = naive.union(x, y);
            if (merged) {
                System.out.println("\n   → union(" + x + ", " + y + "): " +
                    "raíz " + rx + " se cuelga de raíz " + ry);
            } else {
                System.out.println("\n   → union(" + x + ", " + y + "): " +
                    "YA están en el mismo conjunto (raíz común: " + rx + ")");
            }
            printState("Después de union(" + x + ", " + y + "):", naive.parent, null);
        }

        // Mostrar find de cada elemento
        System.out.println("\n   Resultado final:");
        for (int i = 0; i < n; i++) {
            System.out.println("   find(" + i + ") = " + naive.find(i));
        }

        // ============================================================
        //  DEMO 2: UNION BY RANK
        // ============================================================
        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("  UNION BY RANK (optimizada)");
        System.out.println("═══════════════════════════════════════════════");

        UnionFindRank ranked = new UnionFindRank(n);
        printState("Estado inicial:", ranked.parent, ranked.rank);

        for (int[] op : ops) {
            int x = op[0], y = op[1];
            boolean merged = ranked.union(x, y);
            if (merged) {
                System.out.println("\n   → union(" + x + ", " + y + "): fusionados");
            } else {
                System.out.println("\n   → union(" + x + ", " + y + "): " +
                    "YA están en el mismo conjunto");
            }
            printState("Después de union(" + x + ", " + y + "):", ranked.parent, ranked.rank);
        }

        // Mostrar find de cada elemento
        System.out.println("\n   Resultado final:");
        for (int i = 0; i < n; i++) {
            System.out.println("   find(" + i + ") = " + ranked.find(i));
        }

        // ============================================================
        //  COMPARACIÓN VISUAL
        // ============================================================
        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("  COMPARACIÓN FINAL");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Union Naive — el árbol puede tener más altura");
        System.out.println("  Union by Rank — el árbol se mantiene plano");
        System.out.println();
        System.out.println("  Con path compression, ambos terminan con");
        System.out.println("  todos los nodos apuntando directo a la raíz,");
        System.out.println("  pero Union by Rank GARANTIZA O(α(n)) incluso");
        System.out.println("  sin path compression.");
    }
}
