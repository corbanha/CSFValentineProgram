package csf_val;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CSFVPrinter implements Printable{

	public static class PrintItem{
		public PrintItem(float x, String displayString){
			this.x = x; // between 0 and 1, the percent of the width to start on the flyer
			this.displayString = displayString;
		}
		
		public PrintItem(String saveArgs){
			x = Float.parseFloat(saveArgs.substring(0, saveArgs.indexOf(29)));
			displayString = saveArgs.substring(saveArgs.indexOf(29) + 1);
		}
		
		public String toSaveString(){
			return "" + x + ((char) 29) + displayString;
		}
		
		protected float x;
		protected String displayString;
		
		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public String getDisplayString() {
			return displayString;
		}

		public void setDisplayString(String displayString) {
			this.displayString = displayString;
		}
	}
	
	public static class SimularResultData{
		public SimularResultData(String[] firstRow, String[] simularities){
			this.firstRow = firstRow;
			this.simularities = simularities;
		}
		protected String[] firstRow;
		protected String[] simularities;
	}
	
	public static class StudentPrintData{
		public StudentPrintData(String name, SimularResultData[] results){
			this.name = name;
			this.results = results;
		}
		
		protected String name;
		protected SimularResultData[] results;
	}
	
	private StudentPrintData[] data;
	private CSFVProject proj;
	
	private Color mainFontColor;
	private Color lesserFontColor;
	
	private BufferedImage happyValentine;
	
	public static final String RESULT_PERCENT_KEY = "%";
	
	public CSFVPrinter(CSFVProject proj, Student[] students) {
		this.proj = proj;
		setStudentData(students);
		setInkDarkness(proj.getInkDarkness());
	}
	
	/**
	 * Sets up the printer to print the given students. Info about how the students will be displayed will be sent via the project
	 * @param proj
	 * @param students
	 */
	public void setStudentData(Student[] students){
		data = new StudentPrintData[students.length];
		for(int i = 0; i < data.length; i++){
			SimularResultData[] matchData = new SimularResultData[Math.min(65, students[i].getStudentMatches().size())];
			for(int j = 0; j < matchData.length; j++){
				String[] topRowResults = new String[proj.getTopRowItems().size()];
				for(int k = 0; k < topRowResults.length; k++){
					topRowResults[k] = parsePrintItem(proj.getTopRowItems().get(k).displayString,
							students[i].getStudentMatches().get(j).getStudent(), students[i].getStudentMatches().get(j).getPercent());
				}
				matchData[j] = new SimularResultData(topRowResults, students[i].getStudentMatches().get(j).getSimilarities());
			}
			data[i] = new StudentPrintData(parsePrintItem(proj.getStudentNameFormat(), students[i], 0),
					matchData);
		}
	}
	
	public void setInkDarkness(float inkDarkness){
		if(inkDarkness > 1) inkDarkness = 1;
		if(inkDarkness < 0) inkDarkness = 0;
		proj.setInkDarkness(inkDarkness);		
		
		mainFontColor = setPixelBrightness(Color.BLACK, inkDarkness);
		
		lesserFontColor = setPixelBrightness(Color.DARK_GRAY, inkDarkness);
		try {
			happyValentine = setImageBrightness(ImageIO.read(new File(proj.getCustomImageLocation())), inkDarkness);
		} catch (IOException e) {
			happyValentine = setImageBrightness(Backend.getImageResource(getClass(), "happyValentine.jpg"), inkDarkness);
		}		
	}
	
	public void printStudentResultPages(){
		PrinterJob pj = PrinterJob.getPrinterJob();
		PageFormat pf = pj.defaultPage();
		Paper paper = new Paper();
		double margin = 36; //half inch
		paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
		pf.setPaper(paper);
		pf.setOrientation(PageFormat.PORTRAIT);
		
		pj.setPrintable(this, pf);
		if(pj.printDialog()){
			try{
				pj.print();
			}catch(PrinterException e){
				e.printStackTrace();
			}
		}
	}

	public void generatePrintPreview(Graphics2D g2){
		//proj contains the ink darkness, resultRowFontSize, topRowPrintItems, StudentNameFormat, twoStudentsPerSheet
		PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
		Paper paper = new Paper();
		double margin = 36; //half inch
		paper.setImageableArea(margin, margin, 468, 648);
		pageFormat.setPaper(paper);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		
		//we want to display to the user the longest possible items in the demo
		String[] longestItems = new String[proj.getQuestions().size()];
		for(int i = 0; i < longestItems.length; i++){
			int length = Integer.MIN_VALUE, index = -1;
			for(int j = 0; j < proj.getStudents().size(); j++){
				if(proj.getStudents().get(j).getAnswerChoices()[i].length() > length){
					length = proj.getStudents().get(j).getAnswerChoices()[i].length();
					index = j;
				}
			}
			longestItems[i] = proj.getStudents().get(index).getAnswerChoices()[i];
		}
		
		Student longestStudent = new Student(longestItems);
				
		String[] topRowResults = new String[proj.getTopRowItems().size()];
		for(int k = 0; k < topRowResults.length; k++){
			topRowResults[k] = parsePrintItem(proj.getTopRowItems().get(k).displayString, longestStudent, 79.4f);
		}
		
		String[] similarities = {"Fav Class", "Fav Ice Cream", "Chocolate", "Candy", "Summer plans", "Homework", "Sports", "Free time"};
		
		SimularResultData[] srd = new SimularResultData[100 / proj.getNumStudentsPerSheet()];
		for(int i = 0; i < srd.length; i++){
			srd[i] = new SimularResultData(topRowResults, similarities);
		}
		
		StudentPrintData[] data = new StudentPrintData[proj.getNumStudentsPerSheet()];
		for(int i = 0; i < data.length; i++){
			data[i] = new StudentPrintData(parsePrintItem(proj.getStudentNameFormat(), longestStudent, 0), srd);
		}
		
		drawMultipleStudentsPerPage(data, g2, pageFormat);
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		Graphics2D g2 = (Graphics2D) graphics;
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
				
		int stuStart = pageIndex * proj.getNumStudentsPerSheet();
				
		if(stuStart >= data.length) return NO_SUCH_PAGE;
		
		StudentPrintData[] sheetStudents = new StudentPrintData[proj.getNumStudentsPerSheet()];
		for(int i = 0; i < proj.getNumStudentsPerSheet(); i++){
			if(stuStart < data.length){
				sheetStudents[i] = data[stuStart];
			}else{
				sheetStudents[i] = null;
			}
			stuStart++;
		}
		
		drawMultipleStudentsPerPage(sheetStudents, g2, pageFormat);
		return PAGE_EXISTS;
	}
	
	private void drawMultipleStudentsPerPage(StudentPrintData[] stus, Graphics2D g2, PageFormat pf){
		float spaceBetweenEachStudent = proj.getSpaceBetweenStudentPrints();
		float spacePerStudent = (float) ((pf.getImageableHeight() - spaceBetweenEachStudent * (proj.getNumStudentsPerSheet() - 1)) / 
				proj.getNumStudentsPerSheet());
		
		for(int i = 0; i < proj.getNumStudentsPerSheet(); i++){
			if(stus.length >= proj.getNumStudentsPerSheet() && stus[i] != null){
				drawStudentOneSided(stus[i], g2, pf, i * (spaceBetweenEachStudent + spacePerStudent), 
						spacePerStudent + i * (spaceBetweenEachStudent + spacePerStudent));
			}
		}
	}
	
	private void drawStudentOneSided(StudentPrintData stu, Graphics2D g2, PageFormat pf, float start, float end){
		float y = start;
		
		g2.setFont(new Font("Sans-Serif", Font.PLAIN, proj.getTopNameFontSize()));
		g2.setPaint(mainFontColor);
		y += g2.getFontMetrics().getStringBounds(stu.name, g2).getHeight();
		g2.drawString(stu.name, (int) ((pf.getImageableWidth() - g2.getFontMetrics().stringWidth(stu.name)) / 2), y);
		
		y += 4; //space between upper big name and the results below
		
		//get ready for the students
		g2.setFont(new Font("Sans-Serif", Font.PLAIN, proj.getResultRowFontSize()));
		float lesserFontHeight = (float) g2.getFontMetrics().getStringBounds(stu.name, g2).getHeight();
		
		//draw the custom message if there is one
		if(!proj.getCustomMessage().trim().equals("")){
			//we want to center it on the screen, and trim it if it is too big
			String message = proj.getCustomMessage();
			while(g2.getFontMetrics().getStringBounds(message, g2).getWidth() > pf.getImageableWidth())
				message = message.substring(1, message.length() - 1);
			
			y += lesserFontHeight;
			g2.drawString(message, (int) ((pf.getImageableWidth() - g2.getFontMetrics().getStringBounds(message, g2).getWidth()) / 2), y);
		}
		
		y += 3;
		
		//draw the topmost line
		g2.setPaint(lesserFontColor);
		g2.drawLine(0, (int) y, (int) pf.getImageableWidth(), (int) y);
		for(int i = 0; y + lesserFontHeight < end - proj.getMinHeightOfImage() && i < stu.results.length; i++){
			g2.setPaint(mainFontColor);
			y += lesserFontHeight;
			printFirstRow(g2, pf, y, stu.results[i]);
			
			//print the similarities if needed
			if(i < proj.getNumSimilarsToShow()){
				y += lesserFontHeight;
				printSimularities(g2, pf, y, stu.results[i]);
			}
			
			//draw our line
			y += 3;
			g2.setPaint(lesserFontColor);
			g2.drawLine(0, (int) y, (int) pf.getImageableWidth(), (int) y);
		}
		
		y += 5;
		
		if(happyValentine != null && proj.getMinHeightOfImage() > 0){
			float maxWidth = 1;
			if(proj.getCustomImageLocation().trim().equals("")) maxWidth = .75f;
			printImage(g2, pf, y, (float) (pf.getImageableWidth() * maxWidth), end - y - 10, happyValentine);
		}
	}
	
	private void printFirstRow(Graphics2D g2, PageFormat pf, float y, SimularResultData stu){
		for(int i = 0; i < stu.firstRow.length; i++){
			//the first row items should already be sorted
			double upperBound = (i == proj.getTopRowItems().size() - 1 ? pf.getImageableWidth() :
				proj.getTopRowItems().get(i + 1).x * pf.getImageableWidth());
			
			String itemString = stu.firstRow[i];
			while(g2.getFontMetrics().stringWidth(itemString) + proj.getTopRowItems().get(i).x * pf.getImageableWidth() > upperBound){
				if(itemString.length() == 0) break;
				itemString = itemString.substring(0, itemString.length() - 1);
			}
			g2.drawString(itemString, (float) (proj.getTopRowItems().get(i).x * pf.getImageableWidth()), y);
		}
	}
	
	private void printSimularities(Graphics2D g2, PageFormat pf, float y, SimularResultData stu){
		if(stu.simularities.length == 0){
			g2.drawString("No similarities", 0, y);
			return;
		}
		String sims = "Similarities: " + stu.simularities[0];
		int num = 1;
		while(num < stu.simularities.length && g2.getFontMetrics().stringWidth(sims + ", " + stu.simularities[num]) < pf.getImageableWidth()){
			sims += ", " + stu.simularities[num];
			num++;
		}
		
		g2.drawString(sims, 0, y);
	}
	
	private void printImage(Graphics2D g2, PageFormat pf, float y, float maxWidth, float maxHeight, BufferedImage image){
		float imageHeight = Math.min(maxHeight, image.getHeight());
		float imageWidth = Math.min(maxWidth, image.getWidth());
		float scale = Math.min(imageHeight / image.getHeight(), imageWidth / image.getWidth());
		
		g2.drawImage(image, (int) (pf.getImageableWidth() / 2 - image.getWidth() * scale / 2),
				(int) y, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
	}
	
	/**
	 * Sets the brightness of an image
	 * @param image
	 * @param brightness between 0 and 1 of the brightness level
	 * @return
	 */
	private BufferedImage setImageBrightness(BufferedImage image, float brightness){
		if(image == null) return null;
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				image.setRGB(x, y, colorToInt(setPixelBrightness(new Color(image.getRGB(x, y)), brightness)));
			}
		}
		return image;
	}
	
	private static int colorToInt(Color color){
		int rgb = color.getRed();
		rgb = (rgb << 8) + color.getGreen();
		rgb = (rgb << 8) + color.getBlue();
		return rgb;
	}
	
	private Color setPixelBrightness(Color color, float brightness){
		return new Color(setBrightness(color.getRed(),brightness), setBrightness(color.getGreen(), brightness),
				setBrightness(color.getBlue(), brightness));
	}
	
	private int setBrightness(int p, float brightness){
		//return (int) Math.sqrt(Math.abs((65025 - Math.pow(p, 2)) * brightness - 65025));
		return (int) Math.abs((255 - p) * brightness - 255);
	}
	
	public StudentPrintData[] getStudentPrintData(){
		return data;
	}
	
	public static String parsePrintItem(String pi, Student stu, float percent){
		ArrayList<String> args = Backend.getCustomPrintArgsFromString(pi);
		String str = "";
		for(int i = 0; i < args.size(); i++){
			if(Backend.gi(args.get(i)) == null){
				//not number
				if(args.get(i).contains(CSFVPrinter.RESULT_PERCENT_KEY)){
					str += args.get(i).replaceAll(CSFVPrinter.RESULT_PERCENT_KEY, String.format("%.1f", percent) + "%");
				}else{
					str += args.get(i);
				}
			}else if(Backend.gi(args.get(i)) <= stu.getAnswerChoices().length && Backend.gi(args.get(i)) > 0){
				//is number and corresponds to answer choice
				str += stu.getAnswerChoices()[Backend.gi(args.get(i)) - 1];
			}else{
				//is number but too high/low so we'll print it
				str = "" + args.get(i);
			}
		}
		return str;
	}

}
