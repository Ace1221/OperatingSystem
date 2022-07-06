package operatingSystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	public ArrayList<String> instructions = new ArrayList<String>();

	public Parser(String Filepath) {
		FileReader fr = null;
		try {
			fr = new FileReader(Filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" ");
				if (splitted.length < 3 || (splitted.length == 3 && !splitted[2].equals("input")))
					instructions.add(line);
				else {
					if (splitted.length == 3) {
						instructions.add(splitted[2] + " " + splitted[1]);
						instructions.add(splitted[0] + " " + splitted[1]);
					} else {
						instructions.add(splitted[2] + " " + splitted[3] + " " + splitted[1]);
						instructions.add(splitted[0] + " " + splitted[1]);
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
