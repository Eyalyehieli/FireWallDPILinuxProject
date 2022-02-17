package packetsNetFilterDB;

public class StructsTable {
	private int id;
	private String name;
	private int code;
	private int size;
	private ProtocolTable protocol;
	
	public StructsTable(String name,int code, int size, ProtocolTable protocol) {
		this.name = name;
		this.code =code;
		this.size = size;
		this.protocol = protocol;
	}
	
	public StructsTable(String name,int code, int size) {
		this.name = name;
		this.code =code;
		this.size = size;
	}
	
	public StructsTable(String name,int code, ProtocolTable protocol) {
		this.name = name;
		this.code =code;
		this.protocol = protocol;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public ProtocolTable getProtocol() {
		return protocol;
	}
	public void setProtocol(ProtocolTable protocol) {
		this.protocol = protocol;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
