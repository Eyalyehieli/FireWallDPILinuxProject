package PacketGenerator;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class StructFieldsTable 
{
	//---------------properties----------------//
	private String name;
	private String type;
	private String minRange;
	private String maxRange;
	
	//------------C'tors-----------------//
	public StructFieldsTable(String name,String type,String minRange,String maxRange)
	{
		this.name=name;
		this.type=type;
		this.minRange=minRange;//getRange(type,minRange);
		this.maxRange=maxRange;//getRange(type,maxRange);
	}
	public StructFieldsTable(String type,String minRange,String maxRange)
	{
		this.type=type;
		this.minRange=minRange;//getRange(type,minRange);
		this.maxRange=maxRange;//getRange(type,maxRange);
	}
	
	//-------------functions----------------//
	public byte[] rangeAsByteArray()
	{
		Random rand = new Random();
		if(type==null||type.isEmpty())
		{
			return null;
		}
		else if(type.equals("INT"))
		{
			int min=Integer.valueOf(minRange);
			int max=Integer.valueOf(maxRange);
			return ByteBuffer.allocate(Integer.BYTES).putInt(ThreadLocalRandom.current().nextInt(min, max)).array();
			
		}
		else if(type.equals("DOUBLE"))
		{
			double min=Double.valueOf(minRange);
			double max=Double.valueOf(maxRange);
			return ByteBuffer.allocate(Double.BYTES).putDouble(min + (max - min) * rand.nextDouble()).array();
		}
		else if(type.equals("FLOAT"))
		{
			float min=Float.valueOf(minRange);
			float max=Float.valueOf(maxRange);
			return ByteBuffer.allocate(Float.BYTES).putFloat(min + (max - min) * rand.nextFloat()).array();
			
		}
		else if(type.equals("CHAR"))
		{
			char min=minRange.charAt(0);
			char max=maxRange.charAt(0);//using byte as char because byte in java8 is 2 bytes
			return ByteBuffer.allocate(Byte.BYTES).put((byte) (ThreadLocalRandom.current().nextInt((int)min,(int)(max)))).array();
		}
		else if(type.equals("SHORT"))
		{
			short min=Short.valueOf(minRange);
			Short max=Short.valueOf(maxRange);
			return ByteBuffer.allocate(Short.BYTES).putShort((short) ThreadLocalRandom.current().nextInt(min, max)).array();
		}
		else if(type.equals("LONG"))
		{
			long min=Long.valueOf(minRange);
			long max=Long.valueOf(maxRange);
			return ByteBuffer.allocate(Long.BYTES).putLong(ThreadLocalRandom.current().nextLong(min, max)).array();
		}
		return null;
	}
	
	public int getSizeOfRangeClass()
	{
		if(type==null||type.isEmpty())
		{
			return 0;
		}
		else if(type.equals("INT"))
		{
			return Integer.BYTES;
			
		}
		else if(type.equals("DOUBLE"))
		{
			return  Double.BYTES;
			
		}
		else if(type.equals("FLOAT"))
		{
			return  Float.BYTES;
			
		}
		else if(type.equals("CHAR"))
		{
			return  Character.BYTES-1;
		}
		else if(type.equals("SHORT"))
		{
			return  Short.BYTES;
		}
		else if(type.equals("LONG"))
		{
			return Long.BYTES;
		}
		return 0;
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
	
}
