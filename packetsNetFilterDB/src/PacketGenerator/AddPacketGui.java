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
	//------------properties-------------------//
	File file;
	FileWriter fileWriter;
	FileReader fileReader;
	sqliteDB sqlitedb;
	
	//------C'tor---------------//
	public AddPacketGui(int width,int height,String msg,File file) throws IOException, SQLException
	{
		super(width,height,msg);
		this.file=file;
		fileReader=new FileReader(file);
		sqlitedb=sqliteDB.getSqliteDBInstance();
	}
	
	//----------functions---------------//
	@Override
	public void createGUI() throws SQLException
	{
		//-----------------Setting Vars---------------------------------//
		JLabel ipLabel=super.createLabel(frm, "Enter Proper ip:", 230, 50, 200, 100);
		JTextField ipTextField=super.createTextField(frm, 510, 80, 200, 50);
		JLabel portLabel=super.createLabel(frm, "Enter Proper port:", 230, 150, 200, 100);
		JTextField portTextField=super.createTextField(frm, 510, 180, 200, 50);
		JLabel protocolNameLabel=super.createLabel(frm, "Enter Protocol Name:", 230, 250, 200, 100);
		JTextField protocolNameTextField=super.createTextField(frm, 510, 280, 200, 50);
		JLabel structCodeLabel=super.createLabel(frm, "Enter Proper Struct code:", 230, 350, 200, 100);
		JTextField structCodeTextField=super.createTextField(frm, 510, 380, 200, 50);
		JButton generateBtn=super.createButton(frm, "Generate", 300, 500, 200, 50);
		
		//---------------Event functions--------------------//
		generateBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				 JSONObject packetDetails;
				 JSONObject structFieldAsJson = null;
				 JSONObject structFieldDetails = null;
				 JSONArray structFieldsAsJsonArray=new JSONArray();
				 ArrayList<StructFieldsTable> structFields=null;
				 
				 String ip=ipTextField.getText();
				 int port=Integer.valueOf(portTextField.getText());
				 String protocolName=protocolNameTextField.getText();
				 int structCode=Integer.valueOf(structCodeTextField.getText());
				 
				 try 
				 {
					 //-----------create packet with all struct fields-----------//
					structFields=sqlitedb.getAllStructFields(ip, port,protocolName, structCode); 
					packetDetails=InsertPacketMetaDataIntoJsonObject(ip,port,protocolName,structCode);	
					for(int i=0;i<structFields.size();i++)
						{
							structFieldsAsJsonArray.add(InsertStructFieldIntoJsonArray(structFieldDetails,structFieldAsJson, structFields, i));
						}
					InsertStructFieldsIntoPacket(packetDetails,structFieldsAsJsonArray);
					//-----------write packet into json file-----------//
					WritePacketIntoJsonFile(packetDetails);
					fileWriter.close();
					fileReader.close();
					frm.dispose();
					
					} 
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					 }
					 	
				}
		});
		
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void WritePacketIntoJsonFile(JSONObject packetDetails) throws IOException, ParseException
	{
		JSONObject finalFile = new JSONObject();
		JSONParser fileParser=new JSONParser();
		JSONObject existedJsonFile=(JSONObject)fileParser.parse(fileReader);
		JSONArray existedJsonFileAsJsonArray=(JSONArray)existedJsonFile.get("Packets");
		existedJsonFileAsJsonArray.add(packetDetails);
		finalFile.put("Packets", existedJsonFileAsJsonArray);
		fileWriter=new FileWriter(file);
		fileWriter.write(finalFile.toJSONString());
		fileWriter.flush();
	}
	public void InsertStructFieldsIntoPacket(JSONObject packetDetails,JSONArray structFieldsAsJsonArray)
	{
		packetDetails.put("Struct Fields", structFieldsAsJsonArray);
	}
	public JSONObject InsertPacketMetaDataIntoJsonObject(String ip,int port,String protocolName,int structCode)
	{
		JSONObject packetDetails=new JSONObject();
		packetDetails.put("Ip", ip);
	 	packetDetails.put("Port", port);
	 	packetDetails.put("Protocol Name", protocolName);
		packetDetails.put("StructCode", structCode);
		return packetDetails;
	}
	
	public JSONObject InsertStructFieldIntoJsonArray(JSONObject structFieldDetails,JSONObject structFieldAsJson, ArrayList<StructFieldsTable> structFields,int i)
	{
		structFieldDetails=new JSONObject();
		structFieldAsJson=new JSONObject();
		structFieldDetails.put("Type", structFields.get(i).getType());
		InsertStructFieldRangeIntoJsonObject(structFieldDetails,structFields.get(i).getMinRange(),structFields.get(i).getType(),"MinRange");
		InsertStructFieldRangeIntoJsonObject(structFieldDetails,structFields.get(i).getMaxRange(),structFields.get(i).getType(),"MaxRange");
		structFieldAsJson.put("struct Field", structFieldDetails);
		return structFieldAsJson;
		
	}
	public void InsertStructFieldRangeIntoJsonObject(JSONObject structFieldsDetails,String range,String type,String whichRange)
	{
		if(type.equals("INT"))
		{
			structFieldsDetails.put(whichRange,Integer.valueOf(range));
		}
		if(type.equals("DOUBLE"))
		{
			structFieldsDetails.put(whichRange,Double.valueOf(range));
		}
		if(type.equals("FLOAT"))
		{
			structFieldsDetails.put(whichRange,Float.valueOf(range));
		}
		if(type.equals("CHAR"))
		{
			structFieldsDetails.put(whichRange,range);
		}
		if(type.equals("SHORT"))
		{
			structFieldsDetails.put(whichRange,Short.valueOf(range));
		}
		if(type.equals("LONG"))
		{
			structFieldsDetails.put(whichRange,Long.valueOf(range));
		}
	}
}
