package com.uds.yl.controler;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.ui.FindIndexLawFrame;
import com.uds.yl.utils.LogFactory;

public class FormulatorLegalCheckControler_T  implements BaseControler  {
	private Logger logger = LogFactory.initLog("FormulatorLegalCheckControler",Level.INFO.getName());
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	
	private String mArea;//�䷽������
	private String mType;//�䷽������
	private TCComponentBOMLine topBomLine = null;//�䷽�汾��BOM��top��Ŀ
	private List<TCComponentItemRevision> checkLawRevList = null;//��������ƥ������з���
	
	private List<TCComponentBOMLine> waitMaterialBomList = null;//�ȴ�������Ӽ�Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//�ȴ�����ָ��Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//�ȴ�������Ӽ�Bom��Ӧ��pojo
	private List<IndexItemBean> waitIndexBeanList = null;//�ȴ�����ָ��Bom��Ӧ��pojo
	
	
	private List<MaterialBean> checkMaterialBeanList = null;//��Ӽ�����Bom��Ӧ��pojo
	private List<IndexItemBean> checkIndexBeanList = null;//ָ�귨��Bom��Ӧ��pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//������Ҫд��excel������
	
	private List<TCComponentItemRevision> searchMaterialLawRevList;//�����б�ѡ���ԭ�Ϸ���
	private List<String>  searchMateriallawNameList;//�����б�ѡ���ԭ�Ϸ��������
	
	private List<TCComponentItemRevision> searchNutritionLawRevList;//�����б�ѡ���Ӫ��ǿ��������
	private List<String> searchNutritionLawNameList;//�����б�ѡ���Ӫ��ǿ������������
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	
		try {
			mArea = itemRev.getProperty("u8_area");
//			mType = itemRev.getProperty("u8_type");
			//TODO:typeҪ�����䷽������
			mType = itemRev.getProperty("object_name");
		} catch (TCException e) {
			e.printStackTrace();
		}
		//�����䷽��ȡ�䷽��BOM topLine
 		topBomLine = iFormulatorLegalCheckService.getTopBomLine(itemRev);
		if(topBomLine==null){
			MessageBox.post("�����䷽��BOM�ṹ","",MessageBox.ERROR);
			return;
		}
		
		//�����䷽��ȡԭ�ϵ�BOM
		waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(topBomLine);
		//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
		waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
		
		//�����䷽��ȡָ���BOM
//		waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(topBomLine);
		//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
		waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(topBomLine);
		
		//�䷽�����ֵ���������Ӽ���ָ�꣩
		for(MaterialBean bean : waitMaterialBeanList){
			bean.type = mType;
		}
		for(IndexItemBean bean : waitIndexBeanList){
			bean.type = mType;
		}
		
		
		FormulaCheckJframe formulaCheckJframe = new FormulaCheckJframe();
		formulaCheckJframe.setVisible(true);
		
	}
	
	
	
	class FormulaCheckJframe extends JFrame{
		private TCComponentItemRevision lawRevision;//�������ָ��ķ���
		private String lawIndexName;//�������ָ��ķ��������
		
		public JPanel contentPane;
		private JLabel indexLawLabel;
		public FormulaCheckJframe() {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 656, 369);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("���棺");
			lblNewLabel.setFont(new Font("����", Font.PLAIN, 17));
			lblNewLabel.setBounds(134, 85, 71, 30);
			contentPane.add(lblNewLabel);
			
			JButton okBtn = new JButton("ȷ��");
			okBtn.setBounds(102, 248, 93, 30);
			contentPane.add(okBtn);
			
			JButton cancleBtn = new JButton("ȡ��");
			cancleBtn.setBounds(431, 248, 93, 30);
			contentPane.add(cancleBtn);
			
			
			
			indexLawLabel = new JLabel("");
			indexLawLabel.setBounds(234, 86, 119, 30);
			contentPane.add(indexLawLabel);
			
			JButton findBtn = new JButton("����");
			findBtn.setBounds(363, 86, 93, 30);
			contentPane.add(findBtn);
		
			
			//ȷ��
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					
					//�����䷽��ȡ����---Ҫ��ȡѡ�е�   ���ָ�������
					checkLawRevList = new ArrayList<>();
					checkLawRevList.add(lawRevision);
					
					if(checkLawRevList.size()==0||checkLawRevList==null){
						MessageBox.post("û��ƥ��ķ���","",MessageBox.INFORMATION);
						return;
					}
					
					
					//��ȡ�����е���Ӽ�Bean   Ҫ������Ӽ���������ѡ�еķ��� 
					checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
					//��ȡ�����е�ָ��Bean�Ǵӷ���������˵  ��������ѡ�еĲ�Ʒ��׼
					checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
					
					
					//�����Ӽ�  	��Ӽ��ķ���Ƚ����� ר������һ������  ����
					List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
					//���ָ��
					List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
					
					//д��excel��
					allCheckedBeanList.addAll(materialCheckedBean);
					allCheckedBeanList.addAll(indexCheckedBean);
					
					iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
				}
			});
			
			//ȡ��
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					return;
				}
			});
			
			
			//�ҵ���Ӧ��ָ�귨��
			findBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					FindIndexLawFrame findIndexLawFrame = new FindIndexLawFrame(new AbstractCallBack() {
						@Override
						public void setLawAndName(String lawName, TCComponentItemRevision lawRev) {
							super.setLawAndName(lawName, lawRev);
							lawRevision = lawRev;
							lawIndexName = lawName;
							indexLawLabel.setText(lawIndexName);
						}
					});
					findIndexLawFrame.setVisible(true);
				}
			});
			
		}
	}

	
}


