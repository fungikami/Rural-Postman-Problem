import ve.usb.grafoLib.*

val CARPETA = "GrafosPrueba/"

fun main(args: Array<String>) {
    val filename = "$CARPETA/${args[0]}"
    testAPA(filename)
}

fun testAPA(filename: String) {
    println("Grafo de prueba: $filename")
    
    val g = GrafoNoDirigido(filename, true)
    val apa = ApareamientoPerfectoAvido(g)

    println("Apareamiento Perfecto obtenido:")
    println(apa.obtenerApareamiento().joinToString(" - "))
}