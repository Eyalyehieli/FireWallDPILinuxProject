package PacketGenerator;

public class StructFieldsTable 
{
	private String name;
	private String type;
	private Object minRange;
	private Object maxRange;
	
	public StructFieldsTable(String name,String type,String minRange,String maxRange)
	{
		this.name=name;
		this.type=type;
		this.minRange=getRange(type,minRange);
		this.maxRange=getRange(type,maxRange);
	}
	
	public static Object getRange(String rangeType,String value)
	{
		if(rangeType==null||rangeType.isEmpty())
		{
			return null;
		}
		else if(rangeType.equals("INT"))
		{
			return new Integer(Integer.valueOf(value));
			
		}
		else if(rangeType.equals("DOUBLE"))
		{
			return new Double(Double.valueOf(value));
			
		}
		else if(rangeType.equals("FLOAT"))
		{
			return new Float(Float.valueOf(value));
			
		}
		else if(rangeType.equals("CHAR"))
		{
			return new Character(Character.valueOf(value.charAt(0)));
		}
		else if(rangeType.equals("SHORT"))
		{
			return new Short(Short.valueOf(value));
		}
		else if(rangeType.equals("LONG"))
		{
			return new Long(Long.valueOf(value));
		}
		return null;
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

	public Object getMinRange() {
		return minRange;
	}

	public void setMinRange(Object minRange) {
		this.minRange = minRange;
	}

	public Object getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Object maxRange) {
		this.maxRange = maxRange;
	}
	
}
