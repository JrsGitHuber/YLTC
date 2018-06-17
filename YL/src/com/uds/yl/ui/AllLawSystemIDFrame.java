package com.uds.yl.ui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.omg.CosNotification.DeadlineOrder;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.tcutils.QueryUtil;

public class AllLawSystemIDFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	
	private List<String> lawSystemIdList;


	/**
	 * Create the frame.
	 */
	public AllLawSystemIDFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 315, 402);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 271, 331);
		contentPane.add(scrollPane);
		
		table = new JTable(){
			@Override
			public boolean editCellAt(int row, int column) {
				return false;
			}
		};
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"体系ID"
			}
		));
		scrollPane.setViewportView(table);
		
		
		//初始化
		lawSystemIdList = new ArrayList<String>();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawRevsion.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"体系ID"}, new String[]{"*"});
		for(TCComponent component : searchResult){
			TCComponentItemRevision revision = (TCComponentItemRevision) component;
			try {
				String itemId = revision.getProperty("item_id");
				String systemID = itemId.split(" ")[0];
				lawSystemIdList.add(systemID);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for(int i=0;i<lawSystemIdList.size();i++){
			model.addRow(new String[]{ 
					lawSystemIdList.get(i) 
					});
		}
		
	}
}