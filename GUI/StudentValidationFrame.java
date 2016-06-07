package GUI;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import csf_val.Backend;
import csf_val.CSFVProject;

public class StudentValidationFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -143813801289211321L;
	private JPanel contentPane;
	private JTextField txtQ1NameFormat;
	private JTextField txtQ2NameFormat;

	/**
	 * Create the frame.
	 */
	public StudentValidationFrame(Component parentComponent, CSFVProject proj) {
		setLocationRelativeTo(parentComponent);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Backend.getImageResource(getClass(), Backend.iconLocation));
		setResizable(false);
		setBounds(100, 100, 570, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblStudentAutoValidation = new JLabel("Student Auto Validation (coming soon!)");
		lblStudentAutoValidation.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblStudentAutoValidation.setBounds(10, 11, 534, 35);
		contentPane.add(lblStudentAutoValidation);
		
		JCheckBox chckbxCheckForDuplicates = new JCheckBox("Check for duplicates");
		chckbxCheckForDuplicates.setEnabled(false);
		chckbxCheckForDuplicates.setBounds(20, 53, 191, 23);
		contentPane.add(chckbxCheckForDuplicates);
		
		JCheckBox chckbxSpotSuspiousEntries = new JCheckBox("Spot suspious entries (look for unappropriate words)");
		chckbxSpotSuspiousEntries.setEnabled(false);
		chckbxSpotSuspiousEntries.setBounds(20, 79, 283, 23);
		contentPane.add(chckbxSpotSuspiousEntries);
		
		JCheckBox chckbxCheckNames = new JCheckBox("Check Names (compare name to US Census data)");
		chckbxCheckNames.setEnabled(false);
		chckbxCheckNames.setBounds(20, 105, 273, 23);
		contentPane.add(chckbxCheckNames);
		
		JComboBox<String> comboQ1NameChoice = new JComboBox<>();
		comboQ1NameChoice.setEnabled(false);
		comboQ1NameChoice.setBounds(50, 159, 137, 20);
		contentPane.add(comboQ1NameChoice);
		
		JComboBox<String> comboQ2NameChoice = new JComboBox<>();
		comboQ2NameChoice.setEnabled(false);
		comboQ2NameChoice.setBounds(50, 190, 137, 20);
		contentPane.add(comboQ2NameChoice);
		
		JLabel lblQuestion = new JLabel("Question:");
		lblQuestion.setBounds(50, 135, 80, 14);
		contentPane.add(lblQuestion);
		
		txtQ1NameFormat = new JTextField();
		txtQ1NameFormat.setEnabled(false);
		txtQ1NameFormat.setBounds(207, 159, 137, 20);
		contentPane.add(txtQ1NameFormat);
		txtQ1NameFormat.setColumns(10);
		
		txtQ2NameFormat = new JTextField();
		txtQ2NameFormat.setEnabled(false);
		txtQ2NameFormat.setBounds(207, 190, 137, 20);
		contentPane.add(txtQ2NameFormat);
		txtQ2NameFormat.setColumns(10);
		
		JLabel lblNameFormatex = new JLabel("Name format (ex: LAST, FIRST)");
		lblNameFormatex.setBounds(206, 135, 161, 14);
		contentPane.add(lblNameFormatex);
		
		JLabel lblNameFormatInterpretation = new JLabel("Name Format Interpretation:");
		lblNameFormatInterpretation.setBounds(377, 135, 167, 14);
		contentPane.add(lblNameFormatInterpretation);
		
		JButton btnSkip = new JButton("Continue");
		btnSkip.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Backend.saveUnencryptedProject(proj);
				setVisible(false);
				dispose();
				new MainFrame(contentPane, proj, 0);
			}
		});
		btnSkip.setBounds(455, 227, 89, 23);
		contentPane.add(btnSkip);
		
		JButton btnValidate = new JButton("Validate");
		btnValidate.setEnabled(false);
		btnValidate.setBounds(217, 227, 89, 23);
		contentPane.add(btnValidate);
		
		setVisible(true);
	}
}
