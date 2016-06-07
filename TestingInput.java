package csf_val;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TestingInput {
	
	//TODO add the functionality to the backend class.
	/*public static void main(String[] args){
				
		ArrayList<String> inputs = readDataFromFile("C://Users//CA//Desktop//csfinput.txt");
		//printArrayList(inputs);
		assessArrayListData(inputs);
		
	}*/
	
	public static ArrayList<String> readDataFromFile(String filePath){
		File file = new File(filePath);
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if(!file.exists()) return arrayList;
		
		try{
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()){
				arrayList.add(scanner.nextLine());
			}
			
			scanner.close();
		}catch(Exception e){}
		return arrayList;
	}
	
	public static void printArrayList(ArrayList<String> arrayList){
		for(String string : arrayList){
			System.out.println(string);
		}
	}
	
	public static void assessArrayListData(ArrayList<String> data){
		int highestNumberOfCommas = 0;
		
		for(int i = 0; i < data.size(); i++){
			int subCommaCount = 0;
			for(int j = 0; j < data.get(i).length(); j++){
				if(data.get(i).substring(j, j + 1).equals(",")) subCommaCount++;
			}
			if(subCommaCount > highestNumberOfCommas) highestNumberOfCommas = subCommaCount;
		}
		
		System.out.println("Highest number of commas: " + highestNumberOfCommas);
	
		if(highestNumberOfCommas == 0){
			//users only had option of choosing one choice, and none of the choices had a comma
			
			ArrayList<String> elementsNoDups = new ArrayList<String>();
			
			for(int i = 0; i < data.size(); i++){
				if(!elementsNoDups.contains(data.get(i))){
					elementsNoDups.add(data.get(i));
				}
			}
			
			//System.out.println("Number of elements excluding duplicats: " + elementsNoDups.size());
			//System.out.println("Number of elements: " + data.size());
			
			double multScore = 100 - 100.0 * elementsNoDups.size() / data.size(); //Likeliness that this question is a multiple choice
			
			System.out.println("Likelyhood that this is a multiple choice question " + multScore);
			if(multScore >= 75){
				System.out.println("We believe that this is a multiple choice question");
			}else if(multScore >= 25){
				System.out.println("This could be either a multiple choice or a free response question");
			}else{
				System.out.println("We believe that this is a free response question");
			}
		}else{
			//users could choose more than one, or one of the options had a comma in it.
			
			ArrayList<String> possibleOptions = new ArrayList<String>();
			
			for(int i = 0; i < data.size(); i++){
				possibleOptions.addAll(breakStringInputAllPossibleComboArrayList(data.get(i)));
			}
			
			//If the item is single, remove all of it's duplicates, if the item is made up of multiple, remove it if there are no duplicates
			for(int i = 0; i < possibleOptions.size(); i++){
				if(possibleOptions.get(i).contains(",")){
					boolean areDups = false;
					for(int j = i + 1; j < possibleOptions.size(); j++){
						if(possibleOptions.get(j).equals(possibleOptions.get(i))){
							possibleOptions.remove(j);
							areDups = true;
							j--;
						}					
					}
					if(!areDups){
						possibleOptions.remove(i);
						i--;
					}
				}else{
					for(int j = i + 1; j < possibleOptions.size(); j++){
						if(possibleOptions.get(j).equals(possibleOptions.get(i))){
							possibleOptions.remove(j);
							j--;
						}
					}
				}
			}
			
			//if the option has not been selected by itself, remove it
			/*for(int i = 0; i < possibleOptions.size(); i++){
				if(numOfItemInArrayList(data, possibleOptions.get(i)) == 0){
					possibleOptions.remove(i);
					i--;
				}
			}*/

			System.out.println("All possible options:");
			for(int i = 0; i < possibleOptions.size(); i++){
				System.out.println((numOfItemInItemsInArrayList(data, possibleOptions.get(i)) + 
						numOfItemInArrayList(data, possibleOptions.get(i))) + " " + possibleOptions.get(i));
			}
		}
	}
	
	private static ArrayList<String> breakStringIntoArray(String str){
		ArrayList<String> elements = new ArrayList<String>();
		
		while(true){
			elements.add(pullFrontOption(str));
			if(str.contains(", ")) str = str.substring(str.indexOf(", ") + 2);
			else break;
		}
		
		return elements;
	}
	
	private static ArrayList<String> breakStringInputAllPossibleComboArrayList(String str){
		ArrayList<String> allCombos = breakStringIntoArray(str);
		
		int numSmallest = allCombos.size();
		for(int i = 2; i < numSmallest + 2; i++){ //i = size of items we are grabbing
			for(int j = 0; j < numSmallest + 1 - i; j++){ // j = item we are starting at to get item
				String newItem = "";
				for(int k = j; k < j + i; k++){
					newItem += allCombos.get(k);
					if(k != j + i - 1) newItem += ", ";
				}
				allCombos.add(newItem);
			}
		}
		return allCombos;
	}
	
	private static String pullFrontOption(String str){
		if(!str.contains(", ")) return str;
		return str.substring(0, str.indexOf(", "));
	}
	
	public static int numOfItemInArrayList(ArrayList<String> data, String str){
		int numOfElement = 0;
		
		for(int i = 0; i < data.size(); i++){
			if(data.get(i).equals(str)) numOfElement++;
		}
		
		return numOfElement;
	}
	
	private static int numOfItemInItemsInArrayList(ArrayList<String> data, String str){
		int numOfElement = 0;
		
		for(int i = 0; i < data.size(); i++){
			for(int j = 0; j < data.get(i).length() - str.length() + 1; j++){
				//System.out.println("String length: " + str.length() + "substring length: " + data.get(i).substring(j, str.length()).length());
				if(data.get(i).substring(j, str.length() + j).equals(str)) numOfElement++;
			}
		}
		
		return numOfElement;
	}
}
