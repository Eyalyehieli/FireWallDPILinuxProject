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
	
	SqliteDB sqlitedb;
	ArrayList<StructsTable> structs;
	int selectedRow;
	int selectedColumn;
	public AddProtocolGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	public void createGUI(String ip,int port,String protocolName,int activeStatus) throws SQLException 
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Code", "Name"},0)
		{
			 @Override
	         public Class getColumnClass(int columnIndex) {
	            switch(columnIndex)
	            {
	            	case 0: return Integer.class;
	            	case 1: return String.class;
	            	default: return null;
	            }
	         }
			};
		JLabel nameLabel=super.createLabel(this.frm, "Protocol Name:", 350, 100, 200, 100);
		JTextField nameTextField=super.createTextField(this.frm, 500, 125, 200, 50);	
		
		JLabel ipLabel=super.createLabel(this.frm, "Ip:", 300, 200, 200, 50);
		JTextField ipTextField=super.createTextField(this.frm, 350, 200, 200, 50);
		
		JLabel portLabel=super.createLabel(this.frm, "Port:", 630, 200, 200, 50);
		JTextField portTextField=super.createTextField(this.frm, 680, 200, 200, 50);
		
		JLabel structsLabel=super.createLabel(this.frm, "Structs", 550, 250, 200, 100);
		
		JTable structsTable=super.createTable(this.frm, model,325, 350, 500, 500);
		
		JButton AddButton=super.createButton(this.frm, "Add", 950, 350, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit", 950, 610, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove", 950, 480, 200, 100);
		JButton FinishButton=super.createButton(this.frm, "click me if you have finished",850 ,50, 300, 100);
		FinishButton.setVisible(false);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		
		
		if(ip!=""&&port!=Integer.MIN_VALUE&&protocolName!=""&&activeStatus!=Integer.MIN_VALUE)//for edit
		{
			nameTextField.setText(protocolName);
			ipTextField.setText(ip);
			portTextField.setText(String.valueOf(port));
			for(int i=0;i<((DefaultTableModel) structsTable.getModel()).getRowCount();i++)
		  	{
		  		((DefaultTableModel) structsTable.getModel()).removeRow(i);
		  	}
		    ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
			protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
			structs=sqlitedb.GetAllStructsByProtocolId(protocol);
			for(StructsTable struct:structs)
			{
				((DefaultTableModel) structsTable.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
			}		
			nameTextField.setEnabled(false);
			ipTextField.setEnabled(false);
			portTextField.setEnabled(false);
		}
		
		structsTable.getModel().addTableModelListener(new TableModelListener() {

		    @Override
		    public void tableChanged(TableModelEvent e) 
		    {
		    	if(e.getType()!=e.UPDATE)
		    	{
		    		return;
		    	}
		    	selectedRow=structsTable.getSelectedRow();
	        	selectedColumn=structsTable.getSelectedColumn();
	        	if(selectedColumn==0)
	        	{
	        		int newCode=Integer.valueOf(structsTable.getModel().getValueAt(selectedRow, 0).toString());
	        		String strcutName=structsTable.getModel().getValueAt(selectedRow, 1).toString();
	        		ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
	        		try {
						protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol, -1));
						if(checkValidationStructCode(new StructsTable(strcutName,newCode,protocol))==false)
						{
							sqlitedb.updateStructCodeByStrcutNameAndProtocolId(protocol, strcutName, newCode);
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
	        	else if(selectedColumn==1)
	        	{
	        		String newStructName=structsTable.getModel().getValueAt(selectedRow, 1).toString();
	        		int structCode=Integer.valueOf(structsTable.getModel().getValueAt(selectedRow, 0).toString());
	        		ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
	        		try {
		        			protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol, -1));
		        			if(checkValidationStructName(new StructsTable(newStructName,structCode,protocol))==false)
		        			{
		        				sqlitedb.updateStrcutNameByStructCodeAndProtocolId(protocol, structCode, newStructName);
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
		    }
		});
		
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  try {
					  if(nameTextField.getText()!="")
					  {
						  TextChanged();
					  }
					  else
					  {
						  ipTextField.setText("");
						  portTextField.setText("");
					  }
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }
			  public void removeUpdate(DocumentEvent e) {
				  try {
					  if(nameTextField.getText()!="")
					  {
						  TextChanged();
					  }
					  else
					  {
						  ipTextField.setText("");
						  portTextField.setText("");
					  }
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
			  public void insertUpdate(DocumentEvent e) {
				 try {
					  if(nameTextField.getText()!="")
					  {
						  TextChanged();
					  }
					  else
					  {
						  ipTextField.setText("");
						  portTextField.setText("");
					  }
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }

			  public void TextChanged() throws SQLException {
				  
				  	//GUI.deleteAllJtableRows(structsTable);
				  	ipTextField.setText("");
					portTextField.setText("");
				  	String protocol_name= nameTextField.getText();
				  	ProtocolTable protocolIPAndPort=new ProtocolTable(protocol_name);
				  	protocolIPAndPort.setId(sqlitedb.GetProtocolIdByProtocolName(protocolIPAndPort,-1));
				    ipTextField.setText(sqlitedb.getIpByProtocolId(protocolIPAndPort.getId()));
				    portTextField.setText(String.valueOf(sqlitedb.getPortByProtocolId(protocolIPAndPort.getId())));
				    ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
					structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					/*for(StructsTable struct:structs)
					{
						((DefaultTableModel) structsTable.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
					}*/
					Refresh.refreshStructsTable(structsTable, structs);
			     }
			  }
			);
		
		RefreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					ProtocolTable protocol = new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
					structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					Refresh.refreshStructsTable(structsTable, structs);
				} catch (SQLException e1) {
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
				if(selectedRow!=-1)
				{
					AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Edit Struct");
					try {
					   
						int StructCode=Integer.valueOf(((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 0).toString());
						String StructName=((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 1).toString();
						addStructGUI.createGUI(StructName,StructCode,nameTextField.getText(),Integer.valueOf(portTextField.getText()),ipTextField.getText());
				}
					catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					try {
						String new_ip=ipTextField.getText();
						int new_port=Integer.valueOf(portTextField.getText());
						String protocol_name=nameTextField.getText();
						ProtocolTable protocol=new ProtocolTable(protocol_name);
						protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
						ConnectionTable connection=new ConnectionTable(ip,port);
						connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
						sqlitedb.updateProtocolAndConnection(protocol,connection,protocol_name,new_ip,new_port);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		}
		});
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					int struct_id;
					int selected_row=structsTable.getSelectedRow();
					int code=Integer.valueOf(structsTable.getModel().getValueAt(selected_row, 0).toString());
					String name=structsTable.getModel().getValueAt(selected_row, 1).toString();
					ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,0));
					struct_id=sqlitedb.GetStructIdByCodeAndProtocol(new StructsTable(name,code,protocol),0,0);
					sqlitedb.DeleteStructById(struct_id);
					((DefaultTableModel) structsTable.getModel()).removeRow(selected_row);
					//structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					//Refresh.refreshStructsTable(structsTable, structs);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		);
		
		AddButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Add Struct");
				try 
				{	
					Boolean isExistProtocol=false;
					Boolean isExistPort=false;
					if(ip=="")//because its for adding
					{
						 isExistProtocol=checkValidationProtocol(new ProtocolTable(nameTextField.getText()));
						 isExistPort=checkValidationPort(new ConnectionTable(ipTextField.getText(),Integer.valueOf(portTextField.getText())));
					}
					if(isExistPort==false&&isExistProtocol==false)
					{
						addStructGUI.createGUI("",Integer.MIN_VALUE,nameTextField.getText(),Integer.valueOf(portTextField.getText()),ipTextField.getText());
					}
					else
					{
						JOptionPane.showMessageDialog(null,"Occupied Values,try Again" );
					}
				} 
				catch (SQLException e1) 
				{
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
			}
		});
		
		this.frm.setLayout(null); 
	    this.frm.setSize(this.width,this.height);   
	    this.frm.setVisible(true);
	    this.frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
