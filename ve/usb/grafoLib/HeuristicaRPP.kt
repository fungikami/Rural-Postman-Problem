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

/**
 * Implementación del algoritmo heurístico para resolver el
 * Problema del Cartero Rural (RPP) basado en el algoritmo
 * propuesto por Christofides et al. y modificado por Pearn 
 * y Wu.
 */
public class HeuristicaRPP {
    companion object {
        
        /* ---------------- ALGORITMO ---------------- */

        private val mapa = HashMap<Int, Int>()
        private val mapaInverso = mutableListOf<Int>()
        private val numVR: Int
            get() = mapa.size
        
        /**
         * Ejecuta el algoritmo propuesto por Christofides et al. y modificado
         * por Pearn y Wu para hallar una solución al problema RPP.
         * 
         * Tiempo de ejecución: O(|V|³).
         * Precondición:  [g] es un grafo no dirigido.
         *                [R] es un conjunto de aristas requeridas del RPP.
         *                [usarVertexScan] es un booleano que indica si se 
         *                emplea el apareamiento perfecto Vertex Scan o Ávido.
         * Postcondición: [algoritmoHeuristicoRPP] es un iterable de Aristas que 
         *                que representa el camino solución del problema RPP.
         */
        private fun algoritmoHeuristicoRPP(
            g: GrafoNoDirigido,
            R: MutableSet<Arista>,
            usarVertexScan: Boolean
        ): Iterable<Arista> {
            // Crea grafo GR = <VR, R> (VR son los vértices de R)
            var gP = crearGRInicial(R)

            // Verifica si G' = GR es conexo y par
            val cc = ComponentesConexasDFSIter(gP)
            val numCC = cc.numeroDeComponentesConexas()
            val esConexo = numCC == 1
            
            // Obtiene la matriz de costos de caminos de costo mínimo
            val (matCCM, dijks) = calcularCostosMinimos(g)

            // Función spl. Dom: V
            val spl = { u: Int, v: Int -> matCCM[u][v] }

            // Si G' no es conexo (sin importar si es o no par)
            if (!esConexo) {
                // Por cada par de componentes conexas, hallar costo mínimo
                val ccmEntreComponentes = HashMap<Pair<Int, Int>, Pair<Int, Int>>()

                /** 
                 * Función que retorna el camino de costo mínimo en el grafo G
                 * entre las componentes conexas [u] y [v] de GCC.
                 */
                val caminoEntreComponentes: (Int, Int) -> Iterable<Arista> = {
                    u: Int, v: Int -> 
                    val (s, t) = ccmEntreComponentes[Pair(u, v)]
                        ?: ccmEntreComponentes[Pair(v, u)]!!
                    dijks[s].obtenerCaminoDeCostoMinimo(t)
                }
                
                // Construye grafo G_t de componentes conexas de G'
                val gT = crearGt(cc, ccmEntreComponentes, spl)

                // Obtiene los lados del árbol mínimo cobertor y los lados asociados a CCM
                val eMST = ArbolMinimoCobertorPrim(gT).obtenerLados()

                val eT = mutableSetOf<Arista>()
                eMST.forEach {
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    
                    caminoEntreComponentes(u, v).forEach { lado -> eT.add(lado) }
                }

                // Agrega a G' vértices en E_t que no se encuentren VR
                eT.forEach { agregarAVR(it) }

                // Agrega a G' los lados E_t, se permite lados duplicados
                gP = aumentarGP(gP)
                eT.forEach { gP.agregarArista(mapearAristaV_A_VR(it)) }
            }

            if (!esConexo || !esPar(gP)) {
                /**
                 * Crea un mapa de GR = <VR, R> -> G0 = <V0, E0>
                 * Solo se crea el mapeo inverso mediante la función h^-1 de 
                 * G0 = <V0, E0> -> GR = <VR, R> porque la función h no se usa. 
                 */
                val mapaInversoV0 = mutableListOf<Int>()
                val hInv = { v: Int -> mapaInversoV0[v] }

                for (u in 0 until gP.obtenerNumeroDeVertices()) {
                    if (gP.grado(u) % 2 == 1) mapaInversoV0.add(u)
                }

                // Construye grafo G0 = <V0, E0> con costos del CCM de G
                val numV0 = mapaInversoV0.size
                val g0 = GrafoNoDirigido(numV0)
                for (u in 0 until numV0) {
                    for (v in u+1 until numV0) {
                        g0.agregarArista(Arista(
                                u, v,
                                spl(fInv(hInv(u)), fInv(hInv(v)))
                            )
                        )
                    }
                }

                // Determina apareamiento perfecto M al grafo G0
                val M = if (usarVertexScan) {
                    ApareamientoVertexScan(g0).obtenerApareamiento()
                } else {
                    ApareamientoPerfectoAvido(g0).obtenerApareamiento()
                }

                // Determina los nuevos lados a agregar en G'
                val ladosAgregar = mutableListOf<Arista>()
                M.forEach {
                    // Obtiene el CCM
                    val u = it.cualquieraDeLosVertices()
                    val v = it.elOtroVertice(u)
                    
                    // G0 -> G' -> G
                    val (s, t) = Pair(fInv(hInv(u)), fInv(hInv(v)))

                    dijks[s].obtenerCaminoDeCostoMinimo(t).forEach {
                        agregarAVR(it)
                        ladosAgregar.add(mapearAristaV_A_VR(it))
                    }
                }

                // Agrega a G' los lados de los CCM, se permite lados duplicados
                gP = aumentarGP(gP)
                ladosAgregar.forEach { gP.agregarArista(it) }
            }

            return CicloEulerianoGrafoNoDirigido(gP).obtenerCicloEuleriano()
        }

        /**
         * Retorna un entero que corresponde al mapeo de un vértice del
         * grafo G = <V, E> a un vértice válido del grafo GR = <VR, R>.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [v] es un entero no negativo.
         * Postcondición: [f] es un entero no negativo.
         */
        private fun f(v: Int): Int = mapa[v]!!

        /**
         * Retorna un entero que corresponde al mapeo de un vértice del
         * grafo GR = <VR, R> a un vértice válido del grafo G = <V, E>.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [v] es un entero no negativo.
         * Postcondición: [fInv] es un entero no negativo.
         */
        private fun fInv(v: Int): Int = mapaInverso[v]

        /**
         * Agrega, de no existir, los vértices de una Arista [a] 
         * al conjunto de vértices VR.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [a] es una Arista de GR.
         * Postcondición: VR = VR0 U {u, v}.
         */
        private fun agregarAVR(a: Arista) {
            val u = a.cualquieraDeLosVertices()
            val v = a.elOtroVertice(u)
            
            intArrayOf(u, v).forEach {
                if (!mapa.containsKey(it)) {
                    mapa[it] = numVR
                    mapaInverso.add(it)
                }
            }
        }

        /**
         * Retorna el mapeo de una arista de G = <V, E> a GR = <VR, R>.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición:  [a] es una Arista del grafo G.
         * Postcondición: [mapearAristaV_A_VR] es una Arista del grafo GR.
         */
        private fun mapearAristaV_A_VR(a: Arista): Arista {
            val u = a.cualquieraDeLosVertices()
            val v = a.elOtroVertice(u)
            return Arista(f(u), f(v), a.peso())
        }

        /**
         * Retorna un grafo no dirigido donde los vértices corresponden
         * a los vértices de [R] mapeados.
         * 
         * Tiempo de ejecución: O(|VR| + |R|).
         * Precondición:  [R] es un conjunto de Aristas a agregar al grafo.
         * Postcondición: [crearGRInicial] es el grafo GR = <VR, R>.
         */
        private fun crearGRInicial(R: MutableSet<Arista>): GrafoNoDirigido {
            val gR = GrafoNoDirigido(numVR)
            R.forEach { gR.agregarArista(mapearAristaV_A_VR(it)) }
            return gR
        }

        /**
         * Aumenta el tamaño del grafo no dirigido [gP] = <VR0, ER0>
         * según el tamaño actual de VR.
         * 
         * Tiempo de ejecución: O(|VR| + |ER0|).
         * Precondición:  [gP] es un grafo no dirigido.
         * Postcondición: gP = <VR, ER0>
         */
        private fun aumentarGP(gP: GrafoNoDirigido): GrafoNoDirigido {
            val gPNuevo = GrafoNoDirigido(numVR)
            gP.aristas().forEach { gPNuevo.agregarArista(it) }
            return gPNuevo
        }

        /**
         * Retorna un grafo no dirigido completo donde cada vértice corresponde
         * a una componente conexa de [cc] y el peso de cada lado corresponde al
         * mínimo valor de [spl] entre los vértices de las componentes conexas.
         * 
         * Construye un [mapaCCM] como se indica en la postcondición.
         * 
         * Tiempo de ejecución: O(|VT|⁴).
         * Precondición:  [cc] es una instancia de ComponentesConexasDFSIter 
         *                     correspondiente a GCC.
         *                [mapaCCM] es un HashMap que mapea pares a pares (de enteros).
         *                [spl] es una función que retorna el costo del CCM entre dos vértices.
         * Postcondición: [crearGt] es un grafo no dirigido completo Gt.
         *                [mapaCCM] contiene el mapeo de un lado de [crearGt] a los vértices 
         *                          iniciales y finales del CCM correspondiente al grafo G.
         */
        private fun crearGt(
            cc: ComponentesConexasDFSIter,
            mapaCCM: HashMap<Pair<Int, Int>, Pair<Int, Int>>,
            spl: (Int, Int) -> Double
        ): GrafoNoDirigido {
            val numCC = cc.numeroDeComponentesConexas()
            val gT = GrafoNoDirigido(numCC)

            // componente[i] = lista de vértices que pertenecen a la i-ésima CC de G'
            val componente = Array(numCC) { mutableListOf<Int>() }
            for (v in 0 until numVR) componente[cc.obtenerComponente(v)].add(v)
            
            for (u in 0 until numCC) {
                for (v in u+1 until numCC) {
                    val (s, t, costo) = c_et(spl, componente[u], componente[v])
                    val lado = Arista(u, v, costo)
                    gT.agregarArista(lado)
                    
                    mapaCCM[Pair(u, v)] = Pair(s, t)
                }
            }

            return gT
        }

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
            
            return (0 until n).all { g.grado(it) % 2 == 0 }
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
         * Encuentra el camino de costo mínimo en G entre los vértices de las
         * componentes conexas [comp1] y [comp2] de GCC.
         * 
         * Tiempo de ejecución: O(comp1.size * comp2.size).
         * Precondición:  [spl] es una función que retorna el costo del CCM 
         *                entre dos vértices de G.
         *                [comp1] y [comp2] son listas de vértices.
         * Postcondición: [c_et] es un triple que contiene:
         *                - Un entero que representa el vértice inicial del CCM 
         *                  en G entre las componentes [comp1] y [comp2] de GCC
         *                - Un entero que representa el vértice final del CCM 
         *                  em G entre las componentes [comp1] y [comp2] de GCC.
         *                - Un real que representa el costo del CCM [comp2].
         */
        private fun c_et(
            spl: (Int, Int) -> Double, 
            comp1: MutableList<Int>, 
            comp2: MutableList<Int>
        ): Triple<Int, Int, Double> {
            
            var (i, j, costoMin) = Triple(-1, -1, POSITIVE_INFINITY)

            comp1.forEach { u ->
                comp2.forEach { v ->
                    val costo = spl(fInv(u), fInv(v))
                    if (costo < costoMin) {
                        i = fInv(u)
                        j = fInv(v)
                        costoMin = costo
                    }
                }
            }

            return Triple(i, j, costoMin)
        }

        // ---------------- EJECUCIÓN DEL CLIENTE ----------------
        
        /**
         * Construye un grafo no dirigido instancia de un problema RPP
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
         * Tiempo de ejecución: O(|V| + |E|).
         * Precondición:  [nombreArchivo] es un String que representa el camino 
         *                a un archivo con una instancia del problema RPP con el
         *                formato especificado.
         * Postcondición: [extraerDatos] es un par que contiene:
         *                - El grafo no dirigido que modela la instancia de entrada.
         *                - Las aristas requeridas de la instancia de entrada.              
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
                
                agregarAVR(a)
                g.agregarArista(a)
                R.add(a)
            }

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
         * Función principal que recibe como entrada los argumentos
         * de la entrada estándar indicando el algoritmo de apareamiento
         * a usar [v para VertexScan | a para el algoritmo ávido] y una ruta
         * válida a un archivo de entrada en formato URPP.
         *
         * Ejecuta el algoritmo basado en el propuesto por Christofides et al.
         * y modificado por Pearn y Wu. sobre la instancia contenida en el
         * archivo de entrada e imprime el ciclo de retorno en la salida 
         * estándar, junto con el costo del ciclo y el tiempo de ejecución.
         *
         * @throws [RuntimeException] [args].size != 2.
         * @throws [RuntimeException] [args][0] != "a" && [args][0] != "v".
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
                ciclo = algoritmoHeuristicoRPP(g, R, vertexScan)
            }
            
            // Imprime solución en salida estándar
            ciclo.forEach {
                val u = fInv(it.cualquieraDeLosVertices()) + 1
                print("$u ")
            }
            val aFinal = ciclo.last()
            val u = aFinal.cualquieraDeLosVertices()
            val v = fInv(aFinal.elOtroVertice(u)) + 1
            println("$v")
            
            val costoTotal = ciclo.sumOf { it.peso() }.toInt()
            println(costoTotal)
            println("%.3f segs.".format(ms / 1000.0))

            if (!verificarSolucionRPP(ciclo, R)) {
                println("Error: La solución no es válida.")
            }
        }

        // ---------------- VERIFICADOR DEL ALGORITMO ----------------
        /**
         * Retorna un booleano que indica si [ciclo] es una solución
         * válida al problema RPP con conjunto [R] de lados requeridos.
         * 
         * Tiempo de ejecución: O(|R| + ciclo.size).
         * Precondición:  [ciclo] es un iterable con las Aristas del ciclo
         *                        solución obtenido.
         *                [R] es el conjunto de aristas requeridas del RPP.
         * Postcondición: [verificarSolucionRPP] es
         *                  - True si [ciclo] es una solución válida al problema RPP.
         *                  - False de otra forma.
         */ 
        private fun verificarSolucionRPP(
            ciclo: Iterable<Arista>,
            R: MutableSet<Arista>
        ): Boolean {
            val RPares = mutableSetOf<Pair<Int, Int>>()          
            R.forEach {
                val u = it.cualquieraDeLosVertices()
                val v = it.elOtroVertice(u)
                RPares.add(Pair(u, v))
                RPares.add(Pair(v, u))
            }
            
            // Verificación de correctitud del circuito obtenido
            var sumideroAnterior = ciclo.first().cualquieraDeLosVertices()
            var aristaAparece = mutableSetOf<Arista>()

            ciclo.forEach {
                if (!aristaAparece.add(it)) {
                    println("Una arista se repite en el ciclo (lado: $it)")
                    return false
                }

                val fuenteActual = it.cualquieraDeLosVertices()
                if (sumideroAnterior != fuenteActual) {
                    println("Dos aristas adyacentes no concuerdan (lado: $it)")
                    return false
                }
                sumideroAnterior = it.elOtroVertice(fuenteActual)

                // Verificacion que ciclo contiene los lados de R
                val u = it.cualquieraDeLosVertices()
                val v = it.elOtroVertice(u)

                RPares.remove(Pair(fInv(u), fInv(v)))
                RPares.remove(Pair(fInv(v), fInv(u)))
            }

            if (RPares.isNotEmpty()) {
                println("Hay lados de R que no aparecen en el ciclo")
                println(R)
                return false
            }

            return true
        }
    }
}