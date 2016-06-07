package GUI;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import csf_val.Backend;
import csf_val.QuestionType;
import csf_val.ScoreType;
import csf_val.UndeterminedQuestion;

public class QuestionItem extends JPanel {
	private static final long serialVersionUID = -7892862980992984166L;
	private JTextField txtWeight;
	private JTextField txtMinAmount;
	private JTextField txtMaxAmount;
	private JTextField txtShortValue;
	private JCheckBox chckbxImportThisQuestoin;
	private JCheckBox chckbxDeterminitive;
	private JCheckBox chckbxUsedForSorting;
	private JComboBox<String> questionTypeComboBox;
	private JComboBox<String> scoreTypeComboBox;
	private JTextArea txtrIfAMultiple;
	private JLabel lblQuestion;
	private JLabel lblCoupleIndex;
	
	private int coupleIndex = -1; 
	private int number;

	/**
	 * @wbp.parser.constructor
	 */
	public QuestionItem(int number, UndeterminedQuestion uq){
		coupleIndex = uq.getCoupleQuestionIndex();
		applyUndeterminedQuestion(number, uq);
	}
	/**
	 * Create the panel.
	 */
	public QuestionItem(int number, String question, String[] userAnswers) {
		boolean couldBeLinearScale = true;
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for(int i = 0; i < userAnswers.length && couldBeLinearScale; i++){
			if(Backend.fi(userAnswers[i]) == null) couldBeLinearScale = false;
			else{
				int num = (int) Backend.fi(userAnswers[i]).floatValue();
				if(num < min) min = num;
				if(num > max) max = num;
			}
		}
		
		UndeterminedQuestion uq = new UndeterminedQuestion();
		uq.setDeterminitive(false);
		uq.setQuestion(question);
		uq.setCoupleQuestionIndex(-1);
		uq.setImportQuestion(true);
		uq.setScoreType(ScoreType.PERCENTAGE);
		uq.setShortValue("");
		uq.setUseForSorting(true);
		uq.setWeight(1);
		
		if(!couldBeLinearScale){
			//assume it to be multipleChoice
			uq.setLikelyQuestionType(QuestionType.MULTIPLE_CHOICE);
			uq.setOptions(Backend.getPossibleOptionsFromMultipleChoiceAnswers(userAnswers, 100));
			if(uq.getOptions().length > 50 || uq.getOptions().length == 0){
				uq.setOptions(new String[]{});
				uq.setLikelyQuestionType(QuestionType.SHORT_ANSWER);
			}
			uq.setMax(10);
			uq.setMin(1);
		}else{
			uq.setLikelyQuestionType(QuestionType.LINEAR_SCALE);
			uq.setMax(max);
			uq.setMin(min);
			uq.setOptions(new String[]{"Make this process easy! Open up the survey as", "it would be seen by a user. Then copy and",
					"paste the options here!"});
		}
		
		applyUndeterminedQuestion(number, uq);
	}
	
	private void applyUndeterminedQuestion(int number, UndeterminedQuestion uq){
		this.number = number;
		contentSetUp();
		lblQuestion.setText(number + ") " + uq.getQuestion());
		chckbxImportThisQuestoin.setSelected(uq.isImportQuestion());
		chckbxUsedForSorting.setSelected(uq.isUseForSorting());
		chckbxDeterminitive.setSelected(uq.isDeterminitive());
		txtWeight.setText("" + uq.getWeight());
		
		questionTypeComboBox.setSelectedIndex(QuestionType.getIntFromQuestionType(uq.getLikelyQuestionType()));
		
		int scoreType = ScoreType.getIntFromScoreType(uq.getScoreType());
		if(scoreType == 0) scoreType = 2;
		else if(scoreType == 1) scoreType = 3;
		else if(scoreType == 2) scoreType = 0;
		else if(scoreType == 3) scoreType = 1;
		scoreTypeComboBox.setSelectedIndex(scoreType);
		
		txtShortValue.setText(uq.getShortValue());
		txtMinAmount.setText("" + uq.getMin());
		txtMaxAmount.setText("" + uq.getMax());
		
		txtrIfAMultiple.setText("");
		for(int i = 0; i < uq.getOptions().length; i++){
			txtrIfAMultiple.append(uq.getOptions()[i]);
			if(i < uq.getOptions().length - 1) txtrIfAMultiple.append("\n");
		}
		
		if(uq.getCoupleQuestionIndex() != -1) lblCoupleIndex.setText("Couple Question: " + (uq.getCoupleQuestionIndex() + 1));
		
		repaint();
	}
	
