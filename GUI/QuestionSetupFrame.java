package GUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import csf_val.Backend;
import csf_val.CSFVProject;
import csf_val.LinearScale;
import csf_val.MultipleChoice;
import csf_val.Student;
import csf_val.UndeterminedQuestion;

public class QuestionSetupFrame extends JFrame {
	private static final long serialVersionUID = -5909441337114340300L;
	private JPanel contentPane;
	private QuestionItem[] questionItems;
	private JPanel questionItemPanel;
	private JButton btnNext;

	public QuestionSetupFrame(Component parentComponent, CSFVProject proj){
		setContent();
		questionItems = new QuestionItem[proj.getQuestions().size()];
		setIconImage(Backend.getImageResource(getClass(), Backend.iconLocation));
		setLocationRelativeTo(parentComponent);
		
		for(int i = 0; i < proj.getQuestions().size(); i++){
			UndeterminedQuestion uq = new UndeterminedQuestion();
			uq.setCoupleQuestionIndex(proj.getQuestions().get(i).getCoupleIndex());
			uq.setDeterminitive(proj.getQuestions().get(i).isDeterminitive());
			uq.setImportQuestion(true);
			uq.setLikelyQuestionType(proj.getQuestions().get(i).getQuestionType());
			if(proj.getQuestions().get(i) instanceof LinearScale){
				uq.setMax(((LinearScale) proj.getQuestions().get(i)).getMax());
				uq.setMin(((LinearScale) proj.getQuestions().get(i)).getMin());
			}else{
				uq.setMax(10);
				uq.setMin(1);
			}
			if(proj.getQuestions().get(i) instanceof MultipleChoice){
				uq.setOptions(((MultipleChoice) proj.getQuestions().get(i)).getOptions());
			}else{
				uq.setOptions(new String[]{"If a Multiple Choice, Checkboxes, or Radio", "Button question, type the options here, one on",
					"each line exactly as they appear on the survey"});
			}
			uq.setQuestion(proj.getQuestions().get(i).getQuestion());
			uq.setScoreType(proj.getQuestions().get(i).getScoreType());
			uq.setUseForSorting(proj.getQuestions().get(i).isUsedInSorting());
			uq.setWeight(proj.getQuestions().get(i).getWeight());
			uq.setShortValue(proj.getQuestions().get(i).getShortValue());
			
			questionItems[i] = new QuestionItem(i + 1, uq);
			questionItemPanel.add(questionItems[i]);
		}
		
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Do stuff
				int coupleQs = JOptionPane.showConfirmDialog(contentPane, "Would you like to set up any couple (linked) questions?",
						"Couple Questions?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				proj.getQuestions().clear();
				//put the questions in the proj
				for(int i = 0; i < questionItems.length; i++){
					if(questionItems[i].getUndeterminedQuestion().isImportQuestion()){
						proj.getQuestions().add(questionItems[i].getUndeterminedQuestion().getAsQuestion());
					}
				}
				
				//add in the students
				int numQuestionsForImport = 0;
				for(int i = 0; i < questionItems.length; i++){
					if(questionItems[i].getUndeterminedQuestion().isImportQuestion())
						numQuestionsForImport++;
				}
				
				if(numQuestionsForImport != proj.getStudents().get(0).getAnswerChoices().length){
					for(int i = 0; i < proj.getStudents().size(); i++){
						ArrayList<String> newStudentAnswers = new ArrayList<>();
						for(String str: proj.getStudents().get(i).getAnswerChoices()) newStudentAnswers.add(str);
						for(int j = 0, k = 0; j < questionItems.length; j++, k++){
							if(!questionItems[j].getUndeterminedQuestion().isImportQuestion()){
								newStudentAnswers.remove(k);
								k--;
							}
						}
						proj.getStudents().set(i, new Student(newStudentAnswers.toArray(new String[newStudentAnswers.size()])));
					}
				}
				
				if(coupleQs == JOptionPane.OK_OPTION){
					if(JOptionPane.showConfirmDialog(contentPane, "Save time later! Coupled questions have to have the same type/options/min/max.\n"
							+ "Would you like to double check that these are all correct for the questions you want to couple?",
							"Double check questions?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION){
						
						//go to the couple question screen
						setVisible(false);
						dispose();
						new CoupleSetUpFrame(contentPane, proj);
					}
				}else if(coupleQs == JOptionPane.NO_OPTION){
					//go to the student screen
					setVisible(false);
					dispose();
					new StudentValidationFrame(contentPane, proj);
				}
			}
		});
		
