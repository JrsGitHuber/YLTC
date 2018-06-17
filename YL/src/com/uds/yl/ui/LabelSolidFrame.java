package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.controler.LabelGeneratorSolidExcelControler;
import com.uds.yl.tcutils.QueryUtil;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

//��̬��ǩ������  Ҫ�������
public class LabelSolidFrame extends JFrame {

	private JPanel contentPane;
	
	private JComboBox wetLossCombox;//������ʾʪ����ĵ�combox
	private JComboBox dryLossCombox;//������ʾ�ɷ���ĵ�combox
	private JComboBox dateLossCombox;//������ʾ��������ĵ�combox
	private JComboBox typeCombox;//������ʾ�䷽����combox
	private JButton showWetLossBtn;//ʪ�����ģʽ�Ĳ鿴
	private JButton showDryLossBtn;//�ɷ����ģʽ�Ĳ鿴
	private JButton showDateLossBtn;//���������ģʽ�Ĳ鿴
	private JButton okBtn;
	private JButton cancleBtn;
	
	
	private List<String> lossNameList;//���ģʽ����ʾ��combox�е�ֵ
	private List<TCComponentItemRevision> lossItemRevList;//ģʽ��ĵ����еļ���
	private TCComponentItemRevision wetLossItemRevsion;//ѡ�е�ʪ�����ģʽ
	private TCComponentItemRevision dryLossItemRevsion;//ѡ�еĸɷ����ģʽ
	private TCComponentItemRevision dateLossItemRevsion;//ѡ�еı��������ģʽ
	
	
	private TCComponentItemRevision itemRevision ;

	/**
	 * Create the frame.
	 */
	public LabelSolidFrame(TCComponentItemRevision itemRv ) {
		this.itemRevision = itemRv;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel label_2 = new JLabel("ʪ�����:");
		label_2.setBounds(18, 51, 111, 15);
		contentPane.add(label_2);
		
		
		wetLossCombox = new JComboBox();
		wetLossCombox.setBounds(167, 48, 111, 21);
		contentPane.add(wetLossCombox);
		
		showWetLossBtn = new JButton("�鿴");
		showWetLossBtn.setBounds(315, 47, 77, 23);
		contentPane.add(showWetLossBtn);
		
		
		JLabel label_8 = new JLabel("�ɷ���ģ�");
		label_8.setBounds(18, 99, 125, 15);
		contentPane.add(label_8);
		
		JLabel label_9 = new JLabel("��������ģ�");
		label_9.setBounds(18, 155, 111, 15);
		contentPane.add(label_9);
		
		dryLossCombox = new JComboBox();
		dryLossCombox.setBounds(167, 96, 111, 21);
		contentPane.add(dryLossCombox);
		
		dateLossCombox = new JComboBox();
		dateLossCombox.setBounds(167, 152, 111, 21);
		contentPane.add(dateLossCombox);
		
		showDryLossBtn = new JButton("�鿴");
		showDryLossBtn.setBounds(315, 95, 77, 23);
		contentPane.add(showDryLossBtn);
		
		showDateLossBtn = new JButton("�鿴");
		showDateLossBtn.setBounds(315, 151, 77, 23);
		contentPane.add(showDateLossBtn);
		
		okBtn = new JButton("ȷ��");
		okBtn.setBounds(75, 213, 93, 23);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(260, 213, 93, 23);
		contentPane.add(cancleBtn);
		
		
		//��ʼ����ĵ�˵��
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LOSSITEM.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����" }, new String[] { "*" });
		lossNameList = new ArrayList<>();
		lossItemRevList = new ArrayList<>();
		for (TCComponent component : searchResult) {
			if (component instanceof TCComponentItem) {
				TCComponentItem lossItem = (TCComponentItem) component;
				try {
					String lossItemName = lossItem.getProperty("object_name");
					TCComponentItemRevision lossItemRev = lossItem.getLatestItemRevision();
					if (lossItemRev == null) {
						continue;
					}
					lossNameList.add(lossItemName);
					wetLossCombox.addItem(lossItemName);
					dryLossCombox.addItem(lossItemName);
					dateLossCombox.addItem(lossItemName);
					lossItemRevList.add(lossItemRev);
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		// ���һ���յ�ѡ��
		lossNameList.add(" ");
		wetLossCombox.addItem(" ");
		dryLossCombox.addItem(" ");
		dateLossCombox.addItem(" ");
		lossItemRevList.add(null);

		if (lossItemRevList.size() > 0) {// �������Ķ���Ļ� Ĭ��ѡ�е�һ��
			wetLossItemRevsion = lossItemRevList.get(0);
			dryLossItemRevsion = lossItemRevList.get(0);
			dateLossItemRevsion = lossItemRevList.get(0);
		}
		
		
		//����ʪ�����ģʽ��combox�ļ����¼�
		wetLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = wetLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
					wetLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		//���øɷ����ģʽ��combox�ļ����¼�
		dryLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = dryLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
					dryLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		//���ñ��������ģʽ��combox�ļ����¼�
		dateLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = dateLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
					dateLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		
		//�鿴ʪ�����ģʽ��
		showWetLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(wetLossItemRevsion==null){//û����Ķ���
					MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(wetLossItemRevsion);
				showLossFrame.setVisible(true);
				
			}
		});
		
		//�鿴�ɷ����ģʽ
		showDryLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dryLossItemRevsion==null){//û����Ķ���
					MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(dryLossItemRevsion);
				showLossFrame.setVisible(true);
			}
		});
		
		
		//�鿴�����ڵ����ģʽ
		showDateLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dateLossItemRevsion==null){//û����Ķ���
					MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(dateLossItemRevsion);
				showLossFrame.setVisible(true);
			}
		});
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LabelGeneratorSolidExcelControler labelGeneratorSolidExcelControler = new LabelGeneratorSolidExcelControler();
				labelGeneratorSolidExcelControler.userTask(itemRevision,
						wetLossItemRevsion,dryLossItemRevsion,dateLossItemRevsion);
			}
		});
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		
		
	}
}
