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
	List<TCComponentItemRevision> searchResultList = null;//table�е����ݶ�Ӧ�İ汾����
	List<String> nameList = null;//table�е����ݶ�Ӧ�İ汾������
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
			
			JLabel lblNewLabel = new JLabel("������׼��ȡ����");
			lblNewLabel.setFont(new Font("����", Font.BOLD, 15));
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
			
			JLabel lblNewLabel_1 = new JLabel("�������ƣ�");
			lblNewLabel_1.setBounds(31, 31, 75, 25);
			panel.add(lblNewLabel_1);
			
			JLabel label = new JLabel("�汾��");
			label.setBounds(362, 31, 54, 25);
			panel.add(label);
			
			selectRevisionEdt = new JTextField();
			selectRevisionEdt.setColumns(10);
			selectRevisionEdt.setBounds(426, 31, 163, 25);
			panel.add(selectRevisionEdt);
			selectRevisionEdt.setEditable(false);
			
			JLabel label_1 = new JLabel("���Ʊ�׼��");
			label_1.setFont(new Font("����", Font.BOLD, 12));
			label_1.setBounds(31, 84, 85, 25);
			panel.add(label_1);
			
			JLabel label_2 = new JLabel("���ƣ�");
			label_2.setBounds(31, 119, 54, 25);
			panel.add(label_2);
			
			searchNameEdt = new JTextField();
			searchNameEdt.setColumns(10);
			searchNameEdt.setBounds(116, 119, 152, 25);
			panel.add(searchNameEdt);
			
			JLabel label_3 = new JLabel("�汾��");
			label_3.setBounds(305, 119, 54, 25);
			panel.add(label_3);
			
			searchRevsionEdt = new JTextField();
			searchRevsionEdt.setColumns(10);
			searchRevsionEdt.setBounds(369, 119, 163, 25);
			panel.add(searchRevsionEdt);
			
			JButton searchBtn = new JButton("����");
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
		
			
			JButton copyBtn = new JButton("����");
			copyBtn.setBounds(238, 452, 93, 30);
			panel.add(copyBtn);
			
			JButton appendBtn = new JButton("׷��");
			appendBtn.setBounds(354, 452, 93, 30);
			panel.add(appendBtn);
			
			
			JButton cancleBtn = new JButton("ȡ��");
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			cancleBtn.setBounds(465, 453, 93, 30);
			panel.add(cancleBtn);
			
			//����ѡ�еİ汾�����
			selectNameEdt.setText(itemRev.getProperty("u8_techstandardtype"));
			selectRevisionEdt.setText(itemRev.getProperty("item_revision_id"));
			
			//����
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = searchNameEdt.getText();
					String revision = searchRevsionEdt.getText();
					
					
					//��ȡ��ѯ���  �������ֺͰ汾  ����������  ���� itemRevision����  ��������ʱ����ҵ���
					searchResultList = iTechStandardService.getSearchItemRevison(name, revision, "");
				    nameList= iTechStandardService.getSearchItemRevisonName(searchResultList);
					
				    //���Table
				    DefaultTableModel model = (DefaultTableModel) table.getModel();
				    int rowCount = model.getRowCount();
				    for(int i=rowCount-1;i>=0;i--){
				    	model.removeRow(i);
				    }
					//���½����table��
					String[] nameStrs = new String[nameList.size()];
					nameStrs = nameList.toArray(nameStrs);
					model.addRow(nameStrs);
				}
				
			});
			
			
			//����  ��ѡ�еļ�����׼��BOM�ṹ�����  ��table��ѡ�е�BOM�ṹճ����ȥ
			copyBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					//ѡ�еİ汾 �� table��ѡ�еİ�
					TCComponentItemRevision selectItemRevison = null;
					TCComponentItemRevision tableSelectItemRevison = null;
					
					int selectedIndex = table.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("��ѡ��","",MessageBox.ERROR);
						return;
					}
					tableSelectItemRevison = searchResultList.get(selectedIndex);
					iTechStandardService.copyBomToSelectedItemRevision(itemRev, tableSelectItemRevison);
					MessageBox.post("OK","",MessageBox.INFORMATION);
				}
			});
		
			
			//׷��
			appendBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					//ѡ�еİ汾 �� table��ѡ�еİ�
					TCComponentItemRevision selectItemRevison = null;
					TCComponentItemRevision tableSelectItemRevison = null;
					
					int selectedIndex = table.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("��ѡ��","",MessageBox.ERROR);
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
