package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class NewParser {
	public NewParser(){}
	
	public List<String> parse(String filename) throws IOException{
		List<String> toReturn = new ArrayList<String>();

		ClassLoader classLoader = getClass().getClassLoader();
		String absolutePath = classLoader.getResource(filename).getPath();
		BufferedReader br = new BufferedReader(new FileReader(absolutePath) );
		

		try{
			StringBuilder tempSB = new StringBuilder();

			String line = br.readLine();
			
			int bracketCounter=0;
			boolean addChar = false;
			
			while(line != null){
				
				for(int i=0; i<line.length(); ++i){
					char c = line.charAt(i);
					//System.out.println(c);

					if((c == '[' || c == ']' || c==',') && bracketCounter == 0){
						addChar = false;
					}
					
					// start of our main json
					else if(c == '{' && bracketCounter == 0){
						addChar = true;
						bracketCounter++;
					}
					
					// start of nested json
					else if(c == '{' && bracketCounter>0){
						addChar = true;
						bracketCounter++;
					}
					
					// ending of nested json
					else if(c == '}' && bracketCounter>0){
						addChar = true;
						bracketCounter--;
					}

					else{
						// do nothing
					}
					

					if(addChar == true){
						tempSB.append(c);
					}

					// end of main json
					if(bracketCounter == 0 ){
						addChar = true;
						String s = tempSB.toString();
						toReturn.add(s.trim());
						tempSB = new StringBuilder();
					}
					
				}
				
				tempSB.append("\n");
				line = br.readLine();
			}
			
			return toReturn;
		}
		
		finally{
		}

	}
}



