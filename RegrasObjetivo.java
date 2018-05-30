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
inputVariable2.setRange(-1.570, 1.570);
inputVariable2.addTerm(new Trapezoid("MuitoEsquerda", -1.570, -1.570, -1.000, -0.800));
inputVariable2.addTerm(new Trapezoid("Esquerda", -1.000, -0.800, -0.360, -0.180));
inputVariable2.addTerm(new Trapezoid("Centro", -0.360, -0.180, 0.180, 0.360));
inputVariable2.addTerm(new Trapezoid("Direita", 0.180, 0.360, 0.800, 1.000));
inputVariable2.addTerm(new Trapezoid("MuitoDireita", 0.800, 1.000, 1.570, 1.570));
engine.addInputVariable(inputVariable2);

OutputVariable outputVariable1 = new OutputVariable();
outputVariable1.setEnabled(true);
outputVariable1.setName("MotorEsq");
outputVariable1.setRange(-1.000, 1.000);
outputVariable1.fuzzyOutput().setAccumulation(new AlgebraicSum());
outputVariable1.setDefuzzifier(new Centroid(200));
outputVariable1.setDefaultValue(0.200);
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
outputVariable2.setDefaultValue(0.200);
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
ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoEsquerda then MotorEsq is ReversoRapido and MotorDir is Rapido", engine));
ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Esquerda then MotorEsq = ReversoLento and MotorDir is Lento", engine));
ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is Direita then MotorEsq is Lento and MotorDir is ReversoLento", engine));
ruleBlock.addRule(Rule.parse("if AnguloComObjetivo is MuitoDireita then MotorEsq is Rapido and MotorDir is ReversoRapido", engine));
ruleBlock.addRule(Rule.parse("if DistanciaObjetivo is MuitoPerto and AnguloComObjetivo is Centro then MotorEsq is Lento and MotorDir is Lento", engine));
engine.addRuleBlock(ruleBlock);


