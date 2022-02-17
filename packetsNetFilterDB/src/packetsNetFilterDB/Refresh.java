package packetsNetFilterDB;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.omg.CORBA.DATA_CONVERSION;

public class Refresh {
	
	
	public static void refreshFireWallTable(JTable table,ArrayList<FIreWallRulesTable> data)
	{
		//System.out.println("before"+table.getModel().getRowCount());
		GUI.deleteAllJtableRows(table,"FireWallTable");
		Boolean isActive;
		//System.out.println("data size: "+data.size());
		for(FIreWallRulesTable fireWallRule:data)
		{
			if(fireWallRule.getActiveStatus()==1)
			{
				isActive=true;
			}
			else
			{
				isActive=false;
			}
			((DefaultTableModel) table.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp().toString(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName().toString(),isActive});
		}
		//System.out.println("after"+table.getModel().getRowCount());
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

}
