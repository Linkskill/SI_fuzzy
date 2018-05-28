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
outputVariable1.setName("FrenteForte");
outputVariable1.setRange(0.000, 0.200);
outputVariable1.fuzzyOutput().setAccumulation(null);
outputVariable1.setDefuzzifier(null);
outputVariable1.setDefaultValue(Double.NaN);
outputVariable1.setLockValidOutput(false);
outputVariable1.setLockOutputRange(false);
outputVariable1.addTerm(new Triangle("MotorEsq", 0.000, 0.200, 0.200));
outputVariable1.addTerm(new Triangle("MotorDir", 0.000, 0.200, 0.200));
engine.addOutputVariable(outputVariable1);

OutputVariable outputVariable2 = new OutputVariable();
outputVariable2.setEnabled(true);
outputVariable2.setName("CurvaEsqLeve");
outputVariable2.setRange(0.000, 0.200);
outputVariable2.fuzzyOutput().setAccumulation(null);
outputVariable2.setDefuzzifier(null);
outputVariable2.setDefaultValue(Double.NaN);
outputVariable2.setLockValidOutput(false);
outputVariable2.setLockOutputRange(false);
outputVariable2.addTerm(new Triangle("MotorEsq", 0.000, 0.200, 0.200));
outputVariable2.addTerm(new Triangle("MotorDIr", 0.000, 0.100, 0.100));
engine.addOutputVariable(outputVariable2);

OutputVariable outputVariable3 = new OutputVariable();
outputVariable3.setEnabled(true);
outputVariable3.setName("CurvaDirLeve");
outputVariable3.setRange(0.000, 0.200);
outputVariable3.fuzzyOutput().setAccumulation(null);
outputVariable3.setDefuzzifier(null);
outputVariable3.setDefaultValue(Double.NaN);
outputVariable3.setLockValidOutput(false);
outputVariable3.setLockOutputRange(false);
outputVariable3.addTerm(new Triangle("MotorDir", 0.000, 0.200, 0.200));
outputVariable3.addTerm(new Triangle("MotorEsq", 0.000, 0.100, 0.100));
engine.addOutputVariable(outputVariable3);

OutputVariable outputVariable4 = new OutputVariable();
outputVariable4.setEnabled(true);
outputVariable4.setName("CurvaEsqForte");
outputVariable4.setRange(0.000, 0.200);
outputVariable4.fuzzyOutput().setAccumulation(null);
outputVariable4.setDefuzzifier(null);
outputVariable4.setDefaultValue(Double.NaN);
outputVariable4.setLockValidOutput(false);
outputVariable4.setLockOutputRange(false);
outputVariable4.addTerm(new Triangle("MotorEsq", 0.000, 0.200, 0.200));
outputVariable4.addTerm(new Triangle("MotorDIr", 0.000, 0.000, 0.000));
engine.addOutputVariable(outputVariable4);

OutputVariable outputVariable5 = new OutputVariable();
outputVariable5.setEnabled(true);
outputVariable5.setName("CurvaDirForte");
outputVariable5.setRange(0.000, 0.200);
outputVariable5.fuzzyOutput().setAccumulation(null);
outputVariable5.setDefuzzifier(null);
outputVariable5.setDefaultValue(Double.NaN);
outputVariable5.setLockValidOutput(false);
outputVariable5.setLockOutputRange(false);
outputVariable5.addTerm(new Triangle("MotorDir", 0.000, 0.200, 0.200));
outputVariable5.addTerm(new Triangle("MotorEsq", 0.000, 0.000, 0.000));
engine.addOutputVariable(outputVariable5);

OutputVariable outputVariable6 = new OutputVariable();
outputVariable6.setEnabled(true);
outputVariable6.setName("MeiaVolta");
outputVariable6.setRange(-0.200, 0.200);
outputVariable6.fuzzyOutput().setAccumulation(null);
outputVariable6.setDefuzzifier(null);
outputVariable6.setDefaultValue(Double.NaN);
outputVariable6.setLockValidOutput(false);
outputVariable6.setLockOutputRange(false);
outputVariable6.addTerm(new Triangle("MotorEsq", 0.000, 0.200, 0.200));
outputVariable6.addTerm(new Triangle("MotorDir", -0.200, -0.200, 0.000));
engine.addOutputVariable(outputVariable6);

OutputVariable outputVariable7 = new OutputVariable();
outputVariable7.setEnabled(true);
outputVariable7.setName("FrenteFraca");
outputVariable7.setRange(0.000, 0.200);
outputVariable7.fuzzyOutput().setAccumulation(null);
outputVariable7.setDefuzzifier(null);
outputVariable7.setDefaultValue(Double.NaN);
outputVariable7.setLockValidOutput(false);
outputVariable7.setLockOutputRange(false);
outputVariable7.addTerm(new Triangle("MotorEsq", 0.000, 0.100, 0.100));
outputVariable7.addTerm(new Triangle("MotorDir", 0.000, 0.100, 0.100));
engine.addOutputVariable(outputVariable7);

RuleBlock ruleBlock = new RuleBlock();
ruleBlock.setEnabled(true);
ruleBlock.setName("");
ruleBlock.setConjunction(null);
ruleBlock.setDisjunction(null);
ruleBlock.setActivation(null);
engine.addRuleBlock(ruleBlock);


