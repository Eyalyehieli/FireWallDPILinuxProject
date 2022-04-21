package PacketGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import packetsNetFilterDB.StructsFieldsTable;

public class transmit 
{
	//----------------properties-----------------//
	File file;
	FileReader fileReader;
	sqliteDB sqlitedb;
	
	//------------C'tor----------------//
	public transmit(File file) throws IOException, SQLException
	{
		this.file=file;
		fileReader=new FileReader(file);
		 sqlitedb=sqliteDB.getSqliteDBInstance();
	}
	
	//---------------functions------------------//
	public void startTransmit() throws InterruptedException
	{
		
		//-----------Setting Vars------------------// 
		 JSONArray structFieldsAsJsonArray;
		 JSONObject currentPacket;
		 JSONObject currentStructField;
		 Iterator<JSONObject> packetIterator;
		 Iterator <JSONObject> structFieldsIterator;
		 ArrayList<StructFieldsTable> structFields = new ArrayList<StructFieldsTable>();
		 int port,structCode,packetSize;
		 String ip,protocolName;
		 
		try
		{
			packetIterator=getPacketsIterator();
			while(packetIterator.hasNext())
			{
				currentPacket=packetIterator.next();
				structFieldsIterator=getStrcutFieldsIterator(currentPacket);
				while(structFieldsIterator.hasNext())
				{
					currentStructField=(JSONObject) structFieldsIterator.next().get("struct Field");
					structFields.add(createNewStructFieldFromJsonFile(currentStructField));
				}
				//------------extract meta data of the packet-----------//
				packetSize=getPacketSize(structFields);
				port=Long.valueOf(currentPacket.get("Port").toString()).intValue();
				ip=(String)currentPacket.get("Ip");
				protocolName=(String)currentPacket.get("Protocol Name");
				structCode=Long.valueOf(currentPacket.get("StructCode").toString()).intValue();
				//---------send packet and clear structFields list for the next iteration----------//
				sendPacket(port,ip,protocolName,structCode,structFields,packetSize);
				structFields.clear();
				Thread.sleep(1000);
				
			}
		} 
		catch (IOException | ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public StructFieldsTable createNewStructFieldFromJsonFile(JSONObject currentStructField)
	{
		String type=(String) currentStructField.get("Type");
		Object minRange=currentStructField.get("MinRange");
		Object maxRange=currentStructField.get("MaxRange");
		return new StructFieldsTable(type,minRange.toString(),maxRange.toString());
	}
	public Iterator<JSONObject> getStrcutFieldsIterator(JSONObject currentPacket)
	{
		JSONArray structFieldsAsJsonArray = (JSONArray)currentPacket.get("Struct Fields");
		return structFieldsAsJsonArray.iterator();
	}
	
	public Iterator<JSONObject> getPacketsIterator() throws IOException, ParseException
	{ 
		JSONParser parser=new JSONParser();
		JSONObject existedJsonFile;
		JSONArray existedJsonFileAsJsonArray;
		existedJsonFile=(JSONObject)parser.parse(fileReader);
		existedJsonFileAsJsonArray=(JSONArray)existedJsonFile.get("Packets");
		return existedJsonFileAsJsonArray.iterator();
	}
	
	public void sendPacket(int port,String ip,String protocolName,int structCode ,ArrayList<StructFieldsTable> structFiedls,int packetSize) throws IOException, InterruptedException
	{
		try 
		{
			//////////////Server////////////////////
			 serverThread server =new serverThread(port,packetSize);
			 
			 /////////////Client//////////////////////
			 clientThread client=new clientThread(ip,port,sqlitedb.getProtocolIdByProtocolName(protocolName),structCode,structFiedls,packetSize);
			 
			 server.start();
			 //------wait for binding- the client do not sent the packet before the server wait for the packet (**udp problem)
			 server.waitForBind();
			 client.start();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		 
	}
	public int getPacketSize(ArrayList<StructFieldsTable> structFiedls)
	{
		int count=8;//8 => because structCode is integer and ProtocolId is integer => 4+4=8 => sizeof(int)=4
		for(StructFieldsTable structField:structFiedls)
		{
			count+=structField.getSizeOfRangeClass();
					
		}
		return count;
	}
	
}
