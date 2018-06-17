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
	
	private String mArea;//配方的区域
	private String mType;//配方的类型
	private TCComponentBOMLine topBomLine = null;//配方版本的BOM的top项目
	private List<TCComponentItemRevision> checkLawRevList = null;//搜索到的匹配的所有法规
	
	private List<TCComponentBOMLine> waitMaterialBomList = null;//等待检查的添加剂Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//等待检查的指标Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//等待检查的添加剂Bom对应的pojo
	private List<IndexItemBean> waitIndexBeanList = null;//等待检查的指标Bom对应的pojo
	
	
	private List<MaterialBean> checkMaterialBeanList = null;//添加剂法规Bom对应的pojo
	private List<IndexItemBean> checkIndexBeanList = null;//指标法规Bom对应的pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//检查过的要写到excel的数据
	
	private List<TCComponentItemRevision> searchMaterialLawRevList;//下拉列表供选择的原料法规
	private List<String>  searchMateriallawNameList;//下拉列表供选择的原料法规的名字
	
	private List<TCComponentItemRevision> searchNutritionLawRevList;//下拉列表供选择的营养强化剂法规
	private List<String> searchNutritionLawNameList;//下拉列表供选择的营养强化剂法规名称
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	
		try {
			mArea = itemRev.getProperty("u8_area");
//			mType = itemRev.getProperty("u8_type");
			//TODO:type要先以配方的名称
			mType = itemRev.getProperty("object_name");
		} catch (TCException e) {
			e.printStackTrace();
		}
		//根据配方获取配方的BOM topLine
 		topBomLine = iFormulatorLegalCheckService.getTopBomLine(itemRev);
		if(topBomLine==null){
			MessageBox.post("请检查配方的BOM结构","",MessageBox.ERROR);
			return;
		}
		
		//根据配方获取原料的BOM
		waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(topBomLine);
		//根据原料的BOM获取对应的Bean
		waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
		
		//根据配方获取指标的BOM
//		waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(topBomLine);
		//根据对应的原料的BOM获取对应的指标的Bean
		waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(topBomLine);
		
		//配方的名字单独处理（添加剂和指标）
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
		private TCComponentItemRevision lawRevision;//用来检查指标的法规
		private String lawIndexName;//用来检查指标的法规的名字
		
		public JPanel contentPane;
		private JLabel indexLawLabel;
		public FormulaCheckJframe() {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 656, 369);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("法规：");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 17));
			lblNewLabel.setBounds(134, 85, 71, 30);
			contentPane.add(lblNewLabel);
			
			JButton okBtn = new JButton("确定");
			okBtn.setBounds(102, 248, 93, 30);
			contentPane.add(okBtn);
			
			JButton cancleBtn = new JButton("取消");
			cancleBtn.setBounds(431, 248, 93, 30);
			contentPane.add(cancleBtn);
			
			
			
			indexLawLabel = new JLabel("");
			indexLawLabel.setBounds(234, 86, 119, 30);
			contentPane.add(indexLawLabel);
			
			JButton findBtn = new JButton("查找");
			findBtn.setBounds(363, 86, 93, 30);
			contentPane.add(findBtn);
		
			
			//确定
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					
					//根据配方获取法规---要获取选中的   检查指标的数组
					checkLawRevList = new ArrayList<>();
					checkLawRevList.add(lawRevision);
					
					if(checkLawRevList.size()==0||checkLawRevList==null){
						MessageBox.post("没有匹配的法规","",MessageBox.INFORMATION);
						return;
					}
					
					
					//获取法规中的添加剂Bean   要检测的添加剂都来自于选中的法规 
					checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
					//获取法规中的指标Bean是从法规中来的说  都来自于选中的产品标准
					checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
					
					
					//检查添加剂  	添加剂的法规比较特殊 专门来自一个法规  待定
					List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
					//检查指标
					List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
					
					//写到excel中
					allCheckedBeanList.addAll(materialCheckedBean);
					allCheckedBeanList.addAll(indexCheckedBean);
					
					iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
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
			
			
			//找到对应的指标法规
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


