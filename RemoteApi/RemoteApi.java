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
 * @author gabriel
 */
public class RemoteApi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        remoteApi vrep = new remoteApi();
                
        String serverIP = "127.0.0.1";
	int serverPort = 19999;
        int clientID = vrep.simxStart(serverIP, serverPort, true, true, 5000, 5);
        
        if (clientID != -1)
        {
            System.out.println("Connected to remote API server");   

            // inicialização do robo, sensores e motores
            IntW robo_handle = null;
            if(vrep.simxGetObjectHandle(clientID,"bubbleRob", robo_handle, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok)
                System.out.println("conectado ao robô bubbleRob (por enquanto\n" +
                        "depois temos que trocar pro nosso\n");
            System.out.println("?");
            IntW left_sensor = null,
                 lm_sensor = null,
                 middle_sensor = null,
                 rm_sensor = null,
                 right_sensor = null;
            if(vrep.simxGetObjectHandle(clientID, "Left_ultrasonic", left_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_ultrasonic", lm_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic", middle_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "RM_ultrasonic", rm_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_ultrasonic", right_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok
                )
                System.out.println("Conectado aos sensores ultrassom\n");
            else if(vrep.simxGetObjectHandle(clientID, "Left_Vision_sensor", left_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_Vision_sensor", lm_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_Vision_sensor", middle_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "RM_Vision_sensor", rm_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_Vision_sensor", right_sensor, vrep.simx_opmode_oneshot_wait) == vrep.simx_return_ok
                )
                System.out.println("Conectado aos sensores de visão\n");
            else
                System.out.println("Deu pau nos sensores\n");

            IntW left_motor_handle = null;
            IntW right_motor_handle = null;
            if(vrep.simxGetObjectHandle(clientID, "bubbleRob_leftMotor", left_motor_handle, vrep.simx_opmode_oneshot_wait) != vrep.simx_return_ok)
                System.out.println("Handle do motor esquerdo nao encontrado!\n");
            else
                System.out.println("Conectado ao motor esquerdo!\n");

            if(vrep.simxGetObjectHandle(clientID, "bubbleRob_rightMotor", right_motor_handle, vrep.simx_opmode_oneshot_wait) != vrep.simx_return_ok)
                System.out.println("Handle do motor direito nao encontrado!\n");
            else
                System.out.println("Conectado ao motor direito!\n");

            // Lê uma vez as informações para testar
            FloatWA angle = null; //alpha, beta e gamma. Usa-se o gamma
            FloatWA position = null; //x, y, z. Nao usa-se o z
            vrep.simxGetObjectOrientation(clientID, robo_handle.getValue(), -1, angle, vrep.simx_opmode_streaming);
            vrep.simxGetObjectPosition(clientID, robo_handle.getValue(), -1, position, vrep.simx_opmode_streaming);
            if(angle.getLength() == 0)
                System.out.println("Problema na leitura da orientação!\n");
            if(position.getLength() == 0)
                System.out.println("Problema na leitura da posição!\n");

            /* Testar como os valores são retornados (distância, ponto?)
            simxReadProximitySensor(clientID, middle_sensor, &readingMU, detectedObjetMU, &detectedObjetHandleMU, detectedSurfaceMU, simx_opmode_streaming);//apenas ultrassom central esta sendo usado
            simxReadVisionSensor(clientID, Left_Vision_sensor, &readingLVS, &DataLVS, &auxLVS, simx_opmode_streaming);
            simxReadVisionSensor(clientID, LM_Vision_sensor, &readingLMVS, &DataLMVS, &auxLMVS, simx_opmode_streaming);
            simxReadVisionSensor(clientID, Middle_Vision_sensor, &readingMVS, &DataMVS, &auxMVS, simx_opmode_streaming);
            simxReadVisionSensor(clientID, RM_Vision_sensor, &readingRMVS, &DataRMVS, &auxRMVS, simx_opmode_streaming);
            simxReadVisionSensor(clientID, Right_Vision_sensor, &readingRVS, &DataRVS, &auxRVS, simx_opmode_streaming);
            */
            
            //loop de execucao
            float X_inicial, Y_inicial;
            float ang_inicial;
            while(vrep.simxGetConnectionId(clientID) != -1) {
                //Lê posição e ângulo
                vrep.simxGetObjectOrientation(clientID, robo_handle.getValue(), -1, angle, vrep.simx_opmode_streaming);
                vrep.simxGetObjectPosition(clientID, robo_handle.getValue(), -1, position, vrep.simx_opmode_streaming);
                X_inicial = position.getArray()[0];
                Y_inicial = position.getArray()[1];
                ang_inicial = angle.getArray()[2];

                // ...
            }
            
            // Before closing the connection to V-REP, make sure
            // that the last command sent out had time to arrive.
            // You can guarantee this with (for example):
            IntW pingTime = new IntW(0);
            vrep.simxGetPingTime(clientID,pingTime);

            // Now close the connection to V-REP:   
            vrep.simxFinish(clientID);
        } else
            System.out.println("Failed connecting to remote API server");
    }
    
}
