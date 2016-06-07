package csf_val;

import java.util.ArrayList;

public class MultipleChoice extends Question{
	private String[] options;
	public MultipleChoice(String question, boolean usedInSorting, boolean determinitive, int coupleIndex,
			ScoreType scoreType, QuestionType questionType, String[] options){
		super(question, usedInSorting, determinitive, coupleIndex, scoreType, questionType);
		this.options = options;
	}
	
	public MultipleChoice(Question q){
		this(q.getQuestion(), q.isUsedInSorting(), q.isDeterminitive(), q.getCoupleIndex(), q.getScoreType(), q.getQuestionType(),
				new String[]{});
		setShortValue(q.getShortValue());
		setWeight(q.getWeight());
	}
	
	public MultipleChoice(Question q, String[] options){
		this(q.getQuestion(), q.isUsedInSorting(), q.isDeterminitive(), q.getCoupleIndex(), q.getScoreType(), q.getQuestionType(), options);
		setShortValue(q.getShortValue());
		setWeight(q.getWeight());
	}
	
	public MultipleChoice(ArrayList<String> saveStringArgs){
		super(saveStringArgs);
		ArrayList<String> options = new ArrayList<String>();
		for(int i = 8; i < saveStringArgs.size(); i++){
			options.add(saveStringArgs.get(i));
		}
		
		this.options = new String[options.size()];
		for(int i = 0; i < options.size(); i++){
			this.options[i] = options.get(i);
		}
	}

	@Override
	public String getSaveString(){
		String optionsString = "";
		for(int i = 0; i < options.length; i++){
			optionsString += ("" + (char) 29) + options[i];
		}
		
		return super.getSaveString() + optionsString;
	}
	
	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}
	
	public void addOption(String option){
		String[] options = new String[this.options.length + 1];
		for(int i = 0; i < this.options.length; i++){
			options[i] = this.options[i];
		}
		options[options.length - 1] = option;
		this.options = options;
	}
	
	public void removeOption(int index){
		if(index < 0 || index >= this.options.length) return;
		String[] options = new String[this.options.length - 1];
		for(int i = 0, j = 0; i < this.options.length; i++){
			if(i == index) continue;
			options[j] = this.options[i];
			j++;
		}
		this.options = options;
	}
}
