# SI-Fuzzy

## Instalação

* Clone o repositório
```
	git clone https://github.com/GabrielEug2/SI-Fuzzy.git
```

* Instale o [V-REP 3.5](http://www.coppeliarobotics.com/downloads.html) (depois eu testo com o 3.4)
* Instale o [Netbeans 8.2 com JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk-netbeans-jsp-142931.html) (apenas para desenvolvimento)

## Para rodar

* Abra o V-REP com a cena __nome_da_cena__.ttt

* (Desenvolvimento) Abra o projeto mazeRemoteApi no Netbeans
	* Build
	* Run

<!---
* (Apenas executar) Rode o .jar
-->

## Fuzzyficação de Sensores

Todos os sensores são representados pelo mesmo gráfico de trapezóides:
* Perto: 0.000 - 0.000 - 0.050 - 0.075 
* Medio: 0.050 - 0.080 - 0.120 - 0.150
* Longe: 0.125 - 0.150 - 0.200 - 0.200 

A velocidade é, representada pelos motores direito e esquerdo, delimitada por trapezóides:
* ReversoRapido: (-1.000) - (-1.000) - (-0.600) - (-0.400)
* ReversoLento:  (-0.600) - (-0.400) - 0.000 - 0.000
* Lento: 	 0.000 - 0.000 - 0.400 - 0.600
* Rapido: 	 0.400 - 0.600 - 1.000 - 1.000

## Links úteis

* [API Remota do V-REP](http://www.coppeliarobotics.com/helpFiles/en/remoteApiFunctionsJava.htm)
