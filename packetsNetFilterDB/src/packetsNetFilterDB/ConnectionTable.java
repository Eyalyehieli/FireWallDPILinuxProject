package packetsNetFilterDB;

import java.sql.SQLException;

public class ConnectionTable {
	//----------properties----------//
	private int id;
	private String ip;
	private int port;
	
	//----------C'tor----------//
	public ConnectionTable(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	//----------functions----------//
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString()
	{
		return this.ip+","+this.port;
	}
	public static ConnectionTable getConnectionFromDB(String ip,int port,Boolean isForceToInsertion,SqliteDB sqlitedb)
	{
		try
		{
			ConnectionTable connection=new ConnectionTable(ip,port);
			connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, isForceToInsertion));
			return connection;
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

}
