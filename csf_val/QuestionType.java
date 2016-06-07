package csf_val;

public enum QuestionType {
	MULTIPLE_CHOICE, CHECKBOXES, DROPDOWN, LINEAR_SCALE, MULTIPLE_CHOICE_GRID, SHORT_ANSWER,
	DATE, TIME, PARAGRAPH, TIMESTAMP, UNKNOWN;
	
	@Override
	public String toString(){
		switch(this){
		case MULTIPLE_CHOICE:
			return "Multiple Choice";
		case CHECKBOXES:
			return "Checkboxes";
		case DROPDOWN:
			return "Dropdown";
		case LINEAR_SCALE:
			return "Linear Scale";
		case MULTIPLE_CHOICE_GRID:
			return "Multiple Choice Grid";
		case SHORT_ANSWER:
			return "Short Answer";
		case DATE:
			return "Date";
		case TIME:
			return "Time";
		case PARAGRAPH:
			return "Paragraph";
		case TIMESTAMP:
			return "Timestamp";
		case UNKNOWN:
			return "Unknown";
		default:
			return null;
		}
	}
	
	public static QuestionType getQuestionTypeFromInt(int i){
		switch(i){
		case 0:
			return QuestionType.MULTIPLE_CHOICE;
		case 1:
			return QuestionType.CHECKBOXES;
		case 2:
			return QuestionType.DROPDOWN;
		case 3:
			return QuestionType.LINEAR_SCALE;
		case 4:
			return QuestionType.MULTIPLE_CHOICE_GRID;
		case 5:
			return QuestionType.SHORT_ANSWER;
		case 6:
			return QuestionType.DATE;
		case 7:
			return QuestionType.TIME;
		case 8:
			return QuestionType.PARAGRAPH;
		case 9:
			return QuestionType.TIMESTAMP;
		case 10:
			return QuestionType.UNKNOWN;
		default:
			return null;
		}
	}
	
	public static QuestionType getQuestionTypeFromString(String str){
		QuestionType qt = QuestionType.UNKNOWN;
		str = str.trim();
		if(str.equals("Multiple Choice")){
			 qt = QuestionType.MULTIPLE_CHOICE;
		}else if(str.equals("Checkboxes")){
			qt = QuestionType.CHECKBOXES;
		}else if(str.equals("Dropdown")){
			qt = QuestionType.DROPDOWN;
		}else if(str.equals("Linear Scale")){
			qt = QuestionType.LINEAR_SCALE;
		}else if(str.equals("Multiple Choice Grid")){
			qt = QuestionType.MULTIPLE_CHOICE_GRID;
		}else if(str.equals("Short Answer")){
			qt = QuestionType.SHORT_ANSWER;
		}else if(str.equals("Date")){
			qt = QuestionType.DATE;
		}else if(str.equals("Time")){
			qt = QuestionType.TIME;
		}else if(str.equals("Paragraph")){
			qt = QuestionType.PARAGRAPH;
		}else if(str.equals("Timestamp")){
			qt = QuestionType.TIMESTAMP;
		}
		return qt;
	}
	
	public static int getIntFromQuestionType(QuestionType qt){
		switch(qt){
		case MULTIPLE_CHOICE:
			return 0;
		case CHECKBOXES:
			return 1;
		case DROPDOWN:
			return 2;
		case LINEAR_SCALE:
			return 3;
		case MULTIPLE_CHOICE_GRID:
			return 4;
		case SHORT_ANSWER:
			return 5;
		case DATE:
			return 6;
		case TIME:
			return 7;
		case PARAGRAPH:
			return 8;
		case TIMESTAMP:
			return 9;
		case UNKNOWN:
			return 10;
		default:
			return -1;
		}
	}
}
