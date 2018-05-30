Engine engine = new Engine();
engine.setName("");

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

InputVariable inputVariable4 = new InputVariable();
inputVariable4.setEnabled(true);
inputVariable4.setName("SensorDiagEsq");
inputVariable4.setRange(0.000, 0.200);
inputVariable4.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable4.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable4.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable4);

InputVariable inputVariable5 = new InputVariable();
inputVariable5.setEnabled(true);
inputVariable5.setName("SensorDiagDir");
inputVariable5.setRange(0.000, 0.200);
inputVariable5.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable5.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable5.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable5);

InputVariable inputVariable6 = new InputVariable();
inputVariable6.setEnabled(true);
inputVariable6.setName("SensorFrente2");
inputVariable6.setRange(0.000, 0.200);
inputVariable6.addTerm(new Trapezoid("Perto", 0.000, 0.000, 0.050, 0.075));
inputVariable6.addTerm(new Trapezoid("Medio", 0.050, 0.080, 0.120, 0.150));
inputVariable6.addTerm(new Trapezoid("Longe", 0.125, 0.150, 0.200, 0.200));
engine.addInputVariable(inputVariable6);

OutputVariable outputVariable1 = new OutputVariable();
outputVariable1.setEnabled(true);
outputVariable1.setName("MotorEsq");
outputVariable1.setRange(-0.200, 0.200);
outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable1.setDefuzzifier(new Centroid(200));
outputVariable1.setDefaultValue(Double.NaN);
outputVariable1.setLockValidOutput(false);
outputVariable1.setLockOutputRange(false);
outputVariable1.addTerm(new Trapezoid("Reverso", -0.200, -0.200, -0.100, 0.000));
outputVariable1.addTerm(new Trapezoid("Fraco", 0.000, 0.000, 0.050, 0.075));
outputVariable1.addTerm(new Trapezoid("Media", 0.050, 0.075, 0.125, 0.150));
outputVariable1.addTerm(new Trapezoid("Forte", 0.125, 0.150, 0.200, 0.200));
engine.addOutputVariable(outputVariable1);

OutputVariable outputVariable2 = new OutputVariable();
outputVariable2.setEnabled(true);
outputVariable2.setName("MotorDir");
outputVariable2.setRange(-0.200, 0.200);
outputVariable2.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable2.setDefuzzifier(new Centroid(200));
outputVariable2.setDefaultValue(Double.NaN);
outputVariable2.setLockValidOutput(false);
outputVariable2.setLockOutputRange(false);
outputVariable2.addTerm(new Trapezoid("Reverso", -0.200, -0.200, -0.100, 0.000));
outputVariable2.addTerm(new Trapezoid("Fraco", 0.000, 0.000, 0.050, 0.075));
outputVariable2.addTerm(new Trapezoid("Media", 0.050, 0.075, 0.125, 0.150));
outputVariable2.addTerm(new Trapezoid("Forte", 0.125, 0.150, 0.200, 0.200));
engine.addOutputVariable(outputVariable2);

RuleBlock ruleBlock = new RuleBlock();
ruleBlock.setEnabled(true);
ruleBlock.setName("");
ruleBlock.setConjunction(null);
ruleBlock.setDisjunction(null);
ruleBlock.setActivation(null);
ruleBlock.addRule(Rule.parse("if SensorEsq is Medio or SensorDiagEsq is Medio and SensorDir is Longe or SensorDiagDir is Longe then MotorEsq is Forte and MotorDir is Media", engine));
ruleBlock.addRule(Rule.parse("if SensorDir is Medio or SensorDiagDir is Medio and SensorEsq is Longe or SensorDiagEsq is Longe then MotorDir is Forte and MotorEsq is Media", engine));
ruleBlock.addRule(Rule.parse("if SensorEsq is Perto or SensorDiagEsq is Perto and SensorDir is Longe or SensorDir is Medio or SensorDiagDir is Longe or SensorDiagDir is Medio then MotorEsq is Forte and MotorDir is Fraco", engine));
ruleBlock.addRule(Rule.parse("if SensorDir is Perto or SensorDiagDir is Perto and SensorEsq is Longe or SensorEsq is Medio or SensorDiagEsq is Longe or SensorDiagEsq is Medio then MotorDir is Forte and MotorEsq is Fraco", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Longe or SensorFrente2 is Longe then MotorDir is Forte and MotorEsq is Forte", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Medio or SensorFrente2 is Medio then MotorDir is Media and MotorEsq is Media", engine));
ruleBlock.addRule(Rule.parse("if SensorFrente is Perto or SensorFrente2 is Perto and SensorDir is Perto or SensorDiagDir is Perto and SensorEsq is Perto or SensorDiagEsq is Perto then MotorDir is Forte and MotorEsq is Reverso", engine));
engine.addRuleBlock(ruleBlock);


