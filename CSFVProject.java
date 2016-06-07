package csf_val;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import csf_val.CSFVPrinter.PrintItem;
import csf_val.Student.StudentData;

public class CSFVProject {

	private String name;
	private String saveLocation;
	private long creationDate;
	private ArrayList<Question> questions;
	private ArrayList<Student> students;
	private boolean areStudentsSorted = false;

	//print settings
	private float inkDarkness = .65f;
	private int resultRowFontSize = 12;
	private int topNameFontSize = 20;
	private int numStudentsPerSheet = 2;
	private int numSimilarsToShow = 1;
	private float spaceBetweenStudentPrints = 30;
	private float minHeightOfImage = 40;
	private String customMessage = "Don't forget to tell your friends to pick up their results!";
	private String customImageLocation = ""; //blank means that no custom image will be used
	private String studentNameFormat; //index in topRowItems of the student name format to be used for printing
	private ArrayList<PrintItem> topRowItems = new ArrayList<PrintItem>();

	public CSFVProject(String name, String saveLocation, long creationDate, ArrayList<Question> questions,
			ArrayList<Student> students, ArrayList<PrintItem> topRowItems, float inkDarkness, int resultRowFontSize,
			int topNameFontSize, int numStudentsPerSheet, int numSimilarsToShow, String studentNameFormat,
			float spaceBetweenStudentPrints, float minHeightOfImage){
		this.name = name;
		this.saveLocation = saveLocation;
		this.creationDate = creationDate;
		this.questions = questions;
		this.students = students;
		this.topRowItems = topRowItems;
		this.inkDarkness = inkDarkness;
		this.resultRowFontSize = resultRowFontSize;
		this.topNameFontSize = topNameFontSize;
		this.numStudentsPerSheet = numStudentsPerSheet;
		this.studentNameFormat = studentNameFormat;
		this.numSimilarsToShow = numSimilarsToShow;
		this.spaceBetweenStudentPrints = spaceBetweenStudentPrints;
		this.minHeightOfImage = minHeightOfImage;
	}

	public CSFVProject(String name, String saveLocation){
		this(name, saveLocation, System.currentTimeMillis(), new ArrayList<Question>(), new ArrayList<Student>(), new ArrayList<PrintItem>(),
				.65f, 12, 20, 2, 1, "Set the Student Name Format", 30, 40);
	}

