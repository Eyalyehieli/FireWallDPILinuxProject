package PacketGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;

public class projectConfig extends GUI {

	//-----------properties---------------//
	private String password;
	
	//----------C'tor-----------------------//
	public projectConfig(int width,int height,String msg,String password)
	{
		super(width,height,msg);
		this.password=password;
	}
	
	//------------functions------------------//
	@Override
	public void createGui()
	{
		//-----------Setting Vars-----------------//
		JButton commitNfqToIptables=super.createButton(frm, "Commit Nfq",100 ,120, 200, 100);
		JButton unCommitNfqToIptables=super.createButton(frm, "UnCommit Nfq", 500, 120, 200, 100);
		JButton insmodMITM=super.createButton(frm, "Insmod MITM", 100, 300, 200, 100);
		JButton rmmodMITM=super.createButton(frm, "Rmmod MITM",500 , 300, 200, 100);
		
		//----------Event functions-----------------//
		commitNfqToIptables.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					execBashCommandAsRoot("sudo -S iptables -I INPUT -j NFQUEUE --queue-num 0");
					Thread.sleep(500);
					frm.dispose();
				} 
				catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		unCommitNfqToIptables.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				try 
				{
					execBashCommandAsRoot("sudo -S iptables -F");
					Thread.sleep(500);
					frm.dispose();
				} 
				catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		insmodMITM.addActionListener(new ActionListener()
		{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						execBashCommand("cd /home/eyalyehieli/Desktop/FireWallProject/FireWallDPILinuxProject/MITM && make clean");
						execBashCommand("cd /home/eyalyehieli/Desktop/FireWallProject/FireWallDPILinuxProject/MITM && make");
						execBashCommandAsRoot("sudo -S insmod /home/eyalyehieli/Desktop/FireWallProject/FireWallDPILinuxProject/MITM/myMITM.ko");
						Thread.sleep(500);
						frm.dispose();
					} 
					catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		});
		
		rmmodMITM.addActionListener(new ActionListener()
		{
				public void actionPerformed(ActionEvent e)
				{
					try 
					{
						execBashCommandAsRoot("sudo -S rmmod /home/eyalyehieli/Desktop/FireWallProject/FireWallDPILinuxProject/MITM/myMITM.ko");
						Thread.sleep(500);
						frm.dispose();
					} 
					catch (InterruptedException e1) {
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
	
	public void execBashCommand(String command)
	{
		String[] cmd = {"/bin/bash","-c",command};
		Process pb=null;
		 try 
		 {
			 pb = Runtime.getRuntime().exec(cmd);
			 Thread.sleep(200);
	     } 
		 catch (IOException | InterruptedException e)  {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void execBashCommandAsRoot(String command)
	{
		String[] cmd = {"/bin/bash","-c","echo "+this.password+"| "+command};
		 try 
		 {
			Process pb = Runtime.getRuntime().exec(cmd);
			Thread.sleep(200);
		 } 
		 catch (IOException | InterruptedException e1)  {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
