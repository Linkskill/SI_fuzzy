# SI-Fuzzy

## Instalação

* Clone o repositório
```
	git clone https://github.com/GabrielEug2/SI-Fuzzy.git
```

* Instale o [V-REP 3.5](http://www.coppeliarobotics.com/downloads.html)
* Instale o [Netbeans 8.2 com JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk-netbeans-jsp-142931.html) (apenas para desenvolvimento)

## Para rodar

* Abra o V-REP com a cena /mazeRemoteApi/CenaKhepheraK3.ttt.
* Aperta play para iniciar a simulação.

#### Desenvolvimento
* Coloque o arquivo `remoteApiJava.dll` (Windows) ou `remoteApi.so` (Linux) dentro da pasta do projeto, /mazeRemoteApi.

    Esse arquivo se encontra dentro da sua instalação do VREP, em /programming/remoteApiBindings/lib

* Abra o projeto mazeRemoteApi no Netbeans
	* Build
	* Run

#### Só executar
* Copie os arquivos de mazeRemoteApi/dist/ para um diretório de sua escolha
* Coloque o arquivo `remoteApiJava.dll` (Windows) ou `remoteApi.so` (Linux) neste mesmo diretório.

    Esse arquivo se encontra dentro da sua instalação do VREP, em /programming/remoteApiBindings/lib

* Rode o .jar via
```
java -jar mazeRemoteApi.jar
```

## Links úteis

* [API Remota do V-REP](http://www.coppeliarobotics.com/helpFiles/en/remoteApiFunctionsJava.htm)
