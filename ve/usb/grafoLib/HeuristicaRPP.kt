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
 * propuesto por Christofides et al.
 */
public class HeuristicaRPP {
    companion object {
        
        // --------- ALGORITMO ---------
        
        private val mapa = HashMap<Int, Int>()
        private var mapaInverso = IntArray(0)

        private fun ejecutarAlgoritmo(
            g: GrafoNoDirigido,
            R: MutableSet<Arista>,
            usarVertexScan: Boolean
        ): Iterable<Arista> {
            // Crea grafo G_R = <V_R, R> (V_R son los vértices de R)
            val n = g.obtenerNumeroDeVertices()
            val gP = GrafoNoDirigido(n)
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
            val CCM = calcularMatrizCostoMinimo(g)
            println("Matriz de costos:")
            CCM.forEach { println(it.joinToString(", ")) }

            /*
                0  3   6  3   7  9  5         
                3  0   4  2   6  6  2
                6  4   0  6  10  7  3
                3  2   6  0   4  8  4 
                7  6  10  4   0  5  8
                9  6   7  8   5  0  4
                5  2   3  4   8  4  0
            */
            
            // Si G' no es conexo (sin importar si es o no par)
            if (!esConexo) {
                // Líneas 9 a 15
                // Construye grafo G_t de componentes conexas de G'
                // (El peso de cada arista es el del camino de costo minimo de (vi, vj))
                val gT = GrafoNoDirigido(numCC)

                // Crea un arreglo con los vertices de cada componente conexa
                val vertCC = Array(numCC) { ArrayList<Int>() }
                for (v in 0 until n) vertCC[cc.obtenerComponente(v)].add(v)

                // Por cada par de componentes conexas, hallar costo mínimo
                for (u in 0 until numCC) {
                    for (v in u+1 until numCC) {
                        val costo = costoMinComponente(CCM, vertCC[u], vertCC[v])
                        gT.agregarArista(Arista(u, v, costo))
                    }
                }

                // Obtiene el árbol mínimo cobertor


                // Elimina los lados duplicados de E_t0


                // Agrega G' vértices en E_t que no se encuentren VR


                // Agrega a G' los lados E_t, se permite lados duplicados

            }

            if (!esConexo || !esPar(gP)) {
                // Líneas 16 a 23

                // Determina conjunto de vértices V_0 de grado impar 
                // val v0 = MutableSet<Int>() 
                // for (v in 0 until n) {
                //     if (gP.grado(v) % 2 == 1) v0.add(v)
                // }

                // Construye grafo G_0 con V_0
                // val G0 = GrafoNoDirigido(v0.size)


                // Determina apareamiento perfecto M
                // val M = if (usarVertexScan) ApareamientoVertexScan(G0) else ApareamientoPerfectoAvido(G0)

                // M.forEach {
                //     Obtener el CCMvi,vj asociado a (vi, vj);
                //     ParaCada lado (i, j) ∈ CCMvi,vj hacer
                //         si i /∈ G0 entonces se agrega el vértice i a G0
                //         si j /∈ G0 entonces se agrega el vértice j a G0
                //         Se agrega el lado (i, j) a G0 sin importar que se encuentre duplicado
                // }

            }

            // ***************************************************************
            // OJOOOO, DEVOLVER QUE LOS VERTICES COMIENCEN DESDE 1 Y NO DESDE 0
            // ***************************************************************
            return CicloEulerianoGrafoNoDirigido(gP).obtenerCicloEuleriano()
        }

        private fun costoMinComponente(
            costos: Array<DoubleArray>, 
            comp1: ArrayList<Int>, 
            comp2: ArrayList<Int>
        ): Double {
            var min = POSITIVE_INFINITY
            comp1.forEach { u ->
                comp2.forEach { v ->
                    val costo = costos[u][v]
                    if (costo < min) min = costo
                }
            }

            return min
        }

        private fun f(v: Int): Int = mapa[v]!!

        private fun fInversa(v: Int): Int = mapaInverso[v]

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

        private fun calcularMatrizCostoMinimo(g: GrafoNoDirigido): Array<DoubleArray> {
            val n = g.obtenerNumeroDeVertices()
            val W = Array<DoubleArray>(n) { DoubleArray(n) }
            
            for (i in 0 until n) {
                val dij = DijkstraGrafoNoDirigido(g, i)

                for (j in i + 1 until n) {
                    W[i][j] = dij.costoHasta(j)
                    W[j][i] = dij.costoHasta(j)
                }
            }

            return W
        }

        // -------- EJECUCIÓN DEL CLIENTE -------- 
        private fun desv(valorObt: Int, valorOpt: Int): Int {
            return (valorObt - valorOpt) * 100 / valorOpt
        }

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
            var k = 0

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
                    mapa[u] = k
                    mapaLista.add(u)
                    k++
                }

                if (!mapa.containsKey(v)) {
                    mapa[v] = k
                    mapaLista.add(v)
                    k++
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

        @JvmStatic fun main(args: Array<String>) {
            // Verifica argumentos
            if (args.size != 2 || (args[0] != "a" && args[0] != "v")) {
                println("Error: Usage >./runHeurRPP.sh [a|v] <ruta/a/instancia>")
                exitProcess(1)
            }

            // Extrae el grafo G y el conjunto R del archivo          
            val vertexScan = args[0] == "v"
            val (g, R) = extraerDatos(args[1])

            println("Vertices del grafo: ${g.obtenerNumeroDeVertices()}")

            val mapaCorrecto = (
                (mapaInverso.size == mapa.size) &&
                (0 until mapa.size).all { f(fInversa(it)) == it }  &&
                mapaInverso.all { fInversa(f(it)) == it }
            )
            println("Mapa construido correctamente: $mapaCorrecto")

            // Ejecuta el algoritmo RPP
            var ciclo: Iterable<Arista>
            val ms = measureTimeMillis { 
                ciclo = ejecutarAlgoritmo(g, R, vertexScan)
            }
            
            // Imprime salida
            ciclo.forEach { print("${it.cualquieraDeLosVertices() - 1} ") }
            val u = ciclo.last().cualquieraDeLosVertices()
            println(ciclo.last().elOtroVertice(u) - 1)
            println(ciclo.sumOf { it.peso() }.toInt())
            println("%.3f segs.".format(ms / 1000.0))
        }
    }
}