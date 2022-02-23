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
	
	private final int ACTIVE=1;
	private final int IN_ACTIVE=0;
	private SqliteDB sqlitedb;
	private  ArrayList<StructsFieldsTable> structFields;
	private boolean isFirst=false;
	private String changeToValue;
	private int taps_counter=0;
	
	public AddStructFieldGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	public void createGUI(int code,String structName,String protocolName,int port,String ip) throws SQLException
	{
		   this.sqlitedb=SqliteDB.getSqliteDBInstance();
		   this.structFields=new ArrayList<StructsFieldsTable>(); 
		   DefaultTableModel model=new DefaultTableModel(new Object[]{"Field Name", "Type","Min Range","Max Range"},0);
		   ProtocolTable protocolTable=new ProtocolTable(protocolName);
		   ConnectionTable connectionTable=new ConnectionTable(ip,Integer.valueOf(port));
		   FIreWallRulesTable fireWallRulesTable=new FIreWallRulesTable(ACTIVE);
		   String[] columns= {"Field Name","Type","Min Range","Max Range"};
		   //String[][] data= {{"1","2","3","4"},{"1","2","3","4"}};
	       //JFrame frm=super.createFrame("netFilterDBApplication");
	       JButton finished_button=super.createButton(this.frm,"Click me to add field to the structure",400,700,400,50); 
	       JButton addProperty_button=super.createButton(this.frm,"Click me to display the field",400,630,400,50);
	       
	       /*JLabel structNameLabel=super.createLabel(this.frm,"Enter the Struct name:",40,50,200,100);
	       JTextField structNameTextField=super.createTextField(this.frm,300,75,200,50);
	       
	       JLabel structCodeLabel=super.createLabel(this.frm, "Enter the struct Code", 640, 50, 200, 100);
	       JTextField structCodeTextField=super.createTextField(this.frm, 900, 75, 200, 50);*/
	       
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
	       JButton finishEditButton=super.createButton(this.frm, "Done", 60, 480, 200, 50);
	       changeFieldLabel.setVisible(false);
	       changeFieldLabelTextField.setVisible(false);
	       finishEditButton.setVisible(false);
	       
	       
	       RemoveButton.addActionListener(new ActionListener()
		   {
	 	   		  @Override
	 	   		  public void actionPerformed(ActionEvent e)
	 	   		  {
	 	   			int selected_row=structFieldsTabel.getSelectedRow();
	 	   			//int selected_column=structFieldsTabel.getSelectedColumn();
	 	   		    ((DefaultTableModel) structFieldsTabel.getModel()).removeRow(selected_row);
	 	   		    taps_counter=0;
	 	   		  }
	 		   }
	 		   );
	       
	       
	       finishEditButton.addActionListener(new ActionListener()
		   {
	 	   		  @Override
	 	   		  public void actionPerformed(ActionEvent e)
	 	   		  {
	 	   			changeToValue=changeFieldLabelTextField.getText();
	 	   			structFieldsTabel.getModel().setValueAt(changeToValue, structFieldsTabel.getSelectedRow(), structFieldsTabel.getSelectedColumn());
		 	   		changeFieldLabel.setVisible(false);
		   			changeFieldLabelTextField.setVisible(false);
		   		    finishEditButton.setVisible(false);
	 	   		  }
	 		   });
	       
	       EditButton.addActionListener(new ActionListener()
		   {
 	   		  @Override
 	   		  public void actionPerformed(ActionEvent e)
 	   		  {
 	   			int selected_row=structFieldsTabel.getSelectedRow();
 	   			int selected_column=structFieldsTabel.getSelectedColumn();
 	   			String fieldToChange=structFieldsTabel.getModel().getValueAt(selected_row,selected_column).toString();
 	   		    String fieldName=structFieldsTabel.getModel().getColumnName(selected_column).toString();
 	   			changeFieldLabel.setVisible(true);
 	   			changeFieldLabelTextField.setVisible(true);
 	   		    finishEditButton.setVisible(true);
 	   		    changeFieldLabel.setText("Enter new "+fieldName+":");
 	   		    //structFieldsTabel.getModel().setValueAt(changeToValue, selected_row, selected_column);
 	   		    
 	   		    
 	   		  }
 		   }
 		   );
	       
	       addProperty_button.addActionListener(new ActionListener()
	    		   {
	    	   		  @Override
	    	   		  public void actionPerformed(ActionEvent e)
	    	   		  {
	    	   			    Boolean IsToInsert=true;
		    	   			String fieldName=fieldNameTextField.getText();
		    	   			String type=cbTypes.getSelectedItem().toString();
		    	   			String minRange=minRangeTextField.getText();
		    	        	String maxRange=maxRangeTextField.getText();
		    	        	//structFields.add(new StructsFieldsTable(fieldName,type,minRange,maxRange));
		    	        	if(StructsFieldsTable.CheckRange(type, minRange, maxRange)==true)
		    	        	{
		    	        		JOptionPane.showMessageDialog(null, "Range i'snt proper");
		    	        		return;
		    	        	}
		    	        	for(int i=0;i<structFieldsTabel.getRowCount();i++)
		    	        	{
		    	        		if(structFieldsTabel.getValueAt(i,0).toString().equals(fieldName))
		    	        		{
		    	        			IsToInsert=false;
		    	        		}
		    	        	}
		    	        	if(IsToInsert==true)
		    	        	{
			    	        	fieldNameTextField.setText("");
			    	        	minRangeTextField.setText("");
			    	        	maxRangeTextField.setText("");
			    	        	JOptionPane.showMessageDialog(null, "Successfuly added");
			    	        	((DefaultTableModel) structFieldsTabel.getModel()).addRow(new Object[]{fieldName,type,minRange,maxRange});
			    	        	taps_counter++;
		    	        	}
		    	        	else
		    	        	{
		    	        		JOptionPane.showMessageDialog(null, "Wrong Values,try again");
		    	        	}
	    	   		  }
	    		   });
	       
	       finished_button.addActionListener(new ActionListener()
	    		   {
	    	          @Override
	    	          public void actionPerformed(ActionEvent e)
	    	          {
	    	        	  int fireWallRules_id,protocol_id,connection_id,struct_id,structField_id;
	    	        	  int size=0;
	    	        	  int i=0,index=0;
	    	        	  Boolean isToInsert=true;
	    	        	  readDataFromJTableToAArrayList(structFieldsTabel,structFields);
	    	        	  for(StructsFieldsTable structField:structFields)
	    	        	  {
	    	        		  size+=StructsFieldsTable.getSizeOfTypeInBits(structField.getType());
	    	        	  }
	    	        	  
	    	        	  try {
		    	        		    StructsTable struct=new StructsTable(structName,Integer.valueOf(code),size);
									protocol_id=sqlitedb.GetProtocolIdByProtocolName(protocolTable,0);
									connection_id=sqlitedb.GetConnectionIdByIpAndPort(connectionTable,0);
									protocolTable.setId(protocol_id);
									connectionTable.setId(connection_id);
									fireWallRulesTable.setConnection(connectionTable);
									fireWallRulesTable.setProtocol(protocolTable);
									fireWallRules_id=sqlitedb.GetFireWallRulesId(fireWallRulesTable,0);
									fireWallRulesTable.setId(fireWallRules_id);
									struct.setProtocol(protocolTable);
									struct_id=sqlitedb.GetStructIdByCodeAndProtocol(struct,0,1);
									struct.setId(struct_id);
									
								for(StructsFieldsTable structField:structFields)
								{
									structField.setStruct(struct);
									if(checkValidationStructFieldName(structField)==true)
									{
										isToInsert=false;
										index=i;
									}
									i++;
								}
								
								if(isToInsert==true)
								{
								for(StructsFieldsTable structField:structFields)
			    	        	  {
									structField_id=sqlitedb.GetStructFieldId(structField,0);//here is the insertion
			    	        	  }
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Wrong values at row number "+index+1+", try again");
								}
	    	        	  	}
	    	        	  catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null,e1.toString());
						}
	    	        	  if(isToInsert==true)
	    	        	  {
		    	        	  JOptionPane.showMessageDialog(null, "Successfuly added");  
		    	        	  frm.dispose();
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


