KC=		kotlinc
KFLAG=		-cp
PKG_GRAPHLIB=	ve/usb/grafoLib

all:	\
	$(PKG_GRAPHLIB)/Color.class\
	$(PKG_GRAPHLIB)/Lado.class\
	$(PKG_GRAPHLIB)/Arco.class\
	$(PKG_GRAPHLIB)/Arista.class\
	$(PKG_GRAPHLIB)/Grafo.class\
	$(PKG_GRAPHLIB)/GrafoNoDirigido.class\
	$(PKG_GRAPHLIB)/GrafoDirigido.class\
	$(PKG_GRAPHLIB)/BusquedaEnAmplitud.class\
	$(PKG_GRAPHLIB)/BusquedaEnProfundidad.class\
	$(PKG_GRAPHLIB)/CicloDigrafo.class\
	$(PKG_GRAPHLIB)/UtilidadesKt.class\
	$(PKG_GRAPHLIB)/OrdenTopologico.class\
	$(PKG_GRAPHLIB)/CFC.class\
	$(PKG_GRAPHLIB)/ConjuntosDisjuntos.class\
	$(PKG_GRAPHLIB)/ComponentesConexasCD.class\
	$(PKG_GRAPHLIB)/ComponentesConexasDFS.class\
	$(PKG_GRAPHLIB)/ComponentesConexasDFSIter.class\
	$(PKG_GRAPHLIB)/ArbolMinimoCobertorKruskal.class\
	$(PKG_GRAPHLIB)/ArbolMinimoCobertorPrim.class\
	$(PKG_GRAPHLIB)/CicloEuleriano.class\
	$(PKG_GRAPHLIB)/MetricasDeGrafo.class\
	$(PKG_GRAPHLIB)/DosColoreable.class\
	$(PKG_GRAPHLIB)/LCA.class\
	$(PKG_GRAPHLIB)/Sol2SAT.class\
	$(PKG_GRAPHLIB)/BellmanFord.class\
	$(PKG_GRAPHLIB)/CCMDAG.class\
	$(PKG_GRAPHLIB)/Dijkstra.class\
	$(PKG_GRAPHLIB)/FloydWarshall.class\
	$(PKG_GRAPHLIB)/Johnson.class\
	$(PKG_GRAPHLIB)/CicloEulerianoGrafoNoDirigido.class\
	$(PKG_GRAPHLIB)/DijkstraGrafoNoDirigido.class\
	$(PKG_GRAPHLIB)/ApareamientoPerfectoAvido.class\
	$(PKG_GRAPHLIB)/ApareamientoVertexScan.class\
	$(PKG_GRAPHLIB)/HeuristicaRPPKt.class\
	

$(PKG_GRAPHLIB)/Color.class: $(PKG_GRAPHLIB)/Color.kt
	$(KC) $(PKG_GRAPHLIB)/Color.kt

$(PKG_GRAPHLIB)/Lado.class: $(PKG_GRAPHLIB)/Lado.kt
	$(KC) $(PKG_GRAPHLIB)/Lado.kt

$(PKG_GRAPHLIB)/Arco.class: $(PKG_GRAPHLIB)/Arco.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Arco.kt

$(PKG_GRAPHLIB)/Arista.class: $(PKG_GRAPHLIB)/Arista.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Arista.kt

$(PKG_GRAPHLIB)/Grafo.class: $(PKG_GRAPHLIB)/Grafo.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Grafo.kt

$(PKG_GRAPHLIB)/GrafoNoDirigido.class: $(PKG_GRAPHLIB)/GrafoNoDirigido.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/GrafoNoDirigido.kt

$(PKG_GRAPHLIB)/GrafoDirigido.class: $(PKG_GRAPHLIB)/GrafoDirigido.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/GrafoDirigido.kt

$(PKG_GRAPHLIB)/BusquedaEnAmplitud.class: $(PKG_GRAPHLIB)/BusquedaEnAmplitud.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/BusquedaEnAmplitud.kt

$(PKG_GRAPHLIB)/BusquedaEnProfundidad.class: $(PKG_GRAPHLIB)/BusquedaEnProfundidad.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/BusquedaEnProfundidad.kt

$(PKG_GRAPHLIB)/CicloDigrafo.class: $(PKG_GRAPHLIB)/CicloDigrafo.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/CicloDigrafo.kt

$(PKG_GRAPHLIB)/OrdenTopologico.class: $(PKG_GRAPHLIB)/OrdenTopologico.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/OrdenTopologico.kt

$(PKG_GRAPHLIB)/UtilidadesKt.class: $(PKG_GRAPHLIB)/Utilidades.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Utilidades.kt

