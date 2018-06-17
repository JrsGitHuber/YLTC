package com.uds.yl.utils;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableUtils {

	public static void setRowBackgroundColor(JTable table, final List<Integer> redIndexList,final int cloumnIndex) {
		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (redIndexList.contains(row) && cloumnIndex == column) {
						component.setBackground(Color.RED);
					}else if(!redIndexList.contains(row) && cloumnIndex == column){
						component.setBackground(Color.GREEN);
					}else{
						component.setBackground(Color.WHITE);
						component.setForeground(Color.BLACK);
					}
					return component;
				}
			};
			table.setDefaultRenderer(Object.class, tcr);
			table.updateUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
