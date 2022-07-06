package operatingSystem;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import exceptions.Blocked;

public class Program {

	private int processExecuting = -1;
	private Mutex file, input, output;
	private Queue<Integer> ready;
	private Queue<Integer> finished;
	private Parser p1, p2, p3;
	private SysCalls sys = new SysCalls();
	private MemoryWord[] mem = new MemoryWord[40];
	private Scanner in = new Scanner(System.in);
	private HashMap<Integer, TempStorage> tp;
	private boolean flag1, flag2, flag3 = false;

	public Program() {
		p1 = new Parser("Program_1.txt");
		p2 = new Parser("Program_2.txt");
		p3 = new Parser("Program_3.txt");
		tp = new HashMap<Integer, TempStorage>();
		file = new Mutex();
		input = new Mutex();
		output = new Mutex();
		output = new Mutex();
		ready = new LinkedList<Integer>();
		finished = new LinkedList<Integer>();
	}

	public void execute(String line) throws Blocked {
		String[] ins = line.split(" ");
		switch (ins[0]) {
		case "semWait":
			switch (ins[1]) {
			case "userInput":
				input.semWait(processExecuting);
				break;
			case "userOutput":
				output.semWait(processExecuting);
				break;
			case "file":
				file.semWait(processExecuting);
				break;
			}
			break;
		case "semSignal":
			switch (ins[1]) {
			case "userInput":
				int temp = input.semSignal(processExecuting);
				System.out.println();
				if (temp != -1) {
					ready.add(temp);
				}
				break;
			case "userOutput":
				int temp1 = output.semSignal(processExecuting);
				if (temp1 != -1) {
					ready.add(temp1);
				}
				break;

			case "file":
				int temp2 = file.semSignal(processExecuting);
				if (temp2 != -1) {
					ready.add(temp2);
				}
				break;
			}
			break;
		case "print":
			System.out.println("Printer:");
			System.out.println(searchInMemory(processExecuting, ins[1]).getData());
			System.out.println();
			break;
		case "printFromTo":

			int temp1 = Integer.parseInt(searchInMemory(processExecuting, ins[1]).getData());
			int temp2 = Integer.parseInt(searchInMemory(processExecuting, ins[2]).getData());

			System.out.println("Printer:");
			sys.printFromTo(temp1, temp2);
			System.out.println();
			break;
		case "readFile":
			String tempp = searchInMemory(processExecuting, ins[1]).getData();
			if (tempp != null) {
				addVariable(processExecuting, "readFile", sys.readFile(tempp));
				if (ins.length > 2) {
					tp.put(processExecuting, new TempStorage(processExecuting, ins[2], sys.readFile(tempp)));
				}
			}
			break;
		case "writeFile":
			String tempp1 = searchInMemory(processExecuting, ins[1]).getData();
			String tempp2 = searchInMemory(processExecuting, ins[2]).getData();
			if (tempp1 != null && tempp2 != null)
				sys.writeFile(tempp1, tempp2);
			break;
		case "assign":
			if (ins.length == 3) {
				addVariable(processExecuting, ins[1], ins[2]);

			} else {
				if (tp.get(processExecuting) != null) {
					addVariable(processExecuting, tp.get(processExecuting).getVariableName(),
							tp.get(processExecuting).getVariableData());
				}
			}
			break;
		case "input":
			System.out.println("Please enter a value");
			String temp = in.next();
			System.out.println();
			tp.put(processExecuting, new TempStorage(processExecuting, ins[1], temp));

			break;

		}

	}

	public MemoryWord searchInMemory(int pid, String variableName) {
		int i = 0;
		if (pid == Integer.parseInt(mem[0].getData())) {
			i = 0;
			while (mem[i] != null && !mem[i].getName().equals(variableName)) {
				i++;
			}
			return mem[i];
		} else {
			i = 20;
			while (mem[i] != null && !mem[i].getName().equals(variableName)) {
				i++;
			}
			return mem[i];

		}
	}

