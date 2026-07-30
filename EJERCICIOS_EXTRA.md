# Ejercicios Extra de Grafos — DFS, BFS y Dijkstra

Complemento de los 11 ejercicios de la presentación (`index.html`). Aquí hay **18 ejercicios nuevos (12–29)** concentrados en los tres algoritmos centrales del curso, ordenados de menor a mayor dificultad dentro de cada bloque.

- Las soluciones ejecutables están en **[SolucionesExtra.java](SolucionesExtra.java)**: `javac SolucionesExtra.java && java SolucionesExtra`.
- Cada solución de este documento está dentro de un bloque plegable, así el archivo sirve tal cual como taller para repartir.

## Cómo usarlos en clase

| Momento | Ejercicios sugeridos | Por qué |
|---|---|---|
| **Tablero, en vivo** | 12, 13, 24, 26 | Se trazan a mano en 5–10 min y fijan la mecánica (colores, tiempos, tabla de `dist[]`) |
| **Parejas, 15 min** | 14, 15, 17, 18, 20, 22, 25 | Tienen respuesta corta y verificable; buenos para "escríbanlo y comparamos" |
| **Taller / tarea** | 16, 19, 21, 27, 28, 29 | Requieren implementar, no solo trazar |
| **Discusión conceptual** | 23 y 26 | No son de calcular: son de *entender por qué el algoritmo falla* |

> 💡 El par **23 + 26** es la mejor transición del tema: el 23 explica por qué se necesita Dijkstra cuando hay pesos, y el 26 explica por qué se necesita Bellman-Ford cuando esos pesos pueden ser negativos.

## Tabla resumen

| # | Algoritmo | Ejercicio | Dificultad | Concepto clave |
|---|---|---|---|---|
| 12 | DFS | Tiempos y tipos de arista | ★★★☆ | `d[]`/`f[]`, back edge ⇒ ciclo |
| 13 | DFS | Imports circulares | ★★☆☆ | 3 colores + reconstruir el ciclo |
| 14 | DFS | Conteo de islas | ★★☆☆ | Grafo implícito (grilla) |
| 15 | DFS | ¿Es bipartito? | ★★★☆ | 2-coloreo, ciclo impar |
| 16 | DFS | Articulación y puentes | ★★★★ | `low[]` (Tarjan) |
| 17 | DFS | Contar caminos simples | ★★★☆ | Backtracking real (desmarcar) |
| 18 | BFS | Laberinto | ★★☆☆ | `prev[]` y reconstrucción |
| 19 | BFS | Incendio forestal | ★★★☆ | Multi-fuente y niveles |
| 20 | BFS | Saltos del caballo | ★★★☆ | Grafo implícito de 64 nodos |
| 21 | BFS | 0-1 BFS con Deque | ★★★★ | Deque en vez de heap |
| 22 | BFS | Estados numéricos | ★★★☆ | Espacio de estados acotado |
| 23 | BFS | BFS vs pesos | ★★☆☆ | Aristas ≠ costo |
| 24 | Dijkstra | Traza paso a paso | ★★☆☆ | Extraer mínimo + relajar |
| 25 | Dijkstra | Contar caminos mínimos | ★★★☆ | Manejo del empate |
| 26 | Dijkstra | Peso negativo | ★★★☆ | Por qué falla el greedy |
| 27 | Dijkstra | Grilla con terreno | ★★★☆ | Peso = costo de la celda |
| 28 | Dijkstra | Una arista gratis | ★★★★ | Estado extendido / capas |
| 29 | Dijkstra | Cuello de botella | ★★★★ | Cambiar `+` por `min` |

---

# 🟣 Bloque DFS

## Ejercicio 12 — Tiempos de descubrimiento y clasificación de aristas ★★★☆

Grafo **dirigido**, vecinos visitados en **orden ascendente**, DFS desde el nodo 0:

```
Aristas: 0→1, 0→2, 0→3, 1→3, 2→4, 3→0, 4→1, 4→5

      0 ──────► 1
      │ ╲       │
      │  ╲      ▼
      ▼   ╲───► 3 ──┐
      2          ▲  │ (3→0 vuelve al inicio)
      │          │  │
      ▼          └──┘
      4 ──► 5
      └──► 1 (arista hacia otra rama ya terminada)
```

**Preguntas:**
1. Llena la tabla de `d[u]` (instante en que se descubre) y `f[u]` (instante en que se termina). El reloj arranca en 1 y avanza en cada evento.
2. Clasifica cada una de las 8 aristas como **árbol**, **retroceso** (back), **avance** (forward) o **cruce** (cross).
3. ¿El grafo tiene ciclo? ¿Cómo lo sabes mirando solo la clasificación?

