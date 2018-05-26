/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;

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
            
            // inicialização do robo
            String robot_name = "bubbleRob";
            System.out.print("Procurando objeto " + robot_name + "...");
            IntW robot_handle = new IntW(0);
            if(vrep.simxGetObjectHandle(clientID, robot_name, robot_handle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("Conectado! <<<<< " +
                    " depois temos que trocar pro nosso robô!");
            else {
                System.out.println("Falhou!");
                System.out.println(robot_name + "não existe!");
                end_connection(vrep, clientID);
            }
    
            // inicialização dos sensores
            System.out.print("Conectando-se aos sensores...");
            IntW left_sensor = new IntW(0),
                 lm_sensor = new IntW(0),
                 middle_sensor = new IntW(0),
                 rm_sensor = new IntW(0),
                 right_sensor = new IntW(0);
            if (vrep.simxGetObjectHandle(clientID, "Left_ultrasonic", left_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_ultrasonic", lm_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic", middle_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "RM_ultrasonic", rm_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_ultrasonic", right_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok
                )
                System.out.println("Sucesso! (ultrassom)");
            else if(vrep.simxGetObjectHandle(clientID, "Left_Vision_sensor", left_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_Vision_sensor", lm_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_Vision_sensor", middle_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "RM_Vision_sensor", rm_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_Vision_sensor", right_sensor, vrep.simx_opmode_blocking) == vrep.simx_return_ok
                )
                System.out.println("Sucesso! (visão)");
            else {
                System.out.println("Falhou!\nSem sensores não dá. Saindo...");
                end_connection(vrep, clientID);
            }
            
            // inicialização dos motores
            IntW left_motor_handle = new IntW(0),
                 right_motor_handle = new IntW(0);
            System.out.println("Se conectando aos motores...");
            if(vrep.simxGetObjectHandle(clientID, robot_name + "_leftMotor", left_motor_handle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor esquerdo ok!");
            else {
                System.out.println("  Motor esquerdo não encontrado!");
                end_connection(vrep, clientID);
            }

            if(vrep.simxGetObjectHandle(clientID, robot_name + "_rightMotor", right_motor_handle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor direito ok!");
            else {
                System.out.println("  Motor direito não encontrado!");
                end_connection(vrep, clientID);
            }

            //loop de execucao
            FloatWA position = new FloatWA(3), //x, y, z. Nao usa-se o z
                    angle = new FloatWA(3); //alpha, beta e gamma. Usa-se o gamma
            float current_x, current_y,
                  current_angle;
            long time_limit = 2*NANOS_PER_S;
            System.out.println("  << Enquanto ele não se movimenta, roda só um pouco e depois para >> ");
            System.out.println("  << Depois podemos colocar um limite de tempo para ele desistir >> ");
            long startTime = System.nanoTime();
        
            vrep.simxGetObjectOrientation(clientID, robot_handle.getValue(), -1, angle, vrep.simx_opmode_streaming);
            vrep.simxGetObjectPosition(clientID, robot_handle.getValue(), -1, position, vrep.simx_opmode_streaming);

            while (vrep.simxGetConnectionId(clientID) != -1 &&
                   System.nanoTime() - startTime < time_limit) {
                //Lê posição e ângulo
                vrep.simxGetObjectOrientation(clientID, robot_handle.getValue(), -1, angle, vrep.simx_opmode_streaming);
                vrep.simxGetObjectPosition(clientID, robot_handle.getValue(), -1, position, vrep.simx_opmode_streaming);
                current_x = position.getArray()[0];
                current_y = position.getArray()[1];
                current_angle = angle.getArray()[2];

                System.out.format("Posicao (x,y): %.2f %.2f)\n", current_x, current_y);
                System.out.format("Angulo em radianos: %.3f\n", current_angle);
                
                // Pega os valores dos sensores
                    // Qual o tipo dos valores retornados?
                // Faz inferência fuzzy pra calcular a velocidade de cada motor
                // Seta a velocidade em cada motor
            }
            
            System.out.println("(Conexão fechada) Encerrando...");
            end_connection(vrep, clientID);
        }
        else {
            System.out.println("Falhou!");
            System.out.println("Verifique se o V-REP está rodando e com a cena _____ aberta!");
        }
    }
    
    public static void end_connection(remoteApi vrep, int clientID) {
        // Before closing the connection to V-REP, make sure that the last command sent out had time to arrive. You can guarantee this with (for example):
        IntW pingTime = new IntW(0);
        vrep.simxGetPingTime(clientID,pingTime);

        // Now close the connection to V-REP:   
        vrep.simxFinish(clientID);
    }

}
            
