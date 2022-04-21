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
		//------------create mainGui/first window----------------//
		MainFrm mainFrm =new MainFrm(1000,600,"MainGui");
		mainFrm.createGUI(mainFrm.getFrm());      
	}
}