<details><summary>✅ Solución</summary>

| Nodo | 0 | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|---|
| `d` | 1 | 2 | 6 | 3 | 7 | 8 |
| `f` | 12 | 5 | 11 | 4 | 10 | 9 |

**Clasificación** (regla según el color del destino en el momento de mirar la arista):

| Arista | Tipo | Por qué |
|---|---|---|
| 0→1 | Árbol | 1 estaba BLANCO |
| 1→3 | Árbol | 3 estaba BLANCO |
| 3→0 | **Retroceso** | 0 está GRIS (sigue abierto en la pila) |
| 0→2 | Árbol | 2 estaba BLANCO |
| 2→4 | Árbol | 4 estaba BLANCO |
| 4→1 | **Cruce** | 1 es NEGRO y `d[1]=2 < d[4]=7` → otra rama |
| 4→5 | Árbol | 5 estaba BLANCO |
| 0→3 | **Avance** | 3 es NEGRO y `d[0]=1 < d[3]=3` → descendiente |

**Sí hay ciclo**, y se detecta con un solo criterio: existe una arista de **retroceso** (3→0), que cierra el ciclo `0 → 1 → 3 → 0`.

> Regla de oro: en un grafo dirigido, **hay ciclo ⟺ el DFS encuentra al menos una arista de retroceso**. Es exactamente el chequeo que hace un compilador para rechazar dependencias circulares.
</details>

---

## Ejercicio 13 — Imports circulares en un compilador ★★☆☆

Un compilador procesa estos módulos, donde `u → v` significa "**u importa a v**":

```
main    → parser
parser  → lexer,  parser → ast
lexer   → symtab
ast     → symtab
symtab  → semantic
semantic→ parser        ← sospechoso...
```

**Preguntas:**
1. ¿Existe una dependencia circular? Ejecuta DFS con 3 colores desde `main` (vecinos en el orden listado).
2. Si existe, escribe el ciclo **exacto** (los módulos que lo forman, en orden).
3. ¿Qué estructura de datos permite reconstruir el ciclo y no solo detectarlo?

<details><summary>✅ Solución</summary>

**Sí hay dependencia circular.** Traza (G = gris, N = negro):

```
main(G) → parser(G) → lexer(G) → symtab(G) → semantic(G) → parser ← ¡ya está GRIS!
```

**Ciclo:** `parser → lexer → symtab → semantic → parser`

Para reconstruirlo se usa un arreglo **`padre[]`**: cuando la arista `semantic → parser` toca un nodo GRIS, se retrocede `semantic → symtab → lexer → parser` con `padre[]` hasta llegar al nodo gris, y se invierte la lista.

**Por qué GRIS y no "visitado":** un nodo NEGRO ya terminó, así que una arista hacia él **no** cierra ciclo (sería de avance o de cruce). Solo el GRIS —que sigue en la pila de recursión— significa "volví sobre mis propios pasos". Con un simple `boolean[] visitado` se reportarían ciclos falsos.
</details>

---

## Ejercicio 14 — Conteo de islas ★★☆☆

En la grilla, `1` = tierra y `0` = agua. Dos celdas de tierra son de la misma isla si son vecinas en **horizontal o vertical** (no en diagonal).

```
1 1 0 0 1
1 1 0 0 1
1 0 1 0 0
0 0 0 1 1
1 0 0 1 1
```

**Preguntas:**
1. ¿Cuántas islas hay?
2. ¿Cuántas celdas tiene la más grande?
3. ¿Cuál es la complejidad? ¿Dónde está el grafo si nadie construyó una lista de adyacencia?

<details><summary>✅ Solución</summary>

**5 islas.** Tamaños en orden de descubrimiento: `[5, 2, 1, 4, 1]`. La **mayor tiene 5 celdas**: `(0,0) (0,1) (1,0) (1,1) (2,0)`.

Las otras: `(0,4)(1,4)` = 2 · `(2,2)` = 1 · `(3,3)(3,4)(4,3)(4,4)` = 4 · `(4,0)` = 1.

**Complejidad:** `O(filas × columnas)` — cada celda se visita a lo sumo una vez.

**Dónde está el grafo:** es **implícito**. Cada celda es un nodo y las aristas se calculan al vuelo con los desplazamientos `(±1, 0)` y `(0, ±1)`. Construir la lista de adyacencia sería desperdiciar memoria. Es el mismo problema de "componentes conexas" del Ejercicio 2, con otro disfraz.
</details>

