package packetsNetFilterDB;

import java.sql.SQLException;

public class ProtocolTable {
	//-----------properties-------------//
	private int id;
	private String name;

	//----------C'tors-------------//
	public ProtocolTable(String name) {
		this.name = name;
	}
	
	public ProtocolTable(int id)
	{
		this.id=id;
	}
	
	//-----------functions-----------//
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
	
	
	public String toString()
	{
		return this.name;
	}
	
	public static ProtocolTable getProtocolFromDB(String protocolName,Boolean isToForceInsertion,SqliteDB sqlitedb)
	{
		try {
				ProtocolTable protocol=new ProtocolTable(protocolName);
				protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,isToForceInsertion));
				return protocol;
			} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