	public void addVariable(int pid, String variableName, String data) {
		int i = 0;
		if (pid == Integer.parseInt(mem[0].getData())) {
			i = 0;
			System.out.println(variableName);
			while (mem[i] != null && !mem[i].getName().equals(variableName)) {
				i++;
			}
			if (mem[i] != null) {
				mem[i].setData(data);
			} else {
				i = 0;
				while (mem[i] != null && !mem[i].getName().equals("VariableSpace")) {
					i++;
				}
				i++;
				while (mem[i] != null && !mem[i].getData().equals("Empty")) {
					i++;
				}
				if (mem[i] != null) {
					mem[i].setName(variableName);
					mem[i].setData(data);
				}

			}
			;
		} else {
			i = 20;
			while (i < 40 && mem[i] != null && !mem[i].getName().equals(variableName)) {
				i++;
			}
			if (i < 40 && mem[i] != null) {
				mem[i].setData(data);
			} else {
				i = 20;
				while (i < 40 && mem[i] != null && !mem[i].getName().equals("VariableSpace")) {
					i++;
				}
				i++;
				while (i < 40 && mem[i] != null && !mem[i].getData().equals("Empty")) {
					i++;
				}
				if (i < 40 && mem[i] != null) {
					mem[i].setName(variableName);
					mem[i].setData(data);
				}

			}
		}
	}

	public void insertInMemory(int pid, int lb, ArrayList<String> inst) {
		int i = lb;
		mem[i++] = new MemoryWord("Pid", pid + "", pid);
		mem[i++] = new MemoryWord("State", State.READY.toString(), pid);
		mem[i++] = new MemoryWord("programCounter", 0 + "", pid);
		mem[i++] = new MemoryWord("lowerBound", lb + "", pid);
		mem[i++] = new MemoryWord("upperBound", (lb + 19) + "", pid);
		int j = 1;
		for (String s : inst)
			mem[i++] = new MemoryWord("Instruction" + (j++), s, pid);
		mem[i++] = new MemoryWord("VariableSpace", null, pid);
		mem[i++] = new MemoryWord("a", "Empty", pid);
		mem[i++] = new MemoryWord("b", "Empty", pid);
		mem[i++] = new MemoryWord("c", "Empty", pid);
	}