	private void contentSetUp(){
		setPreferredSize(new Dimension(721, 201));
		setLayout(null);
		
		lblQuestion = new JLabel("");
		lblQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblQuestion.setBounds(11, 8, 649, 17);
		add(lblQuestion);
		
		chckbxUsedForSorting = new JCheckBox("Used For Sorting");
		chckbxUsedForSorting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateEnables();
			}
		});
		chckbxUsedForSorting.setToolTipText("If this question should be used in sorting, for example, you should import the question for the student's name but it should not be used in sorting");
		chckbxUsedForSorting.setSelected(true);
		chckbxUsedForSorting.setBounds(134, 36, 105, 23);
		add(chckbxUsedForSorting);
		
		chckbxImportThisQuestoin = new JCheckBox("Import this question");
		chckbxImportThisQuestoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateEnables();
			}
		});
		chckbxImportThisQuestoin.setToolTipText("Whether or not this question should be imported");
		chckbxImportThisQuestoin.setSelected(true);
		chckbxImportThisQuestoin.setBounds(11, 36, 121, 23);
		add(chckbxImportThisQuestoin);
		
		chckbxDeterminitive = new JCheckBox("Determinitive");
		chckbxDeterminitive.setToolTipText("If the question is determinative, then in sorting the student will not be matched unless this question is met 100%. Use the score type to help customize this. It should be used for items like sex, or perfered grade level.");
		chckbxDeterminitive.setBounds(255, 36, 89, 23);
		add(chckbxDeterminitive);
		
		JLabel lblWeight = new JLabel("Weight:");
		lblWeight.setToolTipText("The weight of this question, 1 is normal");
		lblWeight.setBounds(380, 38, 45, 14);
		add(lblWeight);
		
		txtWeight = new JTextField();
		txtWeight.setText("1");
		txtWeight.setBounds(425, 36, 36, 20);
		add(txtWeight);
		txtWeight.setColumns(1);
		
		JLabel lblQuestionType = new JLabel("Question Type:");
		lblQuestionType.setToolTipText("The question type. It should be the same question type as in the Google Form");
		lblQuestionType.setBounds(488, 42, 74, 14);
		add(lblQuestionType);
		
		questionTypeComboBox = new JComboBox<>();
		questionTypeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Multiple Choice", "Checkboxes", "Dropdown", "Linear Scale", "Multiple Choice Grid", "Short Answer", "Date", "Time", "Paragraph", "Timestamp", "Unknown"}));
		questionTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateEnables();
			}
		});
		questionTypeComboBox.setBounds(567, 39, 118, 20);
		add(questionTypeComboBox);
		
		JLabel lblScoreType = new JLabel("Score Type:");
		lblScoreType.setBounds(10, 69, 58, 14);
		add(lblScoreType);
		
		scoreTypeComboBox = new JComboBox<>();
		scoreTypeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Percentage", "Inverted Percentage", "Boolean AND", "Boolean OR", "Boolean NAND", "Boolean NOR", "Boolean XOR", "Boolean XNOR"}));
		scoreTypeComboBox.setBounds(73, 66, 125, 20);
		add(scoreTypeComboBox);
		
		JLabel lblMin = new JLabel("Min:");
		lblMin.setToolTipText("The minimum amount for the linear scale question");
		lblMin.setBounds(28, 125, 20, 14);
		add(lblMin);
		
		txtMinAmount = new JTextField();
		txtMinAmount.setEnabled(false);
		txtMinAmount.setText("1");
		txtMinAmount.setBounds(53, 122, 86, 20);
		add(txtMinAmount);
		txtMinAmount.setColumns(10);
		
		JLabel lblMax = new JLabel("Max:");
		lblMax.setToolTipText("The maximum amount for the linear scale question");
		lblMax.setBounds(144, 125, 24, 14);
		add(lblMax);
		
		txtMaxAmount = new JTextField();
		txtMaxAmount.setEnabled(false);
		txtMaxAmount.setText("10");
		txtMaxAmount.setBounds(173, 122, 86, 20);
		add(txtMaxAmount);
		txtMaxAmount.setColumns(10);
		
		JLabel lblShortValue = new JLabel("Short Value(leave blank if none):");
		lblShortValue.setToolTipText("If this textfield is filled, then the value in the textfield will be showed on the results sheet of the similar things that the student had with another student.");
		lblShortValue.setBounds(11, 97, 157, 14);
		add(lblShortValue);
		
		txtShortValue = new JTextField();
		txtShortValue.setBounds(173, 94, 86, 20);
		add(txtShortValue);
		txtShortValue.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(269, 88, 442, 104);
		add(scrollPane);
		
		txtrIfAMultiple = new JTextArea();
		txtrIfAMultiple.setText("If a Multiple Choice, Checkboxes, or Radio\nButton question, type the options here, one on\neach line exactly as they appear on the survey");
		scrollPane.setViewportView(txtrIfAMultiple);
		
		JLabel lblIfMultipleChoice = new JLabel("If Multiple Choice, Checkboxes, or Radio Button, type options exactly here, one per line.");
		lblIfMultipleChoice.setToolTipText("Type the question options here for Multiple Choice, Checkboxes, or Radio Button questions");
		lblIfMultipleChoice.setBounds(265, 69, 446, 17);
		add(lblIfMultipleChoice);
		
		lblCoupleIndex = new JLabel("");
		lblCoupleIndex.setBounds(28, 150, 231, 14);
		add(lblCoupleIndex);
	}
	
	public UndeterminedQuestion getUndeterminedQuestion(){
		UndeterminedQuestion uq = new UndeterminedQuestion();
		uq.setDeterminitive(chckbxDeterminitive.isSelected());
		uq.setImportQuestion(chckbxImportThisQuestoin.isSelected());
		uq.setLikelyQuestionType(QuestionType.getQuestionTypeFromString(questionTypeComboBox.getSelectedItem().toString()));
		try{
			uq.setMax(Integer.parseInt(txtMaxAmount.getText()));
		}catch(Exception e){
			uq.setMax(10);
		}
		try{
			uq.setMin(Integer.parseInt(txtMinAmount.getText()));
		}catch(Exception e){
			uq.setMin(1);
		}
		
		Scanner optionsScanner = new Scanner(txtrIfAMultiple.getText());
		optionsScanner.useDelimiter("\n");
		ArrayList<String> options = new ArrayList<>();
		while(optionsScanner.hasNext()) options.add(optionsScanner.next());
		for(String str : options) if(str.equals("")) options.remove(str);
		optionsScanner.close();
		uq.setOptions(options.toArray(new String[options.size()]));
		
		uq.setCoupleQuestionIndex(coupleIndex);
		uq.setQuestion(lblQuestion.getText().substring(2 + ("" + number).length()));
		uq.setScoreType(ScoreType.getScoreTypeFromString(scoreTypeComboBox.getSelectedItem().toString()));
		uq.setShortValue(txtShortValue.getText());
		uq.setUseForSorting(chckbxUsedForSorting.isSelected());
		try{
			uq.setWeight(Float.parseFloat(txtWeight.getText()));
		}catch(Exception e){
			uq.setWeight(1);
		}
		
		return uq;
	}
	
	private void updateEnables(){
		JComponent[] comp = new JComponent[]{chckbxUsedForSorting, chckbxDeterminitive, txtWeight, scoreTypeComboBox,
				questionTypeComboBox, txtShortValue, questionTypeComboBox, txtMinAmount, txtMaxAmount, txtrIfAMultiple};
		
		for(int i = 0; i < comp.length; i++) comp[i].setEnabled(true);
		
		if(!chckbxImportThisQuestoin.isSelected()){
			for(int i = 0; i < comp.length; i++) comp[i].setEnabled(false);
		}else if(!chckbxUsedForSorting.isSelected()){
			for(int i = 1; i < comp.length; i++) comp[i].setEnabled(false);
		}else{
			String qt = questionTypeComboBox.getSelectedItem().toString();
			if(qt.equals("Multiple Choice") || qt.equals("Checkboxes") || qt.equals("Dropdown")){
				txtMaxAmount.setEnabled(false);
				txtMinAmount.setEnabled(false);
			}else if(qt.equals("Linear Scale")){
				txtrIfAMultiple.setEnabled(false);
			}else{
				txtMaxAmount.setEnabled(false);
				txtMinAmount.setEnabled(false);
				txtrIfAMultiple.setEnabled(false);
				scoreTypeComboBox.setEnabled(false);
				txtShortValue.setEnabled(false);
				txtWeight.setEnabled(false);
				chckbxDeterminitive.setEnabled(false);
			}
		}
		repaint();
	}
}
