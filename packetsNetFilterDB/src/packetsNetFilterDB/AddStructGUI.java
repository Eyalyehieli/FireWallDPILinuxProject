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
	
	//----------properties----------//
	SqliteDB sqlitedb;
	String changeToValue;
	int selectedRow;
	int selectedColumn;
	ArrayList<StructsFieldsTable> structFields;
	//----------C'tor----------//
	public AddStructGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	

	
	//----------functions----------//
	public void createGUI(String StructName,int StructCode,int structSize,String protocolName) throws SQLException 
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Field Name","Type","Min Range","Max Range"},0);
		JLabel structNameLabel=super.createLabel(this.frm,"Struct name:",250, 150, 200, 50);
	    JTextField structNameTextField=super.createTextField(this.frm,350, 150, 200, 50);  
	    JLabel structSizeLabel=super.createLabel(frm, "Struct size:", 580, 150, 200, 50);
	    JTextField structSizeTextField=super.createTextField(frm, 680, 150, 200, 50);
	    JLabel structCodeLabel=super.createLabel(this.frm, "Struct Code:",380, 50, 100, 100);
	    JTextField structCodeTextField=super.createTextField(this.frm, 500, 75, 200, 50);
		JLabel structsFieldsLabel=super.createLabel(this.frm, "Struct Fields", 535, 180, 200, 100);
		JTable structFieldsTable=super.createTable(this.frm, model,325, 280, 500, 500);
		JButton AddButton=super.createButton(this.frm, "Add Struct Field", 950, 350, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit Struct Field", 950, 610, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove Struct Field", 950, 480, 200, 100);
		JButton FinishButton=super.createButton(this.frm, "click me if you have finished",850 ,50, 300, 100);
		JLabel changeFieldLabel=super.createLabel(this.frm, "Enter new value:",20 ,400,220,50);
	    JTextField changeFieldLabelTextField=super.createTextField(this.frm, 200, 400, 140, 50);
	    JButton finishEditButton=super.createButton(this.frm, "Done", 60, 480, 200, 50);
	    JButton RefreshButton=super.createButton(this.frm, "Ref", 0, 0, 100, 50);
	    
	    changeFieldLabel.setVisible(false);
		changeFieldLabelTextField.setVisible(false);
		finishEditButton.setVisible(false);
		FinishButton.setVisible(false);
		
		//-------check if the frm opened for edit struct-------// 
		if(isForEditingStruct(StructName,StructCode))
		{
			//-------------The frm opened for edit struct-----------//
			initialStructFrmForEditing(structSizeTextField,structFieldsTable,structCodeTextField,structNameTextField,StructName,StructCode,structSize,protocolName);
		}
	       
		//--------Event functions---------//
		RefreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					//-------set struct vars to get the db data--------//
					int code=Integer.valueOf(structCodeTextField.getText()) ;
					String name=structNameTextField.getText();
					ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
				    StructsTable struct=StructsTable.getStructFromDB(protocol, name, code, 0, sqlitedb, false, false);
				    //--------refresh-----------------//
				    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
					Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
					structSizeTextField.setText(String.valueOf(sqlitedb.GetStructSizeByStructCodeAndProtocol(struct.getCode(),protocol.getId())));
				} 
				catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		structCodeTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e)
			  {
				  checkForChange();
			  }
			  public void removeUpdate(DocumentEvent e) 
			  {
				  checkForChange();
			  }
			  public void insertUpdate(DocumentEvent e) 
			  {
				  checkForChange();
			  }
			  
			  public void checkForChange()
			  {
				  try {
						//-----------try to find a struct-----------//
						  if(structCodeTextField.getText()!="")
						  {
							  TextChanged();
						  }
						 //----------empty TextFields, no struct has written-------//
						  else
						  {
							  structNameTextField.setText("");
						  }
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
			  
			  public void TextChanged() throws SQLException {
				 
				  	structNameTextField.setText("");
				  	structSizeTextField.setText("");
				  	GUI.deleteAllJtableRows(structFieldsTable,"StructFieldsTable");
				  	
				  	//-------just if structCodeTextField is numeric because otherwise it wouldn't parse to int--------//
				  	if(isNumeric(structCodeTextField.getText()))
				  	{
				  		int code=Integer.valueOf(structCodeTextField.getText());
				  		String name=structNameTextField.getText();
					  	ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
					    StructsTable struct=StructsTable.getStructFromDB(protocol, name, code, 0, sqlitedb, false, false);
					    
					    //-------- if there is not struct it wouldn't be change---------//
					    structNameTextField.setText(sqlitedb.getStructNameByProtocolAndStructCode(protocol, code));
					    structSizeTextField.setText(String.valueOf(sqlitedb.GetStructSizeByStructCodeAndProtocol(code, protocol.getId())));
					   
					    //--------//if there is struct like this will be change,otherwise not---------//
					    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
						Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
				  	}
			  }
		});
		
		 finishEditButton.addActionListener(new ActionListener()
		   {
	 	   	 @Override
	 	   	 public void actionPerformed(ActionEvent e)
	 	   	 {
	 	   		 //---------table cell was selected for editing----------//
		 	   	 if(selectedRow!=-1)
		 	   	{
		 	   		try
					{
				 	   	int structField_id;
				 	   	changeToValue=changeFieldLabelTextField.getText();
				 	   			
				 	   	//-------extracr data to get structField id for checking structField duality-------//
					 	String fieldName=structFieldsTable.getValueAt(selectedRow, 0).toString();
				  		String type=structFieldsTable.getValueAt(selectedRow, 1).toString();
				  		String minRange=structFieldsTable.getValueAt(selectedRow, 2).toString();
				  		String maxRange=structFieldsTable.getValueAt(selectedRow, 3).toString();
								
				  		//-----get the struct field id of the struct field to change/the selected struct field-----//
						ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
						StructsTable struct=StructsTable.getStructFromDB(protocol, structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()), 0, sqlitedb, false, false);	
					    structField_id=sqlitedb.GetStructFieldId(new StructsFieldsTable(fieldName,type,minRange,maxRange,struct),false);
							   
					    //-------check if there is struct field in this struct with the same fields------//
					    checkForStructFieldDuality(structFieldsTable,struct ,structField_id);
				 	   			
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
	   			
	   		  //---------table cell was selected for editing----------//
	   			if(selectedRow!=-1)
	   			{
	   		    String fieldName=structFieldsTable.getModel().getColumnName(selectedColumn).toString();
	   			changeFieldLabel.setVisible(true);
	   			changeFieldLabelTextField.setVisible(true);
	   		    finishEditButton.setVisible(true);
	   		    changeFieldLabel.setText("Enter new "+fieldName+":");
	   			}
	   		  }
		   });
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			  	int selected_row=structFieldsTable.getSelectedRow();
				String fieldName=structFieldsTable.getValueAt(selected_row, 0).toString();
				String type=structFieldsTable.getValueAt(selected_row, 1).toString();
				String minRange=structFieldsTable.getValueAt(selected_row, 2).toString();
				String maxRange=structFieldsTable.getValueAt(selected_row, 3).toString();
				removeStructField(structFieldsTable,protocolName,structSizeTextField,structNameTextField, structCodeTextField,fieldName,type, minRange,maxRange,selected_row); 
			}
		});
		
		AddButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					//----new StructField frm to commit new strcut fields to struct---------//
					ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
					StructsTable struct=StructsTable.getStructFromDB(protocol,structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()), 0, sqlitedb, false, false);
					if(isForEditingStruct(StructName,StructCode))
					{
						//-------the frm is created for edit struct------//
						AddStructFieldGUI addStructFieldGUI=new AddStructFieldGUI(1200,800,"Add Struct");
						addStructFieldGUI.createGUI(Integer.valueOf(structCodeTextField.getText()),structNameTextField.getText(),protocolName);
					}
					else
					{
			//-------the frm is created for add struct so need to check validation---------//
						//------check if occupied struct meta data has written---------//
						if(!isOccupiedStructMetaData(struct))
						{
							//-----the struct code and struct name isn't occupied-------//
							AddStructFieldGUI addStructFieldGUI=new AddStructFieldGUI(1200,800,"Add Struct");
							addStructFieldGUI.createGUI(Integer.valueOf(structCodeTextField.getText()),structNameTextField.getText(),protocolName);
						}
						else
						{
							//-------the struct code or the struct name or both are occupied-------//
							JOptionPane.showMessageDialog(null, "Occupied struct code ot struct name, please try again");
						}
					}
				} 
				catch (SQLException e1) 
				{
					JOptionPane.showMessageDialog(null, e1.toString());
				}
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
	
	public boolean isOccupiedStructMetaData(StructsTable struct)
	{
		try
		{
			return sqlitedb.IsExistValidationStrcutName(struct)|| sqlitedb.IsExistValidationStrcutCode(struct);
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean isForEditingStruct(String structName,int structCode)
	{
		return structName!=""&&structCode!=Integer.MIN_VALUE;
	}
	
	public void initialStructFrmForEditing(JTextField structSizeTextField,JTable structFieldsTable ,JTextField structCodeTextField,JTextField structNameTextField,String StructName,int StructCode,int structSize,String protocolName)
	{
		try 
		{
			structCodeTextField.setText(String.valueOf(StructCode));
			structNameTextField.setText(StructName);
			structSizeTextField.setText(String.valueOf(structSize));
			
			int code=StructCode;
			String name=StructName;
			//-----------display struct meta data---------//
		  	ProtocolTable protocol=	ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
		    StructsTable struct=StructsTable.getStructFromDB(protocol, name, code, 0, sqlitedb, false, false);
		    structFields=sqlitedb.GetAllStructFieldsByStructId(struct);
			Refresh.refreshStructFieldsTable(structFieldsTable, structFields);
			structCodeTextField.setEnabled(false);
			structNameTextField.setEnabled(false);
			structSizeTextField.setEnabled(false);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
	
	public void checkForStructFieldDuality(JTable structFieldsTable,StructsTable struct ,int structField_id)
	{
		try 
		{
			structFieldsTable.setValueAt(changeToValue, selectedRow, selectedColumn);
		    String fieldName=structFieldsTable.getValueAt(selectedRow, 0).toString();
	  		String type=structFieldsTable.getValueAt(selectedRow, 1).toString();
	  		String minRange=structFieldsTable.getValueAt(selectedRow, 2).toString();
	  		String maxRange=structFieldsTable.getValueAt(selectedRow, 3).toString();
	  		if(checkValidationStructFieldName(new StructsFieldsTable(fieldName,type,minRange,maxRange,struct))==false)
	  		{
	  			
				sqlitedb.updateStructField(structField_id,fieldName,type,minRange,maxRange);
				SqliteDB.sendSignalToNfqFIreWall();
			} 
	  		else
	  		{
	  			JOptionPane.showMessageDialog(null, "Wrong Values, try again");
	  		}
		}
  		catch (SQLException e) 
  		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
  		
	public void removeStructField(JTable structFieldsTable,String protocolName,JTextField structSizeTextField,JTextField structNameTextField,JTextField structCodeTextField,String fieldName,String type,String minRange,String maxRange,int selected_row)
	{
		 try 
		 {		
			 	sqlitedb.beginTransaction();
			 	int structField_id;
				ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false, sqlitedb);
				StructsTable struct=StructsTable.getStructFromDB(protocol,structNameTextField.getText(),Integer.valueOf(structCodeTextField.getText()),0 ,sqlitedb, false, false);
				structField_id=sqlitedb.GetStructFieldId(new StructsFieldsTable(fieldName,type,minRange,maxRange,struct),false);
				sqlitedb.DeleteStructFieldById(structField_id);
				sqlitedb.updateStructSize(struct, sqlitedb.GetStructSizeByStructCodeAndProtocol(struct.getCode(), protocol.getId()), StructsFieldsTable.getSizeOfTypeInBits(type));
				sqlitedb.commitTransaction();
				structSizeTextField.setText(String.valueOf(sqlitedb.GetStructSizeByStructCodeAndProtocol(struct.getCode(), protocol.getId())));
				((DefaultTableModel)structFieldsTable.getModel()).removeRow(selected_row);
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
	
	public boolean checkValidationStructFieldName(StructsFieldsTable structField)
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