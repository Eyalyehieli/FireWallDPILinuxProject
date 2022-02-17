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
	
	SqliteDB sqlitedb;
	int selectedColumn;
	int selectedRow;
	ArrayList<FIreWallRulesTable> fireWallRules;
	public MainGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}

	@Override
	public void createGUI(JFrame frame) throws SQLException
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Ip", "Port","Protocol","Status"},0)
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
	         }
			};
		sqlitedb.createTable("Protocols","id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(255) NOT NULL");
		sqlitedb.createTable("Connections","id INTEGER PRIMARY KEY AUTOINCREMENT,ip varchar(255) NOT NULL,port INTEGER NOT NULL");
		sqlitedb.createTable("Structs", "id INTEGER PRIMARY KEY AUTOINCREMENT,code INTEGER NOT NULL ,name varchar(255) NOT NULL,size INTEGER NOT NULL,protocol_id INTEGER NOT NULL,FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		sqlitedb.createTable("StructFields","id INTEGER PRIMARY KEY AUTOINCREMENT,fieldName varchar(255) NOT NULL,type varchar(255) NOT NULL,minRange varchar(255) NOT NULL,maxRange varchar(255) NOT NULL,struct_id INTEGER NOT NULL,FOREIGN KEY(struct_id) REFERENCES Structs(id)");
		sqlitedb.createTable("FireWallRules","id INTEGER PRIMARY KEY AUTOINCREMENT,activeStatus INTEGER NOT NULL,connection_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL,FOREIGN KEY(connection_id) REFERENCES Connections(id),FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		String[] columns= {"Ip","Port","Protocol","Status"};
		//String[][] data= {{"1","2","3","4"},{"1","2","3","4"}};
		
		JLabel fireWallLabel=super.createLabel(this.frm, "Fire Wall Rules", 400, 50, 200, 100);
		//fireWallTableRules.setVisible(false);
		JButton AddButton=super.createButton(this.frm, "Add",950, 100, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove",950, 300, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit", 950, 500, 200, 100);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		
		JTable fireWallTableRules=super.createTable(this.frm,model,20,150,900,600);
		
		Boolean isActive;
		fireWallRules=sqlitedb.GetAllFireWallRules();
		//GUI.deleteAllJtableRows(fireWallTableRules);
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
		
		
		fireWallTableRules.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		        //Count all checked boxes
		    	
		    	if(e.getType()!=e.UPDATE)
		    	{
		    		return;
		    	}
		    	
		        	selectedRow=fireWallTableRules.getSelectedRow();
		        	selectedColumn=fireWallTableRules.getSelectedColumn();
							if(selectedColumn==0||selectedColumn==1)
							{
								//System.out.println("try to update port");
								//System.out.println("row count= "+fireWallTableRules.getModel().getRowCount());
								String ip=fireWallTableRules.getModel().getValueAt(selectedRow, 0).toString();
								String protocolName=fireWallTableRules.getModel().getValueAt(selectedRow, 2).toString();
								int port=Integer.valueOf((fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
								ConnectionTable connectionToCheck=new ConnectionTable(ip,port);
								if(checkValidationPort(connectionToCheck)==false)
								{
									ProtocolTable protocol=new ProtocolTable(protocolName);
									try 
									{
										protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
										ConnectionTable connection=new ConnectionTable(sqlitedb.getIpByProtocolId(protocol.getId()),sqlitedb.getPortByProtocolId(protocol.getId()));
										connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
										sqlitedb.updateProtocolAndConnection(protocol, connection, protocolName, ip, port);
										
									} 
									catch (SQLException e1) 
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Occupied Values,try Again");
								}
							}
							else if(selectedColumn==2)
							{
								String ip=fireWallTableRules.getModel().getValueAt(selectedRow, 0).toString();
								String protocolName=fireWallTableRules.getModel().getValueAt(selectedRow, 2).toString();
								int port=Integer.valueOf(fireWallTableRules.getModel().getValueAt(selectedRow, 1).toString());
								ProtocolTable protocolToCheck=new ProtocolTable(protocolName);
								if(checkValidationProtocol(protocolToCheck)==false)
								{
									ConnectionTable connection=new ConnectionTable(ip,port);
									try
									{
										connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
										ProtocolTable protocol=new ProtocolTable(sqlitedb.getProtocolNameByConnectionId(connection.getId()));
										protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol, -1));
										sqlitedb.updateProtocolAndConnection(protocol, connection, protocolName, ip, port);
									} 
									catch (SQLException e1) 
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Occupied Values,try again");
								}
							}
							else if(selectedColumn==3)
							{
						     if(fireWallTableRules.getValueAt(selectedRow, 3).equals(Boolean.TRUE))
						     {
									String ip=fireWallTableRules.getModel().getValueAt(selectedRow, 0).toString();
									int port=Integer.valueOf(fireWallTableRules.getModel().getValueAt(selectedRow, 1).toString());
									String protocolName=fireWallTableRules.getModel().getValueAt(selectedRow, 2).toString();
									ProtocolTable protocol=new ProtocolTable(protocolName);
									try 
									{
										protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
										ConnectionTable connection=new ConnectionTable(ip,port);
										connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
										FIreWallRulesTable fireWallRule=new FIreWallRulesTable(0,protocol,connection);
										fireWallRule.setId(sqlitedb.GetFireWallRulesId(fireWallRule, -1));
										sqlitedb.updateFireWallRule(fireWallRule,1);
									} 
									catch (SQLException e1)
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
						     }
						     else
						     {
									String ip=fireWallTableRules.getModel().getValueAt(selectedRow, 0).toString();
									int port=Integer.valueOf(fireWallTableRules.getModel().getValueAt(selectedRow, 1).toString());
									String protocolName=fireWallTableRules.getModel().getValueAt(selectedRow, 2).toString();
									ProtocolTable protocol=new ProtocolTable(protocolName);
									try 
									{
										protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
										ConnectionTable connection=new ConnectionTable(ip,port);
										connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
										FIreWallRulesTable fireWallRule=new FIreWallRulesTable(0,protocol,connection);
										fireWallRule.setId(sqlitedb.GetFireWallRulesId(fireWallRule, -1));
										sqlitedb.updateFireWallRule(fireWallRule,0);
									} 
									catch (SQLException e1) 
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
						     }
						}
				}
		    
		});
		
		RefreshButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try 
						{
							fireWallRules=sqlitedb.GetAllFireWallRules();
							Refresh.refreshFireWallTable(fireWallTableRules, fireWallRules);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int fireWallRule_id;
				int selected_row=fireWallTableRules.getSelectedRow();
				String ip=fireWallTableRules.getValueAt(selected_row, 0).toString();
				int port=Integer.valueOf(fireWallTableRules.getValueAt(selected_row, 1).toString());
				String protocolName=fireWallTableRules.getValueAt(selected_row, 2).toString();
				int activeStatus=0;
				if(fireWallTableRules.getValueAt(selected_row, 3).toString()=="true");
				{
					activeStatus=1;
				}
				ConnectionTable connection=new ConnectionTable(ip,port);
			try {
				connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
				ProtocolTable protocol=new ProtocolTable(protocolName);
				protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol, -1));
				FIreWallRulesTable fireWallRule=new FIreWallRulesTable(activeStatus,protocol,connection);
				
				
					fireWallRule_id=sqlitedb.GetFireWallRulesId(fireWallRule, -1);
					sqlitedb.DeleteFireWallRuleById(fireWallRule_id);
					fireWallRules=sqlitedb.GetAllFireWallRules();
					Refresh.refreshFireWallTable(fireWallTableRules, fireWallRules);
					//((DefaultTableModel) fireWallTableRules.getModel()).removeRow(selected_row);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		);

		EditButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedColumn=fireWallTableRules.getSelectedColumn();
				if(selectedColumn!=3)
				{
					AddProtocolGUI addProtocolGUI=new AddProtocolGUI(1200,800,"Edit Protocol");
					try 
						{
							selectedRow=fireWallTableRules.getSelectedRow();
							int activeStatus;
							String ip=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 0).toString();
							int port =Integer.valueOf(((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
							String protocolName=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 2).toString();
							if(fireWallTableRules.getModel().getValueAt(selectedRow, 3).equals(Boolean.TRUE)) {activeStatus=1;}
							else {activeStatus=0;}
							addProtocolGUI.createGUI(ip,port,protocolName,activeStatus);
						}
					catch (SQLException e1) 
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}			
					
			}
		}
		
		);
		
		AddButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						AddProtocolGUI addProtocolGUI=new AddProtocolGUI(1200,800,"Add Protocol");
						try {
							addProtocolGUI.createGUI("",Integer.MIN_VALUE,"",Integer.MIN_VALUE);
							fireWallRules=sqlitedb.GetAllFireWallRules();
							/*GUI.deleteAllJtableRows(fireWallTableRules);
							for(FIreWallRulesTable fireWallRule:fireWallRules)
							{
								((DefaultTableModel) fireWallTableRules.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName(),fireWallRule.getActiveStatus()});
							}*/
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				);
	
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Boolean checkValidationProtocol(ProtocolTable protocol) 
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
	public Boolean checkValidationPort(ConnectionTable connection) 
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
