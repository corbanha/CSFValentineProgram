package GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import csf_val.Backend;
import csf_val.CSFVProject;

public class NewProjectSetup extends JFrame {
	private static final long serialVersionUID = -7331978136115968133L;
	private JPanel contentPane;
	private JTextField txtProjectname;
	private JTextField txtProjectSaveLocation;
	private JTextField txtDataSpreadsheetLocation;
	private JLabel lblImportingTheData;
	private JButton btnCancel;
	private JButton btnOpenExisting;
	private JButton btnOpenfilesaveloc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new NewProjectSetup(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NewProjectSetup(boolean autoOpen) {
		Backend.loadData();
		if(autoOpen && !Backend.getLocationOfLastSave().equals("")){
			new MainFrame(null, Backend.readUnencryptedProject(Backend.getLocationOfLastSave()), 0);
		}else{
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setIconImage(Backend.getImageResource(getClass(), Backend.iconLocation));
			setBounds(100, 100, 450, 230);
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
			
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblWelcome = new JLabel("Welcome!");
			lblWelcome.setForeground(Color.DARK_GRAY);
			lblWelcome.setFont(new Font("Tahoma", Font.PLAIN, 24));
			lblWelcome.setBounds(10, 11, 170, 36);
			contentPane.add(lblWelcome);
			
			JLabel lblLetsStartA = new JLabel("Let's start a new project why don't we?");
			lblLetsStartA.setBounds(10, 47, 329, 14);
			contentPane.add(lblLetsStartA);
			
			JLabel lblANiceProject = new JLabel("A Nice Project Name");
			lblANiceProject.setBounds(10, 72, 103, 14);
			contentPane.add(lblANiceProject);
			
			JLabel lblProjectSaveLocation = new JLabel("Project Save Location");
			lblProjectSaveLocation.setBounds(10, 97, 122, 14);
			contentPane.add(lblProjectSaveLocation);
			
			JLabel lblDataSpreadsheetLocation = new JLabel("Data Spreadsheet Location");
			lblDataSpreadsheetLocation.setBounds(10, 122, 141, 14);
			contentPane.add(lblDataSpreadsheetLocation);
			
			txtProjectname = new JTextField();
			txtProjectname.setBounds(151, 69, 283, 20);
			contentPane.add(txtProjectname);
			txtProjectname.setColumns(10);
			
			txtProjectSaveLocation = new JTextField();
			txtProjectSaveLocation.setBounds(151, 94, 241, 20);
			contentPane.add(txtProjectSaveLocation);
			txtProjectSaveLocation.setColumns(10);
			
			btnOpenfilesaveloc = new JButton("...");
			btnOpenfilesaveloc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//open the dialog chooser for the csfvu project save location
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
					if(chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
						txtProjectSaveLocation.setText(chooser.getSelectedFile().getAbsolutePath().endsWith(".csfv") ? 
								chooser.getSelectedFile().getAbsolutePath() : chooser.getSelectedFile().getAbsolutePath() + ".csfv");
					}
				}
			});
			btnOpenfilesaveloc.setBounds(402, 93, 32, 23);
			contentPane.add(btnOpenfilesaveloc);
			
			txtDataSpreadsheetLocation = new JTextField();
			txtDataSpreadsheetLocation.setBounds(151, 119, 241, 20);
			contentPane.add(txtDataSpreadsheetLocation);
			txtDataSpreadsheetLocation.setColumns(10);
			
