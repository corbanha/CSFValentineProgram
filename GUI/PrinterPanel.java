package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import csf_val.CSFVPrinter;
import csf_val.CSFVPrinter.PrintItem;
import csf_val.CSFVProject;
import csf_val.Student;

public class PrinterPanel extends JPanel {
	private static final long serialVersionUID = -6837707055515308619L;
	private JTextField txtNameFormat;
	private JTextField txtFontDarkness;
	private JTextField txtResultItem;
	private JTextField txtResultItemPercent;
	private JTextField txtItemToRemove;
	private JLabel lblNameFormatInter;
	private JLabel lblPageItemInter;
	private JLabel lblItemResults;
	private JCheckBox chckbxSortStudentsFor;
	private PrintoutJPanel printoutPreviewPanel;
		
	private ArrayList<PrintItem> printItems;
	private JTextField txtRowFontSize;
	private JTextField txtMainFontSize;
	private JTextField txtNumStudentsPerSheet;
	private JTextField txtImageMinHeight;
	private JTextField txtCustomMessage;
	private JTextField txtNumSimularites;
	private JTextField txtSpaceBetweenImagePrints;
	private JTextField txtPrintStudentsStart;
	private JTextField txtPrintStudentsEnd;
	private JButton btnPrint;
	private JTextField txtCustomImageLocation;
	
	private CSFVProject proj;
	
	public class PrintoutJPanel extends JPanel {
		private static final long serialVersionUID = 3756044399262560363L;
		private CSFVProject proj;
		private BufferedImage img;
		
		public PrintoutJPanel(CSFVProject proj){
			
			this.proj = proj;
		}
		
		public void updateImage(){
			BufferedImage pagePreview = new BufferedImage(468 + 36 * 2, 648 + 36 * 2, BufferedImage.TYPE_INT_ARGB);
			Graphics2D pageGraphics = (Graphics2D) pagePreview.createGraphics();
			pageGraphics.setColor(Color.white);
			pageGraphics.fillRect(0, 0, pagePreview.getWidth(), pagePreview.getHeight());
			
			try{
				CSFVPrinter printer = new CSFVPrinter(proj, proj.getStudents().toArray(new Student[proj.getStudents().size()]));
				printer.generatePrintPreview(pageGraphics);
			}catch(Exception e){
				
			}
			
			img = pagePreview;
			pageGraphics.dispose();
			
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
			
			if(getSize().getWidth() > 200 && getSize().getHeight() > 200){
				int height = 0;
				int width = 0;
				int padding = 25 * 2;
				if(getSize().getHeight() * 8.5 < getSize().getWidth() * 11){
					height = (int) getSize().getHeight() - padding;
					width = (int) ((getSize().getHeight() - padding) * 8.5 / 11);
				}else{
					height = (int) ((getSize().getWidth() - padding) * 11 / 8.5);
					width = (int) (getSize().getWidth() - padding);
				}
							
				int startX = (int) ((getSize().getWidth() - width) / 2);
				int startY = (int) ((getSize().getHeight() - height) / 2);
				g2.setColor(Color.gray);
				g2.fillRect(startX + 5, startY + 5, width, height);
				
				//now draw the things on the page
				if(img == null) updateImage();
				try{
					g2.drawImage(img, startX, startY, width, height, null);
									
					g2.setColor(Color.gray);
					g2.drawString("Look in preview is not exact, print as PDF for true look", 10, (int) (getSize().getHeight() - 5));
					
				}catch(Exception e){
					g2.setColor(Color.white);
					g2.fillRect(0, 0, img.getWidth(), img.getHeight());
					g2.setFont(new Font("Ariel", Font.BOLD, 38));
					g2.setColor(Color.BLACK);
					g2.drawString("Error displaying preview", 30, (int) (getSize().getHeight() / 2));
					e.printStackTrace();
				}
				
				g2.setColor(Color.black);
				g2.drawRect(startX, startY, width, height);
			}		
		}
	}
	
