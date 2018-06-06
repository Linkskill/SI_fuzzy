Engine engine = new Engine();
engine.setName("wallDetection");

InputVariable inputVariable1 = new InputVariable();
inputVariable1.setEnabled(true);
inputVariable1.setName("SensorFrente");
inputVariable1.setRange(0.000, 0.500);
inputVariable1.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
inputVariable1.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.190, 0.200));
inputVariable1.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.190, 0.201, 0.500, 0.500));
engine.addInputVariable(inputVariable1);

InputVariable inputVariable2 = new InputVariable();
inputVariable2.setEnabled(true);
inputVariable2.setName("SensorEsq");
inputVariable2.setRange(0.000, 0.500);
inputVariable2.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
inputVariable2.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.190, 0.200));
inputVariable2.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.190, 0.201, 0.500, 0.500));
engine.addInputVariable(inputVariable2);

InputVariable inputVariable3 = new InputVariable();
inputVariable3.setEnabled(true);
inputVariable3.setName("SensorDir");
inputVariable3.setRange(0.000, 0.500);
inputVariable3.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.080));
inputVariable3.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.190, 0.200));
inputVariable3.addTerm(new Trapezoid("LongeOuNaoDetectado", 0.190, 0.201, 0.500, 0.500));
engine.addInputVariable(inputVariable3);

OutputVariable outputVariable1 = new OutputVariable();
outputVariable1.setEnabled(true);
outputVariable1.setName("MotorEsq");
outputVariable1.setRange(-10.000, 10.000);
outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable1.setDefuzzifier(new Centroid(200));
outputVariable1.setDefaultValue(0.000);
outputVariable1.setLockValidOutput(false);
outputVariable1.setLockOutputRange(true);
outputVariable1.addTerm(new Trapezoid("ReversoRapido", -10.000, -10.000, -6.000, -4.000));
outputVariable1.addTerm(new Trapezoid("ReversoLento", -6.000, -4.000, 0.000, 0.000));
outputVariable1.addTerm(new Trapezoid("Lento", 0.000, 0.000, 4.000, 6.000));
outputVariable1.addTerm(new Trapezoid("Rapido", 4.000, 6.000, 10.000, 10.000));
engine.addOutputVariable(outputVariable1);

OutputVariable outputVariable2 = new OutputVariable();
outputVariable2.setEnabled(true);
outputVariable2.setName("MotorDir");
outputVariable2.setRange(-10.000, 10.000);
outputVariable2.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable2.setDefuzzifier(new Centroid(200));
outputVariable2.setDefaultValue(0.000);
outputVariable2.setLockValidOutput(false);
outputVariable2.setLockOutputRange(true);
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
ruleBlock.setActivation(new Minimum());
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and SensorEsq is LongeOuNaoDetectado and SensorDir is LongeOuNaoDetectado then MotorDir is Rapido and MotorEsq is Rapido", engine));
ruleBlock.addRule(Rule.parse("if (SensorFrente is Perto or SensorFrente is Medio) and (SensorEsq is Perto or SensorEsq is Medio) and (SensorDir is Perto or SensorDir is Medio) then MotorEsq is ReversoRapido and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if (SensorFrente is Perto or SensorFrente is Medio) and SensorEsq is Perto and SensorDir is LongeOuNaoDetectado then MotorEsq is Rapido and MotorDir is ReversoRapido", engine));
ruleBlock.addRule(Rule.parse("if (SensorFrente is Perto or SensorFrente is Medio) and SensorEsq is Medio and SensorDir is LongeOuNaoDetectado then MotorEsq is ReversoLento and MotorDir is ReversoRapido", engine));
ruleBlock.addRule(Rule.parse("if (SensorFrente is Perto or SensorFrente is Medio) and SensorEsq is LongeOuNaoDetectado and SensorDir is Perto then MotorEsq is ReversoRapido and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if (SensorFrente is Perto or SensorFrente is Medio) and SensorEsq is LongeOuNaoDetectado and SensorDir is Medio then MotorEsq is ReversoRapido and MotorDir is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorEsq is LongeOuNaoDetectado and SensorDir is LongeOuNaoDetectado then MotorEsq is ReversoRapido and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Medio and SensorEsq is LongeOuNaoDetectado and SensorDir is LongeOuNaoDetectado then MotorEsq is ReversoRapido and MotorDir is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and SensorDir is LongeOuNaoDetectado and (SensorEsq is Perto or SensorEsq is Medio) then MotorEsq is Rapido and MotorDir is Lento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and SensorEsq is LongeOuNaoDetectado and (SensorDir is Perto or SensorDir is Medio) then MotorEsq is Lento and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and ((SensorEsq is Perto and SensorDir is Perto) or (SensorEsq is Medio and SensorDir is Medio)) then MotorEsq is Rapido and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and SensorEsq is Perto and SensorDir is Medio then MotorEsq is Rapido and MotorDir is Lento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is LongeOuNaoDetectado and SensorEsq is Medio and SensorDir is Perto then MotorEsq is Lento and MotorDir is Rapido", engine));
engine.addRuleBlock(ruleBlock);