---

## Ejercicio 15 — ¿Es bipartito? ★★★☆

**Grafo A** (no dirigido): `0-1, 1-2, 2-3, 3-4, 4-5, 5-0`
**Grafo B**: el mismo, más la arista `0-2`.

**Preguntas:**
1. ¿Se pueden pintar los nodos del grafo A con 2 colores sin que dos vecinos compartan color? Si sí, da los dos conjuntos.
2. ¿Y el grafo B?
3. Enuncia el teorema que caracteriza los grafos bipartitos.

<details><summary>✅ Solución</summary>

1. **Grafo A: SÍ es bipartito.** Conjuntos `{0, 2, 4}` y `{1, 3, 5}`. El DFS pinta 0 de color 0, y a cada vecino le pone el color opuesto: `0→1(c1) →2(c0) →3(c1) →4(c0) →5(c1)`, y al cerrar `5-0` los colores son distintos ✓.
2. **Grafo B: NO es bipartito.** La arista `0-2` crea el triángulo `0-1-2-0`. Al recorrer, 0 queda color 0 y 2 también queda color 0 → **conflicto**.
3. **Teorema:** un grafo es bipartito **⟺ no contiene ningún ciclo de longitud impar**. El ciclo de A tiene longitud 6 (par) → bipartito; B tiene un triángulo (longitud 3, impar) → no.

> El mismo algoritmo sirve con BFS: se pinta por niveles y se verifica que ninguna arista una dos nodos del mismo nivel de paridad.
</details>

---

## Ejercicio 16 — Puntos de articulación y puentes ★★★★

Red de servidores (grafo **no dirigido**):

```
Aristas: 0-1, 1-2, 2-0, 1-3, 3-4, 4-5, 5-3, 3-6

   0───1───────3───6
   │ ╲ │      ╱│
   │  ╲│     ╱ │
   2───┘    5──4
```
(El triángulo de la izquierda es `0-1-2`; el de la derecha, `3-4-5`; y `6` cuelga de `3`.)

**Preguntas:**
1. ¿Qué servidores, al caerse, **desconectan** la red? (puntos de articulación)
2. ¿Qué cables son **críticos**? (puentes: si se cortan, la red se parte)
3. ¿Por qué la raíz del DFS necesita una regla especial?

<details><summary>✅ Solución</summary>

Con `disc[]` = instante de descubrimiento y `low[u]` = el `disc` más pequeño alcanzable desde el subárbol de `u` usando a lo sumo una arista de retroceso (DFS desde 0):

```
disc = [1, 2, 3, 4, 5, 6, 7]
low  = [1, 1, 1, 4, 4, 4, 7]
```

1. **Puntos de articulación: `{1, 3}`**
   - Si cae **1**: `{0, 2}` quedan aislados del resto.
   - Si cae **3**: se desconectan `6` y también `{4, 5}`.
   - `0`, `2`, `4` y `5` están cada uno dentro de un triángulo → hay ruta alterna. `6` es una hoja.
2. **Puentes: `{1-3, 3-6}`** — son exactamente las aristas que no pertenecen a ningún ciclo.
3. **Criterios:**
   - `u` no raíz es articulación si tiene un hijo `v` con `low[v] >= disc[u]` (el subárbol de `v` no puede "saltarse" a `u`).
   - La arista `u-v` es puente si `low[v] > disc[u]` (desigualdad **estricta**: ni siquiera se alcanza a `u` por otro lado).
   - **La raíz** no tiene padre, así que la primera condición no aplica: es articulación **solo si tiene 2 o más hijos** en el árbol DFS. Aquí `0` tiene un solo hijo → no es articulación.
</details>

---

## Ejercicio 17 — Contar todos los caminos simples ★★★☆

Grafo dirigido: `0→1, 0→2, 1→2, 1→3, 2→3, 2→4, 3→4`

**Preguntas:**
1. ¿Cuántos caminos distintos hay de 0 a 4? Lístalos.
2. ¿Qué hay que cambiar en el DFS "normal" para poder enumerarlos?
3. ¿Sigue siendo `O(V + E)`?

<details><summary>✅ Solución</summary>

1. **5 caminos:**
   ```
   0 → 1 → 2 → 3 → 4
   0 → 1 → 2 → 4
   0 → 1 → 3 → 4
   0 → 2 → 3 → 4
   0 → 2 → 4
   ```
