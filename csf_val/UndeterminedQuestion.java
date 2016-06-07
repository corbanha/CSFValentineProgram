package csf_val;

public class UndeterminedQuestion {
	protected String question = "";
	protected String[] options = {}; //only multiple choice question
	protected ScoreType scoreType = ScoreType.PERCENTAGE;
	protected boolean useForSorting = true;
	protected boolean importQuestion = true;
	protected boolean determinitive = false;
	protected float weight = 1;
	protected int coupleQuestionIndex = -1;
	protected QuestionType likelyQuestionType = QuestionType.UNKNOWN;
	protected int min = 0, max = 1;
	protected String shortValue = "";
	
	public Question getAsQuestion(){
		if(likelyQuestionType == QuestionType.LINEAR_SCALE){
			LinearScale ls = new LinearScale(question, useForSorting, determinitive, coupleQuestionIndex, scoreType, likelyQuestionType, min, max);
			ls.setWeight(weight);
			ls.setShortValue(shortValue);
			return ls;
		}else if(likelyQuestionType == QuestionType.CHECKBOXES ||
				likelyQuestionType == QuestionType.DROPDOWN ||
				likelyQuestionType == QuestionType.MULTIPLE_CHOICE){
			MultipleChoice mc = new MultipleChoice(question, useForSorting, determinitive, coupleQuestionIndex, scoreType, likelyQuestionType, options);
			mc.setQuestionType(likelyQuestionType);
			mc.setWeight(weight);
			mc.setShortValue(shortValue);
			return mc;
		}else{
			Question q = new Question(question, useForSorting, determinitive, coupleQuestionIndex, scoreType, likelyQuestionType);
			q.setQuestionType(likelyQuestionType);
			q.setWeight(weight);
			q.setShortValue(shortValue);
			return q;
		}
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public ScoreType getScoreType() {
		return scoreType;
	}

	public void setScoreType(ScoreType questionMatchType) {
		this.scoreType = questionMatchType;
	}

	public boolean isUseForSorting() {
		return useForSorting;
	}

	public void setUseForSorting(boolean useForSorting) {
		this.useForSorting = useForSorting;
	}

	public boolean isImportQuestion() {
		return importQuestion;
	}

	public void setImportQuestion(boolean importQuestion) {
		this.importQuestion = importQuestion;
	}
	
	public boolean isDeterminitive(){
		return determinitive;
	}
	
	public void setDeterminitive(boolean determinitive){
		this.determinitive = determinitive;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getCoupleQuestionIndex() {
		return coupleQuestionIndex;
	}

	public void setCoupleQuestionIndex(int coupleQuestionIndex) {
		this.coupleQuestionIndex = coupleQuestionIndex;
	}

	public QuestionType getLikelyQuestionType() {
		return likelyQuestionType;
	}

	public void setLikelyQuestionType(QuestionType likelyQuestionType) {
		this.likelyQuestionType = likelyQuestionType;
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

	public String getShortValue() {
		return shortValue;
	}

	public void setShortValue(String shortValue) {
		if(shortValue.trim().equals("")){
			this.shortValue = " ";
		}else{
			this.shortValue = shortValue;
		}
	}
}