	public PrinterPanel(JFrame parent, CSFVProject proj) {
		this.proj = proj;
		printItems = proj.getTopRowItems();
		
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		printoutPreviewPanel = new PrintoutJPanel(proj);
		splitPane.setRightComponent(printoutPreviewPanel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		
		JPanel leftPanel = new JPanel();
		scrollPane_1.setViewportView(leftPanel);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(500, 850));
		
		JLabel lblPrintShop = new JLabel("Print Shop");
		lblPrintShop.setFont(new Font("Tahoma", Font.PLAIN, 24));
		leftPanel.add(lblPrintShop);
		
		Component verticalStrut = Box.createVerticalStrut(10);
		leftPanel.add(verticalStrut);
		
		chckbxSortStudentsFor = new JCheckBox("Sort Students for print");
		chckbxSortStudentsFor.setSelected(true);
		leftPanel.add(chckbxSortStudentsFor);
		
		JPanel panel = new JPanel();
		leftPanel.add(panel);
		panel.setLayout(null);
		panel.setMaximumSize(new Dimension(450, 225));
		
		JLabel lblRowFontSize = new JLabel("Row Font Size:");
		lblRowFontSize.setBounds(10, 28, 82, 14);
		panel.add(lblRowFontSize);
		
		txtRowFontSize = new JTextField("" + proj.getResultRowFontSize());
		txtRowFontSize.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					int fontSize = Integer.parseInt(txtRowFontSize.getText());
					if(fontSize > 3 && fontSize < 50) proj.setResultRowFontSize(fontSize);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtRowFontSize.setBounds(102, 25, 86, 20);
		panel.add(txtRowFontSize);
		txtRowFontSize.setColumns(10);
		
		JLabel lblMainFontSize = new JLabel("Main Font Size:");
		lblMainFontSize.setBounds(10, 3, 82, 14);
		panel.add(lblMainFontSize);
		
		txtMainFontSize = new JTextField("" + proj.getTopNameFontSize());
		txtMainFontSize.setBounds(102, 0, 86, 20);
		txtMainFontSize.setToolTipText("Between 1 and 100");
		txtMainFontSize.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					int fontSize = Integer.parseInt(txtMainFontSize.getText());
					if(fontSize > 0 && fontSize <= 100) proj.setTopNameFontSize(fontSize);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		panel.add(txtMainFontSize);
		txtMainFontSize.setColumns(10);
		
		JLabel lblNumStudentsPer = new JLabel("Num Students Per Sheet");
		lblNumStudentsPer.setBounds(10, 53, 117, 14);
		panel.add(lblNumStudentsPer);
		
		txtNumStudentsPerSheet = new JTextField("" + proj.getNumStudentsPerSheet());
		txtNumStudentsPerSheet.setToolTipText("Between 1 and 25");
		txtNumStudentsPerSheet.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					int num = Integer.parseInt(txtNumStudentsPerSheet.getText());
					if(num > 0 && num <= 25) proj.setNumStudentsPerSheet(num);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtNumStudentsPerSheet.setBounds(137, 50, 86, 20);
		panel.add(txtNumStudentsPerSheet);
		txtNumStudentsPerSheet.setColumns(10);
		
		JLabel lblImageMinHeight = new JLabel("Image min height");
		lblImageMinHeight.setBounds(10, 78, 86, 14);
		panel.add(lblImageMinHeight);
		
		txtImageMinHeight = new JTextField("" + proj.getMinHeightOfImage());
		txtImageMinHeight.setToolTipText("Between 0 (don't show) and 500");
		txtImageMinHeight.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					float num = Float.parseFloat(txtImageMinHeight.getText());
					if(num >= 0 && num <= 500) proj.setMinHeightOfImage(num);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtImageMinHeight.setBounds(102, 75, 86, 20);
		panel.add(txtImageMinHeight);
		txtImageMinHeight.setColumns(10);
		
		JLabel lblCustomMessageleave = new JLabel("Custom Message (leave blank if none)");
		lblCustomMessageleave.setBounds(10, 103, 213, 14);
		panel.add(lblCustomMessageleave);
		
		txtCustomMessage = new JTextField(proj.getCustomMessage());
		txtCustomMessage.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					proj.setCustomMessage(txtCustomMessage.getText());
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtCustomMessage.setBounds(6, 115, 217, 20);
		panel.add(txtCustomMessage);
		txtCustomMessage.setColumns(10);
		
		JLabel lblNumSimilarities = new JLabel("Num Similarities: ");
		lblNumSimilarities.setBounds(10, 139, 86, 14);
		panel.add(lblNumSimilarities);
		
		txtNumSimularites = new JTextField("" + proj.getNumSimilarsToShow());
		txtNumSimularites.setToolTipText("Between 0 and 100");
		txtNumSimularites.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					int num = Integer.parseInt(txtNumSimularites.getText());
					if(num >= 0 && num <= 100) proj.setNumSimilarsToShow(num);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtNumSimularites.setBounds(102, 136, 86, 20);
		panel.add(txtNumSimularites);
		txtNumSimularites.setColumns(10);
		
		JLabel lblSpaceBetweenPrints = new JLabel("Space Between Prints:");
		lblSpaceBetweenPrints.setBounds(10, 161, 117, 14);
		panel.add(lblSpaceBetweenPrints);
		
		txtSpaceBetweenImagePrints = new JTextField("" + proj.getSpaceBetweenStudentPrints());
		txtSpaceBetweenImagePrints.setToolTipText("Between 0 and 1000");
		txtSpaceBetweenImagePrints.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					float num = Float.parseFloat(txtSpaceBetweenImagePrints.getText());
					if(num >= 0 && num <= 1000) proj.setSpaceBetweenStudentPrints(num);
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtSpaceBetweenImagePrints.setBounds(134, 158, 86, 20);
		panel.add(txtSpaceBetweenImagePrints);
		txtSpaceBetweenImagePrints.setColumns(10);
		
		JLabel lblCustomImageLocation = new JLabel("Custom image location (blank if default)");
		lblCustomImageLocation.setBounds(10, 181, 209, 14);
		panel.add(lblCustomImageLocation);
		
		txtCustomImageLocation = new JTextField(proj.getCustomImageLocation());
		txtCustomImageLocation.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					proj.setCustomImageLocation(txtCustomImageLocation.getText());
					update();
				}catch(Exception e1){}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		txtCustomImageLocation.setBounds(6, 200, 167, 20);
		panel.add(txtCustomImageLocation);
		txtCustomImageLocation.setColumns(10);
		
