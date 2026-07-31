# Teoría de Grafos - Lenguajes y Compiladores

Este repositorio contiene las implementaciones en Java de los algoritmos fundamentales de Teoría de Grafos, junto con una presentación interactiva en HTML que sirve como material de apoyo para la asignatura **Lenguajes y Compiladores (2026-2)**.

## 🚀 Contenido del Proyecto

El proyecto se divide en dos partes principales: el código fuente (algoritmos) y la presentación visual interactiva.

### 1. Algoritmos en Java

Se han implementado y documentado exhaustivamente los siguientes algoritmos:

- **[BFS (Búsqueda en Anchura)](BFS.java)**: Recorrido por niveles usando una Cola. Útil para encontrar el camino más corto en grafos no ponderados.
- **[DFS (Búsqueda en Profundidad)](DFS.java)**: Recorrido en profundidad usando recursión (Pila implícita). Útil para orden topológico y detección de ciclos.
- **[Dijkstra](Dijkstra.java)**: Algoritmo *greedy* para encontrar caminos mínimos desde un nodo origen (solo admite pesos positivos).
- **[Bellman-Ford](BellmanFord.java)**: Algoritmo de programación dinámica para caminos mínimos. Soporta pesos negativos y detecta ciclos negativos.
- **[Floyd-Warshall](FloydWarshall.java)**: Algoritmo para encontrar las distancias mínimas entre *todos los pares* de nodos.
- **[Detección de Ciclos](DeteccionCiclos.java)**: DFS con 3 colores (grafos dirigidos) y DFS con padre (grafos no dirigidos). Reconstruye e imprime el ciclo encontrado.
- **[Ciclo Negativo](CicloNegativo.java)**: Bellman-Ford extendido que no solo detecta el ciclo negativo, sino que lo *reconstruye* (imprime sus nodos y su peso total).
- **[Orden Topológico](OrdenTopologico.java)**: Algoritmo de Kahn (BFS con grados de entrada). Ordena tareas respetando dependencias y también detecta ciclos.
- **[Kruskal](Kruskal.java)**: Árbol de Expansión Mínima (MST) ordenando aristas y usando **Union-Find** para evitar ciclos.
- **[Prim](Prim.java)**: Árbol de Expansión Mínima (MST) haciendo crecer un árbol desde un nodo, con cola de prioridad (similar a Dijkstra).

### 📚 Ciclos y Ciclos Negativos

**¿Qué es un ciclo?** Un ciclo es un camino que comienza y termina en el **mismo vértice** (ej: `1 → 2 → 3 → 1`). Un grafo dirigido sin ciclos se llama **DAG** (*Directed Acyclic Graph*), estructura clave en compiladores para modelar dependencias entre módulos y orden de evaluación.

- **Detección en grafos dirigidos** ([DeteccionCiclos.java](DeteccionCiclos.java)): DFS con 3 colores — BLANCO (sin visitar), GRIS (en la pila de recursión), NEGRO (terminado). Si una arista apunta a un nodo **GRIS**, volvimos a un nodo de nuestro propio camino → hay ciclo (*back edge*).
- **Detección en grafos no dirigidos**: durante el DFS, si un vecino ya visitado **no es el padre** del nodo actual → hay ciclo. (También se puede usar Union-Find, como hace [Kruskal.java](Kruskal.java).)

**¿Qué es un ciclo negativo?** Es un ciclo cuya **suma de pesos es menor que cero** (ej: `1 → 2 → 3 → 1` con pesos `3, 2, -7` suma `-2`). Son un problema grave: cada vuelta al ciclo reduce el costo total, así que la "distancia mínima" tiende a **−∞** y el camino más corto **deja de estar definido**. Por eso Dijkstra no los soporta.