2. **Hay que DESMARCAR el nodo al retroceder.** En el DFS de recorrido, un nodo se marca visitado y nunca se libera (queremos tocar cada nodo una vez). Para enumerar caminos, el mismo nodo puede aparecer en muchos caminos distintos, así que:
   ```java
   enCamino[v] = true;
   dfs(v, ...);
   enCamino[v] = false;   // ← backtracking de verdad
   ```
   El arreglo ya no significa "lo visité alguna vez" sino "**está en el camino que estoy construyendo ahora**" (sirve para no repetir nodos dentro de un mismo camino).
3. **No.** Enumerar caminos es **exponencial** en el peor caso (un grafo completo tiene `O(n!)` caminos). Es una diferencia importante: recorrer un grafo es barato, enumerar sus caminos no.
</details>

---

# 🔵 Bloque BFS

## Ejercicio 18 — Camino más corto en un laberinto ★★☆☆

`.` = libre, `#` = muro, `S` = inicio, `E` = salida. Movimientos en 4 direcciones.

```
     col: 0123456
   fila 0: S..#...
   fila 1: .#.#.#.
   fila 2: .#...#.
   fila 3: .###.#.
   fila 4: ....#.E
```

**Preguntas:**
1. ¿Cuál es el mínimo número de pasos de `S` a `E`?
2. Escribe la ruta.
3. ¿Qué se necesita guardar durante el BFS para poder reconstruirla?

<details><summary>✅ Solución</summary>

1. **14 pasos.**
2. Ruta: `(0,0) (0,1) (0,2) (1,2) (2,2) (2,3) (2,4) (1,4) (0,4) (0,5) (0,6) (1,6) (2,6) (3,6) (4,6)`

   ```
   S**#***
   .#*#*#*
   .#***#*
   .###.#*
   ....#.E
   ```
3. Un arreglo **`prev[][]`**: al descubrir la celda vecina se anota desde qué celda se llegó. Al terminar, se retrocede desde `E` hasta `S` y se invierte la lista. Es el mismo truco del Ejercicio 7 con Dijkstra.

**Por qué BFS y no DFS:** DFS encontraría *un* camino, no el *más corto*. La cola FIFO garantiza que las celdas se descubren en orden de distancia creciente, así que la primera vez que se toca `E` es por la ruta mínima.
</details>

---

## Ejercicio 19 — Propagación de un incendio (BFS multi-fuente) ★★★☆

`1` = árbol sano, `0` = terreno pelado, `2` = árbol ya en llamas. Cada minuto el fuego pasa a los árboles vecinos (4 direcciones).

```
2 1 1 0 1
1 1 0 1 1
0 1 1 1 0
1 0 1 1 2
```

**Preguntas:**
1. ¿Cuántos minutos pasan hasta que el fuego deja de propagarse?
2. ¿Sobrevive algún árbol? ¿Cuál?
3. ¿Por qué **no** funciona correr un BFS separado desde cada foco y quedarse con el máximo?

<details><summary>✅ Solución</summary>

1. **6 minutos.**
2. **Sí, sobrevive 1 árbol:** el de la posición `(3,0)`. Está rodeado de terreno pelado (`(2,0)=0` y `(3,1)=0`), así que el fuego nunca lo alcanza.
3. Porque hay que encolar **todos los focos con distancia 0 antes de entrar al `while`**. Si se corre un BFS por foco y se toma el máximo, se obtiene la respuesta *equivocada*: lo correcto es el **mínimo** sobre los focos para cada celda, y luego el **máximo** sobre las celdas. El BFS multi-fuente hace las dos cosas de una sola pasada, en `O(filas × columnas)`, porque los frentes de fuego avanzan en paralelo y cada celda la reclama el foco que llega primero.

**Patrón general:** siempre que la pregunta sea "distancia al `X` más cercano" para todas las celdas a la vez, la respuesta es BFS multi-fuente, no un BFS por cada `X`.
</details>

---

## Ejercicio 20 — Saltos mínimos del caballo ★★★☆

Tablero de ajedrez 8×8. El caballo se mueve en "L" (8 movimientos posibles).

**Preguntas:**
1. ¿Cuántos saltos mínimos necesita para ir de **a1** `(0,0)` a **h8** `(7,7)`?
2. ¿Cuántos nodos y cuántas aristas tiene este grafo?
3. ¿Cuál es la casilla más "lejana" desde a1?

<details><summary>✅ Solución</summary>

