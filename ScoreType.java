package csf_val;

public enum ScoreType {
	BOOLEAN_AND, BOOLEAN_OR, PERCENTAGE, INVERTED_PERCENTAGE, BOOLEAN_NAND, BOOLEAN_NOR, BOOLEAN_XOR, BOOLEAN_XNOR;
	
	/*
	 OVERVIEW OF SCORETYPES
	
	Boolean AND
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT | 100%|  0% |  0% |  0% |  0% |
	+-----+-----+-----+-----+-----+-----+
	
	Boolean OR
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT | 100%| 100%| 100%| 100%|  0% |
	+-----+-----+-----+-----+-----+-----+
	
	Percentage
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	
	Inverted Percentage (NOT)
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT |  0% | 25% | 50% | 75% | 100%|
	+-----+-----+-----+-----+-----+-----+
	
	Boolean NAND (combine of AND and NOT)
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT |  0% | 100%| 100%| 100%| 100%|
	+-----+-----+-----+-----+-----+-----+
	
	Boolean NOR (combine of OR and NOT)
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT |  0% |  0% |  0% |  0% | 100%|
	+-----+-----+-----+-----+-----+-----+
	
	Boolean XOR
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT |  0% | 100%| 100%| 100%|  0% |
	+-----+-----+-----+-----+-----+-----+
	
	Boolean XNOR (combine of XOR and NOT)
	+-----+-----+-----+-----+-----+-----+
	| PER | 100%| 75% | 50% | 25% |  0% |
	+-----+-----+-----+-----+-----+-----+
	| OUT | 100%|  0% |  0% |  0% | 100%|
	+-----+-----+-----+-----+-----+-----+
	
	*/
	@Override
	public String toString(){
		switch(this){
		case BOOLEAN_AND:
			return "Boolean AND";
		case BOOLEAN_OR:
			return "Boolean OR";
		case PERCENTAGE:
			return "Percentage";
		case INVERTED_PERCENTAGE:
			return "Inverted Percentage";
		case BOOLEAN_NAND:
			return "Boolean NAND";
		case BOOLEAN_NOR:
			return "Boolean NOR";
		case BOOLEAN_XOR:
			return "Boolean XOR";
		case BOOLEAN_XNOR:
			return "Boolean XNOR";
		default:
			return null;
		}
	}
	
	public static int getIntFromScoreType(ScoreType st){
		switch(st){
		case BOOLEAN_AND:
			return 0;
		case BOOLEAN_OR:
			return 1;
		case PERCENTAGE:
			return 2;
		case INVERTED_PERCENTAGE:
			return 3;
		case BOOLEAN_NAND:
			return 4;
		case BOOLEAN_NOR:
			return 5;
		case BOOLEAN_XOR:
			return 6;
		case BOOLEAN_XNOR:
			return 7;
		default: 
			return -1;
		}
	}
	
	public static ScoreType getScoreTypeFromString(String str){
		str = str.trim().toLowerCase();
		
		if(str.equals("boolean and")){
			return ScoreType.BOOLEAN_AND;
		}else if(str.equals("boolean or")){
			return ScoreType.BOOLEAN_OR;
		}else if(str.equals("percentage")){
			return ScoreType.PERCENTAGE;
		}else if(str.equals("inverted percentage")){
			return ScoreType.INVERTED_PERCENTAGE;
		}else if(str.equals("boolean nand")){
			return ScoreType.BOOLEAN_NAND;
		}else if(str.equals("boolean nor")){
			return ScoreType.BOOLEAN_NOR;
		}else if(str.equals("boolean xor")){
			return ScoreType.BOOLEAN_XOR;
		}else if(str.equals("boolean xnor")){
			return ScoreType.BOOLEAN_XNOR;
		}
		
		return null;
	}
	
	public static ScoreType getScoreTypeFromInt(int i){
		switch(i){
		case 0:
			return BOOLEAN_AND;
		case 1:
			return BOOLEAN_OR;
		case 2:
			return PERCENTAGE;
		case 3:
			return INVERTED_PERCENTAGE;
		case 4:
			return BOOLEAN_NAND;
		case 5:
			return BOOLEAN_NOR;
		case 6:
			return BOOLEAN_XOR;
		case 7:
			return BOOLEAN_XNOR;
		default:
			return null;
		}
	}
}
