package GUI;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import csf_val.Backend;
import csf_val.CSFVProject;
import csf_val.LinearScale;
import csf_val.MultipleChoice;
import csf_val.Question;

public class CoupleSetUpFrame extends JFrame {
	private static final long serialVersionUID = -1476803929886829919L;
	private CSFVProject proj;
	private JPanel contentPane;
	private JTextField txtRemoveCouple;
	private JLabel lblQuestionsAlreadyAdded;
	private JComboBox<String> comboFirstQuestion;
	private JComboBox<String> comboSecondQuestion;
	private JButton btnCouple;

	/**
	 * Create the frame.
	 */
	public CoupleSetUpFrame(Component parentComponent, CSFVProject proj) {
		setResizable(false);
		setLocationRelativeTo(parentComponent);
		setIconImage(Backend.getImageResource(getClass(), Backend.iconLocation));
		this.proj = proj;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 770, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSetUpCoupled = new JLabel("Set Up Coupled Questions");
		lblSetUpCoupled.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblSetUpCoupled.setBounds(10, 11, 318, 38);
		contentPane.add(lblSetUpCoupled);
		
		JLabel lblAQuestionCoupled = new JLabel("<html>A question coupled with another question when matching two students essentially "
				+ "swaps the students' responses before checking the answers.<br/>\r\nFor example, say student A is checking itself "
				+ "against student B on question 2 which is coupled to question 3. Student A's response to question 2 will be compared "
				+ "to student B's response on question 3, and vis versa.<br/>\r\nThis functionality is useful in for pairs like \"What "
				+ "is your personality?\" and \"What personality would you like to be matched with?\"");
		lblAQuestionCoupled.setBounds(10, 45, 744, 68);
		contentPane.add(lblAQuestionCoupled);
		
		JLabel lblCouple = new JLabel("Couple");
		lblCouple.setBounds(10, 124, 46, 14);
		contentPane.add(lblCouple);
		
		comboFirstQuestion = new JComboBox<>();
		comboFirstQuestion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//update the second combo box
				ArrayList<Question> availSecondQuestions = new ArrayList<>();
				
				//find which question was picked in the first combo box
				Question q1 = null;
				for(int j = 0; j < proj.getQuestions().size(); j++){
					if(proj.getQuestions().get(j).getQuestion().equals(comboFirstQuestion.getSelectedItem().toString())){
						q1 = proj.getQuestions().get(j);
						break;
					}
				}
				
				if(q1 != null){
					for(int i = 0; i < proj.getQuestions().size(); i++){
						if(proj.getQuestions().get(i).getCoupleIndex() != -1) continue;
						if(proj.getQuestions().get(i).equals(q1)) continue;
						if(!proj.getQuestions().get(i).isUsedInSorting()) continue;
						
						if(q1 instanceof MultipleChoice && proj.getQuestions().get(i) instanceof MultipleChoice){
							availSecondQuestions.add(proj.getQuestions().get(i));
						}else if(q1 instanceof LinearScale && proj.getQuestions().get(i) instanceof LinearScale){
							availSecondQuestions.add(proj.getQuestions().get(i));
						}
					}
				}
				
				if(availSecondQuestions.size() > 0){
					String[] secOptions = new String[availSecondQuestions.size()];
					for(int i = 0; i < availSecondQuestions.size(); i++){
						secOptions[i] = availSecondQuestions.get(i).getQuestion();
					}
					
					comboSecondQuestion.setModel(new DefaultComboBoxModel<String>(secOptions));
					comboSecondQuestion.setEnabled(true);
					btnCouple.setEnabled(true);
				}
				
			}
		});
		comboFirstQuestion.setBounds(71, 121, 219, 20);
		contentPane.add(comboFirstQuestion);
		
		JLabel lblWith = new JLabel("with");
		lblWith.setBounds(317, 124, 46, 14);
		contentPane.add(lblWith);
		
		comboSecondQuestion = new JComboBox<>();
		comboSecondQuestion.setEnabled(false);
		comboSecondQuestion.setBounds(362, 121, 219, 20);
		contentPane.add(comboSecondQuestion);
		
		btnCouple = new JButton("Couple!");
		btnCouple.setEnabled(false);
		btnCouple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Question q1 = null;
				Question q2 = null;
				int q1i = -1;
				int q2i = -1;
				for(int j = 0; j < proj.getQuestions().size(); j++){
					if(proj.getQuestions().get(j).getQuestion().equals(comboFirstQuestion.getSelectedItem().toString())){
						q1 = proj.getQuestions().get(j);
						q1i = j;
					}
					if(proj.getQuestions().get(j).getQuestion().equals(comboSecondQuestion.getSelectedItem().toString())){
						q2 = proj.getQuestions().get(j);
						q2i = j;
					}
				}
				
				//lots of double checks just to ensure that were good!
				if(q1 != q2 && q1 != null && q1i != q2i){
					if(q1 instanceof MultipleChoice && q2 instanceof MultipleChoice){
						MultipleChoice qm1 = (MultipleChoice) q1;
						MultipleChoice qm2 = (MultipleChoice) q2;
						if(qm1.getOptions().length == qm2.getOptions().length){
							boolean optionsGood = true;
							for(int i = 0; i < qm1.getOptions().length; i++){
								optionsGood = false;
								for(int j = 0; j < qm2.getOptions().length; j++){
									if(qm1.getOptions()[i].equals(qm2.getOptions()[j])) optionsGood = true;
									if(optionsGood) break;
								}
								if(!optionsGood) break;
							}
							
							if(optionsGood){
								q1.setCoupleIndex(q2i);
								q2.setCoupleIndex(q1i);
							}else{
								JOptionPane.showMessageDialog(contentPane, "The options for these two questions were not the same\n" +
							"Please go back to the question edit screen and fix them", "Options not same", JOptionPane.ERROR_MESSAGE);
							}
						}											
					}else if(q1 instanceof LinearScale && q2 instanceof LinearScale){
						if(((LinearScale) q1).getMax() == ((LinearScale) q2).getMax() && ((LinearScale) q1).getMin() == ((LinearScale) q2).getMin()){
							q1.setCoupleIndex(q2i);
							q2.setCoupleIndex(q1i);
						}
					}
				}
				
				btnCouple.setEnabled(false);
				comboSecondQuestion.setEnabled(false);
				comboSecondQuestion.setModel(new DefaultComboBoxModel<String>(new String[]{}));
				update();
			}
		});
		btnCouple.setBounds(624, 120, 89, 23);
		contentPane.add(btnCouple);
		
		JLabel lblQuestionsAlreadyCoupled = new JLabel("<html>Questions Already Coupled:<br/>Couple Number,      Q1 coupled with Q2");
		lblQuestionsAlreadyCoupled.setBounds(10, 179, 280, 29);
		contentPane.add(lblQuestionsAlreadyCoupled);
		
		txtRemoveCouple = new JTextField();
		txtRemoveCouple.setBounds(542, 157, 39, 20);
		contentPane.add(txtRemoveCouple);
		txtRemoveCouple.setColumns(10);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					removeCoupleIndex(Integer.parseInt(txtRemoveCouple.getText()));
					
				}catch(Exception e1){}
				
				txtRemoveCouple.setText("");
				update();
			}
		});
		btnRemove.setBounds(624, 154, 89, 23);
		contentPane.add(btnRemove);
		
		JLabel lblRemoveACouple = new JLabel("Remove a couple (enter couple number):");
		lblRemoveACouple.setBounds(302, 160, 230, 14);
		contentPane.add(lblRemoveACouple);
		
		JButton btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
				new StudentValidationFrame(contentPane, proj);
			}
		});
		btnDone.setBounds(665, 289, 89, 23);
		contentPane.add(btnDone);
		
		JButton btnRemoveAllCouples = new JButton("Remove All Couples");
		btnRemoveAllCouples.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < proj.getQuestions().size(); i++){
					proj.getQuestions().get(i).setCoupleIndex(-1);
				}
				update();
			}
		});
		btnRemoveAllCouples.setBounds(10, 289, 127, 23);
		contentPane.add(btnRemoveAllCouples);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 210, 744, 68);
		contentPane.add(scrollPane);
		
		lblQuestionsAlreadyAdded = new JLabel("");
		scrollPane.setViewportView(lblQuestionsAlreadyAdded);
		
		JButton btnReturnToQuestion = new JButton("Return to Question Set Up");
		btnReturnToQuestion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(contentPane, "Going back will not save your current couples, are you sure?", 
						"Progress will not be saved, continue?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
					setVisible(false);
					dispose();
					new QuestionSetupFrame(contentPane, proj);
				}
			}
		});
		btnReturnToQuestion.setBounds(161, 289, 167, 23);
		contentPane.add(btnReturnToQuestion);
		
		update();
		
		setVisible(true);
	}
	
	private void update(){
		//update the infobox
		String text = "<html>";
		ArrayList<Question> questionsAlreadyChecked = new ArrayList<>();
		
		for(int i = 0, j = 1; i < proj.getQuestions().size(); i++){
			if(proj.getQuestions().get(i).getCoupleIndex() != -1 && 
					proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()).getCoupleIndex() != -1 && 
					!questionsAlreadyChecked.contains(proj.getQuestions().get(i))){
				text += "<br/> " + j + ": \"" + proj.getQuestions().get(i).getQuestion() + "\" coupled with \"" +
						proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()).getQuestion() + "\"";
				j++;
				questionsAlreadyChecked.add(proj.getQuestions().get(i));
				questionsAlreadyChecked.add(proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()));
			}
		}
		
		lblQuestionsAlreadyAdded.setText(text);
		
		//update the first question combobox (second is updated when the first is selected)
		ArrayList<String> comOptions = new ArrayList<>();
		for(int i = 0; i < proj.getQuestions().size(); i++){
			if(proj.getQuestions().get(i).getCoupleIndex() == -1 && proj.getQuestions().get(i).isUsedInSorting()){
				comOptions.add(proj.getQuestions().get(i).getQuestion());
			}
		}
		comboFirstQuestion.setModel(new DefaultComboBoxModel<String>(comOptions.toArray(new String[comOptions.size()])));
		
		repaint();
	}
	
	private void removeCoupleIndex(int index){
		ArrayList<Question> questionsAlreadyChecked = new ArrayList<>();
		
		if(index <= 0) return;
		
		for(int i = 0, j = 1; i < proj.getQuestions().size(); i++){
			if(proj.getQuestions().get(i).getCoupleIndex() != -1 && 
					proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()).getCoupleIndex() != -1 && 
					!questionsAlreadyChecked.contains(proj.getQuestions().get(i))){
				if(j == index){
					proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()).setCoupleIndex(-1);
					proj.getQuestions().get(i).setCoupleIndex(-1);
					return;
				}
				j++;
				questionsAlreadyChecked.add(proj.getQuestions().get(i));
				questionsAlreadyChecked.add(proj.getQuestions().get(proj.getQuestions().get(i).getCoupleIndex()));
			}
		}
		
		update();
	}
}
