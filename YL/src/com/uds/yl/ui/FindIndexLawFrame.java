package com.uds.yl.ui;

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
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.utils.StringsUtil;

public class FindIndexLawFrame extends JFrame {

	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private JPanel contentPane;
	private JTextField searchText;
	private JTable table;
	
	public String selectLawName;
	public TCComponentItemRevision selectLawRev;
	
	
	
	private List<TCComponentItemRevision> searchLawRevList;//搜索到的法规集合
	private List<String> searchNameList;//搜索到的法规名字集合

	/**
	 * Create the frame.
	 */
	public FindIndexLawFrame(final AbstractCallBack callBack) {
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 472, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("法规ID：");
		lblNewLabel.setBounds(41, 28, 85, 30);
		contentPane.add(lblNewLabel);
		
		searchText = new JTextField();
		searchText.setBounds(163, 29, 115, 30);
		contentPane.add(searchText);
		searchText.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(94, 105, 249, 113);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"名称"
			}
		));
		scrollPane.setViewportView(table);
		
		JButton findBtn = new JButton("查找");
		findBtn.setBounds(333, 32, 71, 30);
		contentPane.add(findBtn);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(66, 257, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(273, 257, 93, 30);
		contentPane.add(cancleBtn);
		
		
		findBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						ProgressBarDialog progressBarDialog = new ProgressBarDialog();
						progressBarDialog.start();
						
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						//先清除
						int rowCount = model.getRowCount();
						for(int i=0;i<rowCount;i++){
							model.removeRow(0);
						}
						String searchName = searchText.getText();
						if(StringsUtil.isEmpty(searchName)){//如果输入是空 就不让搜索
							MessageBox.post("请输入法规名称","",MessageBox.INFORMATION);
							return;
						}
						//添加新搜索到的内容
						searchLawRevList = iFormulatorLegalCheckService.getCheckLawRevList("*"+searchName+"*");
						if(searchLawRevList==null) return;
						searchNameList = iFormulatorLegalCheckService.getCheckLawNameList(searchLawRevList);
						for(String name : searchNameList){
							model.addRow(new String[]{name});
						}		
						
						progressBarDialog.stop();
					}
				}).start();
						
			}
		});
		
		
		//确定按钮
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				int selectedIndex = table.getSelectedRow();
				if(selectedIndex==-1){
					MessageBox.post("请选择法规","",MessageBox.INFORMATION);//请选择法规
					return;
				}
				
				selectLawName = searchNameList.get(selectedIndex);
				selectLawRev = searchLawRevList.get(selectedIndex);
				
				callBack.setLawAndName(selectLawName, selectLawRev);
				dispose();
			}
		});
		
		//取消
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				return;
			}
		});
		
	}
}