	public void swaptoDisk(int lb, int pid) {
		try {
			BufferedReader myReader = new BufferedReader(new FileReader("disk"));
			StringBuffer inputBuffer = new StringBuffer();
			String line = null;

			while ((line = myReader.readLine()) != null && !(line.equals("process" + pid + "Start"))) {
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			while ((line = myReader.readLine()) != null && !(line.equals("process" + pid + "End"))) {
				continue;
			}
			while ((line = myReader.readLine()) != null) {
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			int i = lb;
			inputBuffer.append("process" + pid + "Start");
			inputBuffer.append('\n');

			while (true) {
				if (mem[i] != null && mem[i].getOwnerProcessId() == pid) {
					// System.out.println(mem[i].toString());
					inputBuffer.append(mem[i++].toString());
					inputBuffer.append('\n');

				} else {
					inputBuffer.append("process" + pid + "End");
					inputBuffer.append('\n');
					break;
				}

			}

			String inputStr = inputBuffer.toString();
			FileOutputStream fileOut = new FileOutputStream("disk");
			fileOut.write(inputStr.getBytes());
			myReader.close();
			fileOut.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void admit(int pid, ArrayList<String> inst) {
		ready.add(pid);
		if (mem[0] == null)
			insertInMemory(pid, 0, inst);
		else {
			if (mem[20] == null)
				insertInMemory(pid, 20, inst);
			else {
				if (Integer.parseInt(mem[0].getData()) != processExecuting) {
					System.out.println("Process " + pid + " has been admitted");
					System.out.println("Process " + mem[0].getData() + " has been swapped out to disk");

					swaptoDisk(0, Integer.parseInt(mem[0].getData()));
					insertInMemory(pid, 0, inst);
					System.out.println("Memory: ");
					System.out.println(Arrays.toString(mem));
					System.out.println();
				} else {
					System.out.println("Process " + pid + " has been admitted");
					System.out.println("Process " + mem[20].getData() + " has been swapped out to disk");

					swaptoDisk(20, Integer.parseInt(mem[20].getData()));
					insertInMemory(pid, 20, inst);
					System.out.println("Memory: ");
					System.out.println(Arrays.toString(mem));
					System.out.println();
				}

			}
		}

	}

	public void swapFromDisk(int pid, int lb) {

		try {
			FileReader file = new FileReader("disk");
			BufferedReader myReader = new BufferedReader(file);
			String line = null;

			while ((line = myReader.readLine()) != null && !(line.equals("process" + pid + "Start"))) {
				continue;
			}
			int i = lb;
			while (((line = myReader.readLine()) != null) && !(line.equals("process" + pid + "End"))) {
				String[] nameData = line.split(":");
				if (nameData[0].equals("lowerBound"))
					mem[i++] = new MemoryWord(nameData[0], lb + "", pid);
				else {
					if (nameData[0].equals("upperBound"))
						mem[i++] = new MemoryWord(nameData[0], (lb + 19) + "", pid);
					else
						mem[i++] = new MemoryWord(nameData[0], nameData[1], pid);

				}
			}
			file.close();
			myReader.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		try {
			FileOutputStream fileOut;
			fileOut = new FileOutputStream("disk");
			fileOut.write("".getBytes());
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Program p = new Program();
		System.out.println("Welcome to our OS!");
		System.out.println("-------------------------------------------------------");

		System.out.println("Please select the time slot for which Program 1 arrives:");
		int tp1 = p.in.nextInt();

		System.out.println("Please select the time slot for which Program 2 arrives:");
		int tp2 = p.in.nextInt();

		System.out.println("Please select the time slot for which Program 3 arrives:");
		int tp3 = p.in.nextInt();

		System.out.println("Please select the time slice:");

		int ts = p.in.nextInt();

		System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><>");
		System.out.println();
		int cycles = 0;
		int k = 0;

		while (p.finished.size() < 3) {
			int pcind = 0;
			int pc = 0;
			int stateind = 0;
			if (cycles == tp1 && !p.flag1)
				p.admit(1, p.p1.instructions);
			if (cycles == tp2 && !p.flag2)
				p.admit(2, p.p2.instructions);
			if (cycles == tp3 && !p.flag3)
				p.admit(3, p.p3.instructions);
			System.out.println("Clock: " + cycles);

			boolean cont = false;
			if (p.processExecuting != -1 && k < ts) {
				cont = true;
			} else {
				cont = false;
				k = 0;
			}
			if (cont || !p.ready.isEmpty()) {

				if (!cont) {
					p.processExecuting = p.ready.remove();
					System.out.println(p.processExecuting);
				}

				k++;

				if (Integer.parseInt(p.mem[0].getData()) == p.processExecuting) {
					stateind = 1;
					p.mem[1].setData(State.RUNNING.toString());
					pcind = 2;
					pc = Integer.parseInt(p.mem[2].getData()) + 5;

				} else {
					if (Integer.parseInt(p.mem[20].getData()) == p.processExecuting) {
						stateind = 21;
						p.mem[21].setData(State.RUNNING.toString());
						pcind = 22;
						pc = Integer.parseInt(p.mem[22].getData()) + 25;

					} else {
						System.out.println("Process " + p.processExecuting + " has been swapped in from disk");
						System.out.println("Process " + p.mem[0].getData() + " has been swapped out to disk");
						p.swaptoDisk(0, Integer.parseInt(p.mem[0].getData()));
						p.swapFromDisk(p.processExecuting, 0);

						stateind = 1;
						p.mem[1].setData(State.RUNNING.toString());
						pcind = 2;
						pc = Integer.parseInt(p.mem[2].getData()) + 5;
						System.out.println("Memory: ");
						System.out.println(Arrays.toString(p.mem));
					}

				}
				String nxt = p.mem[pc].getData();
				try {
					if (!cont) {
						System.out.println("Process " + p.processExecuting + " was chosen.");
						System.out.println("-------------------------------------------");
						System.out.println("The processes that are ready are " + Arrays.toString(p.ready.toArray()));
						System.out.println("The processes that are blocked waiting for the file are "
								+ Arrays.toString(p.file.getBlocked().toArray()));
						System.out.println("The processes that are blocked waiting for the input are "
								+ Arrays.toString(p.input.getBlocked().toArray()));
						System.out.println("The processes that are blocked waiting for the printer are "
								+ Arrays.toString(p.output.getBlocked().toArray()));
						;
						System.out.println("The processes that are finished executing are "
								+ Arrays.toString(p.finished.toArray()));
						;
						System.out.println("Memory: ");
						System.out.println(Arrays.toString(p.mem));
					}
					System.out.println();
					System.out.println("The instruction currently executing is: ");
					System.out.println(nxt);
					System.out.println("-------------------------------------------------------------------------");
					System.out.println();
					p.execute(nxt);

				} catch (Blocked f) {
					p.mem[stateind].setData(State.BLOCKED.toString());
					System.out.println("Process " + p.processExecuting + " got blocked");
					System.out.println("The processes that are ready are " + Arrays.toString(p.ready.toArray()));
					System.out.println("The processes that are blocked waiting for the file are "
							+ Arrays.toString(p.file.getBlocked().toArray()));
					System.out.println("The processes that are blocked waiting for the input are "
							+ Arrays.toString(p.input.getBlocked().toArray()));
					System.out.println("The processes that are blocked waiting for the printer are "
							+ Arrays.toString(p.output.getBlocked().toArray()));
					;
					System.out.println(
							"The processes that are finished executing are " + Arrays.toString(p.finished.toArray()));
					;
					System.out.println("Memory: ");
					System.out.println(Arrays.toString(p.mem));
					System.out.println("-------------------------------------------------------------------------");
					System.out.println();
				}
				System.out.println("Memory: ");
				System.out.println(Arrays.toString(p.mem));

				if (!p.mem[pc + 1].getName().equals("VariableSpace")) {

					if (pcind == 22)
						p.mem[pcind].setData((++pc - 25) + "");
					else
						p.mem[pcind].setData((++pc - 5) + "");

				} else {

					System.out.println("Process " + p.processExecuting + " finished executing");
					System.out.println("The processes that are ready are " + Arrays.toString(p.ready.toArray()));
					System.out.println("The processes that are blocked waiting for the file are "
							+ Arrays.toString(p.file.getBlocked().toArray()));
					System.out.println("The processes that are blocked waiting for the input are "
							+ Arrays.toString(p.input.getBlocked().toArray()));
					System.out.println("The processes that are blocked waiting for the printer are "
							+ Arrays.toString(p.output.getBlocked().toArray()));
					;
					p.mem[stateind].setData(State.FINISHED.toString());
					p.finished.add(p.processExecuting);
					System.out.println(
							"The processes that are finished executing are " + Arrays.toString(p.finished.toArray()));
					;
					System.out.println("Memory: ");
					System.out.println(Arrays.toString(p.mem));
					System.out.println("-------------------------------------------------------------------------");
					System.out.println();
				}

			}
			cycles++;
			if (cycles == tp1) {
				p.admit(1, p.p1.instructions);
				p.flag1 = true;
			}
			if (cycles == tp2) {
				p.admit(2, p.p2.instructions);
				p.flag2 = true;
			}
			if (cycles == tp3) {
				p.admit(3, p.p3.instructions);
				p.flag3 = true;
			}

			if (!(p.mem[stateind].getData().equals("BLOCKED")) && !(p.mem[stateind].getData().equals("FINISHED"))) {
				if (p.mem[stateind].getData().equals("RUNNING") && k == ts) {
					// System.out.println("Im here");
					p.mem[stateind].setData(State.READY.toString());
					p.ready.add(p.processExecuting);
				}

			} else {
				p.processExecuting = -1;
			}

		}

	}

}
