
# Se aparecer algo tipo
# java.lang.UnsatisfiedLinkError: no remoteApiJava in java.library.path
# tem que colocar a pasta atual nessa variável pra ele achar:

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:`pwd`

javac coppelia/*.java
javac RemoteApi.java
java RemoteApi