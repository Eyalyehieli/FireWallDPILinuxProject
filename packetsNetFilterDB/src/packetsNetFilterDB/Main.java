package packetsNetFilterDB;
import java.util.*;
import java.util.List;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Main {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		//Gui gui=new Gui(1200,800);
		//gui.createGui();
		MainGUI mainGUI=new MainGUI(1200,800,"MainGui");
		mainGUI.createGUI(mainGUI.getFrm());      
	}
}