	public CSFVProject(String saveLocation, ArrayList<String> projectArgs){
		this.saveLocation = saveLocation;

		name = projectArgs.get(0);
		try{
			creationDate = Long.parseLong(projectArgs.get(1));

			questions = new ArrayList<Question>();
			int i = 2;
			while(!projectArgs.get(i).equals("" + (char) 30)){
				ArrayList<String> qArgs = Backend.getSaveStringArgsFromSaveString(projectArgs.get(i));
				QuestionType qType = QuestionType.getQuestionTypeFromInt(Integer.parseInt(qArgs.get(6)));
				if(qType == QuestionType.SHORT_ANSWER || qType == QuestionType.DATE || qType == QuestionType.TIME || qType == QuestionType.PARAGRAPH ||
						qType == QuestionType.TIMESTAMP || qType == QuestionType.UNKNOWN){
					questions.add(new Question(qArgs));
				}else if(qType == QuestionType.MULTIPLE_CHOICE || qType == QuestionType.CHECKBOXES || qType == QuestionType.DROPDOWN){
					questions.add(new MultipleChoice(qArgs));
				}else if(qType == QuestionType.LINEAR_SCALE){
					questions.add(new LinearScale(qArgs));
				}else{
					System.out.println("Didn't get the correct type for question " + qArgs.get(0) + "\nThis question will not be added.");
				}
				i++;
			}
			i++;
			students = new ArrayList<Student>();
			while(!projectArgs.get(i).equals("" + (char) 30)){
				students.add(new Student(projectArgs.get(i++)));
			}
			i++;

			//get the ink darkness
			inkDarkness = Float.parseFloat(projectArgs.get(i++));		
			studentNameFormat = projectArgs.get(i++);
			topNameFontSize = Integer.parseInt(projectArgs.get(i++));
			resultRowFontSize = Integer.parseInt(projectArgs.get(i++));
			numStudentsPerSheet = Integer.parseInt(projectArgs.get(i++));
			numSimilarsToShow = Integer.parseInt(projectArgs.get(i++));
			spaceBetweenStudentPrints = Float.parseFloat(projectArgs.get(i++));
			minHeightOfImage = Float.parseFloat(projectArgs.get(i++));
			customMessage = projectArgs.get(i++);
			customImageLocation = projectArgs.get(i++);

			//get the top row print args
			while(i < projectArgs.size() && !projectArgs.get(i).equals("" + (char) 30)){
				topRowItems.add(new PrintItem(projectArgs.get(i++)));
			}
			i++;
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "There was a problem loading the CSFV File\nIt may be an old version, missing, or corrupted",
					"Can't read file", JOptionPane.ERROR_MESSAGE);
			Backend.setLocationOfLastSave("");
			Backend.saveData();
			System.exit(1);
		}	
	}

	public ArrayList<String> getSaveFileArgs(){
		ArrayList<String> fileArgs = new ArrayList<String>();
		fileArgs.add(name);
		fileArgs.add("" + creationDate);
		for(int i = 0; i < questions.size(); i++){
			fileArgs.add(questions.get(i).getSaveString());
		}
		fileArgs.add("" + (char) 30);
		for(int i = 0; i < students.size(); i++){
			fileArgs.add(students.get(i).toSaveString());
		}
		fileArgs.add("" + (char) 30);
		fileArgs.add("" + inkDarkness);
		fileArgs.add(studentNameFormat);
		fileArgs.add("" + topNameFontSize);
		fileArgs.add("" + resultRowFontSize);
		fileArgs.add("" + numStudentsPerSheet);
		fileArgs.add("" + numSimilarsToShow);
		fileArgs.add("" + spaceBetweenStudentPrints);
		fileArgs.add("" + minHeightOfImage);
		fileArgs.add(customMessage);
		fileArgs.add(customImageLocation);
		for(int i = 0; i < topRowItems.size(); i++){
			fileArgs.add(topRowItems.get(i).toSaveString());
		}
		fileArgs.add("" + (char) 30);

		return fileArgs;
	}

	public void assignStudentMatches(JFrame jFrame){
		final JDialog dlg = new JDialog(jFrame, "Assigning Students... ", true);
		JProgressBar dpb = new JProgressBar(0, 1000);

		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		panel.add(BorderLayout.NORTH, new JLabel("Assigning the students, please be patient"));
		panel.add(BorderLayout.CENTER, dpb);
		panel.add(BorderLayout.SOUTH, new JLabel("Once finished, the save menu will open soon after"));

		dlg.add(BorderLayout.CENTER, panel);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(350, 125);
		dlg.setResizable(false);
		dlg.setLocationRelativeTo(jFrame);
		
		ArrayList<String> problems = new ArrayList<>();

		Thread t = new Thread(new Runnable() {
			public void run() {
				dlg.setVisible(true);
				long timeStuStart = 0;
					for(int i = 0; i < students.size(); i++){
						students.get(i).getStudentMatches().clear();
						//for each other student...
						for(int j = 0; j < students.size(); j++){
							if(j == i) continue;
							ArrayList<String> studentSimilarities = new ArrayList<String>();
							float studentPercent = 0;
							double numQForMatching = 0;
							//for each question...
							for(int k = 0; k < questions.size(); k++){
								if(questions.get(k).isUsedInSorting()){
									numQForMatching += questions.get(k).getWeight();
									float per = -100;
									if(questions.get(k).getQuestionType() == QuestionType.LINEAR_SCALE){
										try{
											int stu1Answer = 0;
											try{
												stu1Answer = (int) Double.parseDouble(students.get(i).getAnswerChoices()[k]);
											}catch(Exception e){
												String error = CSFVPrinter.parsePrintItem(getStudentNameFormat(), students.get(i), 0) + 
														" on question " + 
														(k + 1) + " (" + questions.get(k).getQuestion().substring(0, Math.min(30,
																questions.get(k).getQuestion().length())) + 
														(questions.get(k).getQuestion().length() > 30 ? "..." : "") + 
														"). This student's answer needs to be a valid number.";
												if(!problems.contains(error)) problems.add(error);
												continue;
											}
											
											int stu2Answer = (int) Double.parseDouble(students.get(j).getAnswerChoices()[k]);
											if(questions.get(k).getCoupleIndex() != -1){
												stu2Answer = Integer.parseInt(students.get(j).getAnswerChoices()[
												                                                                 questions.get(k).getCoupleIndex()]);
											}

											per = (float) (100 - 100.0 * Math.abs(stu1Answer - stu2Answer) / 
													(((LinearScale) questions.get(k)).getMax() - 
															((LinearScale) questions.get(k)).getMin() + 1));
										}catch(Exception e){
											
											continue;
										}

									}else if(questions.get(k).getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
											questions.get(k).getQuestionType() == QuestionType.CHECKBOXES ||
											questions.get(k).getQuestionType() == QuestionType.DROPDOWN){

										String stu1Stub = students.get(i).getAnswerChoices()[k];
										String stu2Stub;
										if(questions.get(k).getCoupleIndex() == -1){
											stu2Stub = students.get(j).getAnswerChoices()[k];
										}else{
											stu2Stub = students.get(j).getAnswerChoices()[questions.get(k).getCoupleIndex()];
										}

										int intSize = ((MultipleChoice) questions.get(k)).getOptions().length;
										ArrayList<String> stu1ChoosenOptions = new ArrayList<String>(intSize);
										ArrayList<String> stu2ChoosenOptions = new ArrayList<String>(intSize);

										ArrayList<String> options = new ArrayList<String>();
										for(int l = 0; l < intSize; l++){
											options.add(((MultipleChoice) questions.get(k)).getOptions()[l]);
										}

										Collections.sort(options, new Comparator<String>(){
											@Override
											public int compare(String o1, String o2) {
												return o2.length() - o1.length();
											}
										});

										for(int l = 0; l < options.size(); l++){
											if(stu1Stub.contains(options.get(l))){
												stu1ChoosenOptions.add(options.get(l));
												stu1Stub = stu1Stub.substring(0, stu1Stub.indexOf(options.get(l))) +
														stu1Stub.substring(stu1Stub.indexOf(options.get(l)) + 
																options.get(l).length());
											}

											if(stu2Stub.contains(options.get(l))){
												stu2ChoosenOptions.add(options.get(l));
												stu2Stub = stu2Stub.substring(0, stu2Stub.indexOf(options.get(l))) +
														stu2Stub.substring(stu2Stub.indexOf(options.get(l)) + 
																options.get(l).length());
											}
										}

										//Now we'll compare the student choices
										int numCompareQuestionHas = 0;
										for(int l = 0; l < stu1ChoosenOptions.size(); l++){
											if(stu2ChoosenOptions.contains(stu1ChoosenOptions.get(l))) numCompareQuestionHas++;
										}

										if(stu1ChoosenOptions.size() == 0){
											String error = CSFVPrinter.parsePrintItem(getStudentNameFormat(), students.get(i), 0) + 
													" on question " + 
													(k + 1) + " (" + questions.get(k).getQuestion().substring(0, Math.min(30,
															questions.get(k).getQuestion().length())) + 
													(questions.get(k).getQuestion().length() > 30 ? "..." : "") + 
													") This student's answer may be invalid/missing, or the question may not have all of the possible options.";
											if(!problems.contains(error)) problems.add(error);
										}else{
											per = 100 * numCompareQuestionHas / stu1ChoosenOptions.size();
										}

									}else{
										//anything else we can't sort
										numQForMatching -= questions.get(k).getWeight();
										//this is here because I didn't want to have a numQForMatching += weight; in both of the other options
										continue;
									}

									if(per < 0.1f){
										per = 0;
									}
									if(per > 100){
										per = 100;
									}
									if(per > 99.9f){
										per = 100;
									}

									if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_AND){
										if(per != 100) per = 0;
									}else if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_OR){
										if(per > 0) per = 100;
									}else if(questions.get(k).getScoreType() == ScoreType.INVERTED_PERCENTAGE){
										per = 100 - per;
									}else if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_NAND){
										if(per != 100) per = 0;
										per = 100 - per;
									}else if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_NOR){
										if(per > 0) per = 100;
										per = 100 - per;
									}else if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_XOR){
										if(per == 100) per = 0;
										if(per > 0) per = 100;
									}else if(questions.get(k).getScoreType() == ScoreType.BOOLEAN_XNOR){
										if(per == 100) per = 0;
										if(per > 0) per = 100;
										per = 100 - per;
									}

									if(questions.get(k).isDeterminitive()){
										if(per >= 100.0001 || per <= 99.9998) numQForMatching = Integer.MIN_VALUE;
									}
									if(per > 85 && questions.get(k).getShortValue() != null && 
											!questions.get(k).getShortValue().trim().equals("") &&
											!studentSimilarities.contains(questions.get(k).getShortValue()))
										studentSimilarities.add(questions.get(k).getShortValue());

									studentPercent += per * questions.get(k).getWeight();
								}
							}

							if(numQForMatching > 0){
								//if numQForMatching is less than 0 it means a determinative question failed and thus student is not added
								studentPercent /= numQForMatching;
								Collections.shuffle(studentSimilarities);
								students.get(i).addStudentMatch(students.get(j), studentPercent);
								students.get(i).getStudentMatches().get(students.get(i).getStudentMatches().size() - 1).
								setSimilarities(studentSimilarities.toArray(new String[0]));
							}
						}	

						//now sort the student matches
						Collections.sort(students.get(i).getStudentMatches(), new Comparator<StudentData>(){

							@Override
							public int compare(StudentData a, StudentData b) {
								float diff = (b.getPercent() + b.getPercentBump()) - (a.getPercent() + a.getPercentBump());
								if(diff > 0 && diff < 1){
									diff = 1;
								}else if(diff < 0 && diff > -1){
									diff = -1;
								}
								return (int) diff;
							}});

						//We are not going to keep more than 100 of the top students
						while(students.get(i).getStudentMatches().size() > 100) students.get(i).getStudentMatches().remove(100);

						if(System.currentTimeMillis() - timeStuStart > 100){
							dpb.setValue((int) (1000.0 * (i + 1) / students.size()));
							timeStuStart = System.currentTimeMillis();
						}
					}
				dlg.setVisible(false);
				dlg.dispose();
				
				if(problems.size() > 0){
					JDialog errorDialog = new JDialog(jFrame, "Errors Matching Students", true);
					errorDialog.setLocationRelativeTo(jFrame);
					errorDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					errorDialog.setSize(new Dimension(600, 320));
					errorDialog.setResizable(false);
					
					JTextArea textArea = new JTextArea();
					textArea.setEditable(false);
					
					JPanel errorPanel = new JPanel();
					errorPanel.setLayout(null);

					JLabel topLabel = new JLabel("Resolve these errors for more accurate results:");
					topLabel.setBounds(10, 10, 380, 30);
					errorPanel.add(topLabel);
					
					JButton closeButton = new JButton("Close");
					closeButton.setBounds(265, 250, 70, 30);
					closeButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							errorDialog.setVisible(false);
							errorDialog.dispose();
						}
					});
					
					errorPanel.add(closeButton);
					
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setBounds(10, 40, 580, 200);
					scrollPane.setViewportView(textArea);
					
					for(int i = 0; i < problems.size(); i++){
						textArea.append(problems.get(i));
						if(i + 1 < problems.size()) textArea.append("\n");
					}
					errorPanel.add(scrollPane);
					
					errorDialog.add(errorPanel);
					
					errorDialog.repaint();
					errorDialog.setVisible(true);
				}
			}
		});
		t.start();

		dlg.setVisible(true);

		areStudentsSorted = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSaveLocation() {
		return saveLocation;
	}

	public void setSaveLocation(String saveLocation) {
		this.saveLocation = saveLocation;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}

	public ArrayList<Student> getStudents() {
		areStudentsSorted = false;
		return students;
	}

	public void setStudents(ArrayList<Student> students) {
		areStudentsSorted = false;
		this.students = students;
	}

	public float getInkDarkness() {
		return inkDarkness;
	}

	public void setInkDarkness(float inkDarkness) {
		this.inkDarkness = inkDarkness;
	}

	public ArrayList<PrintItem> getTopRowItems() {
		return topRowItems;
	}

	public void setTopRowItems(ArrayList<PrintItem> topRowItems) {
		this.topRowItems = topRowItems;
	}

	public String getStudentNameFormat() {
		return studentNameFormat;
	}

	public void setStudentNameFormat(String studentNameFormat) {
		this.studentNameFormat = studentNameFormat;
	}

	public int getResultRowFontSize() {
		return resultRowFontSize;
	}

	public void setResultRowFontSize(int resultRowFontSize) {
		this.resultRowFontSize = resultRowFontSize;
	}

	public int getTopNameFontSize() {
		return topNameFontSize;
	}

	public void setTopNameFontSize(int topNameFontSize) {
		this.topNameFontSize = topNameFontSize;
	}

	public int getNumStudentsPerSheet() {
		return numStudentsPerSheet;
	}

	public void setNumStudentsPerSheet(int numStudentsPerSheet) {
		this.numStudentsPerSheet = numStudentsPerSheet;
	}

	public int getNumSimilarsToShow() {
		return numSimilarsToShow;
	}

	public void setNumSimilarsToShow(int numSimilarsToShow) {
		this.numSimilarsToShow = numSimilarsToShow;
	}

	public float getSpaceBetweenStudentPrints() {
		return spaceBetweenStudentPrints;
	}

	public void setSpaceBetweenStudentPrints(float spaceBetweenStudentPrints) {
		this.spaceBetweenStudentPrints = spaceBetweenStudentPrints;
	}

	public float getMinHeightOfImage() {
		return minHeightOfImage;
	}

	public void setMinHeightOfImage(float minHeightOfImage) {
		this.minHeightOfImage = minHeightOfImage;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public String getCustomImageLocation() {
		return customImageLocation;
	}

	public void setCustomImageLocation(String customImageLocation) {
		this.customImageLocation = customImageLocation;
	}

	public boolean areStudentsSorted(){
		return areStudentsSorted;
	}

	public void setStudentsSorted(boolean areStudentsSorted){
		this.areStudentsSorted = areStudentsSorted;
	}
}
