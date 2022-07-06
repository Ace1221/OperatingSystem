package operatingSystem;

import java.util.LinkedList;
import java.util.Queue;

import exceptions.Blocked;

public class Mutex {

	private int processId = -1;
	private boolean sem;
	private Queue<Integer> blocked;

	public Mutex() {
		this.blocked = new LinkedList<Integer>();
		this.sem = true;

	}

	public void semWait(int pid) throws Blocked {
		if (sem) {
			processId = pid;
			sem = false;
		} else {
			blocked.add(pid);
			throw new Blocked("This process is blocked");
		}
	}

	public int semSignal(int pid) {
		// System.out.println("wwwww");
		// System.out.println(sem);
		// System.out.println(processId);
		if (!sem && processId == pid) {
			// System.out.println("weeee");

			if (!blocked.isEmpty()) {

				int x = blocked.remove();
				processId = x;
				return x;

			} else {

				processId = -1;
				sem = true;

			}

		}
		return -1;
	}

	public int getProcessId() {
		return processId;
	}

	public boolean isSem() {
		return sem;
	}

	public Queue<Integer> getBlocked() {
		return blocked;
	}
}