			JButton btnOpendatasheetloc = new JButton("...");
			btnOpendatasheetloc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new FileNameExtensionFilter("Spreadsheet Files (.tsv, .csv, .xlsx)", 
							"tsv", "csv", "xlsx"));
					if(chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
						txtDataSpreadsheetLocation.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			btnOpendatasheetloc.setBounds(402, 118, 32, 23);
			contentPane.add(btnOpendatasheetloc);
			
			JButton btnLetsDoThis = new JButton("Let's Do This!");
			btnLetsDoThis.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
									
					if(txtProjectname.getText().trim().equals("")){
						JOptionPane.showMessageDialog(getContentPane(), "You must enter a project name", "Incorrect input",
								JOptionPane.ERROR_MESSAGE);
					}else if(txtProjectSaveLocation.getText().trim().equals("")){
						JOptionPane.showMessageDialog(getContentPane(), "You must enter a project save location", "Incorrect input",
								JOptionPane.ERROR_MESSAGE);
					}else if(!txtProjectSaveLocation.getText().trim().endsWith(".csfv")){
						JOptionPane.showMessageDialog(getContentPane(), "Save Location must end with .csfv", "Incorrect input",
								JOptionPane.ERROR_MESSAGE);
					}else if(txtDataSpreadsheetLocation.getText().trim().equals("")){
						JOptionPane.showMessageDialog(getContentPane(), "You must enter a data spreadsheet", "Incorrect input",
								JOptionPane.ERROR_MESSAGE);
					}else if(!txtDataSpreadsheetLocation.getText().trim().endsWith(".csv") &&
							!txtDataSpreadsheetLocation.getText().trim().endsWith(".tsv") &&
							!txtDataSpreadsheetLocation.getText().trim().endsWith(".xlsx")){
						JOptionPane.showMessageDialog(getContentPane(), "Data spreadsheet must be of type .csv, .tsv, or .xlsx", "Incorrect input",
								JOptionPane.ERROR_MESSAGE);
					}else if(!new File(txtDataSpreadsheetLocation.getText()).exists()){
						JOptionPane.showMessageDialog(getContentPane(), "The data spreadsheet doesn't exist", "Can't find file",
								JOptionPane.ERROR_MESSAGE);
					}else{
						//let's start!
						int r = 0;
						if(new File(txtProjectSaveLocation.getText()).exists()){
							r = JOptionPane.showConfirmDialog(getContentPane(), "Are you sure you want to overwrite " + 
									txtProjectSaveLocation.getText() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						}else{
							r = JOptionPane.OK_OPTION;
						}
						
						if(r == JOptionPane.OK_OPTION){
							btnLetsDoThis.setVisible(false);
							btnCancel.setVisible(false);
							btnOpenExisting.setVisible(false);
							txtDataSpreadsheetLocation.setEnabled(false);
							txtProjectname.setEnabled(false);
							txtProjectSaveLocation.setEnabled(false);
							btnOpendatasheetloc.setEnabled(false);
							btnOpenfilesaveloc.setEnabled(false);
							lblImportingTheData.setVisible(true);
							revalidate();
							update(getGraphics());
							repaint();
							
							
							//do stuff
							long startTime = System.currentTimeMillis();
							String[][] table = Backend.getSpreadsheetFromFile(new File(txtDataSpreadsheetLocation.getText()));
							CSFVProject proj = new CSFVProject(txtProjectname.getText(), txtProjectSaveLocation.getText());
							//Backend.saveProject(proj);
							
							try{
								Thread.sleep(4000 - System.currentTimeMillis() - startTime);
							}catch(Exception e){}
												
							//load up the question screen
							setVisible(false);
							dispose();
							new QuestionSetupFrame(getContentPane(), proj, table);
						}
					}
				}
			});
			btnLetsDoThis.setBounds(331, 167, 103, 23);
			contentPane.add(btnLetsDoThis);
			
			lblImportingTheData = new JLabel("Importing the data from the spreadsheet...");
			lblImportingTheData.setVisible(false);
			lblImportingTheData.setBounds(10, 171, 424, 14);
			contentPane.add(lblImportingTheData);
			
			btnOpenExisting = new JButton("Open Existing");
			btnOpenExisting.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
					if(chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
						setVisible(false);
						dispose();
						new MainFrame(getContentPane(), Backend.readUnencryptedProject(chooser.getSelectedFile().getAbsolutePath()),0 );
					}
				}
			});
			btnOpenExisting.setBounds(150, 167, 120, 23);
			contentPane.add(btnOpenExisting);
			
			btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			btnCancel.setBounds(10, 167, 89, 23);
			contentPane.add(btnCancel);
			setVisible(true);
		}
	}
}
