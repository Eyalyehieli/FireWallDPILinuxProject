package packetsNetFilterDB;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.sqlite.SQLiteConfig;
public class SqliteDB 
{
	//---------properties---------//
	private static SqliteDB sqlitedb=null;
	private Connection con;
	private int transactionLevel = 0;
	
	//-----------C'tor----------------//
	public SqliteDB(String url) throws SQLException
	{
		con=DriverManager.getConnection(url);
		con.createStatement().execute("PRAGMA foreign_keys = ON");
	}
	//-------------singleton implementation-------------//
	public synchronized static SqliteDB getSqliteDBInstance() throws SQLException
	{
		if(sqlitedb==null)
		{
			sqlitedb=new SqliteDB("jdbc:sqlite:"+System.getProperty("user.home")+"/Desktop/FireWallProject/FireWallDPILinuxProject/packetsNetFilterDB/netFilterDB.sqlite");
		}
		return sqlitedb;
	}
	
	//---------------functions-------------------//
	public boolean createTable(String tableName,String fields)throws SQLException
	{
		boolean res=false;
		try {
		Statement stmt=con.createStatement();
		con.setAutoCommit(true);
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+tableName+" ("+fields+")");
		res= true;
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return res;
	}
	
	//
	public String[] GetTypes()
	{
		String[] types= {"INT","CHAR","FLOAT","DOUBLE","SHORT","LONG"};
		return types;
	}
	
	
	public ArrayList<FIreWallRulesTable> GetAllFireWallRules() throws SQLException
	{
		ArrayList<FIreWallRulesTable> fireWallRules=new ArrayList<FIreWallRulesTable>();
		ProtocolTable protocol;
		ConnectionTable connection;
		String query="SELECT ip,port,p.name as protocolName,activeStatus\n" + 
				"FROM FireWallRules fireWallR INNER JOIN Protocols p\n" + 
				"ON fireWallR.protocol_id=p.id\n" + 
				"INNER JOIN Connections c\n" + 
				"ON fireWallR.connection_id=c.id";
		ResultSet rs;
		Statement stmt=con.createStatement();
		rs=stmt.executeQuery(query);
		while(rs.next())
		{
			protocol=new ProtocolTable(rs.getString("protocolName"));
			connection=new ConnectionTable(rs.getString("ip"),rs.getInt("port"));
			fireWallRules.add(new FIreWallRulesTable(rs.getInt("activeStatus"),protocol,connection));
		}
		stmt.close();
		rs.close();
		return fireWallRules;
	}
	