		JButton btnGetCustomImage = new JButton("...");
		btnGetCustomImage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Image File (.png, .jpg)", "png", "jpg"));
				if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
					txtCustomImageLocation.setText(chooser.getSelectedFile().getAbsolutePath());
					proj.setCustomImageLocation(chooser.getSelectedFile().getAbsolutePath());
					update();
				}
			}
		});
		btnGetCustomImage.setBounds(181, 199, 38, 23);
		panel.add(btnGetCustomImage);
		
		JPanel panel_1 = new JPanel();
		leftPanel.add(panel_1);
		panel_1.setMaximumSize(new Dimension(550, 30));
		panel_1.setLayout(null);
		
		JLabel lblFontDarknesshedlund = new JLabel("Font darkness (Hedlund prefers 65): ");
		lblFontDarknesshedlund.setBounds(10, 11, 185, 14);
		panel_1.add(lblFontDarknesshedlund);
		
		txtFontDarkness = new JTextField();
		txtFontDarkness.setToolTipText("Between 0 and 100");
		txtFontDarkness.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try{
					float fd = Float.parseFloat(txtFontDarkness.getText());
					if(fd >= 0 && fd <= 100){
						fd /= 100;
						proj.setInkDarkness(fd);
					}else{
						throw new Exception();
					}
				}catch(Exception e1){}
				printoutPreviewPanel.updateImage();
			}
		});
		txtFontDarkness.setText("" + (100 * proj.getInkDarkness()));
		txtFontDarkness.setBounds(205, 8, 67, 20);
		panel_1.add(txtFontDarkness);
		txtFontDarkness.setColumns(10);
		
		JLabel lblNameFormatex = new JLabel("Name format (ex: \"2, 5\"):");
		leftPanel.add(lblNameFormatex);
		
		txtNameFormat = new JTextField();
		txtNameFormat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				//update the interpretation
				if(proj.getStudents().size() == 0){
					lblNameFormatInter.setText("You need to have at least 2 students");
				}else{
					lblNameFormatInter.setText(CSFVPrinter.parsePrintItem(txtNameFormat.getText(), proj.getStudents().get(0), 79.6f));
				}
				
				proj.setStudentNameFormat(txtNameFormat.getText());
				printoutPreviewPanel.updateImage();
			}
		});
		leftPanel.add(txtNameFormat);
		txtNameFormat.setColumns(10);
		txtNameFormat.setText(proj.getStudentNameFormat());
		txtNameFormat.setMaximumSize(new Dimension(450, 20));
		
		if(proj.getStudents().size() == 0){
			lblNameFormatInter = new JLabel("You need to have at least 2 students");
		}else{
			lblNameFormatInter = new JLabel(CSFVPrinter.parsePrintItem(proj.getStudentNameFormat(),
					proj.getStudents().get(0), 54.4f));
		}
		
		leftPanel.add(lblNameFormatInter);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		leftPanel.add(verticalStrut_1);
		
		JLabel lblNewLabel = new JLabel("Set up the result page items, enter numbers for a question answer, or a % for the percent");
		lblNewLabel.setPreferredSize(new Dimension(350, 15));
		leftPanel.add(lblNewLabel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMaximumSize(new Dimension(600, 200));
		leftPanel.add(panel_2);
		panel_2.setLayout(null);
		
		txtResultItem = new JTextField();
		txtResultItem.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				//update the interpretation
				if(proj.getStudents().size() == 0){
					lblPageItemInter.setText("You need to have at least 2 students");
				}else{
					lblPageItemInter.setText(CSFVPrinter.parsePrintItem(txtResultItem.getText(), proj.getStudents().get(0), 79.8f));
				}
			}
		});
		txtResultItem.setText("2, 1");
		txtResultItem.setBounds(10, 11, 86, 20);
		panel_2.add(txtResultItem);
		txtResultItem.setColumns(10);
		
		JLabel lblAt = new JLabel("at");
		lblAt.setBounds(108, 14, 25, 14);
		panel_2.add(lblAt);
		
		txtResultItemPercent = new JTextField();
		txtResultItemPercent.setText(".2");
		txtResultItemPercent.setToolTipText("Between 0 and 1");
		txtResultItemPercent.setBounds(133, 11, 75, 20);
		panel_2.add(txtResultItemPercent);
		txtResultItemPercent.setColumns(10);
		
		JButton btnAddResultItem = new JButton("Add");
		btnAddResultItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					printItems.add(new PrintItem(Float.parseFloat(txtResultItemPercent.getText()), txtResultItem.getText()));
					txtResultItem.setText("");
					txtResultItemPercent.setText("");
					update();
				}catch(Exception e1){}
			}
		});
		btnAddResultItem.setBounds(218, 10, 64, 23);
		panel_2.add(btnAddResultItem);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 64, 262, 94);
		panel_2.add(scrollPane);
		
		lblItemResults = new JLabel("The items will show up here");
		scrollPane.setViewportView(lblItemResults);
		
		txtItemToRemove = new JTextField();
		txtItemToRemove.setBounds(10, 169, 64, 20);
		panel_2.add(txtItemToRemove);
		txtItemToRemove.setColumns(10);
		
		JButton btnRemoveItem = new JButton("Remove Item");
		btnRemoveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					printItems.remove(Integer.parseInt(txtItemToRemove.getText()) - 1);
					txtItemToRemove.setText("");
					update();
				}catch(Exception e1){}				
			}
		});
		btnRemoveItem.setBounds(84, 168, 104, 23);
		panel_2.add(btnRemoveItem);
		
		if(proj.getStudents().size() == 0){
			lblPageItemInter = new JLabel("You need to have at least 2 students");
		}else{
			lblPageItemInter = new JLabel(CSFVPrinter.parsePrintItem("2, 1", proj.getStudents().get(0), 54.5f));
		}
		
		lblPageItemInter.setBounds(10, 42, 198, 14);
		panel_2.add(lblPageItemInter);
		
		JPanel printPanel = new JPanel();
		printPanel.setMaximumSize(new Dimension(800, 105));
		leftPanel.add(printPanel);
		printPanel.setLayout(null);
		
		JLabel lblPrintStudentsFrom = new JLabel("Print from ");
		lblPrintStudentsFrom.setBounds(10, 11, 118, 14);
		printPanel.add(lblPrintStudentsFrom);
		
		txtPrintStudentsStart = new JTextField("1");
		txtPrintStudentsStart.setBounds(10, 27, 47, 20);
		printPanel.add(txtPrintStudentsStart);
		txtPrintStudentsStart.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setBounds(64, 30, 20, 14);
		printPanel.add(lblTo);
		
		txtPrintStudentsEnd = new JTextField("" + proj.getStudents().size());
		txtPrintStudentsEnd.setBounds(87, 27, 47, 20);
		printPanel.add(txtPrintStudentsEnd);
		txtPrintStudentsEnd.setColumns(10);
		
		btnPrint = new JButton("PRINT!!!");
		btnPrint.setBounds(9, 74, 75, 23);
		printPanel.add(btnPrint);
		
		JLabel lblTrySmallerNumbers = new JLabel("<html>Try smaller numbers if large<br />numbers don't work");
		lblTrySmallerNumbers.setBounds(10, 45, 156, 30);
		printPanel.add(lblTrySmallerNumbers);
		
		JButton btnRefreshPreview = new JButton("Refresh Preview");
		btnRefreshPreview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		btnRefreshPreview.setBounds(176, 27, 111, 23);
		printPanel.add(btnRefreshPreview);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(txtNameFormat.getText().trim().equals("")){
					JOptionPane.showMessageDialog(null, "You must set a name format", "Name Format Required", JOptionPane.OK_OPTION);
				}else{
					if(chckbxSortStudentsFor.isSelected()){
						//sort the proj.getStudents()
						Collections.sort(proj.getStudents(), new Comparator<Student>(){

							@Override
							public int compare(Student a1, Student a2) {
								return CSFVPrinter.parsePrintItem(txtNameFormat.getText(), a1, 78.9f).compareTo(
										CSFVPrinter.parsePrintItem(txtNameFormat.getText(), a2, 78.9f));
							}});
					}
				
					proj.assignStudentMatches(parent);
					update();
					
					try{
						int start = Integer.parseInt(txtPrintStudentsStart.getText());
						int end = Integer.parseInt(txtPrintStudentsEnd.getText());
						Student[] studentsToPrint = new Student[end - start + 1];
						
						for(int i = start - 1, j = 0; i < end; i++, j++){
							studentsToPrint[j] = proj.getStudents().get(i);
						}
						
						CSFVPrinter printer = new CSFVPrinter(proj, studentsToPrint);
						printer.printStudentResultPages();
					
						
						//due to performance issues, it seems like it may be better to just delete all of the student matches after the fact
						for(int i = 0; i < proj.getStudents().size(); i++){
							proj.getStudents().get(i).getStudentMatches().clear();
						}
					}catch(Exception e1){
						e1.printStackTrace();
					}
				}
			}
		});
		update();
	}
	
	public void update(){		
		Collections.sort(printItems, new Comparator<PrintItem>() {

			@Override
			public int compare(PrintItem o1, PrintItem o2) {
				return Math.round((o1.getX() - o2.getX()) * 10000);
			}
		});
		
		lblItemResults.setText("");
		String setString = "<html>";
		for(int i = 0; i < printItems.size(); i++){
			setString += (i + 1) + "   " + printItems.get(i).getDisplayString() + " at " + printItems.get(i).getX() + "<br/>";
		}
		lblItemResults.setText(setString);
		
		txtPrintStudentsEnd.setText("" + proj.getStudents().size());
		
		printoutPreviewPanel.updateImage();
		repaint();
	}
}