- **Detección con Bellman-Ford** ([CicloNegativo.java](CicloNegativo.java)): tras las V−1 iteraciones normales, se hace una **iteración extra**; si alguna arista todavía se puede relajar, hay un ciclo negativo. Con el arreglo `parent[]` se retrocede V veces (para caer dentro del ciclo) y se recorre hasta cerrarlo, obteniendo sus nodos.
- **Detección con Floyd-Warshall** ([FloydWarshall.java](FloydWarshall.java)): si al terminar `dist[i][i] < 0` para algún nodo i, existe un ciclo negativo que pasa por i.

> 💡 **Nota:** Cada archivo `.java` contiene su propio método `main` con un ejemplo práctico para que puedas compilar y ejecutar el algoritmo de forma independiente.

### 2. Ejercicios resueltos

- **[Soluciones.java](Soluciones.java)**: ejercicios 1–11 (los de la presentación: trazas a mano de BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, orden topológico, Union-Find y Kruskal).
- **[SolucionesExtra.java](SolucionesExtra.java)**: ejercicios 12–29, variantes de DFS, BFS y Dijkstra (enunciados en [EJERCICIOS_EXTRA.md](EJERCICIOS_EXTRA.md)).
- **[SolucionesJueces.java](SolucionesJueces.java)**: ejercicios 30–35, **problemas reales de jueces online** resueltos con BFS y DFS. Cada solución se ejecuta contra el caso de ejemplo oficial del enunciado y se compara con la salida esperada:

| # | Problema | Juez | Algoritmo | Idea clave |
|---|----------|------|-----------|------------|
| 30 | [Counting Rooms](https://cses.fi/problemset/task/1192) | CSES 1192 | DFS | La grilla es un grafo: contar componentes conexas (con pila explícita, no recursión) |
| 31 | [Labyrinth](https://cses.fi/problemset/task/1193) | CSES 1193 | BFS | Camino mínimo + reconstrucción de la ruta con `padre[]` |
| 32 | [Cyclic Components](https://codeforces.com/problemset/problem/977/E) | Codeforces 977E | DFS | Una componente es un ciclo ⟺ todos sus vértices tienen grado 2 |
| 33 | [Two Buttons](https://codeforces.com/problemset/problem/520/B) | Codeforces 520B | BFS | El grafo no está dado: nodo = número en pantalla, arista = pulsación |
| 34 | [BFS: Shortest Reach](https://www.hackerrank.com/challenges/bfsshortreach/problem) | HackerRank | BFS | Peso uniforme (6) ⇒ basta BFS, no hace falta Dijkstra |
| 35 | [Roads and Libraries](https://www.hackerrank.com/challenges/torque-and-development/problem) | HackerRank | DFS | Contar componentes conexas + decisión de costo (¡usar `long`!) |

```bash
javac SolucionesJueces.java && java SolucionesJueces
```

### 3. Presentación Interactiva (HTML)

- **[index.html](index.html)**: Es una presentación standalone (sin dependencias externas) diseñada con una estética *premium* y moderna.

**Características de la presentación:**
- 🎬 **Animaciones Interactivas:** Visualizaciones paso a paso (en *Canvas*) para ver cómo se ejecuta cada algoritmo gráficamente.
- 🧩 **Ejercicios y Desafíos:** 17 problemas de diferentes niveles de dificultad (11 de traza a mano + 6 de jueces online), con botones para revelar la solución paso a paso.
- ⌨️ **Navegación Intuitiva:** Se puede navegar usando las flechas del teclado (`←` y `→`).
- 📊 **Comparativas:** Tablas para entender cuándo usar cada algoritmo.

## 🛠️ Cómo Ejecutar el Código

Asegúrate de tener instalado el [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/).

Para compilar y ejecutar cualquier algoritmo, abre tu terminal y usa los siguientes comandos:

```bash
# Compilar un algoritmo (ej: FloydWarshall)
javac FloydWarshall.java

# Ejecutar el algoritmo
java FloydWarshall
```

## 🌐 Cómo Ver la Presentación

Simplemente haz doble clic en el archivo `index.html` para abrirlo en tu navegador web favorito (Chrome, Firefox, Safari, Edge, etc.). No requiere conexión a internet ni levantar un servidor local.