$(PKG_GRAPHLIB)/CFC.class: $(PKG_GRAPHLIB)/CFC.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Utilidades.kt $(PKG_GRAPHLIB)/CFC.kt

$(PKG_GRAPHLIB)/ConjuntosDisjuntos.class: $(PKG_GRAPHLIB)/ConjuntosDisjuntos.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ConjuntosDisjuntos.kt

$(PKG_GRAPHLIB)/ComponentesConexasCD.class: $(PKG_GRAPHLIB)/ComponentesConexasCD.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ComponentesConexasCD.kt

$(PKG_GRAPHLIB)/ComponentesConexasDFS.class: $(PKG_GRAPHLIB)/ComponentesConexasDFS.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ComponentesConexasDFS.kt

$(PKG_GRAPHLIB)/ComponentesConexasDFSIter.class: $(PKG_GRAPHLIB)/ComponentesConexasDFSIter.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ComponentesConexasDFSIter.kt

$(PKG_GRAPHLIB)/ArbolMinimoCobertorKruskal.class: $(PKG_GRAPHLIB)/ArbolMinimoCobertorKruskal.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ArbolMinimoCobertorKruskal.kt

$(PKG_GRAPHLIB)/ColaDePrioridadPrim.class: $(PKG_GRAPHLIB)/ColaDePrioridadPrim.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ColaDePrioridadPrim.kt

$(PKG_GRAPHLIB)/ArbolMinimoCobertorPrim.class: $(PKG_GRAPHLIB)/ArbolMinimoCobertorPrim.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ArbolMinimoCobertorPrim.kt

$(PKG_GRAPHLIB)/CicloEuleriano.class: $(PKG_GRAPHLIB)/CicloEuleriano.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Utilidades.kt $(PKG_GRAPHLIB)/CicloEuleriano.kt

$(PKG_GRAPHLIB)/MetricasDeGrafo.class: $(PKG_GRAPHLIB)/MetricasDeGrafo.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/MetricasDeGrafo.kt

$(PKG_GRAPHLIB)/DosColoreable.class: $(PKG_GRAPHLIB)/DosColoreable.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/DosColoreable.kt

$(PKG_GRAPHLIB)/LCA.class: $(PKG_GRAPHLIB)/LCA.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/LCA.kt

$(PKG_GRAPHLIB)/Sol2SAT.class: $(PKG_GRAPHLIB)/Sol2SAT.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Sol2SAT.kt

$(PKG_GRAPHLIB)/BellmanFord.class: $(PKG_GRAPHLIB)/BellmanFord.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/BellmanFord.kt

$(PKG_GRAPHLIB)/CCMDAG.class: $(PKG_GRAPHLIB)/CCMDAG.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/CCMDAG.kt

$(PKG_GRAPHLIB)/Dijkstra.class: $(PKG_GRAPHLIB)/Dijkstra.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Dijkstra.kt

$(PKG_GRAPHLIB)/FloydWarshall.class: $(PKG_GRAPHLIB)/FloydWarshall.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Utilidades.kt $(PKG_GRAPHLIB)/FloydWarshall.kt

$(PKG_GRAPHLIB)/Johnson.class: $(PKG_GRAPHLIB)/Johnson.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/Utilidades.kt $(PKG_GRAPHLIB)/Johnson.kt

$(PKG_GRAPHLIB)/CicloEulerianoGrafoNoDirigido.class: $(PKG_GRAPHLIB)/CicloEulerianoGrafoNoDirigido.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/CicloEulerianoGrafoNoDirigido.kt

$(PKG_GRAPHLIB)/DijkstraGrafoNoDirigido.class: $(PKG_GRAPHLIB)/DijkstraGrafoNoDirigido.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/DijkstraGrafoNoDirigido.kt

$(PKG_GRAPHLIB)/ApareamientoPerfectoAvido.class: $(PKG_GRAPHLIB)/ApareamientoPerfectoAvido.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ApareamientoPerfectoAvido.kt

$(PKG_GRAPHLIB)/ApareamientoVertexScan.class: $(PKG_GRAPHLIB)/ApareamientoVertexScan.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/ApareamientoVertexScan.kt

$(PKG_GRAPHLIB)/HeuristicaRPPKt.class: $(PKG_GRAPHLIB)/HeuristicaRPP.kt
	$(KC) $(KFLAG) $(PKG_GRAPHLIB): $(PKG_GRAPHLIB)/HeuristicaRPP.kt

clean:
	rm -rf *.class META-INF $(PKG_GRAPHLIB)/*.class $(PKG_GRAPHLIB)/META-INF 
