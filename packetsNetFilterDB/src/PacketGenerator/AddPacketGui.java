package PacketGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;


public class AddPacketGui extends GUI {
	File file;
	FileWriter fileWriter;
	FileReader fileReader;
	sqliteDB sqlitedb;
	
	public AddPacketGui(int width,int height,String msg,File file) throws IOException, SQLException
	{
		super(width,height,msg);
		this.file=file;
		fileReader=new FileReader(file);
		sqlitedb=sqliteDB.getSqliteDBInstance();
	}
	
	@Override
	public void createGUI() throws SQLException
	{
		
		JLabel ipLabel=super.createLabel(frm, "Enter Proper ip:", 230, 50, 200, 100);
		JTextField ipTextField=super.createTextField(frm, 510, 80, 200, 50);
		JLabel portLabel=super.createLabel(frm, "Enter Proper port:", 230, 200, 200, 100);
		JTextField portTextField=super.createTextField(frm, 510, 230, 200, 50);
		JLabel structCodeLabel=super.createLabel(frm, "Enter Proper Struct code:", 230, 350, 200, 100);
		JTextField structCodeTextField=super.createTextField(frm, 510, 380, 200, 50);
		JButton generateBtn=super.createButton(frm, "Generate", 300, 500, 200, 50);
		
		
		generateBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				 JSONObject finalFile = new JSONObject();
				 JSONParser parser=new JSONParser();
				 JSONObject packetAsJson=new JSONObject();
				 JSONObject packetDetails=new JSONObject();
				 JSONObject structFieldAsJson=new JSONObject();
				 JSONObject structFieldDetails;
				 JSONArray structFieldsAsJsonArray=new JSONArray();
				 JSONObject existedJsonFile;
				 JSONArray existedJsonFileAsJsonArray;
				 ArrayList<StructFieldsTable> structFields=null;
				 String ip=ipTextField.getText();
				 int port=Integer.valueOf(portTextField.getText());
				 int structCode=Integer.valueOf(structCodeTextField.getText());
				 
				 try 
				 {
					 structFields=sqlitedb.getAllStructFields(ip, port, structCode);
				 } 
				 catch (SQLException e1) 
				 {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
				 	
				 	packetDetails.put("Ip", ip);
				 	packetDetails.put("Port", port);
					packetDetails.put("StructCode", structCode);
					for(int i=0;i<structFields.size();i++)
					 {
						structFieldDetails=new JSONObject();
						structFieldDetails.put("Type", structFields.get(i).getType());
						//structFieldDetails.put("MinRange",structFields.get(i).getMinRange());
						//structFieldDetails.put("MaxRange", structFields.get(i).getMaxRange());
						InsetStructFieldRangeIntoJsonObject(structFieldDetails,structFields.get(i).getMinRange(),structFields.get(i).getType(),"MinRange");
						InsetStructFieldRangeIntoJsonObject(structFieldDetails,structFields.get(i).getMaxRange(),structFields.get(i).getType(),"MaxRange");
						structFieldAsJson.put("struct Field", structFieldDetails);
						structFieldsAsJsonArray.add(structFieldAsJson);
					 }
					packetDetails.put("Struct Fields", structFieldsAsJsonArray);
					packetAsJson.put("packet", packetDetails);
					try 
					{
						System.out.println(packetAsJson.toString());
						//existedJsonFile=(Object)parser.parse(System.getProperty("user.home")+file.getPath().substring(System.getProperty("user.home").length()));
						existedJsonFile=(JSONObject)parser.parse(fileReader);
						existedJsonFileAsJsonArray=(JSONArray)existedJsonFile.get("Packets");
						//existedJsonFileAsJsonArray=(JSONArray)existedJsonFile;
						System.out.println(existedJsonFileAsJsonArray);
						//existedJsonFileAsJsonArray.add(packetAsJson);
						existedJsonFileAsJsonArray.add(packetDetails);
						finalFile.put("Packets", existedJsonFileAsJsonArray);
						fileWriter=new FileWriter(file);
						fileWriter.write(finalFile.toJSONString());
						fileWriter.flush();
						fileWriter.close();
						fileReader.close();
						frm.dispose();
						 /*StringWriter out = new StringWriter();
						 protocolAsJsonObject.writeJSONString(out);
						 String jsonString=out.toString();
						 System.out.println(jsonString);*/
					} 
					catch (IOException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		});
		
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void InsetStructFieldRangeIntoJsonObject(JSONObject structFieldsDetails,Object range,String type,String whichRange)
	{
		if(type.equals("INT"))
		{
			structFieldsDetails.put(whichRange,Integer.valueOf(range.toString()));
		}
		if(type.equals("DOUBLE"))
		{
			structFieldsDetails.put(whichRange,Double.valueOf(range.toString()));
		}
		if(type.equals("FLOAT"))
		{
			structFieldsDetails.put(whichRange,Float.valueOf(range.toString()));
		}
		if(type.equals("CHAR"))
		{
			structFieldsDetails.put(whichRange,range.toString());
		}
		if(type.equals("SHORT"))
		{
			structFieldsDetails.put(whichRange,Short.valueOf(range.toString()));
		}
		if(type.equals("LONG"))
		{
			structFieldsDetails.put(whichRange,Long.valueOf(range.toString()));
		}
	}
}
