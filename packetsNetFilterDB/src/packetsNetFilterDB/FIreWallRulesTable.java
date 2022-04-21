package packetsNetFilterDB;

import java.sql.SQLException;

public class FIreWallRulesTable {
	
	//----------properties----------//
	private int id;
	private int activeStatus;
	private ConnectionTable connection;
	private ProtocolTable protocol;
	
	//----------C'tors----------//
	public FIreWallRulesTable(int activeStatus,ProtocolTable protocol,ConnectionTable connection)
	{
		this.activeStatus=activeStatus;
		this.protocol=protocol;
		this.connection=connection;
	}
	public FIreWallRulesTable(String protocolName,int port,String ip)
	{
		this.connection=new ConnectionTable(ip,port);
		this.protocol=new ProtocolTable(protocolName);
	}
	
	public FIreWallRulesTable(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	//----------functions----------//
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	public ConnectionTable getConnection() {
		return connection;
	}
	public void setConnection(ConnectionTable connection) {
		this.connection = connection;
	}
	public ProtocolTable getProtocol() {
		return protocol;
	}
	public void setProtocol(ProtocolTable protocol) {
		this.protocol = protocol;
	}
	
	public static FIreWallRulesTable getFIreWallRule(ProtocolTable protocol,ConnectionTable connection,int activeStatus,Boolean isForceToInsertion,SqliteDB sqlitedb)
	{
		try 
		{
			FIreWallRulesTable fireWallRulesTable=new FIreWallRulesTable(activeStatus);
			fireWallRulesTable.setConnection(connection);
			fireWallRulesTable.setProtocol(protocol);
			fireWallRulesTable.setId(sqlitedb.GetFireWallRulesId(fireWallRulesTable,isForceToInsertion));
			return fireWallRulesTable;
		} 
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
