package PacketGenerator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public class serverThread extends Thread {
	
	//------------properties----------------//
	private int port;
	private int packetSize;
	DatagramSocket server;
	private boolean isBinded = false;
	private Object syncRoot = new Object();
	
	//----------C'tor----------------//
	public serverThread(int port,int packetSize)
	{
		this.port=port;
		this.packetSize=packetSize;
	}
	
	//-----------functions---------------//
	public void run()
	{
		try 
		{
			//-----------Setting Vars-----------------//
			server = new DatagramSocket(port);
			server.setSoTimeout(6000);
			System.out.println("server started at port :"+port);
			byte[] serverBuffer = new byte[packetSize];
			DatagramPacket serverPacket  = new DatagramPacket(serverBuffer, serverBuffer.length);
			byte[] field=new byte[4];
			
			//-----------Synchronization for binding------------//
			synchronized (syncRoot)
			{
				isBinded = true;
				syncRoot.notifyAll();
			}
			
			//---------------after binding---------------------//
			System.out.println("server listening on port "+port+": ");
			
			server.receive(serverPacket);
			byte[] data=serverPacket.getData();
				
			System.out.println("got: "+data.length+" bytes");
			System.out.println(port + ": Got packet");
			server.close();
			System.out.println("server closed");
		} 
		catch (SocketTimeoutException e){
			System.out.println(port + ": Didn't get packet :(");
			server.close();
			System.out.println("server closed");
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void waitForBind() throws InterruptedException//this function executed in the main thread
	{ 
		synchronized (syncRoot)
		{
			if (isBinded)
			{
				return;
			}
			syncRoot.wait();//wait for binding from the server thread
		}
	}

}
