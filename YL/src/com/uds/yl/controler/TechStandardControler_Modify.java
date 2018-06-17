package com.uds.yl.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;


import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.service.ITechStandardService;
import com.uds.yl.service.impl.TechStandardServiceImpl;

public class TechStandardControler_Modify implements BaseControler {
	private ITechStandardService iTechStandardService = new TechStandardServiceImpl();
	List<TCComponentItemRevision> searchResultList = null;//table中的数据对应的版本对象
	List<String> nameList = null;//table中的数据对应的版本的名字
	@Override
	public void userTask(final TCComponentItemRevision itemRev) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					TechStandardUI frame = new TechStandardUI(itemRev);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	class TechStandardUI extends JFrame {

		private JPanel contentPane;
		private JTextField selectNameEdt;
		private JTextField selectRevisionEdt;
		private JTextField searchNameEdt;
		private JTextField searchRevsionEdt;
		private JTable table;

		/**
		 * Create the frame.
		 * @throws TCException 
		 */
		public TechStandardUI(final TCComponentItemRevision itemRev) throws TCException {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 802, 615);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("技术标准调取工具");
			lblNewLabel.setFont(new Font("宋体", Font.BOLD, 15));
			lblNewLabel.setBounds(22, 10, 161, 31);
			contentPane.add(lblNewLabel);
			
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(22, 51, 754, 505);
			contentPane.add(panel);
			panel.setLayout(null);
			
			selectNameEdt = new JTextField();
			selectNameEdt.setBounds(116, 31, 152, 25);
			panel.add(selectNameEdt);
			selectNameEdt.setColumns(10);
			selectNameEdt.setEditable(false);
			
			JLabel lblNewLabel_1 = new JLabel("类型名称：");
			lblNewLabel_1.setBounds(31, 31, 75, 25);
			panel.add(lblNewLabel_1);
			
			JLabel label = new JLabel("版本：");
			label.setBounds(362, 31, 54, 25);
			panel.add(label);
			
			selectRevisionEdt = new JTextField();
			selectRevisionEdt.setColumns(10);
			selectRevisionEdt.setBounds(426, 31, 163, 25);
			panel.add(selectRevisionEdt);
			selectRevisionEdt.setEditable(false);
			
			JLabel label_1 = new JLabel("近似标准：");
			label_1.setFont(new Font("宋体", Font.BOLD, 12));
			label_1.setBounds(31, 84, 85, 25);
			panel.add(label_1);
			
			JLabel label_2 = new JLabel("名称：");
			label_2.setBounds(31, 119, 54, 25);
			panel.add(label_2);
			
			searchNameEdt = new JTextField();
			searchNameEdt.setColumns(10);
			searchNameEdt.setBounds(116, 119, 152, 25);
			panel.add(searchNameEdt);
			
			JLabel label_3 = new JLabel("版本：");
			label_3.setBounds(305, 119, 54, 25);
			panel.add(label_3);
			
			searchRevsionEdt = new JTextField();
			searchRevsionEdt.setColumns(10);
			searchRevsionEdt.setBounds(369, 119, 163, 25);
			panel.add(searchRevsionEdt);
			
			JButton searchBtn = new JButton("查找");
			searchBtn.setBounds(639, 120, 93, 25);
			panel.add(searchBtn);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(53, 178, 653, 233);
			panel.add(scrollPane);
			
			table = new JTable(){
				@Override
				public boolean isCellEditable(int i, int j) {
					return false;
				}
			};
			table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"\u540D\u79F0"
				}
			));
			table.getColumnModel().getColumn(0).setPreferredWidth(131);
			scrollPane.setViewportView(table);
		
			
			JButton copyBtn = new JButton("复制");
			copyBtn.setBounds(238, 452, 93, 30);
			panel.add(copyBtn);
			
			JButton appendBtn = new JButton("追加");
			appendBtn.setBounds(354, 452, 93, 30);
			panel.add(appendBtn);
			
			
			JButton cancleBtn = new JButton("取消");
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			cancleBtn.setBounds(465, 453, 93, 30);
			panel.add(cancleBtn);
			
			//设置选中的版本的类别
			selectNameEdt.setText(itemRev.getProperty("u8_techstandardtype"));
			selectRevisionEdt.setText(itemRev.getProperty("item_revision_id"));
			
			//查找
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = searchNameEdt.getText();
					String revision = searchRevsionEdt.getText();
					
					
					//获取查询结果  根据名字和版本  和上面的类别  返回 itemRevision数组  这里是临时查查找到的
					searchResultList = iTechStandardService.getSearchItemRevison(name, revision, "");
				    nameList= iTechStandardService.getSearchItemRevisonName(searchResultList);
					
				    //清空Table
				    DefaultTableModel model = (DefaultTableModel) table.getModel();
				    int rowCount = model.getRowCount();
				    for(int i=rowCount-1;i>=0;i--){
				    	model.removeRow(i);
				    }
					//更新结果在table中
					String[] nameStrs = new String[nameList.size()];
					nameStrs = nameList.toArray(nameStrs);
					model.addRow(nameStrs);
				}
				
			});
			
			
			//复制  将选中的技术标准的BOM结构清除掉  将table中选中的BOM结构粘贴上去
			copyBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					//选中的版本 和 table中选中的版
					TCComponentItemRevision selectItemRevison = null;
					TCComponentItemRevision tableSelectItemRevison = null;
					
					int selectedIndex = table.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("请选择","",MessageBox.ERROR);
						return;
					}
					tableSelectItemRevison = searchResultList.get(selectedIndex);
					iTechStandardService.copyBomToSelectedItemRevision(itemRev, tableSelectItemRevison);
					MessageBox.post("OK","",MessageBox.INFORMATION);
				}
			});
		
			
			//追加
			appendBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					//选中的版本 和 table中选中的版
					TCComponentItemRevision selectItemRevison = null;
					TCComponentItemRevision tableSelectItemRevison = null;
					
					int selectedIndex = table.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("请选择","",MessageBox.ERROR);
						return;
					}
					tableSelectItemRevison = searchResultList.get(selectedIndex);
					iTechStandardService.appendBomToSelectedItemRevision(itemRev, tableSelectItemRevison);
					MessageBox.post("OK","",MessageBox.INFORMATION);
				}
			});
		}
	}

}
