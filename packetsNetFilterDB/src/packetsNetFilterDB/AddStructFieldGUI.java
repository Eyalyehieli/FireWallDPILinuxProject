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
import javax.swing.table.DefaultTableModel;

public class AddStructFieldGUI extends GUI{
	
	//----------properties----------//
	private final int ACTIVE=1;
	private final int InACTIVE=0;
	private SqliteDB sqlitedb;
	private  ArrayList<StructsFieldsTable> structFields;
	private boolean isFirst=false;
	private String changeToValue;
	
	//----------C'tor----------//
	public AddStructFieldGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	
	//----------functions----------//
	public void createGUI(int code,String structName,String protocolName) throws SQLException
	{
		   this.sqlitedb=SqliteDB.getSqliteDBInstance();
		   this.structFields=new ArrayList<StructsFieldsTable>(); 
		   DefaultTableModel model=new DefaultTableModel(new Object[]{"Field Name", "Type","Min Range","Max Range"},0);
		  
		 
		   //String[] columns= {"Field Name","Type","Min Range","Max Range"};
		   
	       JButton finished_button=super.createButton(this.frm,"Click me to add field to the structure",400,700,400,50); 
	       JButton addProperty_button=super.createButton(this.frm,"Click me to display the field",400,630,400,50);
	       
	       JLabel fieldNameLabel=super.createLabel(this.frm,"Enter the field name:",40,80,250,100);
	       JTextField fieldNameTextField=super.createTextField(this.frm,300,110,200,50);
	       
	       JLabel typeLabel=super.createLabel(this.frm,"Choose type:",40,190,270,100);
	       JComboBox cbTypes=super.createComboBox(this.frm,this.sqlitedb.GetTypes(),300,220,200,50);
	       
	       JLabel minRangeLabel=super.createLabel(this.frm,"Enter minimum range of value:",640,80,250,100);
	       JTextField minRangeTextField=super.createTextField(this.frm,900,110,200,50);
	       
	       JLabel maxRangeLabel=super.createLabel(this.frm,"Enter maximum range of value:",640,190,270,100);
	       JTextField maxRangeTextField=super.createTextField(this.frm,900,220,200,50);
	       
	       JButton EditButton=super.createButton(this.frm, "Edit", 900, 450,150 ,50 );
	       JButton RemoveButton=super.createButton(this.frm, "Remove",900 ,550, 150, 50);
	       
	       JTable structFieldsTabel=super.createTable(this.frm, model, 350, 290,500 ,250 );
	       
	       JLabel changeFieldLabel=super.createLabel(this.frm, "Enter new value:",20 ,400,220,50);
	       JTextField changeFieldLabelTextField=super.createTextField(this.frm, 200, 400, 140, 50);
	       JButton DoneEditButton=super.createButton(this.frm, "Done", 60, 480, 200, 50);
	       changeFieldLabel.setVisible(false);
	       changeFieldLabelTextField.setVisible(false);
	       DoneEditButton.setVisible(false);
	       
	       
	       //-------Event functions------------//
	       RemoveButton.addActionListener(new ActionListener()
		   {
	 	   		  @Override
	 	   		  public void actionPerformed(ActionEvent e)
	 	   		  {
	 	   			 removeStructField(structFieldsTabel);
	 	   		  }
		   });
	       
	       
	       DoneEditButton.addActionListener(new ActionListener()
		   {
	    	   //----------active change of the structField property-----------//
	 	   		  @Override
	 	   		  public void actionPerformed(ActionEvent e)
	 	   		  {
	 	   			changeValueOfStructField(changeFieldLabelTextField,structFieldsTabel);
	 	   			changeFieldLabelTextField.setText("");
	 	   			changeFieldLabel.setVisible(false);
		   			changeFieldLabelTextField.setVisible(false);
		   			DoneEditButton.setVisible(false);
	 	   		  }
		   });
	       
	       EditButton.addActionListener(new ActionListener()
		   {
	    	   //-------invoking for changing struct field property----------//
 	   		  @Override
 	   		  public void actionPerformed(ActionEvent e)
 	   		  {
 	   			int selected_row=structFieldsTabel.getSelectedRow();
 	   			int selected_column=structFieldsTabel.getSelectedColumn();
 	   			String fieldToChange=structFieldsTabel.getModel().getValueAt(selected_row,selected_column).toString();
 	   		    String fieldName=structFieldsTabel.getModel().getColumnName(selected_column).toString();
 	   			changeFieldLabel.setVisible(true);
 	   			changeFieldLabelTextField.setVisible(true);
 	   			DoneEditButton.setVisible(true);
 	   		    changeFieldLabel.setText("Enter new "+fieldName+":");
 	   		  }
 		   });
	       
	       addProperty_button.addActionListener(new ActionListener()
	    	{
	    	   	@Override
	    	   	public void actionPerformed(ActionEvent e)
	    	   	{
	    	   		Boolean IsToInsert;
		    	   	String fieldName=fieldNameTextField.getText();
		    	   	String type=cbTypes.getSelectedItem().toString();
		    	 	String minRange=minRangeTextField.getText();
		    	    String maxRange=maxRangeTextField.getText();
		    	    
		    	    //------check if the min>max----------//
		    	    if(StructsFieldsTable.CheckRange(type, minRange, maxRange)==true)
		    	      {
		    	        	JOptionPane.showMessageDialog(null, "Range i'snt proper");
		    	        	return;
		    	      }
		    	   
		    	  //-------check if there is no field like this one-------//
		    	    IsToInsert=DistinctionFieldName(structFieldsTabel,fieldName);
		    	    
		    	  //-------if there is one--------------//
		    	    if(!IsToInsert)
		    	        {
		    	    	JOptionPane.showMessageDialog(null, "Wrong Values,try again");
			   
		    	        }
		    	   //-----if there is not one-------------//
		    	   else
		    	        {
			    		   fieldNameTextField.setText("");
			    	       minRangeTextField.setText("");
			    	       maxRangeTextField.setText("");
			    	       addStructField(structFieldsTabel,fieldName,type,minRange,maxRange);
		    	        }
	    	   	}
	    	 });
	       
	       finished_button.addActionListener(new ActionListener()
	    		   {
	    	          @Override
	    	          public void actionPerformed(ActionEvent e)
	    	          {
						  int size=0,i=0,indexOfWrongField=0;
						  Boolean isToInsert=true;
						  readDataFromJTableToAArrayList(structFieldsTabel,structFields);
						  size=getSizeOfStruct();
						  StructsTable struct=PrepareStruct(protocolName,structName,code,size);
						
						  //------commit every structField to the proper struct--------//
						  for(StructsFieldsTable structField:structFields)
						  {
							 structField.setStruct(struct);
							 if(checkValidationStructFieldName(structField)==true)
							 {
								 isToInsert=false;
								 indexOfWrongField=i;
							 }
							 i++;
						  }
						  InsertAction(isToInsert,indexOfWrongField);
	    	          }
	    		   });
	  
	       
	       this.frm.setLayout(null); 
	       this.frm.setSize(this.width,this.height);   
	       this.frm.setVisible(true);
	       this.frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	
	public void InsertAction(Boolean isToInsert,int indexOfWrongField)
	{
		//---------all fields are good validly-----//
		  if(isToInsert==true)
		  {
			 InsertFields();
		  }
		  //------there is wrong field validly in row number indexOfWrongField+1-------//
		 else
		  {
		     JOptionPane.showMessageDialog(null, "Wrong values at row number "+indexOfWrongField+1+", try again");
		  }
	}
	
	public void InsertFields()
	{
		try
		{
			sqlitedb.beginTransaction();
			int structField_id;
			for(StructsFieldsTable structField:structFields)
			{
				structField_id=sqlitedb.GetStructFieldId(structField,true);//here is the insertion
			} 
			JOptionPane.showMessageDialog(null, "Successfuly added"); 
			sqlitedb.commitTransaction();
			SqliteDB.sendSignalToNfqFIreWall();
		  	frm.dispose();
		}
		catch (SQLException e) 
		{
			try {
				sqlitedb.rollbackTransaction();
			} catch (SQLException e1) {
				// Silencing the rollback since it probably means the database isn't in transaction for some reason.
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public StructsTable PrepareStruct(String protocolName,String structName,int code,int size)
	{
		 ProtocolTable protocol=ProtocolTable.getProtocolFromDB(protocolName,false , sqlitedb);
		 StructsTable struct=StructsTable.getStructFromDB(protocol,structName,Integer.valueOf(code),size, sqlitedb, true, true);
		 return struct; 
	}
	
	public int getSizeOfStruct()
	{
		int size=0;
		for(StructsFieldsTable structField:structFields)
		{
			size+=StructsFieldsTable.getSizeOfTypeInBits(structField.getType());
  	  	}
  	  	return size;
	}
	
	public void removeStructField(JTable structFieldsTabel)
	{
		int selected_row=structFieldsTabel.getSelectedRow();
		((DefaultTableModel) structFieldsTabel.getModel()).removeRow(selected_row);
	}
	
	public void changeValueOfStructField(JTextField changeFieldLabelTextField,JTable structFieldsTabel)
	{
		changeToValue=changeFieldLabelTextField.getText();
		structFieldsTabel.getModel().setValueAt(changeToValue, structFieldsTabel.getSelectedRow(), structFieldsTabel.getSelectedColumn());
	   		
	}
	
	public Boolean DistinctionFieldName(JTable structFieldsTabel,String fieldName)
	{
		Boolean isToInsert=true;
		for(int i=0;i<structFieldsTabel.getRowCount();i++)
        {
        	if(structFieldsTabel.getValueAt(i,0).toString().equals(fieldName))
        	{
        		isToInsert=false;
        	}
        }
		return isToInsert;
	}
	
	public void addStructField (JTable structFieldsTabel,String fieldName,String type,String minRange,String maxRange)
	{
		  ((DefaultTableModel) structFieldsTabel.getModel()).addRow(new Object[]{fieldName,type,minRange,maxRange});	 
	       JOptionPane.showMessageDialog(null, "Successfuly added");
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
		structFields.clear();
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


