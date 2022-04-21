package PacketGenerator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class clientThread {
	
	//--------------properties--------------//
	private String ip;
	private int port;
	private int protocolId;
	private int structCode;
	private int packetSize;
	ArrayList<StructFieldsTable> structFields;
	
	//--------C'tor-------------------//
	public clientThread(String ip,int port,int protocolId,int structCode,ArrayList<StructFieldsTable>structFields,int packetSize)
	{
		this.ip=ip;
		this.port=port;
		this.protocolId=protocolId;
		this.structCode=structCode;
		this.packetSize=packetSize;
		this.structFields=structFields;
	}
	
	//-------------functions---------------------//
	public void start()
	{
		
		try 
		{	
			//---------------Setting Vars--------------------//
			 DatagramSocket client = new DatagramSocket();
			 DatagramPacket clientPacket;
			 InetAddress serverIp = InetAddress.getByName(ip);
			 byte[] clientBuffer=new byte[packetSize];
			 int currentSize=0;
			 byte[]currentStructFieldInBytes;
			 
			 //-------Insert protocol Id into clientBuffer------//
			 currentStructFieldInBytes=copyStructFieldIntoByteArray(protocolId);
			 memcpyToPacketBuffer(clientBuffer,currentStructFieldInBytes,currentSize);
			 currentSize=4;
			 
			 //--------Insert structCode into clientBuffer------//
			 currentStructFieldInBytes=copyStructFieldIntoByteArray(structCode);
			 memcpyToPacketBuffer(clientBuffer,currentStructFieldInBytes,currentSize);
			 currentSize=8;
			 
			 //------------Insert structfields into clientBuffer-------------//
			 for(StructFieldsTable structField:structFields)
				{
				 currentStructFieldInBytes=structField.rangeAsByteArray();
				 memcpyToPacketBuffer(clientBuffer,currentStructFieldInBytes,currentSize);
				 currentSize+=structField.getSizeOfRangeClass();
				}
			 
			 //-------------send packet-----------------------//
			 clientPacket=new DatagramPacket(clientBuffer,clientBuffer.length,serverIp,port);
			 client.send(clientPacket);
			 client.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public byte[] copyStructFieldIntoByteArray(int num)
	{
		 byte[] currentStructFieldInBytes=ByteBuffer.allocate(Integer.BYTES).putInt(num).array();//structCode as byte array
		 return currentStructFieldInBytes;
	}
	public void memcpyToPacketBuffer(byte[] dest,byte[] source,int destIndex)
	{
		for(int i=0;i<source.length;i++)
		{
			dest[destIndex]=source[i];
			destIndex++;
		}
	}
	
}