	public void DeleteAllFireWallRulesOfProtocol(ProtocolTable protocol) throws SQLException
	{
		String deleteQuery="DELETE FROM FireWallRules\n" + 
				"WHERE protocol_id=?";
		PreparedStatement prpStmt=con.prepareStatement(deleteQuery);
		prpStmt.setInt(1, protocol.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void DeleteAllFireWallRulesOfConnection(ConnectionTable connection) throws SQLException
	{
		String deleteQuery="DELETE FROM FireWallRules\n" + 
				"WHERE connection_id=?";
		PreparedStatement prpStmt=con.prepareStatement(deleteQuery);
		prpStmt.setInt(1, connection.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void DeleteFireWallRuleById(int fireWallRuleId) throws SQLException
	{
		String deleteQuery="DELETE FROM FireWallRules\n" + 
				"WHERE id=?";
		String getProtocolQuery="SELECT protocol_id\n" + 
				"FROM FireWallRules\n" + 
				"WHERE id=?";
		String getConnectionQuery="SELECT connection_id\n" + 
				"FROM FireWallRules\n" + 
				"WHERE id=?";
		ResultSet rsProtocols,rsConnections;
		
		PreparedStatement prpStmt=con.prepareStatement(getProtocolQuery);
		prpStmt.setInt(1, fireWallRuleId);
		rsProtocols=prpStmt.executeQuery();
		prpStmt=con.prepareStatement(getConnectionQuery);
		prpStmt.setInt(1, fireWallRuleId);
		rsConnections=prpStmt.executeQuery();
		
		prpStmt=con.prepareStatement(deleteQuery);
		prpStmt.setInt(1, fireWallRuleId);
		prpStmt.executeUpdate();
		
		/*while(rsProtocols.next())
		{
			DeleteProtocolById(rsProtocols.getInt("protocol_id"));
		}
		*/
		/*
		while(rsConnections.next())
		{
			DeleteConnectionById(rsConnections.getInt("connection_id"));
		}
		*/
		prpStmt.close();
		rsProtocols.close();
		rsConnections.close();
	}
	
	public void DeleteConnectionById(int connectionId) throws SQLException
	{
		String query="DELETE FROM Connections\n" + 
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, connectionId);
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void DeleteProtocolById(int protocolId) throws SQLException
	{
		String deleteQuery="DELETE FROM Protocols\n" + 
				"WHERE id=?";
		String getStructsQuery="SELECT id \n" + 
				"FROM Structs\n" + 
				"WHERE protocol_id=?";
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(getStructsQuery);
		prpStmt.setInt(1, protocolId);
		rs=prpStmt.executeQuery();
		while(rs.next())
		{
			DeleteStructById(rs.getInt("id"));
		}
		prpStmt=con.prepareStatement(deleteQuery);
		prpStmt.setInt(1, protocolId);
		prpStmt.executeUpdate();
		prpStmt.close();
		rs.close();
	}
	
	public void DeleteStructFieldById(int structFieldId) throws SQLException
	{
		String query="DELETE FROM StructFields\n"+
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, structFieldId);
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void DeleteStructById(int structId) throws SQLException
	{
		String deleteQuery="DELETE FROM Structs\n" + 
				"WHERE id=?";
		String getStructFieldsQuery="SELECT id \n" + 
				"FROM StructFields\n" + 
				"WHERE struct_id=?";
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(getStructFieldsQuery);
		prpStmt.setInt(1, structId);
		rs=prpStmt.executeQuery();
		while(rs.next())
		{
			DeleteStructFieldById(rs.getInt("id"));
		}
		prpStmt=con.prepareStatement(deleteQuery);
		prpStmt.setInt(1, structId);
		prpStmt.executeUpdate();
		prpStmt.close();
		rs.close();
	}
	
	public ArrayList<StructsTable> GetAllStructsByProtocolId(ProtocolTable protocol) throws SQLException
	{
		ArrayList<StructsTable> structs=new ArrayList<StructsTable>();
		String query="SELECT code,name,size\n" + 
				"FROM Structs\n" + 
				"WHERE protocol_id=?";
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, protocol.getId());
		rs=prpStmt.executeQuery();
		while(rs.next())
		{
			structs.add(new StructsTable(rs.getString("name"),rs.getInt("code"),rs.getInt("size"),protocol));
		}
		rs.close();
		prpStmt.close();
		return structs;
	}
	
	public ArrayList<StructsFieldsTable>GetAllStructFieldsByStructId(StructsTable struct) throws SQLException
	{
		ArrayList<StructsFieldsTable> structFields=new ArrayList<StructsFieldsTable>();
		String query="SELECT fieldName,type,minRange,maxRange\n" + 
				"FROM StructFields\n" + 
				"WHERE struct_id=?";
		ResultSet rs;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, struct.getId());
		rs=prpStmt.executeQuery();
		while(rs.next())
		{
			structFields.add(new StructsFieldsTable(rs.getString("fieldName"),rs.getString("type"),rs.getString("minRange"),rs.getString("maxRange"),struct));
		}
		rs.close();
		prpStmt.close();
		return structFields;
	}
	
	public int createProtocolRow(String protocolName) throws SQLException
	{
		String queryInsert="INSERT INTO Protocols VALUES (NULL,?)";
		String querySelectLastId="SELECT MAX(id) as lastInsertionId FROM Protocols";
		ResultSet rs;
		int id;
		PreparedStatement prpStmt=con.prepareStatement(queryInsert);
		Statement stmt=con.createStatement();
		prpStmt.setString(1, protocolName);
		prpStmt.executeUpdate();
		prpStmt.close();
		
		rs = stmt.executeQuery(querySelectLastId);
		rs.next();
		id= rs.getInt("lastInsertionId");
		stmt.close();
		rs.close();
		return id;	
	}
	
	public int createConnnectionRow(String ip,int port) throws SQLException
	{
		String queryInsert="INSERT INTO Connections VALUES (NULL,?,?)";
		String querySelectLastId="SELECT MAX(id) as lastInsertionId FROM Connections";
		ResultSet rs;
		int id;
		PreparedStatement prpStmt=con.prepareStatement(queryInsert);
		Statement stmt=con.createStatement();
		prpStmt.setString(1, ip);
		prpStmt.setInt(2, port);
		prpStmt.executeUpdate();
		prpStmt.close();
		
		rs = stmt.executeQuery(querySelectLastId);
		rs.next();
		id= rs.getInt("lastInsertionId");
		stmt.close();
		rs.close();
		return id;
		
	}
	
	public int createStrcutsRow(StructsTable struct) throws SQLException
	{
		String queryInsert="INSERT INTO Structs VALUES (NULL,?,?,?,?)";
		String querySelectLastId="SELECT MAX(id) as lastInsertionId FROM Structs";
		ResultSet rs;
		int id;
		PreparedStatement prpStmt=con.prepareStatement(queryInsert);
		Statement stmt=con.createStatement();
		prpStmt.setInt(1, struct.getCode());
		prpStmt.setString(2, struct.getName());
		prpStmt.setInt(3, struct.getSize());
		prpStmt.setInt(4, struct.getProtocol().getId());
		prpStmt.executeUpdate();
		prpStmt.close();
		
		rs = stmt.executeQuery(querySelectLastId);
		rs.next();
		id=rs.getInt("lastInsertionId");
		stmt.close();
		rs.close();
		return id;
	}
	
	public int createStructFieldRow(StructsFieldsTable structField) throws SQLException
	{
		String queryInsert="INSERT INTO StructFields VALUES (NULL,?,?,?,?,?)";
		String querySelectLastId="SELECT MAX(id) as lastInsertionId FROM StructFields";
		ResultSet rs;
		int id;
		PreparedStatement prpStmt=con.prepareStatement(queryInsert);
		Statement stmt=con.createStatement();
		prpStmt.setString(1, structField.getName());
		prpStmt.setString(2,structField.getType() );
		prpStmt.setString(3, structField.getMinRange());
		prpStmt.setString(4, structField.getMaxRange());
		prpStmt.setInt(5, structField.getStruct().getId());
		prpStmt.executeUpdate();
		
		rs = stmt.executeQuery(querySelectLastId);
		rs.next();
		id= rs.getInt("lastInsertionId");
		rs.close();
		return id;
	}
	
	public int createFireWallRulesRow(FIreWallRulesTable fireWallRules) throws SQLException
	{
		String queryInsert="INSERT INTO FireWallRules VALUES (NULL,?,?,?)";
		String querySelectLastId="SELECT MAX(id) as lastInsertionId FROM FireWallRules";
		ResultSet rs;
		int id;
		PreparedStatement prpStmt=con.prepareStatement(queryInsert);
		Statement stmt=con.createStatement();
		prpStmt.setInt(1,fireWallRules.getActiveStatus());
		prpStmt.setInt(2, fireWallRules.getConnection().getId());
		prpStmt.setInt(3, fireWallRules.getProtocol().getId());
		prpStmt.executeUpdate();
		
		rs = stmt.executeQuery(querySelectLastId);
		rs.next();
		id= rs.getInt("lastInsertionId");
		rs.close();
		return id;
	}
	
	public int GetFireWallRulesId(FIreWallRulesTable fireWallRules,Boolean IsToForceInsertion) throws SQLException
	{
		ResultSet rs;
		int id;
		String query="SELECT id\n" + 
				"FROM FireWallRules\n" + 
				"where connection_id = ? AND protocol_id = ?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, fireWallRules.getConnection().getId());
		prpStmt.setInt(2,fireWallRules.getProtocol().getId());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createFireWallRulesRow(fireWallRules);
		}
		rs.close();
		return id;
	}
	
	public int GetStructFieldId(StructsFieldsTable structField,Boolean IsToForceInsertion) throws SQLException
	{
		ResultSet rs;
		int id;
		String query="SELECT id\n" + 
				"FROM StructFields\n" + 
				"where fieldName LIKE ? AND struct_id =?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, structField.getName());
		prpStmt.setInt(2,structField.getStruct().getId());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createStructFieldRow(structField);
		}
		rs.close();
		return id;
	}
	
