package packetsNetFilterDB;

public class StructsFieldsTable {
	private int id;
	private String name;
	private String type;
	private String minRange;
	private String maxRange;
	private StructsTable struct;
	
	public StructsFieldsTable(String name, String type, String minRange, String maxRange, StructsTable struct) {
		this.name = name;
		this.type = type;
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.struct = struct;
	}
	public StructsFieldsTable(String name, String type, String minRange, String maxRange)
	{
		this.name = name;
		this.type = type;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}
	
	public StructsFieldsTable(String name,StructsTable struct)
	{
		this.name=name;
		this.struct=struct;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMinRange() {
		return minRange;
	}
	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}
	public String getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}
	public StructsTable getStruct() {
		return struct;
	}
	public void setStruct(StructsTable struct) {
		this.struct = struct;
	}
	
	public static int getSizeOfTypeInBits(String type)
	{
		switch(type)
		{
		case "INT": return 4;
		case "DOUBLE": return 8;
		case "FLOAT":return 4; 
		case "CHAR":return 2;	
		case "SHORT":return 2;
		case "LONG":return 8;
		}
		return 0;
	}
	
	

}
