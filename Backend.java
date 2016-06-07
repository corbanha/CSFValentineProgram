package csf_val;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Backend {
	
	@Deprecated
	public static class IncorrectEndKeyException extends Exception {
		public IncorrectEndKeyException(){
			super("The end key value didn't match the scramble.");
		}
		
		public IncorrectEndKeyException(String des){
			super(des);
		}
		private static final long serialVersionUID = -7126246062180431677L; //no idea what this is, but the compiler wanted it
	}
	
	public static final String SAVE_FILE_LOCATION = "C:\\CSFVData\\data.txt"; //location of system data save.
	public static final String iconLocation = "icon.png";
	
	protected static String locationOfLastSave = "";
		
	/**
	 * Will load all of the CSFV specific system things. Like program version, last save location, etc.
	 * @return true if there was data to be found ie file was intact and read successfully, false if not.
	 * 	A return of false isn't necessarily bad, it may just mean that this is the user's first time running the program.
	 * @throws Exception 
	 */
	public static boolean loadData(){
		try {
			File dataFile = new File(SAVE_FILE_LOCATION);
			if(dataFile.exists()){
				Scanner scanner = new Scanner(new File(SAVE_FILE_LOCATION));
				
				String lastFileSaveLocation = scanner.nextLine();
				if(new File(lastFileSaveLocation).exists()){
					locationOfLastSave = lastFileSaveLocation;
				}else{
					//can't read the file, won't be supporting backwards compatibility
					locationOfLastSave = "";
				}
				
				scanner.close();
				return true;
			}else{
				saveData();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
		
	public static boolean saveData(){
		try{
			new File(SAVE_FILE_LOCATION.substring(0, SAVE_FILE_LOCATION.lastIndexOf("\\"))).mkdirs();
			PrintWriter writ = new PrintWriter(SAVE_FILE_LOCATION);
			
			writ.println(locationOfLastSave);
			
			writ.flush();
			writ.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static CSFVProject readUnencryptedProject(String projectSaveLocation){
		if(!projectSaveLocation.toLowerCase().trim().endsWith("csfv")){
			return null;
		}
		try{
			File file = new File(projectSaveLocation);
			if(file.exists()){
				ArrayList<String> saveFile = new ArrayList<String>();
				Scanner fileScanner = new Scanner(file);
				while(fileScanner.hasNextLine()){
					saveFile.add(fileScanner.nextLine());
				}
				fileScanner.close();
				return new CSFVProject(projectSaveLocation, saveFile);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean saveUnencryptedProject(CSFVProject project){
		if(!project.getSaveLocation().toLowerCase().trim().endsWith("csfv")) return false;
		try{
			PrintWriter writ = new PrintWriter(new File(project.getSaveLocation()));
			ArrayList<String> args = project.getSaveFileArgs();
			for(int i = 0; i < args.size(); i++){
				writ.println(args.get(i));
			}
			
			writ.flush();
			writ.close();
			setLocationOfLastSave(project.getSaveLocation());
			saveData();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean saveUnencryptedProjectAs(CSFVProject project, String newFilePath){
		if(!newFilePath.endsWith(".csfv")) newFilePath += ".csfv";
		try{
			PrintWriter writ = new PrintWriter(new File(newFilePath));
			ArrayList<String> args = project.getSaveFileArgs();
			for(int i = 0; i < args.size(); i++){
				writ.println(args.get(i));
			}
			
			writ.flush();
			writ.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static String getLocationOfLastSave(){
		return locationOfLastSave;
	}
	
	public static void setLocationOfLastSave(String loc){
		locationOfLastSave = loc;
	}
	
	public static ArrayList<String> getSaveStringArgsFromSaveString(String saveString){
		ArrayList<String> saveStringArgs = new ArrayList<String>();
		while(saveString.length() != 0){
			if(saveString.contains("" + (char) 29)){
				saveStringArgs.add(saveString.substring(0, saveString.indexOf(29)));
				saveString = saveString.substring(saveString.indexOf(29) + 1);
			}else{
				saveStringArgs.add(saveString);
				saveString = "";
			}
		}
		return saveStringArgs;
	}
	
	public static String[][] getSpreadsheetFromFile(File file){
		try{
			if(file.getAbsolutePath().endsWith(".csv")){
				Scanner scanner = new Scanner(file);
				scanner.useDelimiter("\n");
				
				ArrayList<String[]> lines = new ArrayList<>();
				
				while(scanner.hasNext()){
					String line = scanner.next().trim();
					ArrayList<String> fileContents = new ArrayList<>();
	
					String item = "";
					boolean inQuote = false;
					for(int i = 0; i < line.length(); i++){
						if(!inQuote){
							if(line.charAt(i) == '"'){
								//we are entering text surrounded by a quotes
								inQuote = true;
							}else if(line.charAt(i) == ','){
								//move on to the next item
								fileContents.add(item);
								item = "";
							}else{
								item += line.charAt(i);
								if(i + 1 >= line.length()) fileContents.add(item);
							}
						}else{
							//we are in a quote
							if(line.charAt(i) == '"'){
								//have to check and see if the next thing is a quote
								if(i + 1 < line.length() && line.charAt(i + 1) == '"'){
									//a quote
									i++;
									item += "\"";
								}else{
									//the block quote ends here
									fileContents.add(item);
									item = "";
									inQuote = false;
									i++;
								}
							}else{
								item += line.charAt(i);
							}
						}
					}
					
					if(fileContents.size() != 0) lines.add(fileContents.toArray(new String[fileContents.size()]));
				}
				
				scanner.close();
				
				String[][] table = new String[lines.size()][];
				for(int i = 0; i < table.length; i++){
					table[i] = lines.get(i);
				}
				
				
				for(int i = 0; i < table.length; i++){
					for(int j = 0; j < table[i].length; j++){
						System.out.print(table[i][j]);
						if(j + 1 < table[i].length) System.out.print("_");
					}
					System.out.println();
				}
				
				return table;
				
			}else if(file.getAbsolutePath().endsWith(".tsv")){
				Scanner scanner = new Scanner(file);
				ArrayList<String> fileContents = new ArrayList<>();
				
				while(scanner.hasNextLine()){
					fileContents.add(scanner.nextLine());
				}
				scanner.close();
				
				String[][] table = new String[fileContents.size()][];
				for(int i = 0; i < fileContents.size(); i++){
					ArrayList<String> line = new ArrayList<>();
					Scanner lineScanner = new Scanner(fileContents.get(i));
					lineScanner.useDelimiter("\t");
					while(lineScanner.hasNext()){
						line.add(lineScanner.next());
					}
					lineScanner.close();
					
					table[i] = line.toArray(new String[line.size()]);
				}
				return table;
			}else if(file.getAbsolutePath().endsWith(".xlsx")){
				Workbook wb = WorkbookFactory.create(new FileInputStream(file));
				
				String[][] table = new String[wb.getSheetAt(0).getPhysicalNumberOfRows()][wb.getSheetAt(0).getRow(0).getPhysicalNumberOfCells()];
				
				for(int i = 0; i < wb.getSheetAt(0).getPhysicalNumberOfRows(); i++){
					for(int j = 0; j < wb.getSheetAt(0).getRow(0).getPhysicalNumberOfCells(); j++){
						table[i][j] = wb.getSheetAt(0).getRow(i).getCell(j).toString();
					}
				}
				
				return table;
			}else{
				
			}
		}catch(Exception e){
			
		}
		
		return null;
	}

	
	/**
	 * This method will take all of the user answers for a question, and return the top few answers
	 * which are most likely the actual answers to the question
	 * @param userAnswers
	 * @return
	 */
	public static String[] getPossibleOptionsFromMultipleChoiceAnswers(String[] userAnswers, int numGuesses){
		ArrayList<String> allPossibleAnswers = new ArrayList<>();
		for(int i = 0; i < userAnswers.length; i++){
			String[] answersAsArray = userAnswers[i].split(", ");
			for(int j = 0; j < answersAsArray.length; j++){ //start index
				for(int k = 1; k < answersAsArray.length - j + 1; k++){ //the length of the piece to cut out
					String answer = "";
					for(int l = 0; l < k; l++){
						answer += answersAsArray[j + l];
						if(l + 1 < k) answer += ", ";
					}
					allPossibleAnswers.add(answer);
				}
			}			
		}
		
		class AnswerItem{
			String answer;
			int num;
			AnswerItem(String answer){
				this.answer = answer;
			}
		}
		
		ArrayList<AnswerItem> combinedAnswers = new ArrayList<>();
		outer:
		for(String str : allPossibleAnswers){
			for(int i = 0; i < combinedAnswers.size(); i++){
				if(combinedAnswers.get(i).answer.equals(str)){
					combinedAnswers.get(i).num++;
					continue outer;
				}
			}
			combinedAnswers.add(new AnswerItem(str));
		}
		
		Collections.sort(combinedAnswers, new Comparator<AnswerItem>(){
			@Override
			public int compare(AnswerItem o1, AnswerItem o2) {
				return o2.num - o1.num;
			}			
		});
		
		for(int i = 0; i < combinedAnswers.size(); i++)
			if(combinedAnswers.get(i).num == 0){
				combinedAnswers.remove(i);
				i--;
			}
		
		String[] guesses = new String[Math.min(numGuesses, combinedAnswers.size())];
		for(int i = 0; i < guesses.length; i++){
			guesses[i] = combinedAnswers.get(i).answer;
		}
		return guesses;
	}
	
	/**
	 * Will seperate all of the number and string segments from a string. Does not support negative numbers.
	 * Will turn "4, 6: 5" into 4|, |6|: |5
	 * @param str
	 * @return
	 */
	public static ArrayList<String> getCustomPrintArgsFromString(String str){
		ArrayList<String> args = new ArrayList<String>();
		for(int i = 0; i < str.length(); i++){
			if(args.size() > 0){
				if(gi(args.get(args.size() - 1)) != null){
					if(gi(str.substring(i, i + 1)) != null){
						args.set(args.size() -1, args.get(args.size() - 1) + str.substring(i, i + 1));
					}else{
						args.add(str.substring(i, i + 1));
					}
				}else{
					if(gi(str.substring(i, i + 1)) != null){
						args.add(str.substring(i, i + 1));
					}else{
						args.set(args.size() -1, args.get(args.size() - 1) + str.substring(i, i + 1));
					}
				}
			}else{
				args.add(str.substring(i, i + 1));
			}
		}
		
		return args;
	}
	
	/**
	 * For the record this stands for get int, it returns an int from a string if it can, or it will be null.
	 * It will not throw anything.
	 * @param str
	 * @return
	 */
	public static Integer gi(String str){
		try{
			return Integer.parseInt(str);
		}catch(Exception e){
			return null;
		}
	}
	
	public static Float fi(String str){
		try{
			return Float.parseFloat(str);
		}catch(Exception e){
			return null;
		}
	}
	
	public static BufferedImage getImageResource(Class<?> c, String filePath){
		try{
			if(c.getClassLoader().getResource(filePath) != null){
				return ImageIO.read(c.getClassLoader().getResourceAsStream(filePath));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
