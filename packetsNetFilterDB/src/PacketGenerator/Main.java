package PacketGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;


public class Main {

	public static void main(String[] args) throws SQLException {
		//------------create mainGui/first window----------------//
		
	
		MainGui mainGui=new MainGui(800,600,"Packet Generator");
		mainGui.createGUI(mainGui.getFrm());
	}
}
