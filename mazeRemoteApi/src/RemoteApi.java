/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import coppelia.FloatWA;
import coppelia.FloatWAA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Scanner;

/**
 *
 * @author Gabriel Eugenio, Lincoln Batista, Jorge Straub
 */
public class RemoteApi {
    static long NANOS_PER_S = 1000*1000*1000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        remoteApi vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections

        System.out.print("Tentando se conectar ao V-REP via API remota... ");
        String serverIP = "127.0.0.1";
	int serverPort = 19999;
        int clientID = vrep.simxStart(serverIP, serverPort, true, true, 5000, 5);
        
        if (clientID != -1)
        {
            System.out.println("Sucesso!");
            System.out.println("Conectado ao V-REP via API remota!\n");
            
            Scanner in = new Scanner(System.in);
            System.out.println("Objetivo (x y)");
            float goalX = in.nextFloat();
            float goalY = in.nextFloat();
            
            // inicialização do robo
            String robotName = "bubbleRob";
            System.out.print("Procurando objeto " + robotName + "...");
            IntW robotHandle = new IntW(0);
            if(vrep.simxGetObjectHandle(clientID, robotName, robotHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("Conectado! <<<<< " +
                    " depois temos que trocar pro nosso robô!");
            else {
                System.out.println("Falhou!");
                System.out.println(robotName + "não existe!");
                endConnection(vrep, clientID);
            }
    
            // inicialização dos sensores
            final int NUM_SENSORS = 5;
            final float SENSORS_RANGE = (float) 0.2;
            System.out.print("Conectando-se aos sensores...");
            IntWA sensors = new IntWA(NUM_SENSORS);
            
            if (vrep.simxGetObjectHandle(clientID, "Left_ultrasonic", new IntW(sensors.getArray()[0]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_ultrasonic", new IntW(sensors.getArray()[1]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic", new IntW(sensors.getArray()[2]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "RM_ultrasonic", new IntW(sensors.getArray()[3]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_ultrasonic", new IntW(sensors.getArray()[4]), vrep.simx_opmode_blocking) == vrep.simx_return_ok
                )
                System.out.println("Sucesso! (ultrassom)");
//            else if(vrep.simxGetObjectHandle(clientID, "Left_Vision_sensor", new IntW(sensors.getArray()[0]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "LM_Vision_sensor", new IntW(sensors.getArray()[1]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "Middle_Vision_sensor", new IntW(sensors.getArray()[2]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "RM_Vision_sensor", new IntW(sensors.getArray()[3]), vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "Right_Vision_sensor", new IntW(sensors.getArray()[4]), vrep.simx_opmode_blocking) == vrep.simx_return_ok
//                )
//                System.out.println("Sucesso! (visão)");
            else {
                System.out.println("Falhou!\n. Saindo...");
                endConnection(vrep, clientID);
            }

            FloatWAA sensorsReadValues = new FloatWAA(NUM_SENSORS);
            for (int i=0; i < NUM_SENSORS; i++)
                sensorsReadValues.getArray()[i] = new FloatWA(3);

            FloatWA distances = new FloatWA(NUM_SENSORS);
            for (int i=0; i < NUM_SENSORS; i++)
                distances.getArray()[i] = 0;
            
            // inicialização dos motores
            float MAX_SPEED = (float) 2.0;
            IntW leftMotorHandle = new IntW(0),
                 rightMotorHandle = new IntW(0);
            System.out.println("Se conectando aos motores...");
            if(vrep.simxGetObjectHandle(clientID, robotName + "_leftMotor", leftMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor esquerdo ok!");
            else {
                System.out.println("  Motor esquerdo não encontrado!");
                endConnection(vrep, clientID);
            }

            if(vrep.simxGetObjectHandle(clientID, robotName + "_rightMotor", rightMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor direito ok!");
            else {
                System.out.println("  Motor direito não encontrado!");
                endConnection(vrep, clientID);
            }

            //outras variáveis 
            FloatWA position = new FloatWA(3), //x, y, z. Nao usa-se o z
                    angle = new FloatWA(3); //alpha, beta e gamma. Usa-se o gamma
            float currentX, currentY,
                  currentAngle;

            vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
            currentX = position.getArray()[0];
            currentY = position.getArray()[1];
            
            long timeLimit = 10*NANOS_PER_S;
            System.out.println("  << Enquanto ele não se movimenta, roda só um pouco e depois para >> ");
            System.out.println("  << Depois podemos colocar um limite de tempo para ele desistir >> ");
            long startTime = System.nanoTime();

            //loop de execucao
            while (vrep.simxGetConnectionId(clientID) != -1 &&
                   euclideanDistance(currentX, currentY, goalX, goalY) > 0.2 &&
                   System.nanoTime() - startTime < timeLimit) {
                //Lê posição e ângulo
                vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
                currentX = position.getArray()[0];
                currentY = position.getArray()[1];

                //Lê os sensores, calcula as distâncias
                boolean noWallsDetected = true;
                for(int i=0; i < NUM_SENSORS; i++) {
                    vrep.simxReadProximitySensor(clientID, sensors.getArray()[i], null, sensorsReadValues.getArray()[i],null,null,vrep.simx_opmode_blocking);
                    distances.getArray()[i] = (float)
                        (pow(sensorsReadValues.getArray()[i].getArray()[0], 2) +
                         pow(sensorsReadValues.getArray()[i].getArray()[1], 2) +
                         pow(sensorsReadValues.getArray()[i].getArray()[2], 2));
                    distances.getArray()[i] = (float) sqrt(distances.getArray()[i]);
                    
                    if (distances.getArray()[i] >= 0 && distances.getArray()[i] <= SENSORS_RANGE) //detectou algo
                        noWallsDetected = false;
                    else
                        distances.getArray()[i] = 1000;
                        //variáveis fuzzy dos sensores tem que ir até esse valor
                }
                //combina as distâncias em 3
                //o que faz mais sentido quando vai combinar? média? máximo?
                float distanciaEsquerda = (distances.getArray()[0] + distances.getArray()[1]) / 2;
                float distanciaFrente = distances.getArray()[2];
                float distanciaDireita = (distances.getArray()[3] + distances.getArray()[4]) / 2;
                
                if (noWallsDetected) {
                    //vira na direção do objetivo
                    /*
                    makeInference(radiansToTurn, distanciaAteObjetivo?)
                        entrada: anguloPraVirar, varia de -pi/2 a pi/2, distanciaAteObjetivo varia até quanto?
                            (esquerda, centroEsquerda, centro, centroDireita, direita)
                            (perto, longe)
                        saída:  motorEsq, motorDir
                            (rápidoFrente, devagarFrente, rápidoTrás, devagarTrás) ou algo assim
                            se anguloPraVirar é esquerda, motorEsq = rápidoTrás e motorDir = rápidoFrente
                            se anguloPraVirar é centroEsquerda, motorEsq = devagarTrás, motorDir = devagarFrente
                            se anguloPraVirar é centro, motorEsq = rápidoFrente, motorDir = rápidoFrente
                            se anguloPraVirar é centroDireita, motorEsq = rápidoFente, motorDir = rápidoTrás
                            se anguloPraVirar é direita, motorEsq = rápidoFente, motorDir = rápidoTrás
                    
                            se anguloPraVirar é centro e distanciaAteObjetivo é perto, motorEsq e Dir = devagarFrente
                            se anguloPraVirar é centro e distanciaAteObjetivo é longe, motorEsq e Dir = rapidoFrente
                    */
                    // Já troco pro linux, faço esse controller e coloco aqui
                } else {
                    //desvia das paredes
                    /*
                    inferência fuzzy do Link
                    makeInference(distanciaEsquerda, distandiaFrente, distanciaDireita)
                        entrada: 3 distancias, variando de 0 a 0.2?
                            (perto, longe, muitoLonge/não detectou nada)
                            se quiser coloca muitoPerto também, mas é meio desnecessário
                        saída: motorEsq, motorDir
                            com 3 distancias e 3 níveis diferentes, são ~27 regras
                                nos empates faz ir sempre pro mesmo lado. ex:
                                se distanciaEsquerda = muitoLonge, distânciaFrente = perto e
                                    distânciaDireita = muitoLonge, então seta velocidades pra virar pra direita
                    */
                }
//            vrep.simxSetJointTargetVelocity(clientID,leftMotorHandle.getValue(), velocidade calculada, vrep.simx_opmode_oneshot);
//            vrep.simxSetJointTargetVelocity(clientID,rightMotorHandle.getValue(), velocidade calculada, vrep.simx_opmode_oneshot);
            }
            
            System.out.println("(Conexão fechada) Encerrando...");
            endConnection(vrep, clientID);
        }
        else {
            System.out.println("Falhou!");
            System.out.println("Verifique se o V-REP está rodando e com a cena _____ aberta!");
        }
    }
    
    public static void endConnection(remoteApi vrep, int clientID) {
        // Before closing the connection to V-REP, make sure that the last command sent out had time to arrive. You can guarantee this with (for example):
        IntW pingTime = new IntW(0);
        vrep.simxGetPingTime(clientID,pingTime);

        // Now close the connection to V-REP:   
        vrep.simxFinish(clientID);
    }
    
    public static float euclideanDistance(float x1, float y1, float x2, float y2)
    {
        return (float) sqrt(pow(x2-x1, 2) + pow(y2-y1, 2));
    }

}
            
