package packetsNetFilterDB;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class AddProtocolGUI extends GUI{
	//----------properties----------//
	SqliteDB sqlitedb;
	ArrayList<StructsTable> structs;
	int selectedRow;
	int selectedColumn;
	
	//----------C'tor----------//
	public AddProtocolGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	
	//----------functions----------//
	public void createGUI(String protocolName) throws SQLException 
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=GetProtocolTableModel();
		
		JLabel nameLabel=super.createLabel(this.frm, "Protocol Name:", 350, 100, 200, 100);
		JTextField nameTextField=super.createTextField(this.frm, 500, 125, 200, 50);	
		JLabel structsLabel=super.createLabel(this.frm, "Structs", 550, 250, 200, 100);
		JTable structsTable=super.createTable(this.frm, model,325, 350, 500, 500);
		
		JButton AddButton=super.createButton(this.frm, "Add Struct", 950, 350, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit Struct", 950, 610, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove Struct", 950, 480, 200, 100);
		JButton FinishButton=super.createButton(this.frm, "click me if you have finished",850 ,50, 300, 100);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		
		FinishButton.setVisible(false);
		
			//-------------The frm opened-----------//
		initialProtocolFrm(structsTable, protocolName, nameTextField);
		
		//---------Event functions--------------//
		structsTable.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		    	if(!IsForUpdating(e))
		    	{
		    		return;
		    	}
		    	
		    	selectedRow=structsTable.getSelectedRow();
	        	selectedColumn=structsTable.getSelectedColumn();
	        	
	        	if(selectedColumn==0)
	        	{
	        		tryToUpdateStructCode(structsTable,nameTextField);
	        	}
	        	else if(selectedColumn==1)
			    {
	        		tryToUpdateStructName(structsTable,nameTextField);
			    }
		    }
		});
				
		RefreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					ProtocolTable protocol = ProtocolTable.getProtocolFromDB(nameTextField.getText(),false, sqlitedb);
					structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					Refresh.refreshStructsTable(structsTable, structs);
				} 
				catch (SQLException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		EditButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedRow=structsTable.getSelectedRow();
				//--------create edit struct metaData frm--------//
				if(selectedRow!=-1)
				{
					editStruct(structsTable,nameTextField);
				}
			}
		});
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int selected_row=structsTable.getSelectedRow();
				int code=Integer.valueOf(structsTable.getModel().getValueAt(selected_row, 0).toString());
				String structName=structsTable.getModel().getValueAt(selected_row, 1).toString();
				removeStruct(structsTable,selected_row,nameTextField,structName,code);
			}
		});
		
		AddButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addStruct(protocolName,nameTextField);
			}
		});
		
		
		this.frm.setLayout(null); 
	    this.frm.setSize(this.width,this.height);   
	    this.frm.setVisible(true);
	    this.frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	
	public boolean isNumeric(String strNum) {
	    if (strNum == null) 
	    {
	        return false;
	    }
	    try
	    {
	        int num = Integer.parseInt(strNum);
	    } 
	    catch (NumberFormatException nfe)
	    {
	        return false;
	    }
	    return true;
	}
	
	
	public void addStruct(String protocolName,JTextField nameTextField)
	{
		try 
		{
			AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Add Struct");
			Boolean isExistProtocol=false;
			Boolean isExistPort=false;
		//--------there is not existing protocol with this values-------//
			addStructGUI.createGUI("",Integer.MIN_VALUE,Integer.MIN_VALUE,nameTextField.getText());
			
		} 
		catch (SQLException e1) 
		{
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e1.toString());
		}
	}
	
	public DefaultTableModel GetProtocolTableModel()
	{
		return new DefaultTableModel(new Object[]{"Code", "Name"},0)
		{
			 @Override
	         public Class getColumnClass(int columnIndex) 
			 {
	            switch(columnIndex)
	            {
	            	case 0: return Integer.class;
	            	case 1: return String.class;
	            	default: return null;
	            }
	         }
		};
	}
	
	public void initialProtocolFrm(JTable structsTable,String protocolName,JTextField nameTextField) 
	{
		try 
		{
			nameTextField.setText(protocolName);
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
			structs=sqlitedb.GetAllStructsByProtocolId(protocol);
			Refresh.refreshStructsTable(structsTable, structs);
			nameTextField.setEnabled(false);
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void tryToUpdateStructCode(JTable structsTable ,JTextField nameTextField)
	{
		try
		{
    		int newCode=Integer.valueOf(structsTable.getModel().getValueAt(selectedRow, 0).toString());
    		String strcutName=structsTable.getModel().getValueAt(selectedRow, 1).toString();
    		ProtocolTable protocol=ProtocolTable.getProtocolFromDB(nameTextField.getText(),false,sqlitedb);	
			if(checkValidationStructCode(new StructsTable(strcutName,newCode,protocol))==false)
			{
				sqlitedb.updateStructCodeByStrcutNameAndProtocolId(protocol, strcutName, newCode);
				SqliteDB.sendSignalToNfqFIreWall();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Wrong Values,try again");
			}
		} 
		catch (SQLException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void tryToUpdateStructName(JTable structsTable,JTextField nameTextField)
	{
		try 
		{
    		String newStructName=structsTable.getModel().getValueAt(selectedRow, 1).toString();
    		int structCode=Integer.valueOf(structsTable.getModel().getValueAt(selectedRow, 0).toString());
    		ProtocolTable protocol=ProtocolTable.getProtocolFromDB(nameTextField.getText(), false,sqlitedb);
    		
			if(checkValidationStructName(new StructsTable(newStructName,structCode,protocol))==false)
			{
				sqlitedb.updateStrcutNameByStructCodeAndProtocolId(protocol, structCode, newStructName);
				SqliteDB.sendSignalToNfqFIreWall();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Wrong Values,try again");
			}
		}
		catch (SQLException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	 
	public Boolean IsForUpdating(TableModelEvent e)
	{
		return e.getType()==e.UPDATE;
	}
	
	public void editStruct(JTable structsTable,JTextField nameTextField)
	{
		try
		{
			//-----get the proper struct meta data to edit------//
				int StructCode=Integer.valueOf(((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 0).toString());
				int StructSize=Integer.valueOf(sqlitedb.GetStructSizeByStructCodeAndProtocol(StructCode,ProtocolTable.getProtocolFromDB(nameTextField.getText(), false, sqlitedb).getId()));
				String StructName=((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 1).toString();
				AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Edit Struct");
				addStructGUI.createGUI(StructName,StructCode,StructSize,nameTextField.getText());
		}
		catch (SQLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void removeStruct(JTable structsTable,int selected_row,JTextField nameTextField,String structName,int code)
	{
		try
		{
			sqlitedb.beginTransaction();
			ProtocolTable protocol=ProtocolTable.getProtocolFromDB(nameTextField.getText(),false, sqlitedb);
			int struct_id;
			struct_id = sqlitedb.GetStructIdByCodeAndProtocol(new StructsTable(structName,code,protocol),true,false);
			sqlitedb.DeleteStructById(struct_id);
			sqlitedb.commitTransaction();
			((DefaultTableModel) structsTable.getModel()).removeRow(selected_row);
			SqliteDB.sendSignalToNfqFIreWall();
		} 
		catch (SQLException e)
		{
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
	
	public Boolean checkValidationStructName(StructsTable struct) 
	{
		try
		{
			return sqlitedb.IsExistValidationStrcutName(struct);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean checkValidationStructCode(StructsTable struct) 
	{
		try
		{
			return sqlitedb.IsExistValidationStrcutCode(struct);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