1. **6 saltos.**
2. **64 nodos** (una casilla = un nodo) y **168 aristas** no dirigidas — o 336 pares ordenados `(origen, destino)`, porque las casillas del borde tienen menos de 8 movimientos disponibles. Nadie construye la lista de adyacencia: los vecinos se generan con los dos arreglos `dx = {-2,-2,-1,-1,1,1,2,2}`, `dy = {-1,1,-2,2,-2,2,-1,1}`.
3. Tabla de distancias desde `(0,0)` (fila 7 arriba, como un tablero):

   ```
    5  4  5  4  5  4  5  6
    4  3  4  3  4  5  4  5
    3  4  3  4  3  4  5  4
    2  3  2  3  4  3  4  5
    3  2  3  2  3  4  3  4
    2  1  4  3  2  3  4  5
    3  4  1  2  3  4  3  4
    0  3  2  3  2  3  4  5
   ```

   La más lejana es justamente **h8 con 6**. Dato curioso para la clase: `b2` `(1,1)` requiere **4** saltos aunque esté pegada a a1 — la distancia en el grafo no tiene nada que ver con la distancia geométrica.
</details>

---

## Ejercicio 21 — 0-1 BFS con Deque ★★★★

Red de calles dirigidas donde cada tramo es **gratis (0)** o cobra **un peaje (1)**:

```
0→1 (0)   0→2 (1)   1→2 (0)   1→3 (1)   2→3 (0)
2→4 (1)   3→4 (1)   3→5 (1)   4→5 (0)
```

**Preguntas:**
1. ¿Cuál es el peaje mínimo de 0 a 5? Da una ruta que lo logre.
2. Si los pesos son solo 0 y 1, ¿por qué se puede reemplazar la `PriorityQueue` de Dijkstra por un `Deque`?
3. ¿Cuál es la complejidad resultante?

<details><summary>✅ Solución</summary>

1. **1 peaje.** Ruta: `0 →(0) 1 →(0) 2 →(0) 3 →(1) 5`. Distancias: `[0, 0, 0, 0, 1, 1]`.
2. **Regla del 0-1 BFS:** al relajar la arista `u→v`,
   - si el peso es **0**, se inserta `v` **al frente** (`addFirst`): sigue costando lo mismo que `u`, pertenece al mismo "nivel";
   - si el peso es **1**, se inserta **al final** (`addLast`): pertenece al nivel siguiente.

   Con eso la deque queda siempre ordenada por distancia con a lo sumo **dos valores distintos** (`d` y `d+1`), que es justo el invariante que la `PriorityQueue` mantendría con un heap. El heap es innecesario porque el orden se mantiene solo.
3. **`O(V + E)`** en vez de `O((V+E) log V)`. Se elimina por completo el factor logarítmico.

> Aplicación típica: laberintos donde moverse es gratis pero romper una pared cuesta 1, o rutas donde algunos tramos tienen peaje.
</details>

---

## Ejercicio 22 — BFS sobre estados numéricos ★★★☆

Partiendo del número **3**, y usando solo dos operaciones:

- `×2` (duplicar)
- `−1` (restar uno)

**Preguntas:**
1. ¿Cuál es el mínimo número de operaciones para llegar a **10**? Da la secuencia.
2. ¿Dónde están los nodos y las aristas de este grafo?
3. El espacio de estados es infinito. ¿Cómo se garantiza que el algoritmo termine?

<details><summary>✅ Solución</summary>

1. **3 operaciones:** `3 --×2--> 6 --(−1)--> 5 --×2--> 10`

   Es fácil equivocarse yendo por lo "obvio" (`3→6→12→11→10` son 4 pasos). Ese es justo el punto: la búsqueda a ojo falla, el BFS no.
2. **Cada número es un nodo** y **cada operación es una arista de peso 1**. Los vecinos de `x` son `{2x, x−1}`. Nunca se construye la lista de adyacencia: se genera al vuelo, igual que en la grilla y en el tablero de ajedrez.
3. **Acotando el espacio de estados.** Aquí se limita a `0 .. 2·objetivo`: nunca conviene pasarse del doble del objetivo, porque desde ahí solo se puede volver restando de a 1. Sin una cota, la cola crece para siempre por el lado de `×2`.

**Por qué BFS y no Dijkstra:** todas las operaciones cuestan lo mismo (1). Si `×2` costara 5 y `−1` costara 1, habría que usar Dijkstra.
</details>

---

## Ejercicio 23 — ¿Por qué BFS falla con pesos? ★★☆☆

Grafo dirigido **con pesos**:

```
S →(10) T
S →(1)  A →(1) B →(1) T
```

**Preguntas:**
1. ¿Qué responde un BFS que ignora los pesos? ¿Cuánto cuesta *de verdad* esa ruta?
2. ¿Cuál es el camino realmente más barato y cuánto cuesta?
3. Si todos los pesos son enteros pequeños, ¿se puede transformar el grafo para que BFS **sí** dé la respuesta correcta? ¿Cuál es el costo de ese truco?

