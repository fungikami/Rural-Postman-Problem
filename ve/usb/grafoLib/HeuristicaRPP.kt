/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

package ve.usb.grafoLib

import kotlin.system.measureTimeMillis
import kotlin.system.exitProcess
import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.lang.StringBuilder

/**
 * Implementación de un algoritmo ávido para resolver el
 * Problema del Cartero Rural (RPP) basado en algoritmo propuesto
 * por .
 */
public class HeuristicaRPP {
    companion object {
        
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
            R.forEach { gP.agregarArista(it) }

            // Verifica si G' es conexo y par
            val cc = ComponentesConexasDFSIter(gP)
            val esConexo = cc.numeroDeComponentesConexas() == 1

            // Si G' no es conexo (sin importar si es o no par)
            if (!esConexo) {
                // Líneas 9 a 15
                // Construye grafo G_t de componentes conexas de G'
                // (El peso de cada arista es el del camino de costo minimo de (vi, vj))
                // 

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

            return CicloEulerianoGrafoNoDirigido(gP).obtenerCicloEuleriano()
        }

        private fun f(v: Int) = mapa[v]

        private fun fInversa(v: Int) = mapaInverso[v]

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

            val vertexScan = args[0] == "v"
            val (g, R) = extraerDatos(args[1])

            println("Vertices del grafo: ${g.obtenerNumeroDeVertices()}")

            val mapaCorrecto = (
                (0 until mapaInverso.size).all { f(fInversa(it)) == it } &&
                (mapaInverso.size == mapa.size)
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