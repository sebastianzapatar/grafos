import os

HTML_TO_INSERT = """
    <!-- ===================== SLIDE: KAHN (ORDEN TOPOLÓGICO) ===================== -->
    <div class="slide">
        <span class="slide-number-badge"></span>
        <div class="slide-inner">
            <h2 class="slide-title gradient-3">Kahn — Orden Topológico</h2>
            <p class="slide-subtitle">Resuelve dependencias en un DAG usando grados de entrada (In-Degree) y una Cola · O(V + E)</p>

            <div class="two-cols-wide">
                <div>
                    <div class="canvas-container">
                        <canvas id="kahnCanvas" width="520" height="320"></canvas>
                        <div class="animation-controls">
                            <button class="anim-btn primary" onclick="startKahn()">▶ Ejecutar</button>
                            <button class="anim-btn" onclick="pauseKahn()">⏸ Pausar</button>
                            <button class="anim-btn" onclick="resetKahn()">↺ Reset</button>
                            <span class="anim-status" id="kahnStatus">Listo</span>
                        </div>
                    </div>
                    <div class="pseudo-code">
<span class="kw">Kahn</span>(DAG):
    inDegree[*] ← <span class="nb">0</span>
    <span class="kw">para cada</span> arista (u→v): inDegree[v]++
    cola ← {nodos con inDegree == <span class="nb">0</span>}
    <span class="kw">mientras</span> cola no vacía:
        u ← cola.<span class="fn">dequeue</span>()
        procesar(u)
        <span class="kw">para cada</span> vecino v <span class="kw">de</span> u:
            inDegree[v]--
            <span class="kw">si</span> inDegree[v] == <span class="nb">0</span>:
                cola.<span class="fn">enqueue</span>(v)
                    </div>
                </div>
                <div>
                    <div class="code-block" style="max-height: 480px;">
                        <span class="code-label">Java</span>
<span class="kw">public void</span> <span class="fn">topologicalSort</span>() {
    <span class="tp">int</span>[] inDegree = <span class="kw">new</span> <span class="tp">int</span>[V];
    <span class="kw">for</span> (<span class="tp">int</span> i = <span class="nb">0</span>; i &lt; V; i++) {
        <span class="kw">for</span> (<span class="tp">int</span> neighbor : adj.get(i)) {
            inDegree[neighbor]++;
        }
    }

    Queue&lt;<span class="tp">Integer</span>&gt; queue = <span class="kw">new</span> LinkedList&lt;&gt;();
    <span class="kw">for</span> (<span class="tp">int</span> i = <span class="nb">0</span>; i &lt; V; i++) {
        <span class="kw">if</span> (inDegree[i] == <span class="nb">0</span>) queue.add(i);
    }

    List&lt;<span class="tp">Integer</span>&gt; topOrder = <span class="kw">new</span> ArrayList&lt;&gt;();
    <span class="kw">while</span> (!queue.isEmpty()) {
        <span class="tp">int</span> node = queue.<span class="fn">poll</span>();
        topOrder.add(node);

        <span class="kw">for</span> (<span class="tp">int</span> neighbor : adj.get(node)) {
            inDegree[neighbor]--;
            <span class="kw">if</span> (inDegree[neighbor] == <span class="nb">0</span>) {
                queue.add(neighbor);
            }
        }
    }
}
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- ===================== SLIDE: KRUSKAL (MST) ===================== -->
    <div class="slide">
        <span class="slide-number-badge"></span>
        <div class="slide-inner">
            <h2 class="slide-title gradient-4">Kruskal — MST</h2>
            <p class="slide-subtitle">Greedy por Aristas. Usa Union-Find (Disjoint Set) para evitar ciclos · O(E log E)</p>

            <div class="two-cols-wide">
                <div>
                    <div class="canvas-container">
                        <canvas id="kruskalCanvas" width="520" height="320"></canvas>
                        <div class="animation-controls">
                            <button class="anim-btn primary" onclick="startKruskal()">▶ Ejecutar</button>
                            <button class="anim-btn" onclick="pauseKruskal()">⏸ Pausar</button>
                            <button class="anim-btn" onclick="resetKruskal()">↺ Reset</button>
                            <span class="anim-status" id="kruskalStatus">Listo</span>
                        </div>
                    </div>
                    <div class="pseudo-code">
<span class="kw">Kruskal</span>(grafo):
    ordenar aristas por peso ascendente
    mst ← []
    uf ← UnionFind(V)
    <span class="kw">para cada</span> arista (u-v, peso) <span class="kw">en</span> orden:
        <span class="kw">si</span> uf.<span class="fn">find</span>(u) ≠ uf.<span class="fn">find</span>(v):
            uf.<span class="fn">union</span>(u, v)
            mst.<span class="fn">add</span>(arista)
            <span class="kw">si</span> mst.tamaño() == V-1:
                <span class="kw">romper</span>
    <span class="kw">retornar</span> mst
                    </div>
                </div>
                <div>
                    <div class="code-block" style="max-height: 480px;">
                        <span class="code-label">Java</span>
<span class="kw">public void</span> <span class="fn">kruskal</span>() {
    <span class="cm">// 1. Ordenar aristas por peso ascendente</span>
    Collections.<span class="fn">sort</span>(edges);

    UnionFind uf = <span class="kw">new</span> UnionFind(V);
    List&lt;Edge&gt; mst = <span class="kw">new</span> ArrayList&lt;&gt;();
    <span class="tp">int</span> costoTotal = <span class="nb">0</span>;

    <span class="cm">// 2. Procesar cada arista</span>
    <span class="kw">for</span> (Edge e : edges) {
        <span class="cm">// union() devuelve true si u y v</span>
        <span class="cm">// estaban en conjuntos diferentes</span>
        <span class="kw">if</span> (uf.<span class="fn">union</span>(e.u, e.v)) {
            mst.add(e);
            costoTotal += e.weight;
            
            <span class="cm">// 3. Parar al tener V-1 aristas</span>
            <span class="kw">if</span> (mst.size() == V - <span class="nb">1</span>) <span class="kw">break</span>;
        }
    }
}
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- ===================== SLIDE: PRIM (MST) ===================== -->
    <div class="slide">
        <span class="slide-number-badge"></span>
        <div class="slide-inner">
            <h2 class="slide-title gradient-2">Prim — MST</h2>
            <p class="slide-subtitle">Greedy por Nodos. Hace crecer un único árbol usando PriorityQueue · O((V+E) log V)</p>

            <div class="two-cols-wide">
                <div>
                    <div class="canvas-container">
                        <canvas id="primCanvas" width="520" height="320"></canvas>
                        <div class="animation-controls">
                            <button class="anim-btn primary" onclick="startPrim()">▶ Ejecutar</button>
                            <button class="anim-btn" onclick="pausePrim()">⏸ Pausar</button>
                            <button class="anim-btn" onclick="resetPrim()">↺ Reset</button>
                            <span class="anim-status" id="primStatus">Listo</span>
                        </div>
                    </div>
                    <div class="pseudo-code">
<span class="kw">Prim</span>(grafo, inicio):
    minCost[*] ← ∞; minCost[inicio] ← <span class="nb">0</span>
    pq ← MinHeap con (inicio, <span class="nb">0</span>)
    <span class="kw">mientras</span> pq no vacía:
        u ← pq.<span class="fn">extractMin</span>()
        <span class="kw">si</span> enArbol[u]: <span class="kw">continuar</span>
        enArbol[u] ← <span class="kw">true</span>
        <span class="kw">para cada</span> vecino v <span class="kw">de</span> u con peso w:
            <span class="kw">si</span> no enArbol[v] y w < minCost[v]:
                minCost[v] ← w
                padre[v] ← u
                pq.<span class="fn">insert</span>(v, w)
                    </div>
                </div>
                <div>
                    <div class="code-block" style="max-height: 480px;">
                        <span class="code-label">Java</span>
<span class="kw">public void</span> <span class="fn">prim</span>(<span class="tp">int</span> start) {
    <span class="tp">boolean</span>[] enArbol = <span class="kw">new</span> <span class="tp">boolean</span>[V];
    <span class="tp">int</span>[] minCost = <span class="kw">new</span> <span class="tp">int</span>[V];
    <span class="tp">int</span>[] parent = <span class="kw">new</span> <span class="tp">int</span>[V];
    Arrays.<span class="fn">fill</span>(minCost, Integer.MAX_VALUE);
    Arrays.<span class="fn">fill</span>(parent, -<span class="nb">1</span>);

    minCost[start] = <span class="nb">0</span>;
    PriorityQueue&lt;<span class="tp">int</span>[]&gt; pq = <span class="kw">new</span> PriorityQueue&lt;&gt;(
        Comparator.<span class="fn">comparingInt</span>(a -&gt; a[<span class="nb">1</span>]));
    pq.add(<span class="kw">new</span> <span class="tp">int</span>[]{start, <span class="nb">0</span>});

    <span class="kw">while</span> (!pq.isEmpty()) {
        <span class="tp">int</span> u = pq.<span class="fn">poll</span>()[<span class="nb">0</span>];
        <span class="kw">if</span> (enArbol[u]) <span class="kw">continue</span>;
        
        enArbol[u] = <span class="kw">true</span>;
        
        <span class="kw">for</span> (Edge e : adj.get(u)) {
            <span class="kw">if</span> (!enArbol[e.target] 
             &amp;&amp; e.weight &lt; minCost[e.target]) {
                minCost[e.target] = e.weight;
                parent[e.target] = u;
                pq.add(<span class="kw">new</span> <span class="tp">int</span>[]{e.target, e.weight});
            }
        }
    }
}
                    </div>
                </div>
            </div>
        </div>
    </div>
"""

