package packetsNetFilterDB;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
public class MainGUI extends GUI {
	//------------properties---------------//
	SqliteDB sqlitedb;
	int selectedColumn;
	int selectedRow;
	ArrayList<FIreWallRulesTable> fireWallRules;
	
	//----------C'tor-----------------//
	public MainGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	

	public String[] convertConnectionsArrayListToArray(ArrayList<ConnectionTable> connections)
	{
		String[] connectionsArray=new String[connections.size()];
		for(int i=0;i<connectionsArray.length;i++)
		{
			connectionsArray[i]=connections.get(i).toString();
		}
		return connectionsArray;
	}

	public String[] convertProtocolsArrayListToArray(ArrayList<String>protocols)
	{
		String[] protocolsArray=new String[protocols.size()];
		for(int i=0;i<protocolsArray.length;i++)
		{
			protocolsArray[i]=protocols.get(i).toString();
		}
		return protocolsArray;
	}
	//-----------functions-----------------//
	@Override
	public void createGUI(JFrame frame) throws SQLException
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=getFireWallTableModel();
		
		//-------create db tables if do not exist-----------//
		sqlitedb.createTable("Protocols","id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(255) NOT NULL");
		sqlitedb.createTable("Connections","id INTEGER PRIMARY KEY AUTOINCREMENT,ip varchar(255) NOT NULL,port INTEGER NOT NULL");
		sqlitedb.createTable("Structs", "id INTEGER PRIMARY KEY AUTOINCREMENT,code INTEGER NOT NULL ,name varchar(255) NOT NULL,size INTEGER NOT NULL,protocol_id INTEGER NOT NULL,FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		sqlitedb.createTable("StructFields","id INTEGER PRIMARY KEY AUTOINCREMENT,fieldName varchar(255) NOT NULL,type varchar(255) NOT NULL,minRange varchar(255) NOT NULL,maxRange varchar(255) NOT NULL,struct_id INTEGER NOT NULL,FOREIGN KEY(struct_id) REFERENCES Structs(id)");
		sqlitedb.createTable("FireWallRules","id INTEGER PRIMARY KEY AUTOINCREMENT,activeStatus INTEGER NOT NULL,connection_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL,FOREIGN KEY(connection_id) REFERENCES Connections(id),FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		
		String[] columns= {"Ip","Port","Protocol","Status"};
		
		ArrayList<String> protocols=sqlitedb.getAllProtocols();
		JComboBox protocolsComboBox= super.createComboBox(this.frm, convertProtocolsArrayListToArray(protocols), 800, 300, 100, 100);
		protocolsComboBox.setSelectedItem(protocolsComboBox.getItemAt(0));
		ArrayList<ConnectionTable>connections=sqlitedb.getAllConnections();
		JComboBox connectionsComboBox=super.createComboBox(this.frm, convertConnectionsArrayListToArray(connections),950, 300, 200, 100);
		connectionsComboBox.setSelectedItem(connectionsComboBox.getItemAt(0));
		JButton doneButton=super.createButton(this.frm,"Done", 875,500 ,200 ,100 );
		JLabel protocolsLabel=super.createLabel(this.frm,"Protocols:", 800, 200, 100, 100);
		JLabel connectionsLabel=super.createLabel(this.frm, "Connections:", 950,200 ,100 , 100);
		JLabel fireWallLabel=super.createLabel(this.frm, "Fire Wall Rules", 400, 50, 200, 100);
		JButton AddButton=super.createButton(this.frm, "Add Rule",950, 200, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove Rule",950, 400, 200, 100);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		JTable fireWallTableRules=super.createTable(this.frm,model,20,150,900,600);
		showExistFireWallRule(fireWallTableRules);
		
		protocolsComboBox.setVisible(false);
		connectionsComboBox.setVisible(false);
		doneButton.setVisible(false);
		protocolsLabel.setVisible(false);
		connectionsLabel.setVisible(false);
		
		//----------Event functions-----------//
		
		doneButton.addActionListener(new ActionListener()
		 {
			public void actionPerformed(ActionEvent e)
			 {
				String protocolName=protocolsComboBox.getSelectedItem().toString();
				String[] connectionAsStringArray=connectionsComboBox.getSelectedItem().toString().split(",");
				ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName, false, sqlitedb);
				ConnectionTable connection=ConnectionTable.getConnectionFromDB(connectionAsStringArray[0], Integer.valueOf(connectionAsStringArray[1]), false, sqlitedb);
				
				if(checkValidationFireWall(protocol,connection)==false)
				{
				//-----The insertion of the FireWall Rule--------//
					FIreWallRulesTable fireWallRule=FIreWallRulesTable.getFIreWallRule(protocol, connection, 1, true, sqlitedb);
					JOptionPane.showMessageDialog(null, "Successfuly created");
					SqliteDB.sendSignalToNfqFIreWall();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "The Rule Already Exists !");
				}
				AddButton.setVisible(true);
				RemoveButton.setVisible(true);
				protocolsComboBox.setVisible(false);
				connectionsComboBox.setVisible(false);
				doneButton.setVisible(false);
				protocolsLabel.setVisible(false);
				connectionsLabel.setVisible(false);
			 }
		 });
		
		AddButton.addActionListener(new ActionListener()
		 {
			public void actionPerformed(ActionEvent e)
			 {
				AddButton.setVisible(false);
				RemoveButton.setVisible(false);
				protocolsComboBox.setVisible(true);
				connectionsComboBox.setVisible(true);
				doneButton.setVisible(true);
				protocolsLabel.setVisible(true);
				connectionsLabel.setVisible(true);
			 }
		 });
		
		fireWallTableRules.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		       
		    	//-----check just for updating for delete or insert don't execute the code--------//
		    	if(e.getType()!=e.UPDATE)
		    	{
		    		return;
		    	}
		    	
		    	//---------get the vars of the selected rule for any action----------//
		        selectedRow=fireWallTableRules.getSelectedRow();
		        selectedColumn=fireWallTableRules.getSelectedColumn();
		        String ip=fireWallTableRules.getModel().getValueAt(selectedRow, 0).toString();
				String protocolName=fireWallTableRules.getModel().getValueAt(selectedRow, 2).toString();
				int port=Integer.valueOf((fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
				
				//------if port or ip has changed--------//
				if(selectedColumn==0||selectedColumn==1)
				 {
					changeConnection(protocolName,ip,port);
				 }
				
				//-----if the rule status has changed------//
				else if(selectedColumn==3)
				 {

					changeActiveStatus(fireWallTableRules,protocolName,ip,port);
				 }
		    }
		    
		});
		
