package PacketGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;

public class MainGui extends GUI {
	
	public MainGui(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	
	@Override
	public void createGUI(JFrame frame) throws SQLException
	{
		JButton addPacketToJsonFileBtn=super.createButton(frm, "Add Packet", 500, 120, 200, 100);
		JButton transmitBtn=super.createButton(frm, "Start Transmit", 100, 120, 200, 100);
		JButton createJsonFileBtn=super.createButton(frm, "Create JSON File", 300, 300, 200, 100);
		
		transmitBtn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						
					}
				});
		
		createJsonFileBtn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						createJsonFileGui createJsonFile=new createJsonFileGui(800,600,"Create Json File");
						createJsonFile.createGUI();
					}
				});
		
		
		addPacketToJsonFileBtn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						int response=0;
						JFileChooser fileChooser=new JFileChooser();
						response=fileChooser.showOpenDialog(null);
						if(response!=JFileChooser.APPROVE_OPTION)
						{
						   JOptionPane.showMessageDialog(null, "Please Enter Json File");
						}
						else if(fileChooser.getSelectedFile().getName().toLowerCase().endsWith(".json")==false)
						{
							JOptionPane.showMessageDialog(null, "Please Enter Json File");
						}
						else
						{
							try 
							{
								AddPacketGui addPacketGui=new AddPacketGui(800,600,"Generate Packet",fileChooser.getSelectedFile());
								addPacketGui.createGUI();
							} 
							catch (SQLException e1) 
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
							catch (IOException e1) 
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
					}
				});
		
		
		
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public String getFileExtension(File file)
	{
		String extension = "";
		String fileName=file.getName();
		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
		    extension = fileName.substring(i+1);
		}
		return extension;
		
	}
	
}
