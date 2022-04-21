package packetsNetFilterDB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class ProtocolConfigFrm extends GUI {

	SqliteDB sqlitedb;
	int selectedRow;
	ArrayList<String> protocolsList;
	public ProtocolConfigFrm(int width, int height, String msg) {
		super(width, height, msg);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
	public void createGUI(JFrame frm) throws SQLException
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=getProtocolsTableModel();
		protocolsList=sqlitedb.getAllProtocols();
		JTable protocolsTable=super.createTable(frm, model, 0, 150, 600, 400);
		JButton RemoveButton=super.createButton(frm, "Remove protocol", 700, 200, 200, 100);
		JButton AddButton=super.createButton(frm, "Add protocol", 700, 400, 200, 100);
		JButton EditButton=super.createButton(frm, "Edit protocol", 700, 600, 200, 100);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		JLabel protocolNameLabel=super.createLabel(frm, "Enter Protocol Name:", 200,600, 200, 100);
		protocolNameLabel.setVisible(false);
		JTextField protocolNameTextField=super.createTextField(frm, 400, 620, 200, 50);
		protocolNameTextField.setVisible(false);
		JButton doneButton=super.createButton(frm, "Done", 300, 700, 200, 50);
		doneButton.setVisible(false);
		showExistProtocols(protocolsTable);
		
		
		
		EditButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//-------Protocol/create structs------//
				selectedRow=protocolsTable.getSelectedRow();
				try 
				{
					if(selectedRow!=-1)
					{
						AddProtocolGUI protocolFrm=new AddProtocolGUI(1200,800,"Protocol GUI");
						protocolFrm.createGUI((String) protocolsTable.getValueAt(selectedRow, 0));
					}
				}
				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		doneButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String protocolName=protocolNameTextField.getText();
				if(protocolName!="")
				{
					ProtocolTable protocol=new ProtocolTable(protocolName);
					protocol=ProtocolTable.getProtocolFromDB(protocolName, true, sqlitedb);
					protocolsList.add(protocolName);
					JOptionPane.showMessageDialog(null, "Successfuly created");
					protocolNameLabel.setVisible(false);
					protocolNameTextField.setVisible(false);
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
				protocolNameLabel.setVisible(true);
				protocolNameTextField.setVisible(true);
				doneButton.setVisible(true);
			}
		});
		
		protocolsTable.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		       
		    	//-----check just for updating for delete or insert don't execute the code--------//
		    	if(e.getType()!=e.UPDATE)
		    	{
		    		return;
		    	}
		    	
		    	//---------get the vars of the selected rule for any action----------//
		        selectedRow=protocolsTable.getSelectedRow();
		        changeProtocolName(protocolsList.get(selectedRow),(String) protocolsTable.getValueAt(selectedRow, 0));
		        protocolsList.set(selectedRow, (String) protocolsTable.getValueAt(selectedRow, 0));
		        
		    }
		});
		        
		
		RemoveButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					// TODO Auto-generated method stub
					selectedRow=protocolsTable.getSelectedRow();
					try 
					{
						if(selectedRow!=-1)
						{
							sqlitedb.beginTransaction();
							ProtocolTable protocol=ProtocolTable.getProtocolFromDB((String) protocolsTable.getValueAt(selectedRow, 0),false, sqlitedb); 
							sqlitedb.DeleteAllFireWallRulesOfProtocol(protocol);
							sqlitedb.DeleteProtocolById(protocol.getId());
							sqlitedb.commitTransaction();
							((DefaultTableModel)protocolsTable.getModel()).removeRow(selectedRow);	
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
					ArrayList<String> protocols;
					protocols = sqlitedb.getAllProtocols();
					Refresh.refreshProtocolsTable(protocolsTable, protocols);
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
	
	
	 public void updateDbAboutProtocolName(String protocolName, String newProtocolName)
	 {
		 try
		 {
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
			sqlitedb.updateProtocol(protocol, newProtocolName);
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
	 public void changeProtocolName(String protocolName,String newName)
	 {
		 ProtocolTable protocolToCheck=new ProtocolTable(newName);
			//----The user change the protocol name so need to check is the protocol name to change to isn't occupied---------//
		 if(checkValidationProtocol(protocolToCheck)==false)
		  {
			 updateDbAboutProtocolName(protocolName,newName);				
		  }
		 else
		 {
			JOptionPane.showMessageDialog(null, "Occupied Values,try again");
		 }
	 }
	public void showExistProtocols(JTable protocolTable)
	{
		try 
		{
			ArrayList<String> protocols=sqlitedb.getAllProtocols();
			for(String protocol :protocols)
			{
				((DefaultTableModel) protocolTable.getModel()).addRow(new Object[]{protocol});
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DefaultTableModel getProtocolsTableModel()
	{
		return new DefaultTableModel(new Object[]{"Protocols Name"},0)
		{
		 @Override
         public Class getColumnClass(int columnIndex) {
            switch(columnIndex)
            {
            	case 0: return String.class;
            	default: return null;
            }
         }};
	}
}
