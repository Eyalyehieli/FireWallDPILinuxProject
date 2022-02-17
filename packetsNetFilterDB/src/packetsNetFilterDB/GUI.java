package packetsNetFilterDB;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class GUI {
	protected int width;
	protected int height;
	protected JFrame frm;
	public GUI(int width,int height,String msg)
	{
		this.width=width;
		this.height=height;
		frm=this.createFrame(msg);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public JFrame getFrm() {
		return frm;
	}

	public void setFrm(JFrame frm) {
		this.frm = frm;
	}

	public void createGUI() throws SQLException
	{
	    System.out.println("in create GUI func");	
	}
	
	public void createGUI(JFrame frame) throws SQLException
	{
		System.out.println("in create GUI func with frm");	
	}
	
	public JComboBox createComboBox(JFrame frm, String[] values,int x,int y,int width,int height)
	{
		JComboBox cmbBox=new JComboBox(values);
		cmbBox.setBounds(x, y, width, height);
		frm.add(cmbBox);
		return cmbBox;
	}

	public JTextField createTextField(JFrame frm,int x,int y,int width,int height)
	{
		JTextField txtField=new JTextField();
		txtField.setBounds(x, y, width, height);
		frm.add(txtField);
		return txtField;
	}
	
	public JButton createButton(JFrame frm,String messIn,int x,int y,int width,int height)
	{
		JButton btn=new JButton(messIn);
		btn.setBounds(x, y, width, height);
		frm.add(btn);
		return btn;
	}

	public JLabel createLabel(JFrame frm,String messIn,int x,int y,int width,int height)
	{
		JLabel label=new JLabel(messIn);
		label.setBounds(x, y, width, height);
		frm.add(label);
		return label;
	}
	
	public JTable createTable(JFrame frm,DefaultTableModel model,int x,int y,int width,int height)
	{
		JPanel panel=new JPanel();
		panel.setBounds(x, y, width, height);
		JTable table=new JTable(model);
		table.setBounds(x, y, width, height);
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		panel.add(new JScrollPane(table));
		frm.add(panel);
		return table;
	}
	
	
	public JTable createTable(JFrame frm,String[][] data,String[] columnNames,int x,int y,int width,int height)
	{
		JPanel panel=new JPanel();
		panel.setBounds(x, y, width, height);
		JTable table=new JTable(data,columnNames);
		table.setBounds(x, y, width, height);
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		panel.add(new JScrollPane(table));
		frm.add(panel);
		return table;
	}
	
	public static void deleteAllJtableRows(JTable table,String TableName) 
	{
		DefaultTableModel dm = (DefaultTableModel)table.getModel();
		while(dm.getRowCount()>0)
		{
			dm.removeRow(0);
		}
	}
	
	public JFrame createFrame(String messIn)
	{
		JFrame frm=new JFrame(messIn);
		return frm;
	}
	

}
