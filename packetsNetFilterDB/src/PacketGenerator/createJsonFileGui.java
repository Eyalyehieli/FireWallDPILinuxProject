package PacketGenerator;

import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import org.json.simple.*;

import javax.swing.*;

public class createJsonFileGui extends GUI {
	//--------------properties------------//
	FileWriter fileWriter;
	
	//------------c'tor-------------------//
	public createJsonFileGui(int width,int height,String msg)
	{
		super(width, height, msg);
	}

	//----------------functions----------------//
	@Override
	public void createGUI() 
	{
		//---------------Setting Vars--------------------//
		JLabel fileNameLabel=super.createLabel(frm, "Enter File Name:", 150, 100, 200, 100);
		JTextField fileNameTextField=super.createTextField(frm, 300, 130, 300, 50);
		JLabel filePathLabel=super.createLabel(frm, "Enter File Path:", 150, 250, 200, 100);
		JTextField filePathTextField=super.createTextField(frm, 300, 280, 400, 50);
		JButton createBtn=super.createButton(frm, "Create", 300, 400, 200, 100);
		
		//---------------Event functions-------------------//
		createBtn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						String filePath=filePathTextField.getText();
						String fileName=fileNameTextField.getText();
						filePath+="/"+fileName;
						try
						{
							createPakcetsJsonFile(filePath);
							fileWriter.close();
							JOptionPane.showMessageDialog(null, "Succefffuly created");
							frm.dispose();
						}
						catch (IOException e1) {
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
	public void createPakcetsJsonFile(String filePath) throws IOException
	{
		JSONObject packetsAsJson=new JSONObject();
		JSONArray packetsList=new JSONArray();
		fileWriter=new FileWriter(filePath);
		packetsAsJson.put("Packets", packetsList);
		fileWriter.write(packetsAsJson.toJSONString());
		fileWriter.flush();
	}
}