	public int GetStructIdByCodeAndProtocol(StructsTable struct,Boolean IsToForceInsertion,Boolean IsChangeSize) throws SQLException
	{
		ResultSet rs;
		int id,size=0;
		String query="SELECT id\n" + 
				"FROM Structs\n" + 
				"where code = ? AND protocol_id = ?";
		String updateSizeQuery="UPDATE Structs \n" +
				"SET size=? \n" +
				"WHERE code=? And protocol_id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, struct.getCode());
		prpStmt.setInt(2, struct.getProtocol().getId());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
			if(IsChangeSize==true)
			{
				size=this.GetStructSizeByStructCodeAndProtocol(struct.getCode(),struct.getProtocol().getId());
				size+=struct.getSize();
				prpStmt=con.prepareStatement(updateSizeQuery);
				prpStmt.setInt(1, size);
				prpStmt.setInt(2, struct.getCode());
				prpStmt.setInt(3, struct.getProtocol().getId());
				prpStmt.executeUpdate();
			}
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createStrcutsRow(struct);
		}
		prpStmt.close();
		rs.close();
		return id;
	}
	
	public int GetConnectionIdByIpAndPort(ConnectionTable connection,Boolean IsToForceInsertion) throws SQLException
	{
		ResultSet rs;
		int id;
		String query="SELECT id\n" + 
				"FROM Connections\n" + 
				"where ip LIKE ? AND port = ?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, connection.getIp());
		prpStmt.setInt(2, connection.getPort());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createConnnectionRow(connection.getIp(),connection.getPort());
		}
		rs.close();
		return id;
	}
	
	public int GetProtocolIdByProtocolName(ProtocolTable protocol,int port,Boolean IsToForceInsertion) throws SQLException
	{
		ResultSet rs;
		int id;
		String query="SELECT Protocols.id\n" + 
				"FROM Protocols \n" + 
				"INNER JOIN FireWallRules\n" + 
				"ON Protocols.id=FireWallRules.protocol_id\n" + 
				"INNER JOIN Connections\n" + 
				"ON Connections.id=FireWallRules.connection_id\n" + 
				"WHERE Protocols.name LIKE ? AND Connections.port = ?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, protocol.getName());
		prpStmt.setInt(2, port);
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
			
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createProtocolRow(protocol.getName());
		}
		rs.close();
		prpStmt.close();
		return id;
	}
	

	public int GetProtocolIdByProtocolName(ProtocolTable protocol,Boolean IsToForceInsertion) throws SQLException
	{
		ResultSet rs;
		int id;
		String query="SELECT Protocols.id\n" + 
				"FROM Protocols \n" + 
				"WHERE Protocols.name LIKE ?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, protocol.getName());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			id= rs.getInt("id");
			
		}
		else
		{
			if(IsToForceInsertion==false)return -1;
			id= createProtocolRow(protocol.getName());
		}
		rs.close();
		prpStmt.close();
		return id;
	}
	
	
	
	public int GetStructSizeByStructCodeAndProtocol(int code ,int protocol_id) throws SQLException
	{
		String query="SELECT size \n" + 
				"FROM Structs\n" + 
				"WHERE code=? AND protocol_id=?";
		ResultSet rs;
		int size;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, code);
		prpStmt.setInt(2, protocol_id);
		rs=prpStmt.executeQuery();
		rs.next();
		size=rs.getInt("size");
		prpStmt.close();
		rs.close();
		return size;
	}
	
	public String getProtocolNameByConnectionId(int connection_id) throws SQLException
	{
		ResultSet rs;
		String protocolName;
		String query="SELECT name \n" + 
				"FROM FireWallRules fireWallR INNER JOIN Protocols p\n" + 
				"ON fireWallR.protocol_id=p.id\n" + 
				"INNER JOIN Connections c \n"+ 
				"ON fireWallR.connection_id=c.id\n" +
				"WHERE c.id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, connection_id);
		rs=prpStmt.executeQuery();
		//if(!rs.isClosed())
		//{
		rs.next();
		protocolName=rs.getString("name");
		//}
		prpStmt.close();
		rs.close();
		return protocolName;
	}
	
	public String getIpByPort(int port) throws SQLException
	{
		ResultSet rs;
		String ip;
		String query="SELECT ip \n" + 
				"FROM Connections\n"+
				"WHERE port=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, port);
		rs=prpStmt.executeQuery();
		//if(!rs.isClosed())
		//{
		rs.next();
		ip=rs.getString("ip");
		//}
		prpStmt.close();
		rs.close();
		return ip;
	}
	
	public int getPortByProtocolId(int protocol_id) throws SQLException
	{
		ResultSet rs;
		int port;
		String query="SELECT port \n" + 
				"FROM FireWallRules fireWallR INNER JOIN Protocols p\n" + 
				"ON fireWallR.protocol_id=p.id\n" + 
				"INNER JOIN Connections c \n"+ 
				"ON fireWallR.connection_id=c.id\n" +
				"WHERE p.id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, protocol_id);
		rs=prpStmt.executeQuery();
		//if(!rs.isClosed())
		//{
		rs.next();
		port=rs.getInt("port");
		//}
		prpStmt.close();
		rs.close();
		return port;
	}
	
	
	public void updateProtocol(ProtocolTable protocol,String newName) throws SQLException
	{
		String query="UPDATE Protocols \n" +
				"SET name=?\n" +
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1,newName);
		prpStmt.setInt(2,protocol.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void updatePort(ConnectionTable connection,int port) throws SQLException
	{
		String query="UPDATE Connections \n" +
				"SET port=?\n" +
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1,port);
		prpStmt.setInt(2,connection.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	
	}
	public void updateFireWallRule(FIreWallRulesTable fireWallRule, int activeStatus) throws SQLException
	{
		String query="UPDATE FireWallRules \n" +
				"SET activeStatus=?\n" +
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1,activeStatus);
		prpStmt.setInt(2,fireWallRule.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void updateStruct(String structName,int structCode,int protocol_id,int oldStructCode,String oldStrcutName) throws SQLException
	{
		String query="UPDATE Structs \n" + 
				"SET code=?,name=?\n" + 
				"WHERE code=? AND protocol_id=? AND name LIKE ?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1,structCode);
		prpStmt.setString(2,structName);
		prpStmt.setInt(3,oldStructCode);
		prpStmt.setInt(4,protocol_id);
		prpStmt.setString(5,oldStrcutName);
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void updateStrcutNameByStructCodeAndProtocolId(ProtocolTable protocol,int strcutCode,String name) throws SQLException
	{
		String query="UPDATE Structs \n" + 
				"SET name=?\n" + 
				"WHERE code=? AND protocol_id=?"; 
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1,name);
		prpStmt.setInt(2,strcutCode);
		prpStmt.setInt(3,protocol.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	public void updateStructCodeByStrcutNameAndProtocolId(ProtocolTable protocol,String strcutName,int code) throws SQLException
	{
		String query="UPDATE Structs \n" + 
				"SET code=?\n" + 
				"WHERE name=? AND protocol_id=?"; 
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1,code);
		prpStmt.setString(2,strcutName);
		prpStmt.setInt(3,protocol.getId());
		prpStmt.executeUpdate();
		prpStmt.close();	
	}
	
	public void updateProtocolAndConnection(ProtocolTable protocol,ConnectionTable connection,String protocol_name,String new_ip,int new_port) throws SQLException
	{
		String queryProtocol="UPDATE Protocols \n" +
				"SET name=? \n" +
				"WHERE id=?";
		String queryConnecion="UPDATE Connections\n" +
				"SET ip=?, port=?\n" +
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(queryProtocol);
		prpStmt.setString(1,protocol_name);
		prpStmt.setInt(2,protocol.getId());
		prpStmt.executeUpdate();
		
		prpStmt=con.prepareStatement(queryConnecion);
		prpStmt.setString(1, new_ip);
		prpStmt.setInt(2, new_port);
		prpStmt.setInt(3,connection.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public void updateStructField(int structField_id,String fieldName,String type,String minRange,String maxRange) throws SQLException
	{
		String query="UPDATE StructFields  \n" + 
				"SET fieldName=?,type=?,minRange=?,maxRange=?\n" + 
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1,fieldName);
		prpStmt.setString(2,type);
		prpStmt.setString(3,minRange);
		prpStmt.setString(4,maxRange);
		prpStmt.setInt(5,structField_id);
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public Boolean IsExistValidationFireWallRUle(ProtocolTable protocol,ConnectionTable connection) throws SQLException
	{
		String query="SELECT *\n" + 
				"FROM Protocols\n" + 
				"INNER JOIN FireWallRules\n" + 
				"ON Protocols.id=FireWallRules.protocol_id\n" + 
				"INNER JOIN Connections\n" + 
				"ON Connections.id=FireWallRules.connection_id\n" + 
				"WHERE Protocols.id=? AND Connections.id=?";
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, protocol.getId());
		prpStmt.setInt(2, connection.getId());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			flag=true;
			//System.out.println("THere is protocol");
		}
		
		rs.close();
		prpStmt.close();
		return flag;
	}
	public Boolean IsExsistValidatioProtocol(ProtocolTable protocol) throws SQLException
	{
		//System.out.println("start to check protocol");
		String query="SELECT *\n" + 
				"FROM Protocols\n" + 
				"WHERE Protocols.name LIKE ?";
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setString(1, protocol.getName());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			flag=true;
			//System.out.println("THere is protocol");
		}
		
		rs.close();
		prpStmt.close();
		return flag;
	}
	public Boolean IsExsistValidationConnection(ConnectionTable connection) throws SQLException
	{
		//System.out.println("start to check port");
		String query="SELECT *\n" + 
				"FROM Connections\n" + 
				"WHERE Connections.port=?";//check only the port because the port represents app, and its wrong for tow apps
										   //to have the same port(port==app)
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, connection.getPort());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			//System.out.println("There is port");
			flag=true;
		}
		
		rs.close();
		prpStmt.close();
		return flag;
	}
	public Boolean IsExistValidationStrcutName(StructsTable struct) throws SQLException
	{
		String query="SELECT *\n" + 
				"FROM Structs \n" + 
				"INNER JOIN Protocols\n" + 
				"ON Structs.protocol_id=Protocols.id\n" + 
				"WHERE protocol_id = ? AND Structs.name LIKE ?";
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, struct.getProtocol().getId());
		prpStmt.setString(2, struct.getName());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			//System.out.println("There is port");
			flag=true;
		}
		
		rs.close();
		prpStmt.close();
		return flag;
		
	}
	public Boolean IsExistValidationStrcutCode(StructsTable struct) throws SQLException
	{
		String query="SELECT *\n" + 
				"FROM Structs \n" + 
				"INNER JOIN Protocols\n" + 
				"ON Structs.protocol_id=Protocols.id\n" + 
				"WHERE protocol_id = ? AND Structs.code = ?";
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, struct.getProtocol().getId());
		prpStmt.setInt(2, struct.getCode());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			//System.out.println("There is port");
			flag=true;
		}
		
		rs.close();
		prpStmt.close();
		return flag;
	}
	public Boolean IsExistValidationStructFieldName(StructsFieldsTable StructField) throws SQLException
	{
		String query="SELECT *\n" + 
				"FROM Protocols\n" + 
				"INNER JOIN Structs\n" + 
				"ON Protocols.id=Structs.protocol_id\n" + 
				"INNER JOIN StructFields\n" + 
				"ON Structs.id=StructFields.struct_id\n" + 
				"WHERE Protocols.id= ? AND Structs.id = ? AND StructFields.fieldName LIKE ? AND StructFields.type LIKE ? \n"+
				"AND StructFields.minRange LIKE ? AND StructFields.maxRange LIKE ?";
		ResultSet rs;
		Boolean flag=false;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, StructField.getStruct().getProtocol().getId());
		prpStmt.setInt(2, StructField.getStruct().getId());
		prpStmt.setString(3, StructField.getName());
		prpStmt.setString(4, StructField.getType());
		prpStmt.setString(5, StructField.getMinRange());
		prpStmt.setString(6, StructField.getMaxRange());
		rs=prpStmt.executeQuery();
		rs.next();
		if(rs.getRow()>0)
		{
			//System.out.println("There is port");
			flag=true;
		}
		
		rs.close();
		prpStmt.close();
		return flag;
	}
	
	public String getStructNameByProtocolAndStructCode(ProtocolTable protocol,int structCode) throws SQLException
	{
		String query="SELECT name \n" + 
				"FROM Structs\n" + 
				"WHERE code=? AND protocol_id=?";
		ResultSet rs;
		String structName;
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1, structCode);
		prpStmt.setInt(2, protocol.getId());
		rs=prpStmt.executeQuery();
		rs.next();
		structName=rs.getString("name");
		prpStmt.close();
		rs.close();
		return structName;
	}
	
	public void updateStructSize(StructsTable struct,int size,int sizeToSub) throws SQLException
	{
		String query="UPDATE Structs \n" +
				"SET size=?\n" +
				"WHERE id=?";
		PreparedStatement prpStmt=con.prepareStatement(query);
		prpStmt.setInt(1,size-sizeToSub);
		prpStmt.setInt(2,struct.getId());
		prpStmt.executeUpdate();
		prpStmt.close();
	}
	
	public ArrayList<String> getAllProtocols() throws SQLException
	{
		ArrayList<String> protocols=new ArrayList<String>();
		
		String query="SELECT name\n" + 
				"FROM Protocols";
		ResultSet rs;
		Statement stmt=con.createStatement();
		rs=stmt.executeQuery(query);
		while(rs.next())
		{
			protocols.add(rs.getString("name"));
		}
		stmt.close();
		rs.close();
		return protocols;
	}
	public ArrayList<ConnectionTable> getAllConnections() throws SQLException
	{
		ArrayList<ConnectionTable> ports=new ArrayList<ConnectionTable>();
		
		String query="SELECT ip,port \n" + 
				"FROM Connections";
		ResultSet rs;
		Statement stmt=con.createStatement();
		rs=stmt.executeQuery(query);
		while(rs.next())
		{
			ports.add(new ConnectionTable(rs.getString("ip"),rs.getInt("port")));
		}
		stmt.close();
		rs.close();
		return ports;
	}
	
	public static void sendSignalToNfqFIreWall()
	{
		String[] killCmd= {"/bin/bash","-c","echo Eyal1234| sudo -S pkill -SIGUSR1 nfq"};
		Process pb=null;
		 try 
		 {
			 pb = Runtime.getRuntime().exec(killCmd);
			 Thread.sleep(20);
		 } 
		 catch (IOException|InterruptedException e)  {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		pb.destroy();
	}
	
	public void beginTransaction() throws SQLException 
	{
		transactionLevel += 1;
		con.setAutoCommit(false);
	}
	
	public void commitTransaction() throws SQLException 
	{
		if(transactionLevel==0)
		{
			throw new SQLException("You are not it transaction");
		}
		transactionLevel -= 1;
		if (transactionLevel == 0) { 
			con.commit();
			con.setAutoCommit(true);
		}
	}
	
	public void rollbackTransaction() throws SQLException 
	{
		transactionLevel = 0;
		con.rollback();
		con.setAutoCommit(true);
	}
	
}

