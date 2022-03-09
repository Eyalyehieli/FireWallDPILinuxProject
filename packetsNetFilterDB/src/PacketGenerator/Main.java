package PacketGenerator;

import java.sql.SQLException;


public class Main {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		MainGui mainGui=new MainGui(800,600,"Packet Generator");
		mainGui.createGUI(mainGui.getFrm());
	}
}
