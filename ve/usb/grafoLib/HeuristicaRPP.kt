/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

package ve.usb.grafoLib

import kotlin.system.measureTimeMillis
import kotlin.system.exitProcess
import kotlin.Double.Companion.POSITIVE_INFINITY
import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.lang.StringBuilder

// *******************************************************
// esPar puede utilizar g.grado() ?
// ay compadre, seguro que el peso es entero siempre no?
//
//
// *******************************************************

/**
 * Implementación del algoritmo heurístico para resolver el
 * Problema del Cartero Rural (RPP) basado en el algoritmo
 * propuesto por Christofides et al.
 */
public class HeuristicaRPP {
    companion object {
        
        /* ---------------- ALGORITMO ---------------- */

        private val mapa = HashMap<Int, Int>()
        private var mapaInverso = IntArray(0)
        private val numVR: Int
            get() = mapa.size
        
        /**
         * Ejecuta el algoritmo propuesto por Christofides et al. para
         * hallar una solución al problema RPP.
         * 
         * Tiempo de ejecución: O(?).
         * Precondición:  [g] es un grafo no dirigido.
         *                [R] es un conjunto de aristas requeridos de [g].
         *                [usarVertexScan] es un booleano que indica si se 
         *                emplea el apareamiento perfecto Vertex Scan o Ávido.
         * Postcondición: [ejecutarAlgoritmo] es un iterable de Aristas que 
         *                que representa el camino solución del problema RPP.
         */
        private fun ejecutarAlgoritmo(
            g: GrafoNoDirigido,
            R: MutableSet<Arista>,
            usarVertexScan: Boolean
        ): Iterable<Arista> {
            // Crea grafo G_R = <V_R, R> (V_R son los vértices de R)
            var gP = GrafoNoDirigido(numVR)
            R.forEach {
                val u = it.cualquieraDeLosVertices()
                val v = it.elOtroVertice(u)

                // Agrega las aristas mapeadas
                gP.agregarArista(Arista(f(u), f(v), it.peso())) 
            }

            // Verifica si G' es conexo y par
            val cc = ComponentesConexasDFSIter(gP)
            val numCC = cc.numeroDeComponentesConexas()
            val esConexo = numCC == 1
            
            // Obtiene la matriz de costos de caminos de costo mínimo
            val (matCCM, dijks) = calcularCostosMinimos(g)

            // Si G' no es conexo (sin importar si es o no par)
            var mapaLista: MutableList<Int>
            if (!esConexo) {
                // Líneas 9 a 15
                /* Construye grafo G_t de componentes conexas de G'
                (El peso de cada arista es el del camino de costo minimo de (vi, vj)) */
                val gT = GrafoNoDirigido(numCC)

                // Crea un arreglo con los vertices de cada componente conexa
                val vertCC = Array(numCC) { ArrayList<Int>() }
                for (v in 0 until numVR) vertCC[cc.obtenerComponente(v)].add(v)

                // Por cada par de componentes conexas, hallar costo mínimo
                val mapaEt0 = HashMap<Pair<Int, Int>, Pair<Int, Int>>()
                
                for (u in 0 until numCC) {
                    for (v in u+1 until numCC) {
                        val (s, t, costo) = costoMinComponente(matCCM, vertCC[u], vertCC[v])
                        val lado = Arista(u, v, costo)
                        gT.agregarArista(lado)
                        
                        mapaEt0[Pair(u, v)] = Pair(s, t)
                    }
                }

                // Obtiene los lados del árbol mínimo cobertor y los lados asociados a CCM
                val eMST = ArbolMinimoCobertorPrim(gT).obtenerLados()

                val Et = mutableSetOf<Arista>()
                eMST.forEach {
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    
                    val (s, t) = mapaEt0[Pair(u, v)]?: mapaEt0[Pair(v, u)]!!

                    dijks[s].obtenerCaminoDeCostoMinimo(t).forEach { lado -> Et.add(lado) }
                }

                // Agrega a G' vértices en E_t que no se encuentren VR
                mapaLista = mapaInverso.toMutableList()
                Et.forEach {
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    
                    if (!mapa.containsKey(u)) {
                        mapa[u] = numVR
                        mapaLista.add(u)
                    }

                    if (!mapa.containsKey(v)) {
                        mapa[v] = numVR
                        mapaLista.add(v)
                    }
                }

                mapaInverso = mapaLista.toIntArray()

                // Agrega a G' los lados E_t, se permite lados duplicados
                val temp = gP
                gP = GrafoNoDirigido(numVR)
                temp.aristas().forEach { gP.agregarArista(it) }
                Et.forEach { 
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    gP.agregarArista(Arista(f(u), f(v), it.peso()))
                }

            }

            if (!esConexo || !esPar(gP)) {
                // Determina el conjunto de vértices V_0
                // mapa GR -> G0
                val mapaV0 = HashMap<Int, Int>()
                val mapaInversoV0 = mutableListOf<Int>()

                for (u in 0 until gP.obtenerNumeroDeVertices()) {
                    if (gP.grado(u) % 2 == 1) {
                        mapaV0[u] = mapaInversoV0.size
                        mapaInversoV0.add(u)
                    }
                }

                // Construye grafo G_0
                val numV0 = mapaV0.size
                val g0 = GrafoNoDirigido(numV0)
                for (u in 0 until numV0) {
                    for (v in u+1 until numV0) {
                        g0.agregarArista(Arista(
                                u, v,
                                matCCM[fInversa(mapaInversoV0[u])][fInversa(mapaInversoV0[v])]
                            )
                        )
                    }
                }

                // Determina apareamiento perfecto M
                val M = if (usarVertexScan) {
                    ApareamientoVertexScan(g0).obtenerApareamiento()
                 } else {
                    ApareamientoPerfectoAvido(g0).obtenerApareamiento()
                }
    
                val ladosAgregar = mutableListOf<Arista>()
                mapaLista = mapaInverso.toMutableList()

                M.forEach {
                    // Obtiene el CCM
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    
                    // G_0 -> G_R -> G
                    val (s, t) = Pair(fInversa(mapaInversoV0[u]), fInversa(mapaInversoV0[v]))

                    dijks[s].obtenerCaminoDeCostoMinimo(t).forEach {
                        val i = it.cualquieraDeLosVertices()
                        val j = it.elOtroVertice(i)
                        
                        if (!mapa.containsKey(i)) {
                            mapa[i] = numVR
                            mapaLista.add(i)
                        }

                        if (!mapa.containsKey(j)) {
                            mapa[j] = numVR
                            mapaLista.add(j)
                        }

                        ladosAgregar.add(Arista(f(i), f(j), it.peso()))
                    }
                }

                // Agrega a G' los lados de los CCM, se permite lados duplicados
                val temp = gP
                gP = GrafoNoDirigido(numVR)
                temp.aristas().forEach { gP.agregarArista(it) }
                ladosAgregar.forEach { gP.agregarArista(it) }
            }

            return CicloEulerianoGrafoNoDirigido(gP).obtenerCicloEuleriano()
        }

        /**
         * Retorna un entero que corresponde al mapeo de un vértice del
         * grafo G a un vértice válido del grafo GR.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [v] es un entero no negativo.
         * Postcondición: [f] es un entero no negativo.
         */
        private fun f(v: Int): Int = mapa[v]!!

        /**
         * Retorna un entero que corresponde al mapeo de un vértice del
         * grafo GR a un vértice válido del grafo G.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [v] es un entero no negativo.
         * Postcondición: [f] es un entero no negativo.
         */
        private fun fInversa(v: Int): Int = mapaInverso[v]

        /**
         * Retorna un booleano que indica si un grafo no dirigido [g] es par.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [g] es un grafo no dirigido.
         * Postcondición: [esPar] es -True si todos los vértices de [g]
         *                                 tienen grado par
         *                           -False de otra forma.
         */        
        private fun esPar(g: GrafoNoDirigido): Boolean {
            val n = g.obtenerNumeroDeVertices()
            val paridad = BooleanArray(n) { true }

            g.aristas().forEach {
                val u = it.cualquieraDeLosVertices()
                val v = it.elOtroVertice(u)
                
                paridad[u] = !paridad[u]
                paridad[v] = !paridad[v]
            }

            return paridad.all { it }
        }

        /**
         * Retorna una matriz de costos mínimos entre todos los vértices
         * del grafo no dirigido [g] y un arreglo con cada instancia del 
         * algoritmo de Dijkstra para grafos no dirigidos utilizado para calcular.
         * 
         * Tiempo de ejecución: O(|V|*|E|log(|V|)).
         * Precondición:  [g] es un grafo no dirigido.
         * Postcondición: [calcularCostosMinimos] es un par que contiene
         *                  - Arreglo de arreglos de doubles que representa el 
         *                    costo mínimo entre cada par de vértices de [g].
         *                  - Arreglo con instancias de la clase 
         *                    DijkstraGrafoNoDirigido desde cada vértice de [g].
         */ 
        private fun calcularCostosMinimos(g: GrafoNoDirigido):
            Pair<Array<DoubleArray>, Array<DijkstraGrafoNoDirigido>> {
                
            val n = g.obtenerNumeroDeVertices()
            val W = Array<DoubleArray>(n) { DoubleArray(n) }
            val dijks = Array<DijkstraGrafoNoDirigido?>(n) { null }
            
            for (i in 0 until n) {
                val dij = DijkstraGrafoNoDirigido(g, i)
                dijks[i] = dij

                for (j in i + 1 until n) {
                    W[i][j] = dij.costoHasta(j)
                    W[j][i] = dij.costoHasta(j)
                }
            }

            return Pair(W, dijks.filterNotNull().toTypedArray())
        }

        /**
         * Encuentra el camino de costo mínimo entre los vértices de una componente
         * conexa [comp1] y los vértices de otra componente conexa [comp2].
         * 
         * Tiempo de ejecución: O(|V|) con |V| siendo el tamaño de una fila de [mat] ???.
         * Precondición:  [mat] es una matriz de costos mínimos de un grafo.
         *                [comp1] es una componente conexa.
         *                [comp2] es una componente conexa.
         * Postcondición: [costoMinComponente] es un triple que contiene:
         *                - Un entero que representa el vértice fuente del camino 
         *                  de costo mínimo entre las componentes [comp1] y [comp2].
         *                - Un entero que representa el vértice sumidero del camino 
         *                  de costo mínimo entre las componentes [comp1] y [comp2].
         *                - Un double que representa el costo mínimo entre las 
         *                  componentes [comp1] y [comp2].
         */
        private fun costoMinComponente(
            mat: Array<DoubleArray>, 
            comp1: ArrayList<Int>, 
            comp2: ArrayList<Int>
        ): Triple<Int, Int, Double> {
            
            // Función spl
            val spl = { u: Int, v: Int -> mat[u][v] }

            var (i, j, costoMin) = Triple(-1, -1, POSITIVE_INFINITY)

            comp1.forEach { u ->
                comp2.forEach { v ->
                    val costo = spl(fInversa(u), fInversa(v))
                    if (costo < costoMin) {
                        i = fInversa(u)
                        j = fInversa(v)
                        costoMin = costo
                    }
                }
            }

            return Triple(i, j, costoMin)
        }

        /* ---------------- EJECUCIÓN DEL CLIENTE ---------------- */

        /**
         * Retorna un entero que representa el porcentaje de desviación 
         * dado una solución obtenida y la solución óptima.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [valorObt] es un entero.
         *                [valorOpt] es un entero
         * Postcondición: [desv] es un entero que representa la desviación.
         */ 
        private fun desv(valorObt: Int, valorOpt: Int): Double {
            return (valorObt - valorOpt) * 100 / valorOpt.toDouble()
        }

        /**
         * Construye un grafo no dirigido al cual se quiere solucionar el problema RPP,
         * a partir del archivo en la ruta [nombreArchivo].
         * 
         * El formato del archivo es el siguiente:
         *  -La primera línea contiene el nombre de la instancia.
         *  -La segunda línea contiene el número de componentes de la instancia.
         *  -La tercera línea contiene el número de vértices del grafo.
         *  -La cuarta línea contiene el número de aristas requeridas.
         *  -La quinta línea contieen el número de aristas no requeridas.
         *  -Las siguientes líneas contienen los vértices del grafo con el formato:
         *      (-vértice inicial-, -vértice final-)   coste      -entero-       -entero-.
         * 
         * Tiempo de ejecución: O(?).
         * Precondición:  [nombreArchivo] es un String que representa la dirección 
         *                de un archivo con los datos de un grafo no dirigido al que se 
         *                quiere solucionar el problema RPP.
         * Postcondición: [extraerDatos] es un par que contiene:
         *                - Un grafo no dirigido construido con los datos de [nombreArchivo]
         *                - Un conjunto de Aristas construido con los datos de [nombreArchivo]              
         */ 
        private fun extraerDatos(nombreArchivo: String): 
            Pair<GrafoNoDirigido, MutableSet<Arista>> {
            // Verifica si el archivo existe
            if (!File(nombreArchivo).exists()) {
                println("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
                exitProcess(1)
            }

            // Salta Nombre y Comentario
            val sc = Scanner(FileInputStream(nombreArchivo))
            sc.nextLine()   
            sc.nextLine()

            // Línea VERTICES : <nro. de vértices>
            var aux = sc.nextLine()!!.split(" ") 
            val numDeVertices = aux[aux.size - 1].trim().toInt()
            
            // Construye grafo G y conjunto R
            val g = GrafoNoDirigido(numDeVertices)
            val R = mutableSetOf<Arista>()

            // Línea ARISTAS_REQ : <nro. de lados requeridos>
            aux = sc.nextLine()!!.split(" ")
            val aristasReq = aux[aux.size - 1].trim().toInt() 

            // Línea ARISTAS_NOREQ : <nro. de lados no requeridos>
            aux = sc.nextLine()!!.split(" ")
            val aristasNoReq = aux[aux.size - 1].trim().toInt() 

            val mapaLista = ArrayList<Int>()

            // Salta LISTA_ARISTAS_REQ
            sc.nextLine()

            /* Agrega aristas requeridas a G y R y construye
            un mapeo biyectivo de V a V_R */
            repeat(aristasReq) {
                aux = Regex("[0-9]+").findAll(sc.nextLine()!!)
                    .map(MatchResult::value)
                    .toList()
                
                // Los vértices comienzan desde 0
                val (u, v, peso) = Triple(
                    aux[0].toInt() - 1,
                    aux[1].toInt() - 1, 
                    aux[2].toDouble()
                )

                val a = Arista(u, v, peso)

                if (!mapa.containsKey(u)) {
                    mapa[u] = numVR
                    mapaLista.add(u)
                }

                if (!mapa.containsKey(v)) {
                    mapa[v] = numVR
                    mapaLista.add(v)
                }

                g.agregarArista(a)
                R.add(a)
            }

            mapaInverso = mapaLista.toIntArray()

            // Salta LISTA_ARISTAS_NOREQ
            sc.nextLine()

            // Agrega aristas no requeridas a G
            repeat(aristasNoReq) {
                aux = Regex("[0-9]+").findAll(sc.nextLine()!!)
                    .map(MatchResult::value)
                    .toList()
                
                val a = Arista(
                    aux[0].toInt() - 1, 
                    aux[1].toInt() - 1, 
                    aux[2].toDouble()
                )
                g.agregarArista(a)
            }

            return Pair(g, R)
        }

        /**
         * Programa cliente.            
         */ 
        @JvmStatic fun main(args: Array<String>) {
            // Verifica argumentos
            if (args.size != 2 || (args[0] != "a" && args[0] != "v")) {
                println("Error: Usage >./runHeurRPP.sh [a|v] <ruta/a/instancia>")
                exitProcess(1)
            }

            // Extrae el grafo G y el conjunto R del archivo          
            val vertexScan = args[0] == "v"
            val (g, R) = extraerDatos(args[1])

            // Ejecuta el algoritmo RPP
            var ciclo: Iterable<Arista>
            val ms = measureTimeMillis { 
                ciclo = ejecutarAlgoritmo(g, R, vertexScan)
            }
            
            // Imprime salida
            /* ----------- PARA SALIDA ESTÁNDAR -----------
            ciclo.forEach { print("${it.cualquieraDeLosVertices() + 1} ") }
            val u = ciclo.last().cualquieraDeLosVertices()
            println(ciclo.last().elOtroVertice(u) + 1)
            
            val costoTotal = ciclo.sumOf { it.peso() }.toInt()
            println(costoTotal)
            println("%.3f segs.".format(ms / 1000.0))
            ------------------------------------------------- */

            val nombre = args[1].split("/").last()
            val costo = ciclo.sumOf { it.peso() }.toInt()
            val tiempo = ms / 1000.0
            println("$nombre,$costo,$tiempo")
        }
    }
}