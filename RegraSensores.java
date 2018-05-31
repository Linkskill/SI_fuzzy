Engine engine = new Engine();
engine.setName("wallDetection");

InputVariable inputVariable1 = new InputVariable();
inputVariable1.setEnabled(true);
inputVariable1.setName("SensorFrente");
inputVariable1.setRange(0.000, 0.200);
inputVariable1.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable1.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable1.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable1);

InputVariable inputVariable2 = new InputVariable();
inputVariable2.setEnabled(true);
inputVariable2.setName("SensorEsq");
inputVariable2.setRange(0.000, 0.200);
inputVariable2.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable2.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable2.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable2);

InputVariable inputVariable3 = new InputVariable();
inputVariable3.setEnabled(true);
inputVariable3.setName("SensorDir");
inputVariable3.setRange(0.000, 0.200);
inputVariable3.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable3.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable3.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable3);

OutputVariable outputVariable1 = new OutputVariable();
outputVariable1.setEnabled(true);
outputVariable1.setName("MotorEsq");
outputVariable1.setRange(-1.000, 1.000);
outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable1.setDefuzzifier(new Centroid(200));
outputVariable1.setDefaultValue(0.000);
outputVariable1.setLockValidOutput(false);
outputVariable1.setLockOutputRange(true);
outputVariable1.addTerm(new Trapezoid("ReversoRapido", -1.000, -1.000, -0.600, -0.400));
outputVariable1.addTerm(new Trapezoid("ReversoLento", -0.600, -0.400, 0.000, 0.000));
outputVariable1.addTerm(new Trapezoid("Lento", 0.000, 0.000, 0.400, 0.600));
outputVariable1.addTerm(new Trapezoid("Rapido", 0.400, 0.600, 1.000, 1.000));
engine.addOutputVariable(outputVariable1);

OutputVariable outputVariable2 = new OutputVariable();
outputVariable2.setEnabled(true);
outputVariable2.setName("MotorDir");
outputVariable2.setRange(-1.000, 1.000);
outputVariable2.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable2.setDefuzzifier(new Centroid(200));
outputVariable2.setDefaultValue(0.000);
outputVariable2.setLockValidOutput(false);
outputVariable2.setLockOutputRange(true);
outputVariable2.addTerm(new Trapezoid("ReversoRapido", -1.000, -1.000, -0.600, -0.400));
outputVariable2.addTerm(new Trapezoid("ReversoLento", -0.600, -0.400, 0.000, 0.000));
outputVariable2.addTerm(new Trapezoid("Lento", 0.000, 0.000, 0.400, 0.600));
outputVariable2.addTerm(new Trapezoid("Rapido", 0.400, 0.600, 1.000, 1.000));
engine.addOutputVariable(outputVariable2);

RuleBlock ruleBlock = new RuleBlock();
ruleBlock.setEnabled(true);
ruleBlock.setName("");
ruleBlock.setConjunction(new Minimum());
ruleBlock.setDisjunction(new Maximum());
ruleBlock.setActivation(new Minimum());
ruleBlock.addRule(Rule.parse("if SensorFrente is Longe then MotorDir is Rapido and MotorEsq is Rapido", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Medio then MotorDir is Lento and MotorEsq is Lento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is Perto and SensorEsq is Perto then MotorDir is Rapido and MotorEsq is ReversoRapido", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is Longe and SensorEsq is Longe then MotorDir is Rapido and MotorEsq is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Perto and SensorDir is Longe and SensorEsq is Medio or SensorEsq is Perto then MotorEsq is Rapido and MotorDir is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorEsq is Medio then MotorEsq is Rapido and MotorDir is Lento", engine));
ruleBlock.addRule(Rule.parse("if SensorDir is Medio then MotorDir is Rapido and MotorEsq is Lento", engine));
ruleBlock.addRule(Rule.parse("if SensorEsq is Perto then MotorEsq is Rapido and MotorDir is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorDir is Perto then MotorDir is Rapido and MotorEsq is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if SensorDir is Medio and SensorEsq is Medio then MotorDir is Lento and MotorEsq is Lento", engine));
engine.addRuleBlock(ruleBlock);


