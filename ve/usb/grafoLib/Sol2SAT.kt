/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 30/Nov/2021.
 */

package ve.usb.grafoLib

import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.util.LinkedList
import kotlin.math.abs

/**
 * Implementación del algoritmo de Tarjan que soluciona el problema  
 * 2-satisfiability (2-SAT) en tiempo lineal. 
 *
 * Se determina con la creación de una instancia de la clase si la
 * instancia del problema dado es satisfacible, junto con uno de las
 * posibles asignaciones que lo satisface, en caso afirmativo.
 * 
 * @param [nombreArchivo]: camino en el directorio que contiene la  
 *                         fórmula booleana en 2-CNF.
 */
public class Sol2SAT(nombreArchivo: String) {
    var esSatisfacible = true
    var asignacion = BooleanArray(0)

    init {
        // Abrir archivo
        if (!File(nombreArchivo).exists()) {
            throw RuntimeException("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
        }
        
        // Leer archivo
        val sc = Scanner(FileInputStream(nombreArchivo))
        val literales = LinkedList<Pair<Int, Int>>()
        var maxVert = -1
        
        try {
            // Escanea hasta EOF
            while (true) {
                val (lit0, lit1) = sc.nextLine()!!.split(' ')
                
                // Mapea los literales a identificadores en el grafo
                val id0 = id(lit0)
                val id1 = id(lit1)

                // Almacena en maxVert el mayor identificador
                if (id0 > maxVert) maxVert = id0
                if (id1 > maxVert) maxVert = id1

                // Almacena los identificadore en una lista
                literales.add(Pair(id0, id1))
            }
        } catch (e: NoSuchElementException) { }
        
        // dígrafo de implicacion
        val n = maxVert + 2 - (maxVert % 2)
        val dígrafoImp = GrafoDirigido(n)
        asignacion = BooleanArray(n / 2)

        // Agregar lados al dígrafo
        literales.forEach { (id0, id1) ->
            dígrafoImp.agregarArco(Arco(negadoId(id0), id1))
            dígrafoImp.agregarArco(Arco(negadoId(id1), id0))
        }

        // Obtener CFC del dígrafo
        val cfc = CFC(dígrafoImp)

        // Verificar si es satisfacible
        /* (Si para ningún literal xi, xi y -xi están
        en la misma componente conexa) */
        esSatisfacible = !(0 until n step 2).any {
            cfc.estanFuertementeConectados(it, it + 1)
        }

        // Si es satisfacible se determina una asignación válida
        if (esSatisfacible) {
            // Se crea el grafo componente
            val componente = cfc.obtenerGrafoComponente()

            // Se obtiene el ordenamiento topológico al grafo componente
            val topSort = OrdenTopologico(componente).obtenerOrdenTopologico()

            println("Orden topológico: $topSort")
            // Si existe un camino
            for (i in 0 until n step 2) {
                /* Se recorre el ordenamiento topológico hasta encontrar
                xi o -xi */
                for (v in topSort) {
                    if (v == cfc.obtenerIdentificadorCFC(i + 1)) {
                        // Se encontró -xi primero
                        // C(¬xi) < C(xi) -> xi = true
                        asignacion[i / 2] = true
                        break
                    } else if (v == cfc.obtenerIdentificadorCFC(i)) {
                        // Se encontró xi primero
                        // C(xi) < C(¬xi) -> xi = false
                        break
                    }
                }
            }

            for (i in 0 until n step 2) {
                /* Se recorre el ordenamiento topológico hasta encontrar
                xi o -xi */
                for (v in topSort) {
                    if (v == cfc.obtenerIdentificadorCFC(i + 1)) {
                        // Se encontró -xi primero
                        // C(¬xi) < C(xi) -> xi = true
                        asignacion[i / 2] = true
                        break
                    } else if (v == cfc.obtenerIdentificadorCFC(i)) {
                        // Se encontró xi primero
                        // C(xi) < C(¬xi) -> xi = false
                        break
                    }
                }
            }
        }
    }    

    /**
     * Retorna un entero que corresponde al mapeo de un literal
     * de una fórmula booleana en el formato del archivo a un
     * vértice válido del grafo (identificador de literal).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [str] es una String con un literal válido.
     * Postcondición: [id] es el identificador del literal.
     */
    private fun id(str: String): Int {
        if (str == "-0") return 1
        
        val int = str.toInt()
        return if (int >= 0) 2 * int else -2 * int + 1
    }


    /**
     * Mapeo inverso de la función id. Retorna una String que
     * corresponde al literal cuyo identificador es [id].
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [id] es un entero no negativo.
     * Postcondición: [literal] es una String tal que id(literal) = [id].
     */
    private fun literal(id: Int): String {
        if (id == 1) return "-0"
        
        return if (id % 2 == 0) (id / 2).toString() else (-id / 2).toString()
    }

    /**
     * Retorna el identificador correspondiende al negado del literal
     * que representa.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [id] es un entero no negativo.
     * Postcondición: [negadoId] es un entero tal que 
     *                literal(negadoId) = "-${literal(id)}".
     */
    private fun negadoId(id: Int) = if (id % 2 == 0) id + 1 else id - 1 

    /**
     * Retorna un booleano indicando si la fórmula en 2-CNF es satisfacible.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [tieneAsignacionVerdadera] es: -True si la fórmula en 2-CNF del archivo
     *                                                tiene asignación que haga verdadera la fórmula.
     *                                               -False de otra forma.
     */
    fun tieneAsignacionVerdadera(): Boolean = esSatisfacible

    /**
     * Retorna un objeto Iterable que contiene la asignación de cada varible de la fórmula.
     * 
     * @throws [RuntimeException] La fórmula en 2-CNF del archivo no tiene una asignación 
     *                            que la hará verdadera (es decir, no es satisfacible).
     *
     * Ejemplo: Dadas n variables, el objeto de retorno corresponde a la secuencia  
     *          <X0, X1, ..., Xn-1> de booleanos.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [asignacion] es un objeto iterable con la asignación que debe 
     *                tener cada variable Xi, tal que la posición en el contendor 
     *                corresponde al indice de la variable.
     */ 
    fun asignacion(): Iterable<Boolean> {
        if (!this.tieneAsignacionVerdadera()) {
            throw RuntimeException("No existe una asignación que haga verdadera la fórmula booleana.")
        }
        return asignacion.asIterable()
    } 
}