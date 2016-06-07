package csf_val;

import java.util.ArrayList;

public class LinearScale extends Question{
	private int min;
	private int max;
	
	public LinearScale(String question, boolean usedInSorting, boolean determinitive, int coupleIndex,
			ScoreType scoreType, QuestionType questionType, int min, int max){
		super(question, usedInSorting, determinitive, coupleIndex, scoreType, questionType);
		this.min = min;
		this.max = max;
	}
	
	public LinearScale(Question q){
		this(q.getQuestion(), q.isUsedInSorting(), q.isDeterminitive(), q.getCoupleIndex(), q.getScoreType(), q.getQuestionType(), 0, 10);
		setShortValue(q.getShortValue());
		setWeight(q.getWeight());
	}
	
	public LinearScale(Question q, int min, int max){
		this(q.getQuestion(), q.isUsedInSorting(), q.isDeterminitive(), q.getCoupleIndex(), q.getScoreType(), q.getQuestionType(), min, max);
		setShortValue(q.getShortValue());
		setWeight(q.getWeight());
	}
	
	public LinearScale(ArrayList<String> saveStringArgs){
		super(saveStringArgs);
		min = Integer.parseInt(saveStringArgs.get(8));
		max = Integer.parseInt(saveStringArgs.get(9));
	}

	@Override
	public String getSaveString() {
		return super.getSaveString() + ("" + (char) 29) + min + ("" + (char) 29) + max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}