JS_TO_INSERT = """
// ============================================================
// KAHN ANIMATION
// ============================================================
const kahnNodes = [
    { id: '0', x: 260, y: 50, label: '0' },
    { id: '1', x: 420, y: 140, label: '1' },
    { id: '2', x: 100, y: 140, label: '2' },
    { id: '3', x: 260, y: 140, label: '3' },
    { id: '4', x: 360, y: 50, label: '4' },
    { id: '5', x: 160, y: 50, label: '5' }
];

const kahnEdges = [
    { from: '5', to: '2' },
    { from: '5', to: '0' },
    { from: '4', to: '0' },
    { from: '4', to: '1' },
    { from: '2', to: '3' },
    { from: '3', to: '1' },
];

const kahnAnimator = new GraphAnimator('kahnCanvas', kahnNodes, kahnEdges, true);
kahnAnimator.animSteps = [
    { dist: { '0': 'in:2', '1': 'in:2', '2': 'in:1', '3': 'in:1', '4': 'in:0', '5': 'in:0' }, message: 'Paso 1: Calcular grados de entrada (inDegree)' },
    { nodeState: { '4': 'visiting', '5': 'visiting' }, message: 'Nodos con inDegree=0 (4 y 5) entran a la cola' },
    { nodeState: { '5': 'visited' }, edgeState: { 0: 'highlight', 1: 'highlight' }, message: 'Desencolar 5. Reducir inDegree de vecinos (0 y 2)' },
    { dist: { '0': 'in:1', '2': 'in:0' }, message: 'inDegree[0]=1, inDegree[2]=0' },
    { nodeState: { '2': 'visiting' }, edgeState: { 0: 'relaxed', 1: 'relaxed' }, message: 'inDegree[2]=0 -> entra a la cola' },
    { nodeState: { '4': 'visited' }, edgeState: { 2: 'highlight', 3: 'highlight' }, message: 'Desencolar 4. Reducir inDegree de vecinos (0 y 1)' },
    { dist: { '0': 'in:0', '1': 'in:1' }, message: 'inDegree[0]=0, inDegree[1]=1' },
    { nodeState: { '0': 'visiting' }, edgeState: { 2: 'relaxed', 3: 'relaxed' }, message: 'inDegree[0]=0 -> entra a la cola' },
    { nodeState: { '2': 'visited' }, edgeState: { 4: 'highlight' }, message: 'Desencolar 2. Reducir inDegree de vecino (3)' },
    { dist: { '3': 'in:0' }, nodeState: { '3': 'visiting' }, edgeState: { 4: 'relaxed' }, message: 'inDegree[3]=0 -> entra a la cola' },
    { nodeState: { '0': 'visited' }, message: 'Desencolar 0. (No tiene vecinos)' },
    { nodeState: { '3': 'visited' }, edgeState: { 5: 'highlight' }, message: 'Desencolar 3. Reducir inDegree de vecino (1)' },
    { dist: { '1': 'in:0' }, nodeState: { '1': 'visiting' }, edgeState: { 5: 'relaxed' }, message: 'inDegree[1]=0 -> entra a la cola' },
    { nodeState: { '1': 'visited' }, message: 'Desencolar 1. (No tiene vecinos)' },
    { nodeState: { '0': 'finished', '1': 'finished', '2': 'finished', '3': 'finished', '4': 'finished', '5': 'finished' }, clearEdges: true, message: '✓ Orden Topológico: 5, 4, 2, 0, 3, 1' },
];

function startKahn() { kahnAnimator.runAnimation(document.getElementById('kahnStatus')); }
function pauseKahn() { kahnAnimator.pause(); document.getElementById('kahnStatus').textContent = '⏸ Pausado'; }
function resetKahn() { kahnAnimator.reset(); document.getElementById('kahnStatus').textContent = 'Listo'; }

// ============================================================
// KRUSKAL ANIMATION
// ============================================================
const mstNodes = [
    { id: '0', x: 260, y: 50, label: '0' },
    { id: '1', x: 400, y: 140, label: '1' },
    { id: '2', x: 120, y: 140, label: '2' },
    { id: '3', x: 260, y: 230, label: '3' },
];

const mstEdges = [
    { from: '2', to: '3', weight: 4 }, // 0
    { from: '0', to: '3', weight: 5 }, // 1
    { from: '0', to: '2', weight: 6 }, // 2
    { from: '0', to: '1', weight: 10 },// 3
    { from: '1', to: '3', weight: 15 },// 4
];

// false for undirected graph
const kruskalAnimator = new GraphAnimator('kruskalCanvas', mstNodes, mstEdges, false);
kruskalAnimator.animSteps = [
    { message: 'Paso 1: Ordenar aristas por peso ascendente' },
    { message: 'Aristas ordenadas: (2-3):4, (0-3):5, (0-2):6, (0-1):10, (1-3):15' },
    { edgeState: { 0: 'highlight' }, message: 'Evaluar 2-3 (peso 4). ¿Tienen distinto padre?' },
    { nodeState: { '2': 'visiting', '3': 'visiting' }, edgeState: { 0: 'relaxed' }, message: 'uf.union(2, 3) -> ¡Añadida al MST!' },
    { edgeState: { 1: 'highlight' }, message: 'Evaluar 0-3 (peso 5).' },
    { nodeState: { '0': 'visiting' }, edgeState: { 1: 'relaxed' }, message: 'uf.union(0, 3) -> ¡Añadida al MST!' },
    { edgeState: { 2: 'highlight' }, message: 'Evaluar 0-2 (peso 6).' },
    { edgeState: { 2: 'default' }, message: 'uf.find(0) == uf.find(2). ¡DESCARTADA! Formaría ciclo.' },
    { edgeState: { 3: 'highlight' }, message: 'Evaluar 0-1 (peso 10).' },
    { nodeState: { '1': 'visiting' }, edgeState: { 3: 'relaxed' }, message: 'uf.union(0, 1) -> ¡Añadida al MST!' },
    { nodeState: { '0': 'finished', '1': 'finished', '2': 'finished', '3': 'finished' }, message: 'mst.size() == V-1 (3). ¡Kruskal terminado!' }
];

function startKruskal() { kruskalAnimator.runAnimation(document.getElementById('kruskalStatus')); }
function pauseKruskal() { kruskalAnimator.pause(); document.getElementById('kruskalStatus').textContent = '⏸ Pausado'; }
function resetKruskal() { kruskalAnimator.reset(); document.getElementById('kruskalStatus').textContent = 'Listo'; }


// ============================================================
// PRIM ANIMATION
// ============================================================
// We reuse mstNodes and mstEdges, but we need a different animator instance
const primAnimator = new GraphAnimator('primCanvas', mstNodes, mstEdges, false);
primAnimator.animSteps = [
    { dist: { '0': 0, '1': '∞', '2': '∞', '3': '∞' }, nodeState: { '0': 'visiting' }, message: 'Inicio: arrancar en nodo 0. minCost[0]=0' },
    { nodeState: { '0': 'visited' }, edgeState: { 1: 'highlight', 2: 'highlight', 3: 'highlight' }, message: '0 entra al árbol. Evaluar aristas salientes.' },
    { dist: { '1': 10, '2': 6, '3': 5 }, edgeState: { 1: 'relaxed', 2: 'relaxed', 3: 'relaxed' }, message: 'Actualizar minCost: 1(10), 2(6), 3(5). PQ = {3, 2, 1}' },
    { nodeState: { '3': 'visiting' }, message: 'Extraer nodo con menor costo (nodo 3, costo 5).' },
    { nodeState: { '3': 'visited' }, edgeState: { 1: 'relaxed', 2: 'default', 3: 'default', 0: 'highlight', 4: 'highlight' }, message: '3 entra al árbol. Evaluar sus aristas salientes (3-2, 3-1).' },
    { dist: { '1': 10, '2': 4 }, edgeState: { 0: 'relaxed' }, message: 'Actualizar minCost: 2(4) ya que 4 < 6. 1 no mejora (15>10).' },
    { nodeState: { '2': 'visiting' }, message: 'Extraer nodo 2 (menor costo actual 4).' },
    { nodeState: { '2': 'visited' }, edgeState: { 2: 'highlight' }, message: '2 entra al árbol. Arista 2-0 descartada (0 ya en árbol).' },
    { nodeState: { '1': 'visiting' }, message: 'Extraer nodo 1 (costo 10).' },
    { nodeState: { '1': 'visited' }, edgeState: { 3: 'relaxed' }, message: '1 entra al árbol.' },
    { nodeState: { '0': 'finished', '1': 'finished', '2': 'finished', '3': 'finished' }, edgeState: { 0: 'relaxed', 1: 'relaxed', 3: 'relaxed' }, message: '✓ Prim completado. MST = 19' }
];

function startPrim() { primAnimator.runAnimation(document.getElementById('primStatus')); }
function pausePrim() { primAnimator.pause(); document.getElementById('primStatus').textContent = '⏸ Pausado'; }
function resetPrim() { primAnimator.reset(); document.getElementById('primStatus').textContent = 'Listo'; }
"""

with open('/Users/sebastianzapata/Documents/compiladores20262/grafos/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Insert HTML before slide 9
slide_9_marker = "<!-- ===================== SLIDE 9: COMPARATIVA ===================== -->"
if slide_9_marker in content:
    content = content.replace(slide_9_marker, HTML_TO_INSERT + "\\n    " + slide_9_marker)
else:
    print("Could not find Slide 9 marker.")

# Insert JS before </script>
js_marker = "</script>"
if js_marker in content:
    # reverse replace using rpartition
    before, sep, after = content.rpartition(js_marker)
    content = before + JS_TO_INSERT + "\\n" + sep + after

with open('/Users/sebastianzapata/Documents/compiladores20262/grafos/index.html', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated index.html successfully.")
