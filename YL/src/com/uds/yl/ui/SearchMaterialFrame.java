package com.uds.yl.ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.StringsUtil;

public class SearchMaterialFrame extends JFrame {

	private JPanel contentPane;
	private JTextField materialNameText;
	private JTable materialTable;

	private AbstractCallBack  mCallBack;

	private List<TCComponentItemRevision> searchMaterialRevList;
	/**
	 * Create the frame.
	 */
	public SearchMaterialFrame(AbstractCallBack callBack) {
		setTitle("ԭ�ϲ�ѯ");
		this.mCallBack = callBack;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 527, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("���ƣ�");
		lblNewLabel.setBounds(56, 27, 54, 15);
		contentPane.add(lblNewLabel);
		
		materialNameText = new JTextField();
		materialNameText.setBounds(133, 24, 138, 21);
		contentPane.add(materialNameText);
		materialNameText.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(56, 79, 392, 99);
		contentPane.add(scrollPane);
		
		materialTable = new JTable();
		materialTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"����"
			}
		));
		scrollPane.setViewportView(materialTable);
		
		JButton searchBtn = new JButton("����");
		searchBtn.setBounds(334, 23, 93, 23);
		contentPane.add(searchBtn);
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(71, 211, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(261, 211, 93, 23);
		contentPane.add(cancleBtn);
		
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ѡ�е�ѡ��
				DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
				int selectedRow = materialTable.getSelectedRow();
				if(selectedRow==-1){//û��ѡ��
					MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
					return;
				}
				TCComponentItemRevision selectRev = searchMaterialRevList.get(selectedRow);
				
				
				//���ûص��ӿ�
				mCallBack.setMaterialItem(selectRev);
				dispose();
			}
		});
		
		
		//��ѯԭ�ϰ汾
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String materialName = materialNameText.getText();
				if(StringsUtil.isEmpty(materialName)){//����Ϊ�վ���ʾ������
					MessageBox.post("�������ѯԭ�ϵ�����","",MessageBox.INFORMATION);
					return;
				}
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_CD.getValue());
				if(query==null){
					MessageBox.post("������ԭ�ϰ汾��ѯ��","",MessageBox.ERROR);
					return;
				}
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����"}, new String[]{"*"+materialName+"*"});
				searchMaterialRevList = new ArrayList<>();
				for(TCComponent component:searchResult){
					TCComponentItemRevision revision = (TCComponentItemRevision) component;
					try {
						if(revision.getItem()==null){
							continue;
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
					searchMaterialRevList.add(revision);
				}
				
				//����Table
				DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
				int rowCount = model.getRowCount();
				for(int i=0;i<rowCount;i++){
					model.removeRow(0);
				}
				
				for(int i=0;i<searchMaterialRevList.size();i++){
					String name = "";//ԭ�ϵ�����
					try {
						name = searchMaterialRevList.get(i).getProperty("object_name");
					} catch (TCException e) {
						e.printStackTrace();
					}
					model.addRow(new String[]{name});
				}
			}
		});
	
	
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
}