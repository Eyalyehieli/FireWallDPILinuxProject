package packetsNetFilterDB;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.omg.CORBA.DATA_CONVERSION;

public class Refresh {
	
	//--------------functions--------------------//
	public static void refreshFireWallTable(JTable table,ArrayList<FIreWallRulesTable> data)
	{
		GUI.deleteAllJtableRows(table,"FireWallTable");
		Boolean isActive;
		for(FIreWallRulesTable fireWallRule:data)
		{
			isActive=fireWallRule.getActiveStatus()==1?true:false;
			((DefaultTableModel) table.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp().toString(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName().toString(),isActive});
		}
	}
	
	public static void refreshStructFieldsTable(JTable table,ArrayList<StructsFieldsTable> data)
	{
		GUI.deleteAllJtableRows(table,"StructFieldsTable");
		for(StructsFieldsTable structField:data)
		{
			((DefaultTableModel)table.getModel()).addRow(new Object[]{structField.getName(),structField.getType(),structField.getMinRange(),structField.getMaxRange()});
		}
	}
	
	public static void refreshStructsTable(JTable table ,ArrayList<StructsTable> data)
	{
		GUI.deleteAllJtableRows(table,"StrcutTable");
		for(StructsTable struct:data)
		{
			((DefaultTableModel) table.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
		}
	}
	
	public static void refreshProtocolsTable(JTable table,ArrayList<String> data)
	{
		GUI.deleteAllJtableRows(table, "Protocols");
		for(String protocol:data)
		{
			((DefaultTableModel) table.getModel()).addRow(new Object[]{protocol});
		}
	}
	
	public static void refreshConnectionTable(JTable table,ArrayList<ConnectionTable> data)
	{
		GUI.deleteAllJtableRows(table, "Protocols");
		for(ConnectionTable connection:data)
		{
			((DefaultTableModel) table.getModel()).addRow(new Object[]{connection.getIp(),connection.getPort()});
		}
	}

}
