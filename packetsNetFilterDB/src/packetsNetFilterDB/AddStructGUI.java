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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class AddStructGUI extends GUI {
	
	SqliteDB sqlitedb;
	String changeToValue;
	int selectedRow;
	int selectedColumn;
	ArrayList<StructsFieldsTable> structFields;
	public AddStructGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	public void createGUI(String StructName,int StructCode,String protocolName,int port,String ip) throws SQLException 
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Field Name","Type","Min Range","Max Range"},0);
		JLabel structNameLabel=super.createLabel(this.frm,"Enter the Struct name:",40,50,200,100);
	    JTextField structNameTextField=super.createTextField(this.frm,300,75,200,50);
	       
	    JLabel structCodeLabel=super.createLabel(this.frm, "Enter the struct Code:", 640, 50, 200, 100);
	    JTextField structCodeTextField=super.createTextField(this.frm, 900, 75, 200, 50);
		
		JLabel structsFieldsLabel=super.createLabel(this.frm, "Struct Fields", 535, 180, 200, 100);
		
		JTable structFieldsTable=super.createTable(this.frm, model,325, 280, 500, 500);
		
		JButton AddButton=super.createButton(this.frm, "Add", 950, 350, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit", 950, 610, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove", 950, 480, 200, 100);
		JButton FinishButton=super.createButton(this.frm, "click me if you have finished",850 ,50, 300, 100);
		FinishButton.setVisible(false);
		JLabel changeFieldLabel=super.createLabel(this.frm, "Enter new value:",20 ,400,220,50);
	    JTextField changeFieldLabelTextField=super.createTextField(this.frm, 200, 400, 140, 50);
	    JButton finishEditButton=super.createButton(this.frm, "Done", 60, 480, 200, 50);
	    changeFieldLabel.setVisible(false);
		changeFieldLabelTextField.setVisible(false);
		finishEditButton.setVisible(false);
		JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
		if(StructName!=""&&StructCode!=Integer.MIN_VALUE)//for edit
		{
			structCodeTextField.setText(String.valueOf(StructCode));
			structNameTextField.setText(StructName);
			
			int code=Integer.valueOf(structCodeTextField.getText()) ;
			String name=structNameTextField.getText();
		  	for(int i=0;i<((DefaultTableModel) structFieldsTable.getModel()).getRowCount();i++)
			  {
			  	((DefaultTableModel)structFieldsTable.getModel()).removeRow(i);
			  }
		  	ProtocolTable protocol=new ProtocolTable(protocolName);
		  	protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
		    StructsTable struct=new StructsTable(name,code,protocol);
		    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1,0));
		    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
			/*for(StructsFieldsTable structField:structFields)
			  {
				((DefaultTableModel)structFieldsTable.getModel()).addRow(new Object[]{structField.getName(),structField.getType(),structField.getMinRange(),structField.getMaxRange()});
			  }
			  */
		    Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
			structCodeTextField.setEnabled(false);
			structNameTextField.setEnabled(false);
		}
	       
		RefreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					int code=Integer.valueOf(structCodeTextField.getText()) ;
					String name=structNameTextField.getText();
					ProtocolTable protocol=new ProtocolTable(protocolName);
				  	protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
				    StructsTable struct=new StructsTable(name,code,protocol);
				    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1,0));
				    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
					Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		structCodeTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  try {
					  if(structCodeTextField.getText()!="" && structNameTextField.getText()!="")
					  {
						  TextChanged();
					  }
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }
			  public void removeUpdate(DocumentEvent e) {
				  try {
					  if(structCodeTextField.getText()!="" && structNameTextField.getText()!="")
					  {
						  TextChanged();
					  }
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
			  public void insertUpdate(DocumentEvent e) {
				  try {
					  if(structCodeTextField.getText()!="" && structNameTextField.getText()!="")
					  {
						  TextChanged();
					  }
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }

			  public void TextChanged() throws SQLException {
				  
				  if(structCodeTextField.getText()!="" && structNameTextField.getText()!="")
				  {
					/*GUI.deleteAllJtableRows(structFieldsTable);*/
					int code=Integer.valueOf(structCodeTextField.getText()) ;
					String name=structNameTextField.getText();
				  	ProtocolTable protocol=new ProtocolTable(protocolName);
				  	protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
				    StructsTable struct=new StructsTable(name,code,protocol);
				    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1,0));
				    if(struct.getId()!=-1)//if there is struct like this
				    {
					    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
						/*for(StructsFieldsTable structField:structFields)
						{
							((DefaultTableModel)structFieldsTable.getModel()).addRow(new Object[]{structField.getName(),structField.getType(),structField.getMinRange(),structField.getMaxRange()});
						}*/
					    Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
				    }
				    else
				    {
				    	GUI.deleteAllJtableRows(structFieldsTable,"StructFieldsTable");
				    }
			     }
			  }
		});
		
		 finishEditButton.addActionListener(new ActionListener()
		   {
	 	   		  @Override
	 	   		  public void actionPerformed(ActionEvent e)
	 	   		  {
		 	   			  if(selectedRow!=-1)
		 	   			  {
			 	   			int structField_id;
			 	   			changeToValue=changeFieldLabelTextField.getText();
				 	   		String fieldName=structFieldsTable.getValueAt(selectedRow, 0).toString();
					  		String type=structFieldsTable.getValueAt(selectedRow, 1).toString();
					  		String minRange=structFieldsTable.getValueAt(selectedRow, 2).toString();
					  		String maxRange=structFieldsTable.getValueAt(selectedRow, 3).toString();
							try
							{
								ProtocolTable protocol=new ProtocolTable(protocolName);
								protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
								StructsTable struct=new StructsTable(structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()),protocol);
							    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1,0));
							    structField_id=sqlitedb.GetStructFieldId(new StructsFieldsTable(fieldName,type,minRange,maxRange,struct),-1);
							    structFieldsTable.setValueAt(changeToValue, selectedRow, selectedColumn);
							    fieldName=structFieldsTable.getValueAt(selectedRow, 0).toString();
						  		type=structFieldsTable.getValueAt(selectedRow, 1).toString();
						  		minRange=structFieldsTable.getValueAt(selectedRow, 2).toString();
						  		maxRange=structFieldsTable.getValueAt(selectedRow, 3).toString();
						  		if(checkValidationStructFieldName(new StructsFieldsTable(fieldName,struct))==false)
						  		{
						  			sqlitedb.updateStructField(structField_id,fieldName,type,minRange,maxRange);
						  		}
						  		else
						  		{
						  			JOptionPane.showMessageDialog(null, "Wrong Values, try again");
						  		}
				 	   			
					 	   		changeFieldLabel.setVisible(false);
					   			changeFieldLabelTextField.setVisible(false);
					   		    finishEditButton.setVisible(false);
				 	   			
							} 
							catch (SQLException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		 	   		  }
	 	   		  }
	 		   });
	       
	       EditButton.addActionListener(new ActionListener()
		   {
	   		  @Override
	   		  public void actionPerformed(ActionEvent e)
	   		  {
	   			selectedRow=structFieldsTable.getSelectedRow();
	   			selectedColumn=structFieldsTable.getSelectedColumn();
	   			if(selectedRow!=-1)
	   			{
	   			//String fieldToChange=structFieldsTable.getModel().getValueAt(selectedRow,selectedColumn).toString();
	   		    String fieldName=structFieldsTable.getModel().getColumnName(selectedColumn).toString();
	   			changeFieldLabel.setVisible(true);
	   			changeFieldLabelTextField.setVisible(true);
	   		    finishEditButton.setVisible(true);
	   		    changeFieldLabel.setText("Enter new "+fieldName+":");
	   			}
	   		  }
		   }
		   );
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			  	try {
			  		int structField_id;
			  		int selected_row=structFieldsTable.getSelectedRow();
			  		String fieldName=structFieldsTable.getValueAt(selected_row, 0).toString();
			  		String type=structFieldsTable.getValueAt(selected_row, 1).toString();
			  		String minRange=structFieldsTable.getValueAt(selected_row, 2).toString();
			  		String maxRange=structFieldsTable.getValueAt(selected_row, 3).toString();
			  		ProtocolTable protocol=new ProtocolTable(protocolName);
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
					StructsTable struct=new StructsTable(structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()),protocol);
				    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1,0));
				    structField_id=sqlitedb.GetStructFieldId(new StructsFieldsTable(fieldName,type,minRange,maxRange,struct),-1);
				    sqlitedb.DeleteStructFieldById(structField_id);
				    ((DefaultTableModel)structFieldsTable.getModel()).removeRow(selected_row);
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
				AddStructFieldGUI addStructFieldGUI=new AddStructFieldGUI(1200,800,"Add Struct");
				try {
					addStructFieldGUI.createGUI(Integer.valueOf(structCodeTextField.getText()),structNameTextField.getText(),protocolName,port,ip);
					/*for(int i=0;i<((DefaultTableModel) structFieldsTable.getModel()).getRowCount();i++)
				  	{
				  		((DefaultTableModel) structFieldsTable.getModel()).removeRow(i);
				  	}
				  	ProtocolTable protocol=new ProtocolTable(protocolName);
				  	protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
				    StructsTable struct=new StructsTable(structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()),protocol);
				    struct.setId(sqlitedb.GetStructIdByCodeAndProtocol(struct, -1));
				    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
					for(StructsFieldsTable structField:structFields)
					{
						((DefaultTableModel) structFieldsTable.getModel()).addRow(new Object[]{structField.getName(),structField.getType(),structField.getMinRange(),structField.getMaxRange()});
					}*/
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
			}
		}
		);
	       
	      
	       
	       this.frm.setLayout(null); 
	       this.frm.setSize(this.width,this.height);   
	       this.frm.setVisible(true);
	       this.frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	
	public Boolean checkValidationStructFieldName(StructsFieldsTable structField)
	{
		try
		{
			return sqlitedb.IsExistValidationStructFieldName(structField);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void readDataFromJTableToAArrayList(JTable structFieldsTabel,ArrayList<StructsFieldsTable> structFields) {
		String fieldName,type,minRange,maxRange;
		for(int i=0;i<structFieldsTabel.getModel().getRowCount();i++)
		{
			fieldName=structFieldsTabel.getModel().getValueAt(i, 0).toString();
			type=structFieldsTabel.getModel().getValueAt(i, 1).toString();
			minRange=structFieldsTabel.getModel().getValueAt(i, 2).toString();
			maxRange=structFieldsTabel.getModel().getValueAt(i, 3).toString();
			structFields.add(new StructsFieldsTable(fieldName,type,minRange,maxRange));
		}
		
	}
}