/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//Deixa tudo, quando tiver pronto a gente tira o que não utilizar
import com.fuzzylite.*;
import com.fuzzylite.activation.*;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.factory.*;
import com.fuzzylite.hedge.*;
import com.fuzzylite.imex.*;
import com.fuzzylite.norm.*;
import com.fuzzylite.norm.s.*;
import com.fuzzylite.norm.t.*;
import com.fuzzylite.rule.*;
import com.fuzzylite.term.*;
import com.fuzzylite.variable.*;

import coppelia.BoolW;
import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.FloatWAA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.min;
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

        Scanner in = new Scanner(System.in);
        System.out.println("Objetivo (x y)");
        float goalX = in.nextFloat();
        float goalY = in.nextFloat();
        
        System.out.print("Tentando se conectar ao V-REP via API remota... ");
        String serverIP = "127.0.0.1";
	int serverPort = 19999;
        int clientID = vrep.simxStart(serverIP, serverPort, true, true, 5000, 5);
        
        if (clientID != -1)
        {
            System.out.println("Sucesso!");
            System.out.println("Conectado ao V-REP!\n");
            
            // inicialização do robo
            // Para K3
            //String robotName = "K3_robot";
            // Para Bob
            String robotName = "bubbleRob";
            System.out.print("Procurando objeto " + robotName + "...");
            IntW robotHandle = new IntW(0);
            if(vrep.simxGetObjectHandle(clientID, robotName, robotHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("Conectado! <<<<< " +
                    " depois temos que trocar pro nosso robô!");
            else {
                System.out.println("Falhou!");
                System.out.println(robotName + "não encontrado!");
                endConnection(vrep, clientID);
                System.exit(1);
            }

            // inicialização dos sensores
            //final int NUM_SENSORS = 6; //K3
            final int NUM_SENSORS = 5; //Bob
            
            System.out.print("Conectando-se aos sensores...");
            IntW[] sensors = new IntW[NUM_SENSORS];
            for (int i=0; i < NUM_SENSORS; i++) 
                sensors[i] = new IntW(0);
            
            if (vrep.simxGetObjectHandle(clientID, "Left_ultrasonic", sensors[0], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_ultrasonic", sensors[1], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic", sensors[2], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                // Para K3
//                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic0", sensors[3], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "RM_ultrasonic", sensors[4], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
//                vrep.simxGetObjectHandle(clientID, "Right_ultrasonic", sensors[5], vrep.simx_opmode_blocking) == vrep.simx_return_ok
                // Para Bob
                vrep.simxGetObjectHandle(clientID, "RM_ultrasonic", sensors[3], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Right_ultrasonic", sensors[4], vrep.simx_opmode_blocking) == vrep.simx_return_ok
                )
                System.out.println("Sucesso! (ultrassom)");
            else {
                System.out.println("Falhou!\n. Saindo...");
                endConnection(vrep, clientID);
            }
            FloatWA detectedPoint = new FloatWA(3);
            BoolW detected = new BoolW(false);

            float[] distances = new float[NUM_SENSORS];
            
            // inicialização dos motores
            IntW leftMotorHandle = new IntW(0),
                 rightMotorHandle = new IntW(0);
            System.out.println("Se conectando aos motores...");
            
            // Para K3
//            if(vrep.simxGetObjectHandle(clientID, robotName + "_leftWheelMotor", leftMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
//                System.out.println("  Motor esquerdo ok!");
            // Para Bob
            if(vrep.simxGetObjectHandle(clientID, robotName + "_leftMotor", leftMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor esquerdo ok!");
            else {
                System.out.println("  Motor esquerdo não encontrado!");
                endConnection(vrep, clientID);
                System.exit(1);
            }

            // Para K3
//            if(vrep.simxGetObjectHandle(clientID, robotName + "_rightWheelMotor", rightMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
//                System.out.println("  Motor direito ok!");
            // Para Bob
            if(vrep.simxGetObjectHandle(clientID, robotName + "_rightMotor", rightMotorHandle, vrep.simx_opmode_blocking) == vrep.simx_return_ok)
                System.out.println("  Motor direito ok!");
            else {
                System.out.println("  Motor direito não encontrado!");
                endConnection(vrep, clientID);
                System.exit(1);
            }
            
            double velocidadeEsq, velocidadeDir;

            //Lê posição e ângulo pra testar
            FloatWA position = new FloatWA(3), //x, y, z. Nao usa-se o z
                    angle = new FloatWA(3); //alpha, beta e gamma. Usa-se o gamma
            float currentX, currentY,
                  currentAngle;

            vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
            vrep.simxGetObjectOrientation(clientID, robotHandle.getValue(), -1, angle, vrep.simx_opmode_blocking);
            currentX = position.getArray()[0];
            currentY = position.getArray()[1];
            currentAngle = angle.getArray()[2];
            System.out.println("Coordenadas iniciais do " + robotName +
                     ": (" + currentX + ", " + currentY + ")");
            System.out.println("Orientacao: " + currentAngle + " rad");
            
            //engines fuzzy criadas no fuzzylite
            Engine moveTowardsGoal = fuzzyControllerMoveTowardsGoal();
            Engine wallDetection = fuzzyControllerDodgeWall();

            //outras variáveis
            float distanceToGoal = euclideanDistance(currentX, currentY, goalX, goalY);
            long timeLimit = 180*NANOS_PER_S; //3 minutos
            long startTime = System.nanoTime();

            //loop de execucao
            while (vrep.simxGetConnectionId(clientID) != -1 &&
                    distanceToGoal > 0.1 &&
                    System.nanoTime() - startTime < timeLimit) {
                //Lê posição e ângulo
                vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
                vrep.simxGetObjectOrientation(clientID, robotHandle.getValue(), -1, angle, vrep.simx_opmode_blocking);
                currentX = position.getArray()[0];
                currentY = position.getArray()[1];
                currentAngle = angle.getArray()[2];
                System.out.printf("\n(%.2f, %.2f) - %.2f rad\n", currentX, currentY, currentAngle);

                distanceToGoal = euclideanDistance(currentX, currentY, goalX, goalY);

                //Lê os sensores, calcula as distâncias
                boolean noWallsDetected = true;
                for(int i=0; i < NUM_SENSORS; i++) {
                    vrep.simxReadProximitySensor(clientID, sensors[i].getValue(), detected, detectedPoint, null, null, vrep.simx_opmode_blocking);
                    if (detected.getValue()) {
                        noWallsDetected = false;
                        float[] sensorReadValues = detectedPoint.getArray();
                        distances[i] = (float)
                            (pow(sensorReadValues[0], 2) + 
                             pow(sensorReadValues[1], 2) + 
                             pow(sensorReadValues[2], 2));
                        distances[i] = (float) sqrt(distances[i]);
                    } else
                        distances[i] = (float)0.5;
                        //variáveis fuzzy dos sensores tem que ir até esse valor
                }
                
                //combina as distâncias em 3
                //o que faz mais sentido quando vai combinar? média? máximo?
                float distanciaEsquerda = (distances[0] + distances[1]) / 2;
                // Para K3
//                float distanciaFrente = (distances[2] + distances[3]) / 2;
//                float distanciaDireita = (distances[4] + distances[5]) / 2;
                // Para Bob
                float distanciaFrente = distances[2];
                float distanciaDireita = (distances[3] + distances[4]) / 2;
                
                
                if (noWallsDetected) {
                    //vira na direção do objetivo
                    System.out.println("Indo pro objetivo!");
                    
                    System.out.printf("  Angulo atual: %.2f\n", currentAngle);
                    double goalArc = Math.atan2(goalY-currentY, goalX-currentX);
                    System.out.printf("  Goal angle: %.2f\n", goalArc);
                    //pra fazer as contas, converte pra [0..2pi]

                    double angleTurningToOneSide = abs(goalArc - currentAngle);
                    double angleTurningToOtherSide = 2*PI - angleTurningToOneSide;

                    System.out.printf("  AngleTurningOneSide = %.2f\n",  angleTurningToOneSide);
                    System.out.printf("  AngleTurningOtherSide = %.2f\n", angleTurningToOtherSide);

                    double smallestAngleToTurn = min(angleTurningToOneSide,
                                                    angleTurningToOtherSide);
                    double angleToTurn;
                    if (smallestAngleToTurn == angleTurningToOneSide)
                        angleToTurn = goalArc - currentAngle;
                    else
                        angleToTurn = -angleTurningToOtherSide;
                    if(angleToTurn > PI)
                        angleToTurn -= 2*PI;
                    System.out.printf("  Aangle to turn: %.2f\n", angleToTurn);
                    System.out.printf("  Distancia: %.2f\n",  distanceToGoal);
                    
                    moveTowardsGoal.getInputVariable("AnguloComObjetivo").setValue(angleToTurn);
                    moveTowardsGoal.getInputVariable("DistanciaAteObjetivo").setValue(distanceToGoal);
                    
                    moveTowardsGoal.process();
                    
                    velocidadeEsq = moveTowardsGoal.getOutputVariable("MotorEsq").getValue();
                    velocidadeDir = moveTowardsGoal.getOutputVariable("MotorDir").getValue();
                } else {
                    //desvia das paredes
                    System.out.println("Velocidades para desviar das paredes!");
                    
                    System.out.println("  Média esquerda: " + distanciaEsquerda);
                    System.out.println("  Média frente: " + distanciaFrente);
                    System.out.println("  Média direita: " + distanciaDireita);
                    
                    wallDetection.getInputVariable("SensorEsq").setValue(distanciaEsquerda);
                    wallDetection.getInputVariable("SensorFrente").setValue(distanciaFrente);
                    wallDetection.getInputVariable("SensorDir").setValue(distanciaDireita);
                    
                    wallDetection.process();
                    
                    velocidadeEsq = wallDetection.getOutputVariable("MotorEsq").getValue();
                    velocidadeDir = wallDetection.getOutputVariable("MotorDir").getValue();
                }
                System.out.printf("  Esq=%.2f Dir=%.2f\n", velocidadeEsq, velocidadeDir);
                vrep.simxSetJointTargetVelocity(clientID,leftMotorHandle.getValue(), (float) velocidadeEsq, vrep.simx_opmode_oneshot);
                vrep.simxSetJointTargetVelocity(clientID,rightMotorHandle.getValue(), (float) velocidadeDir, vrep.simx_opmode_oneshot);
            }
            
            System.out.println("(Conexão fechada)");
            endConnection(vrep, clientID);
            System.exit(1);
        }
        else {
            System.out.println("Falhou!");
            System.out.println("Verifique se o V-REP está rodando e com a cena _____ aberta!");
        }
    }
    
    public static void endConnection(remoteApi vrep, int clientID) {
        System.out.println("Encerando conexão...");
        //Pausando a simulação pro robô não ficar vagando
        vrep.simxPauseSimulation(clientID, vrep.simx_opmode_oneshot);

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

    public static Engine fuzzyControllerMoveTowardsGoal(){
        Engine engine = new Engine();
        engine.setName("moveTowardsGoal");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setEnabled(true);
        inputVariable1.setName("DistanciaAteObjetivo");
        inputVariable1.setRange(0.000, 10.000);
        inputVariable1.addTerm(new Trapezoid("MuitoPerto", 0.000, 0.000, 0.200, 0.500));
        inputVariable1.addTerm(new Trapezoid("Perto", 0.200, 0.500, 1.000, 1.300));
        inputVariable1.addTerm(new Trapezoid("Longe", 1.000, 1.300, 10.000, 10.000));
        engine.addInputVariable(inputVariable1);

        InputVariable inputVariable2 = new InputVariable();
        inputVariable2.setEnabled(true);
        inputVariable2.setName("AnguloComObjetivo");
        inputVariable2.setRange(-3.150, 3.150);
        inputVariable2.addTerm(new Trapezoid("MuitoDireita", -3.150, -3.150, -1.200, -1.000));
        inputVariable2.addTerm(new Trapezoid("Direita", -1.200, -1.000, -0.300, 0.000));
        inputVariable2.addTerm(new Trapezoid("Centro", -0.300, -0.100, 0.100, 0.300));
        inputVariable2.addTerm(new Trapezoid("Esquerda", 0.000, 0.300, 1.000, 1.200));
        inputVariable2.addTerm(new Trapezoid("MuitoEsquerda", 1.000, 1.200, 3.150, 3.150));
        engine.addInputVariable(inputVariable2);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setEnabled(true);
        outputVariable1.setName("MotorEsq");
        outputVariable1.setRange(0.000, 10.000);
        outputVariable1.fuzzyOutput().setAggregation(new AlgebraicSum());
            //outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.setDefaultValue(0.200);
        outputVariable1.setLockValueInRange(true);
            //outputVariable1.setLockValidOutput(false);
            //outputVariable1.setLockOutputRange(true);
        outputVariable1.addTerm(new Trapezoid("Lento", 0.000, 0.000, 3.000, 3.500));
        outputVariable1.addTerm(new Trapezoid("Medio", 3.000, 3.500, 6.000, 6.500));
        outputVariable1.addTerm(new Trapezoid("Rapido", 6.000, 6.500, 10.000, 10.000));
        engine.addOutputVariable(outputVariable1);

        OutputVariable outputVariable2 = new OutputVariable();
        outputVariable2.setEnabled(true);
        outputVariable2.setName("MotorDir");
        outputVariable2.setRange(0.000, 10.000);
        outputVariable2.fuzzyOutput().setAggregation(new AlgebraicSum());
            //outputVariable2.fuzzyOutput().setAccumulation(new AlgebraicSum());
        outputVariable2.setDefuzzifier(new Centroid(200));
        outputVariable2.setDefaultValue(0.200);
        outputVariable2.setLockValueInRange(true);
            //outputVariable2.setLockValidOutput(false);
            //outputVariable2.setLockOutputRange(true);
        outputVariable2.addTerm(new Trapezoid("Lento", 0.000, 0.000, 3.000, 3.500));
        outputVariable2.addTerm(new Trapezoid("Medio", 3.000, 3.500, 6.000, 6.500));
        outputVariable2.addTerm(new Trapezoid("Rapido", 6.000, 6.500, 10.000, 10.000));
        engine.addOutputVariable(outputVariable2);

        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setEnabled(true);
        ruleBlock.setName("");
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setImplication(new Minimum());
            //ruleBlock.setActivation(new Minimum());
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoDireita then MotorEsq is Rapido and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Direita then MotorEsq is Rapido and MotorDir is Medio", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Centro then MotorEsq is Medio and MotorDir is Medio", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Esquerda then MotorEsq is Medio and MotorDir is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoEsquerda then MotorEsq is Lento and MotorDir is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaAteObjetivo is MuitoPerto then MotorEsq is Lento and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaAteObjetivo is Perto then MotorEsq is Medio and MotorDir is Medio", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaAteObjetivo is Longe then MotorEsq is Rapido and MotorDir is Rapido", engine));
        engine.addRuleBlock(ruleBlock);

        return engine;
    }
    
    public static Engine fuzzyControllerDodgeWall(){
        Engine engine = new Engine();
        engine.setName("wallDetection");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setEnabled(true);
        inputVariable1.setName("SensorFrente");
        inputVariable1.setRange(0.000, 0.500);
        inputVariable1.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
        inputVariable1.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.170, 0.200));
        inputVariable1.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.170, 0.201, 0.500, 0.500));
        engine.addInputVariable(inputVariable1);

        InputVariable inputVariable2 = new InputVariable();
        inputVariable2.setEnabled(true);
        inputVariable2.setName("SensorEsq");
        inputVariable2.setRange(0.000, 0.500);
        inputVariable2.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
        inputVariable2.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.170, 0.200));
        inputVariable2.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.170, 0.201, 0.500, 0.500));
        engine.addInputVariable(inputVariable2);

        InputVariable inputVariable3 = new InputVariable();
        inputVariable3.setEnabled(true);
        inputVariable3.setName("SensorDir");
        inputVariable3.setRange(0.000, 0.500);
        inputVariable3.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
        inputVariable3.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.170, 0.200));
        inputVariable3.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.170, 0.201, 0.500, 0.500));
        engine.addInputVariable(inputVariable3);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setEnabled(true);
        outputVariable1.setName("MotorEsq");
        outputVariable1.setRange(-10.000, 10.000);
        outputVariable1.fuzzyOutput().setAggregation(new AlgebraicSum());
            //outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.setDefaultValue(0.000);
        outputVariable1.setLockValueInRange(true);
            //outputVariable1.setLockValidOutput(false);
            //outputVariable1.setLockOutputRange(true);
        outputVariable1.addTerm(new Trapezoid("ReversoRapido", -10.000, -10.000, -6.000, -4.000));
        outputVariable1.addTerm(new Trapezoid("ReversoLento", -6.000, -4.000, 0.000, 0.000));
        outputVariable1.addTerm(new Trapezoid("Lento", 0.000, 0.000, 4.000, 6.000));
        outputVariable1.addTerm(new Trapezoid("Rapido", 4.000, 6.000, 10.000, 10.000));
        engine.addOutputVariable(outputVariable1);

        OutputVariable outputVariable2 = new OutputVariable();
        outputVariable2.setEnabled(true);
        outputVariable2.setName("MotorDir");
        outputVariable2.setRange(-10.000, 10.000);
        outputVariable2.fuzzyOutput().setAggregation(new AlgebraicSum());
            //outputVariable2.fuzzyOutput().setAccumulation(new AlgebraicSum());
        outputVariable2.setDefuzzifier(new Centroid(200));
        outputVariable2.setDefaultValue(0.000);
        outputVariable2.setLockValueInRange(true);
            //outputVariable2.setLockValidOutput(false);
            //outputVariable2.setLockOutputRange(true);
        outputVariable2.addTerm(new Trapezoid("ReversoRapido", -10.000, -10.000, -6.000, -4.000));
        outputVariable2.addTerm(new Trapezoid("ReversoLento", -6.000, -4.000, 0.000, 0.000));
        outputVariable2.addTerm(new Trapezoid("Lento", 0.000, 0.000, 4.000, 6.000));
        outputVariable2.addTerm(new Trapezoid("Rapido", 4.000, 6.000, 10.000, 10.000));
        engine.addOutputVariable(outputVariable2);

        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setEnabled(true);
        ruleBlock.setName("");
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setImplication(new Minimum());
            //ruleBlock.setActivation(new Minimum());
        ruleBlock.addRule(Rule.parse("if SensorFrente is Perto or SensorFrente is Medio and SensorDir is LongeOuNaoDetectado and SensorEsq is LongeOuNaoDetectado then MotorDir is ReversoLento and MotorEsq is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado then MotorDir is Rapido and MotorEsq is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Medio and SensorDir is Medio or SensorDir is Perto then MotorDir is ReversoLento and MotorEsq is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Medio and SensorEsq is Medio or SensorEsq is Perto then MotorEsq is ReversoLento and MotorDir is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is Perto and SensorEsq is Perto then MotorDir is ReversoLento and MotorEsq is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorEsq is Medio then MotorEsq is Rapido and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is Medio then MotorDir is Rapido and MotorEsq is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorEsq is Perto then MotorEsq is Lento and MotorDir is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is Perto then MotorDir is Lento and MotorEsq is ReversoRapido", engine));
        engine.addRuleBlock(ruleBlock);
        
        return engine;
    }
}
            
