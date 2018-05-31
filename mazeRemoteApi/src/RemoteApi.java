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
            System.out.print("Conectando-se aos sensores...");
            IntW[] sensors = new IntW[NUM_SENSORS];
            for (int i=0; i < NUM_SENSORS; i++)
                sensors[i] = new IntW(0);
            
            if (vrep.simxGetObjectHandle(clientID, "Left_ultrasonic", sensors[0], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "LM_ultrasonic", sensors[1], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
                vrep.simxGetObjectHandle(clientID, "Middle_ultrasonic", sensors[2], vrep.simx_opmode_blocking) == vrep.simx_return_ok &&
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
            
            double velocidadeEsq, velocidadeDir;

            //outras variáveis 
            FloatWA position = new FloatWA(3), //x, y, z. Nao usa-se o z
                    angle = new FloatWA(3); //alpha, beta e gamma. Usa-se o gamma
            float currentX, currentY,
                  currentAngle;

            //Lê posição e ângulo pra testar
            vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
            vrep.simxGetObjectOrientation(clientID, robotHandle.getValue(), -1, angle, vrep.simx_opmode_blocking);
            currentX = position.getArray()[0];
            currentY = position.getArray()[1];
            currentAngle = angle.getArray()[2];
            System.out.println("Coordenadas iniciais do " + robotName +
                     ": " + currentX + "  " + currentY);
            System.out.println("Orientacao (rad): " + currentAngle);
            
            //engines fuzzy criadas no fuzzylite
            Engine moveToGoal = fuzzyControllerToMoveToGoal();
            Engine wallDetection = fuzzyControllerDodgeWall();
            
            long timeLimit = 180*NANOS_PER_S;
            long startTime = System.nanoTime();
            
            //loop de execucao
            while (vrep.simxGetConnectionId(clientID) != -1 &&
                    euclideanDistance(currentX, currentY, goalX, goalY) > 0.2 &&
                    System.nanoTime() - startTime < timeLimit) {
                //Lê posição e ângulo
                vrep.simxGetObjectPosition(clientID, robotHandle.getValue(), -1, position, vrep.simx_opmode_blocking);
                vrep.simxGetObjectOrientation(clientID, robotHandle.getValue(), -1, angle, vrep.simx_opmode_blocking);
                currentX = position.getArray()[0];
                currentY = position.getArray()[1];
                currentAngle = angle.getArray()[2];
                System.out.printf("\n(%.2f, %.2f) - %.2f rad\n", currentX, currentY, currentAngle);

                //Lê os sensores, calcula as distâncias
                boolean noWallsDetected = true;
                for(int i=0; i < NUM_SENSORS; i++) {
                    vrep.simxReadProximitySensor(clientID, sensors[i].getValue(), detected, detectedPoint,null,null,vrep.simx_opmode_blocking);
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
                float distanciaFrente = distances[2];
                float distanciaDireita = (distances[3] + distances[4]) / 2;
                
                if (noWallsDetected) {
                    System.out.println("Vai em direcao ao objetivo!");
                   /*   entrada:
                            AnguloComObjetivo, varia de -pi/2 a pi/2,
                                (muitoEsquerda, esquerda, centro, direita, muitoDireita)
                            distanciaAteObjetivo(?) varia até quanto?
                                (perto, longe)
                        saída: 
                            motorEsq, motorDir
                                (ReversoRapido, reversoLento, Lento, Rapido)
                    */
                    //vira na direção do objetivo
                    double desiredOrientation = Math.atan2(goalY-currentY, goalX-currentX);
                    System.out.printf("  Angulo atual: %.2f\n", currentAngle);
                    System.out.printf("  Angulo desejado: %.2f\n", desiredOrientation);
                    //atan2 e vrep retornam de [-pi..pi], converte pra [0..2pi]
                    if (currentAngle < 0)
                        currentAngle = (float) (2*PI + currentAngle);
                    if (desiredOrientation < 0)
                        desiredOrientation = 2*PI + desiredOrientation;
                    double error = desiredOrientation-currentAngle;
                    if (error > PI)
                        error = error-PI;
                    System.out.printf("  Como conseguir: %.2f\n", error);
                    
                    double distanceToGoal = euclideanDistance(currentX, currentY, goalX, goalY);
                    System.out.printf("  Distancia: %.2f\n",  distanceToGoal);
                    moveToGoal.getInputVariable("AnguloComObjetivo").setValue(error);
                    moveToGoal.getInputVariable("DistanciaObjetivo").setValue(distanceToGoal);
                    moveToGoal.process();
                    velocidadeEsq = moveToGoal.getOutputVariable("MotorEsq").getValue();
                    velocidadeDir = moveToGoal.getOutputVariable("MotorDir").getValue();

                    System.out.printf("  Esq=%.2f Dir=%.2f\n", velocidadeEsq, velocidadeDir);
                    vrep.simxSetJointTargetVelocity(clientID,leftMotorHandle.getValue(), (float) velocidadeEsq, vrep.simx_opmode_oneshot);
                    vrep.simxSetJointTargetVelocity(clientID,rightMotorHandle.getValue(), (float) velocidadeDir, vrep.simx_opmode_oneshot);
                } else {
                    //desvia das paredes
                    System.out.println("Detectou! --> Desvia!");
                    /*
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
                    System.out.println("  Média esquerda: " + distanciaEsquerda);
                    System.out.println("  Média frente: " + distanciaFrente);
                    System.out.println("  Média direita: " + distanciaDireita);
                    
                    wallDetection.getInputVariable("SensorEsq").setValue(distanciaEsquerda);
                    wallDetection.getInputVariable("SensorFrente").setValue(distanciaFrente);
                    wallDetection.getInputVariable("SensorDir").setValue(distanciaDireita);
                    wallDetection.process();
                    velocidadeEsq = wallDetection.getOutputVariable("MotorEsq").getValue();
                    velocidadeDir = wallDetection.getOutputVariable("MotorDir").getValue();
                    
                    System.out.printf("  Esq=%.2f Dir=%.2f\n", velocidadeEsq, velocidadeDir);
                    vrep.simxSetJointTargetVelocity(clientID,leftMotorHandle.getValue(), (float) velocidadeEsq, vrep.simx_opmode_oneshot);
                    vrep.simxSetJointTargetVelocity(clientID,rightMotorHandle.getValue(), (float) velocidadeDir, vrep.simx_opmode_oneshot);
                }
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

        vrep.simxPauseSimulation(clientID, vrep.simx_opmode_oneshot);
        // Now close the connection to V-REP:   
        vrep.simxFinish(clientID);
    }
    
    public static float euclideanDistance(float x1, float y1, float x2, float y2)
    {
        return (float) sqrt(pow(x2-x1, 2) + pow(y2-y1, 2));
    }

    public static Engine fuzzyControllerToMoveToGoal(){
        Engine engine = new Engine();
        engine.setName("moveToGoal");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setEnabled(true);
        inputVariable1.setName("DistanciaObjetivo");
        inputVariable1.setRange(0.000, 10.000);
        inputVariable1.addTerm(new Trapezoid("MuitoPerto", 0.000, 0.000, 0.200, 0.500));
        inputVariable1.addTerm(new Trapezoid("Perto", 0.200, 0.500, 1.000, 1.300));
        inputVariable1.addTerm(new Trapezoid("Longe", 1.000, 1.300, 10.000, 10.000));
        engine.addInputVariable(inputVariable1);

        InputVariable inputVariable2 = new InputVariable();
        inputVariable2.setEnabled(true);
        inputVariable2.setName("AnguloComObjetivo");
        inputVariable2.setRange(-PI, PI);
        inputVariable2.addTerm(new Trapezoid("MuitoEsquerda", -PI, -PI, -1.400, -1.000));
        inputVariable2.addTerm(new Trapezoid("Esquerda", -1.400, -1.000, -0.300, 0.000));
        inputVariable2.addTerm(new Trapezoid("Centro", -0.300, 0.000, 0.000, 0.300));
        inputVariable2.addTerm(new Trapezoid("Direita", 0.000, 0.300, 1.000, 1.400));
        inputVariable2.addTerm(new Trapezoid("MuitoDireita", 1.000, 1.400, PI, PI));
        engine.addInputVariable(inputVariable2);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setEnabled(true);
        outputVariable1.setName("MotorEsq");
        outputVariable1.setRange(-10.000, 10.000);
        outputVariable1.fuzzyOutput().setAggregation(new AlgebraicSum());
            //outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.setDefaultValue(2.500);
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
        outputVariable2.setDefaultValue(0.200);
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
//        ruleBlock.setActivation(new Highest());
//        ruleBlock.setActivation(new Minimum());
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoEsquerda then MotorEsq is ReversoRapido and MotorDir is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Esquerda then MotorEsq is ReversoLento and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Direita then MotorEsq is Lento and MotorDir is ReversoLento", engine));
        ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoDireita then MotorEsq is Rapido and MotorDir is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaObjetivo is MuitoPerto and AnguloComObjetivo is Centro then MotorEsq is Lento and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaObjetivo is Perto and AnguloComObjetivo is Centro then MotorEsq is Lento and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if DistanciaObjetivo is Longe and AnguloComObjetivo is Centro then MotorEsq is Rapido and MotorDir is Rapido", engine));
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
        outputVariable1.setLockValueInRange(false);
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
        outputVariable2.setLockValueInRange(false);
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
        ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado then MotorDir is Rapido and MotorEsq is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Medio then MotorDir is Lento and MotorEsq is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is Perto and SensorEsq is Perto then MotorDir is Rapido and MotorEsq is ReversoRapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is LongeOuNaoDetectado and SensorEsq is LongeOuNaoDetectado then MotorDir is Rapido and MotorEsq is ReversoLento", engine));
        ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is LongeOuNaoDetectado and SensorEsq is Medio or SensorEsq is Perto then MotorEsq is Rapido and MotorDir is ReversoLento", engine));
        ruleBlock.addRule(Rule.parse("if SensorEsq is Medio then MotorEsq is Rapido and MotorDir is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is Medio then MotorDir is Rapido and MotorEsq is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorEsq is Perto then MotorEsq is Rapido and MotorDir is ReversoLento", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is Perto then MotorDir is Rapido and MotorEsq is ReversoLento", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is Medio and SensorEsq is Medio then MotorDir is Lento and MotorEsq is Lento", engine));
        ruleBlock.addRule(Rule.parse("if SensorEsq is LongeOuNaoDetectado then MotorEsq is Rapido and MotorDir is Rapido", engine));
        ruleBlock.addRule(Rule.parse("if SensorDir is LongeOuNaoDetectado then MotorDir is Rapido and MotorEsq is Rapido", engine));
        engine.addRuleBlock(ruleBlock);
        
        return engine;
    }
}
            