<details><summary>✅ Solución</summary>

1. BFS dice que el camino más corto es `S → T` con **1 arista**... pero esa arista cuesta **10**. BFS minimiza el **número de aristas**, no el **costo**.
2. El más barato es `S → A → B → T`, con costo **3** (tres aristas de peso 1). Dijkstra lo encuentra sin problema.
3. **Sí: subdividiendo.** Cada arista de peso `w` se reemplaza por una cadena de `w` aristas de peso 1, insertando `w−1` nodos ficticios. En el grafo resultante todas las aristas valen lo mismo, así que BFS es válido:

   ```
   Grafo original:      4 nodos
   Grafo subdividido:  13 nodos  (la arista S→T de peso 10 aporta 9 nodos ficticios)
   BFS sobre el subdividido: S→T = 3  ✓ coincide con Dijkstra
   ```

   **El costo del truco:** el grafo crece a `O(suma de los pesos)`, que no tiene relación con `V` ni con `E`. Con pesos como 1.000.000 es impracticable — y con pesos decimales, imposible. Por eso existe Dijkstra: obtiene lo mismo en `O((V+E) log V)` sin tocar el grafo.
</details>

---

# 🟢 Bloque Dijkstra

## Ejercicio 24 — Traza completa paso a paso ★★☆☆

```
A→B (7)   A→C (9)   A→F (14)
B→C (10)  B→D (15)
C→D (11)  C→F (2)
D→E (6)   F→E (9)
```

**Preguntas:**
1. Llena la tabla: en cada iteración, ¿qué nodo se extrae del heap y cómo queda `dist[]`?
2. ¿Cuál es la distancia mínima de A a E y por qué camino?
3. ¿Por qué la arista `A→F` de peso 14 termina sin usarse?

<details><summary>✅ Solución</summary>

| Paso | Extrae | `dist[A B C D E F]` |
|---|---|---|
| 1 | **A** | `[0  7  9  ∞  ∞  14]` |
| 2 | **B** | `[0  7  9  22 ∞  14]` |
| 3 | **C** | `[0  7  9  20 ∞  11]` |
| 4 | **F** | `[0  7  9  20 20 11]` |
| 5 | **E** | `[0  7  9  20 20 11]` |
| 6 | **D** | `[0  7  9  20 20 11]` |

**Distancias finales:** `A=0, B=7, C=9, D=20, E=20, F=11`

2. **`A → E = 20`, por el camino `A → C → F → E`** (9 + 2 + 9). Es más barato que `A → C → D → E` (9 + 11 + 6 = 26).
3. Porque al procesar **C** se relaja `C→F` y `dist[F]` baja de **14 a 11**. Cuando F sale del heap, sale con 11: la entrada vieja `(F, 14)` quedó **obsoleta** y se descarta con `if (d > dist[u]) continue;`.

**Detalle fino para discutir:** en el paso 5 se extrae **E antes que D**, aunque ambos valen 20. El desempate lo decide el heap y no afecta el resultado: cuando se extrae D, ninguna relajación mejora nada. Como todos los pesos son ≥ 0, un nodo extraído ya tiene su distancia definitiva.
</details>

---

## Ejercicio 25 — Contar cuántos caminos mínimos hay ★★★☆

```
0→1 (1)   0→2 (1)   0→3 (2)
1→3 (1)   2→3 (1)
3→4 (1)   3→5 (2)   4→5 (1)
```

**Preguntas:**
1. ¿Cuál es la distancia mínima de 0 a 5?
2. ¿Cuántos caminos **distintos** logran esa distancia?
3. ¿Qué hay que agregarle a la relajación de Dijkstra?

<details><summary>✅ Solución</summary>

1. **`dist[5] = 4`**. Arreglo completo: `dist = [0, 1, 1, 2, 3, 4]`.
2. **6 caminos mínimos.** Conteo: `cnt = [1, 1, 1, 3, 3, 6]`.
   - Al nodo 3 (dist 2) llegan **3** caminos: `0→1→3`, `0→2→3` y `0→3` (peso 2 directo).
   - Al nodo 4 (dist 3) llegan esos mismos **3**.
   - Al nodo 5 (dist 4) llegan **3** vía `3→5` (2+2) y **3** vía `4→5` (3+1) = **6**.
