package packetsNetFilterDB;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrm extends GUI {
	
	public MainFrm(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	//-----------functions-----------------//
		@Override
		public void createGUI(JFrame frame) throws SQLException
		{
			JButton FireWallButton=super.createButton(frame, "FireWall Config", 100, 200, 200, 100);
			JButton protocolsButton=super.createButton(frame, "Protocols Config", 350, 200, 200, 100);
			JButton connectionsButton=super.createButton(frame, "Connections Config", 600, 200, 200, 100);
			
			FireWallButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						MainGUI mainGui=new MainGUI(1200,800,"FireWall Configuration");
						mainGui.createGUI(mainGui.getFrm());
					}
					catch (SQLException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			protocolsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						ProtocolConfigFrm protocolsConfigFrm=new ProtocolConfigFrm(1000,800,"Protocols Configurations");
						protocolsConfigFrm.createGUI(protocolsConfigFrm.getFrm());
					} 
					catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			connectionsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{

					try
					{
						ConnectionsConfigFrm connectionConfigFrm=new ConnectionsConfigFrm(1000,800,"Connections Configurations");
						connectionConfigFrm.createGUI(connectionConfigFrm.getFrm());
					}
					catch (SQLException e1) {
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

}
