package operatingSystem;

public class MemoryWord {
	private String name;
	private String data;
	private int ownerProcessId;

	public MemoryWord(String name, String data, int ownerProcessId) {
		this.name = name;
		this.data = data;
		this.ownerProcessId = ownerProcessId;
	}

	public String toString() {
		return this.name + ":" + this.data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getOwnerProcessId() {
		return ownerProcessId;
	}

	public void setOwnerProcessId(int ownerProcessId) {
		this.ownerProcessId = ownerProcessId;
	}

}
