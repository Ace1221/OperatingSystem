package operatingSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SysCalls {

	public void writeFile(String fileName, String data) {
		try {
			File myObj = new File(fileName + ".txt");
			FileWriter myWriter = new FileWriter(fileName + ".txt");
			myWriter.write(data);
			myWriter.close();

		} catch (IOException e) {
			System.out.println("Couldnt write");
			e.printStackTrace();
		}
	}

	public String readFile(String fileName) {
		String res = "";
		try {
			FileReader file = new FileReader(
					new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().length() - 1)
							+ fileName+".txt");
			BufferedReader myReader = new BufferedReader(file);
			String line;

			while ((line = myReader.readLine()) != null) {
				res += line + "\n";
			}
			file.close();
			myReader.close();

		} catch (IOException e) {
			System.out.println("Couldn't read");
			e.printStackTrace();
		}
		return res;

	}

	public void printFromTo(int x, int y) {
		for (int i = x; i < y; i++)
			System.out.println(i);

	}

}