		RefreshButton.addActionListener(new ActionListener()
		 {
			public void actionPerformed(ActionEvent e)
			 {
				try 
				 {
					//---------get the exist rules and refresh----//
					fireWallRules=sqlitedb.GetAllFireWallRules();
					Refresh.refreshFireWallTable(fireWallTableRules, fireWallRules);
				 } 
				catch (SQLException e1)
				 {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
			 }
		 });
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//-------get the values of the rmoved rule to delete------//
				int fireWallRule_id;
				int selected_row=fireWallTableRules.getSelectedRow();
				String ip=fireWallTableRules.getValueAt(selected_row, 0).toString();
				int port=Integer.valueOf(fireWallTableRules.getValueAt(selected_row, 1).toString());
				String protocolName=fireWallTableRules.getValueAt(selected_row, 2).toString();
				int activeStatus=fireWallTableRules.getModel().getValueAt(selectedRow, 3).equals(Boolean.TRUE) ? 1 : 0;
				
				removeFireWallRule(ip,port,protocolName,activeStatus,fireWallTableRules);
				
			}
		});
	
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	
	
	public boolean checkValidationFireWall(ProtocolTable protocol,ConnectionTable connection)
	{
		try 
		{
			return sqlitedb.IsExistValidationFireWallRUle(protocol, connection);
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void updateDbAboutPort(String protocolName,String ip,int port)
	{
		try 
	   	 {
			//----- update the db about the new protocol-------//
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
			ConnectionTable connection=ConnectionTable.getConnectionFromDB(sqlitedb.getIpByPort(port),sqlitedb.getPortByProtocolId(protocol.getId()), false, sqlitedb);
			sqlitedb.updateProtocolAndConnection(protocol, connection, protocolName, ip, port);		
			SqliteDB.sendSignalToNfqFIreWall();
	   	 } 
		catch (SQLException e1) 
		 {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 }
	}
	
	public void changePort(String protocolName,String ip,int port)
	{
		ConnectionTable connectionToCheck=new ConnectionTable(ip,port);
		//----The user change the port so need to check is the port to change to isn't occupied---------//
		if(checkValidationPort(connectionToCheck)==true)
	     {
			updateDbAboutPort(protocolName,ip,port);
		 }
		else
		 {
			//----- if the port to change to is occupied, announce about it
			JOptionPane.showMessageDialog(null, "There is no connection like this,try again!");
		 }
	}
	
	 public void updateDbAboutProtocolName(String protocolName,String ip,int port)
	 {
		 try
		 {
			ConnectionTable connection=ConnectionTable.getConnectionFromDB(ip, port, false, sqlitedb);
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(sqlitedb.getProtocolNameByConnectionId(connection.getId()),false, sqlitedb);
			sqlitedb.updateProtocolAndConnection(protocol, connection, protocolName, ip, port);
			SqliteDB.sendSignalToNfqFIreWall();
		 } 
		catch (SQLException e1) 
		 {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 }
	 }
	 
	public void changeConnection(String protocolName,String ip,int port)
	{
		if(selectedColumn==0)
		{
			JOptionPane.showMessageDialog(null, "Do not change the ip!!!");
		}
		//------else means that the selectedColumn=1(port changed)--------//
		else
		{
			changePort(protocolName,ip,port);
		}
	}
	
	public void updateActiveStatus(String protocolName,String ip,int port,int currentStatus)
	{
		try 
		 {
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
			ConnectionTable connection=ConnectionTable.getConnectionFromDB(ip, port, false, sqlitedb);    
			//----create rule with currentStatus  because before changing it has the currentStatus-------//
			FIreWallRulesTable fireWallRule=FIreWallRulesTable.getFIreWallRule(protocol, connection,1-currentStatus,false, sqlitedb);
			//-----after changing the rule will be the opposite to the currentStatus--------//
			sqlitedb.updateFireWallRule(fireWallRule,currentStatus);
			SqliteDB.sendSignalToNfqFIreWall();
		 } 
		catch (SQLException e1)
		 {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 }
	}
	
	public void changeActiveStatus(JTable fireWallTableRules,String protocolName,String ip,int port)
	{
		//------check if the status changed to be active------//
		if(isStatusChangedToBeAtive(fireWallTableRules))
		 {
			//-----the status changed to be active------//
			updateActiveStatus(protocolName,ip,port,1);
		 }
		//-------in this section of code the status will change to be unActive--------//
		else  
		 {
			updateActiveStatus(protocolName,ip,port,0);
		 }
	}
	public boolean isStatusChangedToBeAtive(JTable fireWallTableRules)
	{
		return fireWallTableRules.getValueAt(selectedRow, 3).equals(Boolean.TRUE);
	}
	
	public DefaultTableModel getFireWallTableModel()
	{
		return new DefaultTableModel(new Object[]{"Ip", "Port","Protocol","Status"},0)
		{
		 @Override
         public Class getColumnClass(int columnIndex) {
            switch(columnIndex)
            {
            	case 0: return String.class;
            	case 1: return Integer.class;
            	case 2: return String.class;
            	case 3: return Boolean.class;
            	default: return null;
            }
         }};
	}
	
	public void showExistFireWallRule(JTable fireWallTableRules)
	{
		try 
		{
			Boolean isActive;
			fireWallRules=sqlitedb.GetAllFireWallRules();
			for(FIreWallRulesTable fireWallRule:fireWallRules)
			{
				
				if(fireWallRule.getActiveStatus()==1)
				{
					isActive=true;
				}
				else
				{
					isActive=false;
				}
				 ((DefaultTableModel) fireWallTableRules.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName(),isActive});
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void removeFireWallRule(String ip,int port,String protocolName,int activeStatus,JTable fireWallTableRules)
	{
		try 
		{
			//----get the proper objects to which represented the FireWallRule-----//
			ConnectionTable connection=ConnectionTable.getConnectionFromDB(ip, port, false, sqlitedb);
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
			FIreWallRulesTable fireWallRule=FIreWallRulesTable.getFIreWallRule(protocol, connection, activeStatus, false, sqlitedb);
			
			//-----delete the rule from db and refresh the rules table in the frm-------//
			sqlitedb.DeleteFireWallRuleById(fireWallRule.getId());
			fireWallRules=sqlitedb.GetAllFireWallRules();
			Refresh.refreshFireWallTable(fireWallTableRules, fireWallRules);
			SqliteDB.sendSignalToNfqFIreWall();
			} 
		catch (SQLException e1)
		{
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
	}
	
	public boolean checkValidationProtocol(ProtocolTable protocol) 
	{
		try
		{
			return sqlitedb.IsExsistValidatioProtocol(protocol);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public boolean checkValidationPort(ConnectionTable connection) 
	{
		try
		{
			return sqlitedb.IsExsistValidationConnection(connection);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
