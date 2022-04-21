package packetsNetFilterDB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class ConnectionsConfigFrm extends GUI {

	SqliteDB sqlitedb;
	int selectedRow;
	int selectedColumn;
	ArrayList<ConnectionTable> connectionsList;
	public ConnectionsConfigFrm(int width, int height, String msg) {
		super(width, height, msg);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
	public void createGUI(JFrame frm) throws SQLException
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=getConnectionsTableModel();
		connectionsList=sqlitedb.getAllConnections();
		JTable ConnectionsTable=super.createTable(frm, model, 0, 150, 600, 400);
		JButton RemoveButton=super.createButton(frm, "Remove Connection", 700, 200, 200, 100);
		JButton AddButton=super.createButton(frm, "Add Connection", 700, 400, 200, 100);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		JLabel ipLabel=super.createLabel(frm, "Enter Ip:", 210,600, 200, 100);
		ipLabel.setVisible(false);
		JTextField ipTextField=super.createTextField(frm, 300, 620, 200, 50);
		ipTextField.setVisible(false);
		JLabel portLabel=super.createLabel(frm, "Enter Port:", 550,600, 200, 100);
		portLabel.setVisible(false);
		JTextField portTextField=super.createTextField(frm, 650, 620, 200, 50);
		portTextField.setVisible(false);
		JButton doneButton=super.createButton(frm, "Done", 500, 700, 200, 50);
		doneButton.setVisible(false);
		showExistConnections(ConnectionsTable);
		
		
		doneButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String ip=ipTextField.getText();
				int port=Integer.valueOf(portTextField.getText());
				if(ip!="")
				{
					ConnectionTable connection=new ConnectionTable(ip,port);
					connection=ConnectionTable.getConnectionFromDB(ip, port, true, sqlitedb);
					connectionsList.add(new ConnectionTable(ip,port));
					JOptionPane.showMessageDialog(null, "Successfuly created");
					ipLabel.setVisible(false);
					ipTextField.setVisible(false);
					portLabel.setVisible(false);
					portTextField.setVisible(false);
					doneButton.setVisible(false);
					SqliteDB.sendSignalToNfqFIreWall();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please Enter Value!");
				}
			}
		});
		
		AddButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				ipLabel.setVisible(true);
				ipTextField.setVisible(true);
				portLabel.setVisible(true);
				portTextField.setVisible(true);
				doneButton.setVisible(true);
			}
		});
		
		ConnectionsTable.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		       
		    	//-----check just for updating for delete or insert don't execute the code--------//
		    	if(e.getType()!=e.UPDATE)
		    	{
		    		return;
		    	}
		    	
		    	//---------get the vars of the selected rule for any action----------//
		        selectedRow=ConnectionsTable.getSelectedRow();
		        selectedColumn=ConnectionsTable.getSelectedColumn();
		        changeConnection((String)ConnectionsTable.getValueAt(selectedRow, 0),connectionsList.get(selectedRow).getPort(),(Integer)ConnectionsTable.getValueAt(selectedRow, 1));
		        connectionsList.set(selectedRow, new ConnectionTable((String)ConnectionsTable.getValueAt(selectedRow, 0),(Integer)ConnectionsTable.getValueAt(selectedRow, 1)));
		        
		    }
		});
		        
		
		RemoveButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					// TODO Auto-generated method stub
					selectedRow=ConnectionsTable.getSelectedRow();
					try 
					{
						if(selectedRow!=-1)
						{
							sqlitedb.beginTransaction();
							ConnectionTable connection=ConnectionTable.getConnectionFromDB(((String) ConnectionsTable.getValueAt(selectedRow, 0)),(Integer) ConnectionsTable.getValueAt(selectedRow, 1),false, sqlitedb); 
							sqlitedb.DeleteAllFireWallRulesOfConnection(connection);
							sqlitedb.DeleteConnectionById(connection.getId());
							sqlitedb.commitTransaction();
							((DefaultTableModel)ConnectionsTable.getModel()).removeRow(selectedRow);	
							JOptionPane.showMessageDialog(null, "Deleted!");
							SqliteDB.sendSignalToNfqFIreWall();
						}
					}
					catch (SQLException e) {
						// TODO Auto-generated catch block
						try {
							sqlitedb.rollbackTransaction();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			
			});
		
		RefreshButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				
				try 
				{
					ArrayList<ConnectionTable> connections;
					connections = sqlitedb.getAllConnections();
					Refresh.refreshConnectionTable(ConnectionsTable, connections);
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
	
	public void updateDbAboutPort(String ip,int port,int newPort)
	{
		try 
	   	 {
			//----- update the db about the new protocol-------//
			ConnectionTable connection=ConnectionTable.getConnectionFromDB(ip,port,false, sqlitedb);
			sqlitedb.updatePort(connection, newPort);	
			SqliteDB.sendSignalToNfqFIreWall();
	   	 } 
		catch (SQLException e1) 
		 {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 }
	}
	
	public void changePort(String ip,int port,int newPort)
	{
		ConnectionTable connectionToCheck=new ConnectionTable(ip,newPort);
		//----The user change the port so need to check is the port to change to isn't occupied---------//
		if(checkValidationPort(connectionToCheck)==false)
	     {
			updateDbAboutPort(ip,port,newPort);
			SqliteDB.sendSignalToNfqFIreWall();
		 }
		else
		 {
			//----- if the port to change to is occupied, announce about it
			JOptionPane.showMessageDialog(null, "Occupied Values,try Again");
		 }
	}
	
	public void changeConnection(String ip,int port,int newPort)
	{
		if(selectedColumn==0)
		{
			JOptionPane.showMessageDialog(null, "Do not change the ip!!!");
		}
		//------else means that the selectedColumn=1(port changed)--------//
		else
		{
			changePort(ip,port,newPort);
		}
	}
	
	public void showExistConnections(JTable connectionsTable)
	{
		try 
		{
			ArrayList<ConnectionTable> connections=sqlitedb.getAllConnections();
			for(ConnectionTable connection :connections)
			{
				((DefaultTableModel) connectionsTable.getModel()).addRow(new Object[]{connection.getIp(),connection.getPort()});
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DefaultTableModel getConnectionsTableModel()
	{
		return new DefaultTableModel(new Object[]{"Ip","Port"},0)
		{
		 @Override
         public Class getColumnClass(int columnIndex) {
            switch(columnIndex)
            {
            	case 0: return String.class;
            	case 1: return Integer.class;
            	default: return null;
            }
         }};
	}

}
