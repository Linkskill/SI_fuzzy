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

RuleBlock ruleBlock = new RuleBlock();
ruleBlock.setEnabled(true);
ruleBlock.setName("");
ruleBlock.setConjunction(null);
ruleBlock.setDisjunction(null);
ruleBlock.setActivation(null);
engine.addRuleBlock(ruleBlock);


