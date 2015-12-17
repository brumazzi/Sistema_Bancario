caixa_class = caixa/*.class
gerente_class = gerente/*.class
server_class = server/*.class dbmanager/*.class

build:
	javac caixa/Caixa.java
	javac gerente/Gerente.java
	javac server/Server.java
	jar -cvfm caixa.jar manifest-caixa/MANIFEST.MF ${caixa_class}
	jar -cvfm gerente.jar manifest-gerente/MANIFEST.MF ${gerente_class}
	jar -cvfm server.jar manifest-server/MANIFEST.MF ${server_class}
	rm ${caixa_class}
	rm ${gerente_class}
	rm ${server_class}