3. Se agrega un contador `cnt[]` y un caso nuevo: **el empate**.

   ```java
   if (dist[u] + w < dist[v]) {        // camino MEJOR
       dist[v] = dist[u] + w;
       cnt[v]  = cnt[u];               // reinicia el conteo
       pq.add(...);
   } else if (dist[u] + w == dist[v]) { // EMPATE
       cnt[v] += cnt[u];               // acumula
   }
   ```

   Se inicializa `cnt[origen] = 1`. La rama `==` es la única línea nueva del algoritmo, y es la que suele olvidarse.
</details>

---

## Ejercicio 26 — Peso negativo: Dijkstra vs Bellman-Ford ★★★☆

```
A→B (2)    A→C (3)    C→B (−2)    B→D (1)
```

No hay ciclo negativo (el grafo es un DAG).

**Preguntas:**
1. ¿Qué distancias reporta Dijkstra (versión clásica, con conjunto de nodos finalizados)?
2. ¿Cuáles son las distancias correctas?
3. ¿En qué momento exacto se equivoca Dijkstra?

<details><summary>✅ Solución</summary>

| Nodo | Dijkstra | Bellman-Ford (correcto) |
|---|---|---|
| A | 0 | 0 |
| B | **2** ❌ | **1** |
| C | 3 | 3 |
| D | **3** ❌ | **2** |

**Dónde se rompe:** Dijkstra extrae B con `dist=2` y lo marca como **finalizado** — su apuesta es "el mínimo del heap ya no puede mejorar". Luego relaja `B→D` y fija `D=3`. Después extrae C (dist 3) y descubre `C→B = 3 + (−2) = 1`, mejor que 2... pero B ya está cerrado, así que la mejora se descarta y D queda mal para siempre.

**Es exactamente donde el argumento greedy se cae:** con pesos ≥ 0, agregar aristas solo puede *aumentar* el costo, así que el mínimo del heap es definitivo. Con una arista negativa esa garantía desaparece.

> ⚠️ **Matiz importante** (buena discusión de clase): la versión "perezosa" de [`Dijkstra.java`](Dijkstra.java) —sin arreglo de finalizados, con `if (d > dist[u]) continue;`— **sí corregiría este caso concreto**, porque vuelve a encolar B con 1. Pero pierde la garantía: con muchas aristas negativas puede reprocesar nodos un número exponencial de veces. La garantía real, en `O(V·E)`, la da **Bellman-Ford**, que además detecta ciclos negativos.
</details>

---

## Ejercicio 27 — Dijkstra sobre una grilla con terreno ★★★☆

Cada celda tiene un **costo de atravesarla**. Ir de `(0,0)` a `(3,3)` en 4 direcciones, minimizando la suma de los costos de las celdas pisadas (incluyendo la inicial y la final).

```
1 3 1 2
2 8 4 1
5 1 1 3
2 2 1 1
```

**Preguntas:**
1. ¿Cuál es el costo mínimo?
2. ¿Cuál es la ruta? ¿Es también la más corta en número de pasos?
3. ¿Por qué aquí no sirve BFS?

<details><summary>✅ Solución</summary>

1. **Costo mínimo = 12.**
2. Ruta: `(0,0)=1 → (1,0)=2 → (2,0)=5 → (2,1)=1 → (2,2)=1 → (3,2)=1 → (3,3)=1`

   Matriz de costos acumulados:
   ```
   [ 1,  4,  5,  7]
   [ 3, 11,  9,  8]
   [ 8,  9, 10, 11]
   [10, 11, 11, 12]
   ```

   **Sí es de la misma longitud**: usa 7 celdas, igual que cualquier camino monótono. Lo que hace es rodear la celda de costo 8. Detalle interesante para la clase: la ruta simétrica por el borde superior (`1+3+1+2+1+3+1`) **también cuesta 12** — hay empate, y el algoritmo reporta una cualquiera de las dos. En cambio, cortar por el centro pisando el 8 cuesta **16**.
3. Porque **los movimientos no cuestan todos lo mismo**: pisar una celda de costo 8 no equivale a pisar una de costo 1. BFS solo es correcto cuando todas las aristas tienen el mismo peso. Aquí el "peso de la arista `u→v`" es el costo de **entrar** a la celda `v`, y hay que usar Dijkstra.
</details>

---

## Ejercicio 28 — Una arista gratis (estado extendido) ★★★★

```
0→1 (5)   0→2 (2)   2→1 (1)   1→3 (1)
2→3 (9)   3→4 (4)   1→4 (10)
```

Tienes **un cupón** que vuelve gratis **una sola** arista del camino.

