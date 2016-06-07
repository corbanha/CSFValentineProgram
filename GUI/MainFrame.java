package GUI;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import csf_val.Backend;
import csf_val.CSFVProject;
import csf_val.LinearScale;
import csf_val.MultipleChoice;
import csf_val.Student;
import csf_val.UndeterminedQuestion;

public class MainFrame {

	private JFrame frame;
	private JTable table;
	private CSFVProject proj;
	private PrinterPanel printerPanel;
	private QuestionItem[] questionItems;
	private JTabbedPane tabbedPane;
	
	private boolean userHasBeenShownWarningAboutEditingStudents = false;

	/**
	 * Create the application.
	 */
	public MainFrame(Component parentComponent, CSFVProject proj, int startTab) {
		this.proj = proj;
		initialize(startTab);
	}

	public void setJTable(JTable jTable){
		table = jTable;
	}
		
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(int startTab) {
		frame = new JFrame();
		frame.setIconImage(Backend.getImageResource(getClass(), Backend.iconLocation));
		frame.setTitle(proj.getName() + " - (" + proj.getSaveLocation() + ")");
		frame.setBounds(100, 100, 622, 622);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				updateQuestionsFromQuestionTab();
				updateStudentsFromTable();
				Backend.saveUnencryptedProject(proj);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic('e');
		menuBar.add(menuFile);
		
		JMenuItem newItem = new JMenuItem("New...");
		newItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Backend.saveUnencryptedProject(proj);
				frame.setVisible(false);
				frame.dispose();
				new NewProjectSetup(false);
			}
		});
		menuFile.add(newItem);
		
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Backend.saveUnencryptedProject(proj);
			}
		});
		saveItem.setMnemonic('S');
		menuFile.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
				if(chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION){
					Backend.saveUnencryptedProjectAs(proj, chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		saveItem.setMnemonic('A');
		menuFile.add(saveAsItem);
		
		JMenuItem openItem = new JMenuItem("Open...");
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
				if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					Backend.saveUnencryptedProject(proj);
					frame.setVisible(false);
					frame.dispose();
					new MainFrame(frame, Backend.readUnencryptedProject(chooser.getSelectedFile().getAbsolutePath()),
							tabbedPane.getSelectedIndex());
				}
			}
		});
		saveItem.setMnemonic('O');
		menuFile.add(openItem);
		
		JMenuItem openNewWindowItem = new JMenuItem("Open in new window...");
		openNewWindowItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
				if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					Backend.saveUnencryptedProject(proj);
					new MainFrame(frame, Backend.readUnencryptedProject(chooser.getSelectedFile().getAbsolutePath()),
							tabbedPane.getSelectedIndex());
				}
			}
		});
		menuFile.add(openNewWindowItem);
		
		JMenuItem reloadProject = new JMenuItem("Reload");
		reloadProject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(frame, "This may mean unsaved changes. Are you sure?", "Are you sure",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
					frame.setVisible(false);
					frame.dispose();
					new MainFrame(frame, Backend.readUnencryptedProject(proj.getSaveLocation()), tabbedPane.getSelectedIndex());
				}
			}
		});
		menuFile.add(reloadProject);
		
		JMenuItem exitWithoutSavingProject = new JMenuItem("Exit Without Saving");
		exitWithoutSavingProject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(frame, "This may mean unsaved changes. Are you sure?", "Are you sure",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
					frame.setVisible(false);
					frame.dispose();
				}
			}
		});
		menuFile.add(exitWithoutSavingProject);
		
		JMenu importFile = new JMenu("Import");
		menuFile.setMnemonic('r');
		menuBar.add(importFile);
		
		JMenuItem studentsItem = new JMenuItem("Students...");
		studentsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Spreadsheet Files (.csv, .tsv, .xlsx)", "csv", "tsv", "xlsx"));
				if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					String[][] table = Backend.getSpreadsheetFromFile(chooser.getSelectedFile());
					if(table.length > 0){
						if(proj.getQuestions().size() != table[0].length){
							JOptionPane.showMessageDialog(frame, "The number of questions in the project and\nquestions in "
									+ "the spreadsheet were different.\nPlease make sure they are the same.", "Error", JOptionPane.ERROR_MESSAGE);
						}else{
							if(JOptionPane.showConfirmDialog(frame, "Before continuing, make sure that the order of the questions\n"
									+ "in the Spreadsheet is the same as the order in the project.\nReady to continue?", 
									"Continue?", JOptionPane.YES_NO_OPTION,
									JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION){
							
								boolean isHeader = JOptionPane.showConfirmDialog(frame, "Is there a header in this spreadsheet file?",
										"Header?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
							
								for(int i = isHeader ? 1 : 0; i < table.length; i++){
									proj.getStudents().add(new Student(table[i]));
								}
								Backend.saveUnencryptedProject(proj);
								frame.setVisible(false);
								frame.dispose();
								new MainFrame(frame, proj, 0);
							}
						}
					}
				}
			}
		});
		saveItem.setMnemonic('S');
		importFile.add(studentsItem);
		
		JMenuItem questionsItem = new JMenuItem("Questions...");
		questionsItem.setEnabled(false);
		saveItem.setMnemonic('Q');
		importFile.add(questionsItem);
		
		JMenuItem printSettingsItem = new JMenuItem("Print Settings...");
		printSettingsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSF Valentine File (.csfv)", "csfv"));
				if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					CSFVProject newProj = Backend.readUnencryptedProject(chooser.getSelectedFile().getAbsolutePath().endsWith(".csfv") ? 
							chooser.getSelectedFile().getAbsolutePath() : chooser.getSelectedFile().getAbsolutePath() + ".csfv");
					proj.setCustomImageLocation(newProj.getCustomImageLocation());
					proj.setCustomMessage(newProj.getCustomMessage());
					proj.setInkDarkness(newProj.getInkDarkness());
					proj.setMinHeightOfImage(newProj.getMinHeightOfImage());
					proj.setNumSimilarsToShow(newProj.getNumSimilarsToShow());
					proj.setNumStudentsPerSheet(newProj.getNumStudentsPerSheet());
					proj.setResultRowFontSize(newProj.getResultRowFontSize());
					proj.setSpaceBetweenStudentPrints(newProj.getSpaceBetweenStudentPrints());
					proj.setStudentNameFormat(newProj.getStudentNameFormat());
					proj.setTopNameFontSize(newProj.getTopNameFontSize());
					proj.setTopRowItems(newProj.getTopRowItems());

					frame.setVisible(false);
					frame.dispose();
					new MainFrame(frame, proj, 2);
				}
			}
		});
		saveItem.setMnemonic('P');
		importFile.add(printSettingsItem);
		
		JMenu aboutMenu = new JMenu("About");
		aboutMenu.setMnemonic('A');
		menuBar.add(aboutMenu);
		
		JMenuItem projectItem = new JMenuItem("Project");
		projectItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = "Project Name: " + proj.getName() + "\nCreation Date: " + new Date(proj.getCreationDate()).toString() +
						"\nNumber of Questions: " + proj.getQuestions().size() + "\nNumber of Students: " + proj.getStudents().size();
				JOptionPane.showMessageDialog(null, str, "About Project", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		saveItem.setMnemonic('P');
		aboutMenu.add(projectItem);
		
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This program was created by Corban Anderson, 2016\nFor inquries/problems email him at corbanha@gmail.com",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		saveItem.setMnemonic('b');
		aboutMenu.add(aboutItem);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		String[][] tableContents = new String[proj.getStudents().size()][proj.getQuestions().size()];
		for(int i = 0; i < tableContents.length; i++){
			for(int j = 0; j < tableContents[i].length; j++){
				try{
					tableContents[i][j] = proj.getStudents().get(i).getAnswerChoices()[j];
				}catch(Exception e){
					tableContents[i][j] = "";
				}				
			}
		}
		
		String[] tableHeader = new String[proj.getQuestions().size()];
		for(int i = 0; i < tableHeader.length; i++){
			tableHeader[i] = proj.getQuestions().get(i).getQuestion();
		}
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane);
		
		table = new JTable(){
			private static final long serialVersionUID = -7985521925724378201L;
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				// This will auto resize the cells based on the data inside
				Component component = super.prepareRenderer(renderer, row, column);
				int renderWidth = (int) component.getPreferredSize().getWidth();
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth((int) Math.max(renderWidth + getIntercellSpacing().getWidth(), tableColumn.getPreferredWidth()));
				return component;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for(int i = 0; i < table.getModel().getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
		
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoCreateRowSorter(true);
		table.setModel(new DefaultTableModel(tableContents, tableHeader));
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getFirstRow() == -1 || e.getColumn() == -1 || e.getFirstRow() > proj.getStudents().size()) return; //this was a delete event
				
				if(userHasBeenShownWarningAboutEditingStudents){
					proj.getStudents().get(e.getFirstRow()).getAnswerChoices()[e.getColumn()] =
							(String) table.getValueAt(e.getFirstRow(), e.getColumn());
				}else if(JOptionPane.showConfirmDialog(null, "This will change this student's information. This may mess up the sort."
						+ "\nUndo (ctrl z) is not supported. This message will only show up once.\nDo you want to make the change?",
						"Are you sure you want to make this change?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) 
						== JOptionPane.OK_OPTION){
					userHasBeenShownWarningAboutEditingStudents = true;
					proj.getStudents().get(e.getFirstRow()).getAnswerChoices()[e.getColumn()] =
							(String) table.getValueAt(e.getFirstRow(), e.getColumn());
				}else{
					userHasBeenShownWarningAboutEditingStudents = true;
					table.getModel().setValueAt(proj.getStudents().get(e.getFirstRow()).getAnswerChoices()[e.getColumn()],
							e.getFirstRow(), e.getColumn());
				}
				printerPanel.update();
			}
		});
		
		InputMap inputMap = table.getInputMap(JTable.WHEN_FOCUSED);
		ActionMap actionMap = table.getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		actionMap.put("delete", new AbstractAction() {
			private static final long serialVersionUID = 5227938938380549345L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(userHasBeenShownWarningAboutEditingStudents){
					//delete
					removeSelectedCells();
				}else if(JOptionPane.showConfirmDialog(null, "This will change this student's information. This may mess up the sort."
						+ "\nUndo (ctrl z) is not supported. This message will only show up once.\nDo you want to make the change?",
						"Are you sure you want to make this change?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) 
						== JOptionPane.OK_OPTION){
					userHasBeenShownWarningAboutEditingStudents = true;
					//delete
					removeSelectedCells();
				}else{
					userHasBeenShownWarningAboutEditingStudents = true;
				}
				printerPanel.update();
			}});
		
		//panel.add(new JScrollPane(table.getTableHeader(), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(BorderLayout.CENTER, scrollPane);
		
		JButton addARow = new JButton("Add a row");
		addARow.setMaximumSize(new Dimension(200, 20));
		addARow.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(userHasBeenShownWarningAboutEditingStudents){
					//delete
					addRow();
				}else if(JOptionPane.showConfirmDialog(null, "If you don't know what you are doing, you may mess up the assigning."
						+ "\nUndo (ctrl z) is not supported. This message will only show up once.\nDo you want to make the change?",
						"Are you sure you want to make this change?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) 
						== JOptionPane.OK_OPTION){
					userHasBeenShownWarningAboutEditingStudents = true;
					//delete
					addRow();
				}else{
					userHasBeenShownWarningAboutEditingStudents = true;
				}
			}
			
			private void addRow(){
				String[] answers = new String[proj.getQuestions().size()];
				for(int i = 0; i < answers.length; i++) answers[i] = "";
				
				((DefaultTableModel) table.getModel()).addRow(answers);
				
				proj.getStudents().add(new Student(answers));
				table.repaint();
				printerPanel.update();
			}
		});
		
		JPanel addARowPanel = new JPanel();
		addARowPanel.add(addARow);
		tablePanel.add(BorderLayout.NORTH, addARowPanel);
		
		tabbedPane.addTab("Student Data", null, tablePanel, null);
		
		JPanel questionTabPanel = new JPanel();
		questionTabPanel.setLayout(new BoxLayout(questionTabPanel, BoxLayout.Y_AXIS));
		
		JButton openCoupleEditScreen = new JButton("Open Couple Edit Screen");
		openCoupleEditScreen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateQuestionsFromQuestionTab();
				Backend.saveUnencryptedProject(proj);
				frame.setVisible(false);
				frame.dispose();
				new CoupleSetUpFrame(frame, proj);
			}
		});
		questionTabPanel.add(openCoupleEditScreen);
		
		questionItems = new QuestionItem[proj.getQuestions().size()];
		
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
			questionTabPanel.add(questionItems[i]);
		}
		
		JScrollPane questionTabScrollPane = new JScrollPane();
		questionTabScrollPane.setViewportView(questionTabPanel);
		questionTabScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		tabbedPane.addTab("Questions", null, questionTabScrollPane, null);
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				updateQuestionsFromQuestionTab();
				updateStudentsFromTable();
			}
		});
		
		printerPanel = new PrinterPanel(frame, proj);
		tabbedPane.addTab("Print Shop", null, printerPanel, null);
		
		tabbedPane.setSelectedIndex(startTab);
		
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	private void removeSelectedCells(){
		int[] rows = table.getSelectedRows();
		for(int i = 0; i < rows.length; i++){
			((DefaultTableModel) table.getModel()).removeRow(table.convertRowIndexToModel(rows[i] - i));
		}
		
		table.repaint();
	}
	
	private void updateQuestionsFromQuestionTab(){
		proj.getQuestions().clear();
		//put the questions in the proj
		for(int i = 0; i < questionItems.length; i++){
			if(questionItems[i].getUndeterminedQuestion().isImportQuestion()){
				proj.getQuestions().add(questionItems[i].getUndeterminedQuestion().getAsQuestion());
			}
		}
	}
	
	private void updateStudentsFromTable(){
		proj.getStudents().clear();
		outer:
		for(int i = 0; i < table.getModel().getRowCount(); i++){
			String[] answers = new String[table.getModel().getColumnCount()];
			int numEmpty = 0;
			for(int j = 0; j < answers.length; j++){
				if(table.getModel().getValueAt(i, j) == null) continue outer;
				answers[j] = ((String) table.getModel().getValueAt(i, j)).trim();
				if(answers[j].equals("")) numEmpty++;
			}
			if(numEmpty != answers.length) proj.getStudents().add(new Student(answers));
		}
	}
}
