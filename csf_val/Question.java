package csf_val;

import java.util.ArrayList;

public class Question {
	
	private String question;
	private boolean usedInSorting;
	private boolean determinitive;
	private int coupleIndex = -1;
	private ScoreType scoreType; //boolean AND, boolean OR, percentage, inverted percentage
	private QuestionType questionType; //multiplechoice, short answer, paragraph, check box, etc.
	private float weight = 1;
	private String shortValue = " "; //like "music" or "fav ice cream" these will show up under the similarities section,
	//if blank it will be skipped
	
	/**
	 * Sets up a question.
	 * @param question the question that the user is asked
	 * @param usedInSorting whether this question will be used in sorting
	 * @param determinitive if true, when checked by another student, if the student does not get 100%,
	 * then they will be removed from that students list of possible students.<br>For example:<br>
	 * Sally is comparing herself to all of the other students. She only wants to be paired with boys,
	 * so the question asking gender is determinitive. If a student gets less than 100% (if they are not
	 * a boy), then they will be removed from Sally's list of top candidates.
	 * @param coupleIndex if not -1, then this refers to the index of the couple question for this question.
	 * If q1 is a couple to q2 then q2 must be a couple to q1. If q2 is changed, then q1 will have it's
	 * reference to q2 removed.
	 * @param scoreType The score type for this question, essentially how the percentage score is brought
	 * about based on the question. <br>Boolean AND: the percentage has to be 100% for
	 * it to be 100%. Otherwise, the percentage score will be 0%.<br>Boolean OR: if the percentage is greater
	 * than 0%, then the percentage is 100%.<br>Percentage: the actual percentage returned by the compare of
	 * the question. This will be different based on the question.<br>Inverted Percentage: this is 100 - the
	 * actual percentage score.
	 * @param questionType This is the question type. It may refer to any of the following possible questions:
	 * multiple choice, checkboxes, dropdown, linear scale, multiple choice grid. And the following questions
	 * which cannot be sorted (the usedInSorting boolean will not matter): short answer, date, time,
	 * paragraph, timestamp, and unknown.
	 */
	public Question(String question, boolean usedInSorting, boolean determinitive, int coupleIndex,
			ScoreType scoreType, QuestionType questionType){
		this.question = question;
		this.usedInSorting = usedInSorting;
		this.determinitive = determinitive;
		this.coupleIndex = coupleIndex;
		this.scoreType = scoreType;
		this.questionType = questionType;		
	}
	
	/**
	 * Create a question. Assumes that the question is not determinitive, and that it has no couple.
	 * @param question
	 * @param usedInSorting
	 * @param scoreType
	 * @param questionType
	 */
	public Question(String question, boolean usedInSorting, ScoreType scoreType, QuestionType questionType){
		this(question, usedInSorting, false, -1, scoreType, questionType);
	}
	
	/**
	 * Create a question. Assumes that the question is used for sorting, is not determinitive, and has no
	 * couple.
	 * @param question
	 * @param scoreType
	 * @param questionType
	 */
	public Question(String question, ScoreType scoreType, QuestionType questionType){
		this(question, true, false, -1, scoreType, questionType);
	}
	
	public Question(Question q){
		this(q.getQuestion(), q.isUsedInSorting(), q.isDeterminitive(), q.getCoupleIndex(), q.getScoreType(), q.getQuestionType());
		setWeight(q.getWeight());
		setShortValue(q.getShortValue());
	}
	
	@Override
	public String toString(){
		return question;
	}
	
	public Question(ArrayList<String> saveStringArgs){
		question = saveStringArgs.get(0);
		usedInSorting = Boolean.parseBoolean(saveStringArgs.get(1));
		determinitive = Boolean.parseBoolean(saveStringArgs.get(2));
		coupleIndex = Integer.parseInt(saveStringArgs.get(3));
		weight = Float.parseFloat(saveStringArgs.get(4));
		scoreType = ScoreType.getScoreTypeFromInt(Integer.parseInt(saveStringArgs.get(5)));
		questionType = QuestionType.getQuestionTypeFromInt(Integer.parseInt(saveStringArgs.get(6)));
		shortValue = saveStringArgs.get(7);
	}
	
	public String getSaveString(){
		String str = "";
		str += question + ("" + (char) 29);
		str += "" + usedInSorting + ("" + (char) 29);
		str += "" + determinitive + ("" + (char) 29);
		str += coupleIndex + ("" + (char) 29);
		str += weight + ("" + (char) 29);
		str += ScoreType.getIntFromScoreType(scoreType) + ("" + (char) 29);
		str += QuestionType.getIntFromQuestionType(questionType) + ("" + (char) 29);
		str += shortValue;
		return str;
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public boolean isUsedInSorting() {
		if(usedInSorting && (questionType == QuestionType.SHORT_ANSWER ||
				questionType == QuestionType.DATE ||
				questionType == QuestionType.TIME ||
				questionType == QuestionType.PARAGRAPH ||
				questionType == QuestionType.TIMESTAMP ||
				questionType == QuestionType.UNKNOWN)){
			usedInSorting = false;
		}
		return usedInSorting;
	}

	public void setUsedInSorting(boolean usedInSorting) {
		this.usedInSorting = usedInSorting;
	}

	public boolean isDeterminitive() {
		return determinitive;
	}

	public void setDeterminitive(boolean determinitive) {
		this.determinitive = determinitive;
	}

	public int getCoupleIndex() {
		return coupleIndex;
	}

	public void setCoupleIndex(int coupleIndex) {
		this.coupleIndex = coupleIndex;
	}

	public ScoreType getScoreType() {
		return scoreType;
	}

	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}
	
	public float getWeight(){
		return weight;
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}
	
	public String getShortValue(){
		return shortValue;
	}
	
	public void setShortValue(String shortValue){
		if(shortValue.trim().equals("")){
			this.shortValue = " "; //this is required for correct saving
		}else{
			this.shortValue = shortValue;
		}
	}
}

