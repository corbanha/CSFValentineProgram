package csf_val;

import java.util.ArrayList;

public class Student {
	
	class StudentData{
		private Student student;
		private float percent;
		private float percentBump;
		private String[] similarities;
		
		public StudentData(Student student, float percent){
			this.setStudent(student);
			this.setPercent(percent);
		}

		public float getPercent() {
			return percent;
		}

		public void setPercent(float percent) {
			this.percent = percent;
		}

		public Student getStudent() {
			return student;
		}

		public void setStudent(Student student) {
			this.student = student;
		}

		public float getPercentBump() {
			return percentBump;
		}

		public void setPercentBump(float percentBump) {
			this.percentBump = percentBump;
		}
		
		public String[] getSimilarities(){
			return similarities;
		}
		
		public void setSimilarities(String[] similarities){
			this.similarities = similarities;
		}
	}

	private String[] answerChoices;
	private ArrayList<StudentData> studentMatches;
	
	public Student(String[] answerChoices){
		this.setAnswerChoices(answerChoices);
		studentMatches = new ArrayList<StudentData>();
	}
	
	public Student(String save){
		String saveString = save;
		ArrayList<String> answers = new ArrayList<String>();
		while(saveString.length() > 0){
			if(saveString.contains("" + (char) 29)){
				answers.add(saveString.substring(0, saveString.indexOf((char) 29)));
				saveString = saveString.substring(saveString.indexOf((char) 29) + 1);
			}else{
				answers.add(saveString);
				saveString = "";
			}
		}
		
		answerChoices = new String[answers.size()];
		for(int i = 0; i < answerChoices.length; i++){
			answerChoices[i] = answers.get(i);
		}
		
		studentMatches = new ArrayList<StudentData>();
	}

	public String[] getAnswerChoices() {
		return answerChoices;
	}

	public void setAnswerChoices(String[] answerChoices) {
		this.answerChoices = answerChoices;
	}
	
	public void addStudentMatch(Student student, float percent){
		StudentData sd = new StudentData(student, percent);
		studentMatches.add(sd);
	}
	
	public ArrayList<StudentData> getStudentMatches() {
		return studentMatches;
	}

	public void setStudentMatches(ArrayList<StudentData> studentMatches) {
		this.studentMatches = studentMatches;
	}
	
	@Override
	public String toString() {
		String student = "Student answer choices:";
		for(int i = 0; i < answerChoices.length; i++){
			student += "\n" + answerChoices[i];
		}
		
		student += "\nStudent Data:";
		for(int i = 0; i < studentMatches.size(); i++){
			student += "\nPer: " + studentMatches.get(i).getPercent() + " PerBump: " + studentMatches.get(i).getPercentBump();
		}
		return student;
	}

	public String toSaveString(){
		String saveString = "";
		//TODO review the saving of the student. As of current, we will not be saving
		//StudentData, the info on which students match with this one the best. This info will have to be recalculated each time.
		for(int i = 0; i < answerChoices.length; i++){
			saveString += answerChoices[i];
			if(i != answerChoices.length - 1) saveString += (char) 29;
		}
		
		return saveString;
	}
}