		repaint();
		setVisible(true);
	}
	
	/**
	 * Create the frame.
	 */
	public QuestionSetupFrame(Component parentComponent, CSFVProject proj, String[][] table) {
		setContent();
		questionItems = new QuestionItem[table[0].length];
		for(int i = 0; i < table[0].length; i++){
			String[] userAnswers = new String[table.length - 1];
			for(int j = 0; j < table.length - 1; j++){
				userAnswers[j] = table[j + 1][i];
			}
			questionItems[i] = new QuestionItem(i + 1, table[0][i], userAnswers);
			questionItemPanel.add(questionItems[i]);
		}
		
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Do stuff
				int coupleQs = JOptionPane.showConfirmDialog(contentPane, "Would you like to set up any couple (linked) questions?",
						"Couple Questions?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				proj.getQuestions().clear();
				proj.getStudents().clear();
				//put the questions in the proj
				for(int i = 0; i < questionItems.length; i++){
					if(questionItems[i].getUndeterminedQuestion().isImportQuestion()){
						proj.getQuestions().add(questionItems[i].getUndeterminedQuestion().getAsQuestion());
					}
				}
				
				//add in the students
				int numQuestionsForImport = 0;
				for(int i = 0; i < questionItems.length; i++){
					if(questionItems[i].getUndeterminedQuestion().isImportQuestion())
						numQuestionsForImport++;
				}
				
				for(int i = 1; i < table.length; i++){
					String[] answerChoices = new String[numQuestionsForImport];
					for(int j = 0, k = 0; j < questionItems.length; j++){
						if(questionItems[j].getUndeterminedQuestion().isImportQuestion()){
							answerChoices[k] = table[i][j];
							k++;
						}
					}
					proj.getStudents().add(new Student(answerChoices));
				}
				
				if(coupleQs == JOptionPane.OK_OPTION){
					if(JOptionPane.showConfirmDialog(contentPane, "Save time later! Coupled questions have to have the same type/options/min/max.\n"
							+ "Would you like to double check that these are all correct for the questions you want to couple?",
							"Double check questions?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION){
						
						//go to the couple question screen
						setVisible(false);
						dispose();
						new CoupleSetUpFrame(contentPane, proj);
					}
				}else if(coupleQs == JOptionPane.NO_OPTION){
					//go to the student screen
					setVisible(false);
					dispose();
					new StudentValidationFrame(contentPane, proj);
				}
			}
		});
		
		repaint();
		setVisible(true);
	}
	
	private void setContent(){
		setResizable(false);
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 770, 736);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblQuestionSetup = new JLabel("Question Setup");
		lblQuestionSetup.setForeground(Color.DARK_GRAY);
		lblQuestionSetup.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblQuestionSetup.setBounds(10, 11, 316, 29);
		contentPane.add(lblQuestionSetup);
		
		JLabel lblHereYouMay = new JLabel("Here you may customize the questions, hover over an option if you are not sure what it does.");
		lblHereYouMay.setBounds(10, 51, 664, 14);
		contentPane.add(lblHereYouMay);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBounds(0, 76, 764, 585);
		contentPane.add(scrollPane);
		
		questionItemPanel = new JPanel();
		questionItemPanel.setLayout(new BoxLayout(questionItemPanel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(questionItemPanel);
		
		JPanel bottomButtonPanel = new JPanel(null);
		bottomButtonPanel.setSize(700, 45);
		questionItemPanel.add(bottomButtonPanel);
		
		btnNext = new JButton("Continue");
		btnNext.setBounds(665, 673, 89, 23);
		contentPane.add(btnNext);
	}
}
