package operatingSystem;

public class TempStorage {

	private int ownerId;
	private String variableName;
	private String variableData;
	public TempStorage(int ownerId, String variableName, String variableData) {
		this.ownerId = ownerId;
		this.variableName = variableName;
		this.variableData = variableData;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public String getVariableData() {
		return variableData;
	}
	public void setVariableData(String variableData) {
		this.variableData = variableData;
	}
	
	
}
