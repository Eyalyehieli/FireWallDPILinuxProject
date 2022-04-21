package packetsNetFilterDB;

public class StructsFieldsTable {
	//-----------properties----------//
	private int id;
	private String name;
	private String type;
	private String minRange;
	private String maxRange;
	private StructsTable struct;
	
	//-----------C'tors---------------//
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
	
	//----------functions-------------//
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
		case "CHAR":return 1;	
		case "SHORT":return 2;
		case "LONG":return 8;
		}
		return 0;
	}
	
	public static Boolean CheckRange(String rangeType,String minRange,String maxRange)
	{
		Object minRangeObj=null;
		Object maxRangeObj=null;
		if(rangeType==null||rangeType.isEmpty())
		{
			return false;
		}
		else if(rangeType.equals("INT"))
		{
			  minRangeObj=new Integer(Integer.valueOf(minRange));
			  maxRangeObj=new Integer(Integer.valueOf(maxRange));
			  return ((Integer)minRangeObj)>=((Integer)maxRangeObj);
		}
		else if(rangeType.equals("DOUBLE"))
		{
			minRangeObj= new Double(Double.valueOf(minRange));
			maxRangeObj=new Double(Double.valueOf(maxRange));
			return ((Double)minRangeObj)>=((Double)maxRangeObj);
		}
		else if(rangeType.equals("FLOAT"))
		{
			minRangeObj= new Float(Float.valueOf(minRange));
			maxRangeObj=new Float(Float.valueOf(maxRange));
			return ((Float)minRangeObj)>=((Float)maxRangeObj);
		}
		else if(rangeType.equals("CHAR"))
		{
			minRangeObj= new Character(Character.valueOf(minRange.charAt(0)));
			maxRangeObj= new Character(Character.valueOf(maxRange.charAt(0)));
			return ((Character)minRangeObj)>=((Character)maxRangeObj);
		}
		else if(rangeType.equals("SHORT"))
		{
			minRangeObj= new Short(Short.valueOf(minRange));
			maxRangeObj= new Short(Short.valueOf(maxRange));
			return ((Short)minRangeObj)>=((Short)maxRangeObj);
		}
		else if(rangeType.equals("LONG"))
		{
			minRangeObj=new Long(Long.valueOf(minRange));
			maxRangeObj=new Long(Long.valueOf(maxRange));
			return ((Long)minRangeObj)>=((Long)maxRangeObj);
		}
		return false;
	}

	

}
