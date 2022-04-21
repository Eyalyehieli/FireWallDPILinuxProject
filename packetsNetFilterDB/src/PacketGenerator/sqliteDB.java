package PacketGenerator;
import java.sql.*;
import java.util.ArrayList;

import packetsNetFilterDB.StructsFieldsTable;



public class sqliteDB {

	//--------properties--------------//
	private static sqliteDB sqlitedb=null;
	private Connection con;
	
	//--------C'tor--------------//
	public sqliteDB(String url) throws SQLException
	{
		con=DriverManager.getConnection(url);
		con.createStatement().execute("PRAGMA foreign_keys = ON");
	}
	//--------Signelton implementation-------------//
	public synchronized static sqliteDB getSqliteDBInstance() throws SQLException
	{
		if(sqlitedb==null)
		{
			sqlitedb=new sqliteDB("jdbc:sqlite:"+System.getProperty("user.home")+"/Desktop/FireWallProject/FireWallDPILinuxProject/packetsNetFilterDB/netFilterDB.sqlite");
		}
		return sqlitedb;
	}
	
	//------------functions--------------//
	public int getProtocolIdByProtocolName(String protocolName) throws SQLException
	{
		String query="SELECT id\n" +
					 "FROM Protocols\n" +
					 "WHERE name LIKE ?";
		int id=-1;
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, protocolName);
		rs=prpStmt.executeQuery();
		if(rs.next())
		{
			id=rs.getInt("id");
		}
		rs.close();
		prpStmt.close();
		return id;
	}
	
	public ArrayList getAllStructFields(String ip,int port,String protocolName,int structCode) throws SQLException
	{
		ArrayList<StructFieldsTable> structFields=new ArrayList<StructFieldsTable>();
		String query="SELECT fieldName,type,minRange,maxRange\n" + 
				"FROM FireWallRules \n" + 
				"INNER JOIN Connections\n" + 
				"ON Connections.id=FireWallRules.connection_id\n" + 
				"INNER JOIN Protocols\n" + 
				"ON Protocols.id=FireWallRules.protocol_id\n" + 
				"INNER JOIN Structs\n" + 
				"ON Structs.protocol_id=Protocols.id\n" + 
				"INNER JOIN StructFields\n" + 
				"ON StructFields.struct_id=Structs.id\n" + 
				"WHERE Connections.ip LIKE ? AND Connections.port = ? AND Protocols.name LIKE ? AND Structs.code= ?";
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, ip);
		prpStmt.setInt(2, port);
		prpStmt.setString(3, protocolName);
		prpStmt.setInt(4, structCode);
		rs=prpStmt.executeQuery();
		while(rs.next())
		{
			structFields.add(new StructFieldsTable(rs.getString("fieldName"),rs.getString("type"),rs.getString("minRange"),rs.getString("maxRange")));
		}
		rs.close();
		prpStmt.close();
		return structFields;
	}
}
