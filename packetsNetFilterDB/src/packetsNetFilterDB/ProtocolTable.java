package packetsNetFilterDB;

public class ProtocolTable {
	private int id;
	private String name;

	
	public ProtocolTable(String name) {
		this.name = name;
	}
	
	public ProtocolTable(int id)
	{
		this.id=id;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