**Preguntas:**
1. ¿Cuál es el costo mínimo de 0 a 4 **sin** cupón?
2. ¿Y **con** cupón? ¿En qué arista conviene gastarlo?
3. ¿Cómo se modela esto sin inventar un algoritmo nuevo?

<details><summary>✅ Solución</summary>

1. **Sin cupón: 8** — ruta `0→2 (2) →1 (1) →3 (1) →4 (4)`.
2. **Con cupón: 3** — ruta `0→2 (2) →1 (1)` y luego **el cupón en la arista `1→4`**, que costaba 10.

   Es contraintuitivo: la tentación es seguir la ruta barata y usar el cupón en `3→4` (ahorra 4, total 4). Pero conviene *desviarse* a un nodo desde el cual sale una arista carísima y quemar ahí el cupón (ahorra 10).
3. **Grafo por capas / estado extendido.** El nodo deja de ser "el lugar" y pasa a ser el par **(lugar, cupón usado)**. Se duplica el grafo:

   ```
   capa 0 = cupón disponible      capa 1 = cupón ya gastado
   arista normal:  (u,k) → (v,k)  con peso w
   usar el cupón:  (u,0) → (v,1)  con peso 0
   ```

   Sobre ese grafo de `2V` nodos se corre **Dijkstra tal cual**, sin modificar una línea del algoritmo. La respuesta es `min(dist[4][0], dist[4][1])`.

   ```
   dist[nodo][0] (cupón intacto) = [0, 3, 2, 4, 8]
   dist[nodo][1] (cupón gastado) = [∞, 0, 0, 1, 3]
   ```

> Este patrón es de los más reutilizables del curso: "con a lo sumo `k` recargas", "con a lo sumo `k` transbordos", "puedo saltarme `k` peajes" → multiplica los nodos por `k+1` y corre Dijkstra.
</details>

---

## Ejercicio 29 — Camino de máxima capacidad (cuello de botella) ★★★★

Red de tuberías **no dirigida**, con la capacidad de cada tramo:

```
0-1 (10)   0-2 (6)   1-2 (8)   1-3 (3)
2-3 (7)    3-4 (5)   2-4 (4)
```

Queremos enviar la mayor cantidad de agua posible de 0 a 4 **por una sola ruta**. La capacidad de una ruta es la de su tramo más angosto (su **cuello de botella**).

**Preguntas:**
1. Calcula la capacidad de cada ruta posible de 0 a 4 y di cuál es la mejor.
2. ¿Qué dos cambios hay que hacerle a Dijkstra?
3. ¿Por qué el argumento greedy sigue siendo válido?

<details><summary>✅ Solución</summary>

1. Rutas y su cuello de botella:

   | Ruta | Capacidades | Cuello de botella |
   |---|---|---|
   | `0-1-2-3-4` | 10, 8, 7, 5 | **5** ✅ |
   | `0-2-3-4` | 6, 7, 5 | **5** ✅ |
   | `0-1-2-4` | 10, 8, 4 | 4 |
   | `0-2-4` | 6, 4 | 4 |
   | `0-1-3-4` | 10, 3, 5 | 3 |

   **Máximo = 5** (empatan dos rutas). El tramo `3-4` de capacidad 5 es el límite real de la red.

2. **Dos cambios:**
   - La cola de prioridad pasa a ser un **max-heap** (se extrae el nodo con **mayor** capacidad garantizada).
   - La relajación cambia la **suma** por el **mínimo**:
     ```java
     int nueva = Math.min(cap[u], w);   // en vez de dist[u] + w
     if (nueva > cap[v]) { ... }        // en vez de <
     ```
   Se inicializa `cap[origen] = ∞` y el resto en 0. Capacidades resultantes: `0→1: 10`, `0→2: 8`, `0→3: 7`, `0→4: 5`.

3. Porque el argumento greedy de Dijkstra no depende de la **suma**, sino de que la operación de combinación sea **monótona**: extender un camino nunca puede *mejorar* su cuello de botella (`min(a, b) ≤ a`), igual que sumar un peso ≥ 0 nunca puede reducir la distancia. Con esa propiedad, el nodo extremo del heap ya es definitivo.

> Misma estructura, otro semirreanillo: `(min, max)` en vez de `(+, min)`. Sirve para ancho de banda en redes, carga máxima en rutas de camiones y capacidad de canales.
</details>

---

## Verificación

Todas las respuestas de este documento salen de ejecutar:

```bash
javac SolucionesExtra.java
java SolucionesExtra
```

Cada ejercicio corresponde al método `ejercicioNN_*()` del archivo, con el enunciado y la idea explicados en el comentario de encabezado.
