package com.uds.yl.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.ui.FindIndexLawFrame;
import com.uds.yl.utils.LogFactory;
//配方搭建起新增版本
public class FormulatorModifyControler_Copy implements BaseControler {
	
	private Logger logger = LogFactory.initLog("FormulatorModifyControler", LogLevel.INFO.getValue());
	
	private Double SUM = 0d;//投料量的总和
	
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();
	private List<MaterialBean> materialTableList = new ArrayList<>();// 表示了搜索原料表中元素的结构
	private List<MaterialBean> formulatorTableList = new ArrayList<>();// 表示了配方原料表中元素的结构
	private List<TCComponentItemRevision> materialTableItemRevList = new ArrayList<>();// 搜索原料包中的的版本对象
	private List<TCComponentItemRevision> formulatorTabelItemRevList = new ArrayList<>();// 配方原料表中的版本对象

	
	//用来做为法规合规性检查
	private TCComponentItemRevision lawRevision;//用来作合规性检查的法规
	private List<TCComponentItemRevision> checkLawRevList;//作为存储等待检查的法规数组
	
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//等待检查的添加剂Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//等待检查的指标Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//等待检查的添加剂Bom对应的pojo
	private List<IndexItemBean> waitIndexBeanList = null;//等待检查的指标Bom对应的pojo
	
	
	private List<MaterialBean> checkMaterialBeanList = null;//添加剂法规Bom对应的pojo
	private List<IndexItemBean> checkIndexBeanList = null;//指标法规Bom对应的pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//检查过的要写到excel的数据
	
	
	@Override
	public void userTask(final TCComponentItemRevision itemRev) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormulaBomCreate frame = new FormulaBomCreate(itemRev);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class FormulaBomCreate extends JFrame {

		private JPanel contentPane;
		private JTextField formulaEdtName;
		private JTextField formulaEdtRevision;
		private JTextField searchEdt_1;
		private JTextField searchEdt_2;
		private JTextField searchEdt_3;
		private JTable materialTable;
		private JTable formulaTable;
		private JTextField sumEdt1;
		private JTextField sumEdt2;
		private JTextField sumEdt3;
		private JTextField supplementEdt;
		private JButton createBtn;
		private JButton containBtn;
		private JButton clearBtn;
		private JButton cancleBtn;
		private JButton supplementBtn;
		private JButton deleteBtn;
		private JButton searchBtn;
		private JButton addBtn;
		private JTextField lawTextField; //显示法规的名称用
		private JButton searchLawBtn;//查询法规
		private JButton lawCheckBtn;//法规合规性检查
		/**
		 * Create the frame.
		 */
		public FormulaBomCreate(final TCComponentItemRevision itemRev) {
			{
				setBounds(100, 100, 878, 636);
				contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				setContentPane(contentPane);
				contentPane.setLayout(null);

				JLabel label = new JLabel("配方管理器");
				label.setBounds(10, 0, 85, 25);
				contentPane.add(label);

				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel.setBounds(10, 25, 842, 563);
				contentPane.add(panel);
				panel.setLayout(null);

				JLabel lblNewLabel = new JLabel("配方/小料：");
				lblNewLabel.setBounds(19, 10, 69, 23);
				panel.add(lblNewLabel);

				formulaEdtName = new JTextField();
				formulaEdtName.setBounds(98, 10, 88, 23);
				panel.add(formulaEdtName);
				formulaEdtName.setColumns(10);
				formulaEdtName.setEditable(false);

				JLabel lblNewLabel_1 = new JLabel("版本：");
				lblNewLabel_1.setBounds(212, 10, 48, 23);
				panel.add(lblNewLabel_1);

				formulaEdtRevision = new JTextField();
				formulaEdtRevision.setBounds(284, 10, 88, 23);
				panel.add(formulaEdtRevision);
				formulaEdtRevision.setColumns(10);
				formulaEdtRevision.setEditable(false);

				JLabel label_1 = new JLabel("原料名：");
				label_1.setBounds(19, 53, 58, 23);
				panel.add(label_1);

				searchEdt_1 = new JTextField();
				searchEdt_1.setColumns(10);
				searchEdt_1.setBounds(98, 53, 88, 23);
				panel.add(searchEdt_1);

				JLabel label_2 = new JLabel("电子代码：");
				label_2.setBounds(212, 53, 72, 23);
				panel.add(label_2);

				searchEdt_2 = new JTextField();
				searchEdt_2.setColumns(10);
				searchEdt_2.setBounds(284, 53, 88, 23);
				panel.add(searchEdt_2);

				JLabel label_3 = new JLabel("供应商：");
				label_3.setBounds(409, 53, 66, 23);
				panel.add(label_3);

				searchEdt_3 = new JTextField();
				searchEdt_3.setColumns(10);
				searchEdt_3.setBounds(495, 53, 88, 23);
				panel.add(searchEdt_3);

				searchBtn = new JButton("搜索");
				searchBtn.setBounds(649, 53, 88, 23);
				panel.add(searchBtn);

				JLabel lblNewLabel_2 = new JLabel("可选原料表：");
				lblNewLabel_2.setBounds(19, 101, 97, 23);
				panel.add(lblNewLabel_2);

				addBtn = new JButton("添加");
				addBtn.setBounds(661, 234, 88, 23);
				panel.add(addBtn);

				JLabel lblNewLabel_3 = new JLabel("配方表：");
				lblNewLabel_3.setBounds(13, 267, 54, 15);
				panel.add(lblNewLabel_3);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(42, 132, 571, 125);
				panel.add(scrollPane);

				materialTable = new JTable();
				materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				materialTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "原料名", "电子代码", "供应商", "单位成本(元/千克)", "基准单位" }));
				scrollPane.setViewportView(materialTable);
				materialTable.setBorder(new LineBorder(new Color(0, 0, 0)));

				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel_1.setBounds(23, 292, 814, 211);
				panel.add(panel_1);
				panel_1.setLayout(null);

				JScrollPane scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(10, 10, 621, 149);
				panel_1.add(scrollPane_1);

				formulaTable = new JTable();
				formulaTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				formulaTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "原料名", "电子代码", "投料量(千克)", "配比(%)", "内容物/理论成本", "互替说明", "替换项", "组合说明" }));
				formulaTable.getColumnModel().getColumn(2).setPreferredWidth(103);
				formulaTable.getColumnModel().getColumn(3).setPreferredWidth(95);
				formulaTable.getColumnModel().getColumn(4).setPreferredWidth(95);
				formulaTable.getColumnModel().getColumn(5).setPreferredWidth(91);
				formulaTable.getColumnModel().getColumn(6).setPreferredWidth(87);
				scrollPane_1.setViewportView(formulaTable);

				deleteBtn = new JButton("删除");
				deleteBtn.setBounds(714, 136, 88, 23);
				panel_1.add(deleteBtn);

				JLabel lblNewLabel_4 = new JLabel("合计：");
				lblNewLabel_4.setBounds(172, 178, 54, 23);
				panel_1.add(lblNewLabel_4);

				sumEdt1 = new JTextField();
				sumEdt1.setBounds(217, 179, 98, 21);
				panel_1.add(sumEdt1);
				sumEdt1.setColumns(10);

				sumEdt2 = new JTextField();
				sumEdt2.setBounds(314, 179, 106, 21);
				panel_1.add(sumEdt2);
				sumEdt2.setColumns(10);

				sumEdt3 = new JTextField();
				sumEdt3.setBounds(417, 179, 88, 21);
				panel_1.add(sumEdt3);
				sumEdt3.setColumns(10);

				JLabel lblNewLabel_5 = new JLabel("元");
				lblNewLabel_5.setBounds(527, 179, 54, 21);
				panel_1.add(lblNewLabel_5);

				JLabel lblNewLabel_6 = new JLabel("定额:");
				lblNewLabel_6.setBounds(650, 56, 54, 23);
				panel_1.add(lblNewLabel_6);

				supplementBtn = new JButton("补足");
				supplementBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				supplementBtn.setBounds(714, 103, 88, 23);
				panel_1.add(supplementBtn);

				supplementEdt = new JTextField();
				supplementEdt.setBounds(714, 56, 90, 23);
				panel_1.add(supplementEdt);
				supplementEdt.setColumns(10);

				createBtn = new JButton("创建");
				createBtn.setBounds(129, 530, 81, 23);
				panel.add(createBtn);

				containBtn = new JButton("营养成分");
				containBtn.setBounds(404, 530, 100, 23);
				panel.add(containBtn);

				cancleBtn = new JButton("取消");
				cancleBtn.setBounds(533, 530, 80, 23);
				panel.add(cancleBtn);

				clearBtn = new JButton("清除");
				clearBtn.setBounds(661, 201, 88, 23);
				panel.add(clearBtn);

				JComboBox<String> comboBox = new JComboBox<String>(new String[] { "", "互替", "组合互替" });
				DefaultCellEditor editor = new DefaultCellEditor(comboBox);
				TableColumn column = formulaTable.getColumnModel().getColumn(5); // n为列的序号，自己修改
				column.setCellEditor(editor);

				JLabel lblNewLabel_7 = new JLabel("法规：");
				lblNewLabel_7.setBounds(421, 105, 54, 15);
				panel.add(lblNewLabel_7);

				lawTextField = new JTextField();
				lawTextField.setBounds(495, 102, 88, 21);
				panel.add(lawTextField);
				lawTextField.setColumns(10);

				searchLawBtn = new JButton("搜索法规");
				searchLawBtn.setBounds(649, 101, 132, 23);
				panel.add(searchLawBtn);

				lawCheckBtn = new JButton("检查");
				lawCheckBtn.setBounds(267, 530, 93, 23);
				panel.add(lawCheckBtn);
			}

			// 配方名字和版本的EditText初始化
			try {
				String formulatorName = itemRev.getProperty("object_name");
				String formulatorRevision = itemRev.getProperty("item_revision_id");
				formulaEdtName.setText(formulatorName);
				formulaEdtRevision.setText(formulatorRevision);
				lawTextField.setEditable(false);
				
				
			} catch (TCException e1) {
				e1.printStackTrace();
			}

			// 如果有BOM视图的话配方表里面的Table是有初始值的
			List<MaterialBean> initBean = iFormulatorModifyService.getInitBean(itemRev);
			List<TCComponentItemRevision> initItemRevLst = iFormulatorModifyService.getInitMaterialItemRevList(itemRev);
			formulatorTabelItemRevList.addAll(initItemRevLst);

			DefaultTableModel formulaModel = (DefaultTableModel) formulaTable.getModel();
			for (MaterialBean materialBean : initBean) {
				formulatorTableList.add(materialBean);// 汇总到配方表格中
				formulaModel.addRow(new String[] { materialBean.objectName, materialBean.code,
						materialBean.U8_inventory, materialBean.bl_quantity, materialBean.price, materialBean.alternate,
						 materialBean.alternateItem,materialBean.groupItem});
			}
			if (initBean.size() > 0) {
				formulaTable.setRowSelectionInterval(0, 0);// 默认选中第一行
			}

			initQuality();// 根据投料量变化改变配比

			// 搜索原料
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 汇总搜索条件 :原料名称、电子代码、供应商
					String materialName = searchEdt_1.getText().toString();
					String materialCode = searchEdt_2.getText().toString();
					String materialSupplier = searchEdt_3.getText().toString();

					// 搜索原材料
					List<MaterialBean> searchBeans = iFormulatorModifyService.getSearchBean(materialName, materialCode,
							materialSupplier);
					List<TCComponentItemRevision> searchItemRevisionList = iFormulatorModifyService.getSearchItemRev(materialName,
							materialCode, materialSupplier);
					// 将搜索结果放到Table中，并且默认选中搜索结果第一条
					DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
					for (int i=0;i<searchBeans.size();i++) {
						MaterialBean materialBean = searchBeans.get(i);
						TCComponentItemRevision itemRevision = searchItemRevisionList.get(i);
						if (!isInMaterialTable(materialBean)) {
							model.addRow(new String[] { materialBean.objectName, materialBean.code,
									materialBean.suppplier, materialBean.price, materialBean.u8Uom });
							materialTableList.add(materialBean);// 汇总到原料表格中
							materialTableItemRevList.add(itemRevision);
						}
					}
					if (searchBeans.size() > 0) {// 有值才能默认选中第一个
						materialTable.setRowSelectionInterval(0, 0);
					}

				}
			});

			// 添加原料
			addBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// 获取原料表中的选中行
					int selectRowIndex = materialTable.getSelectedRow();
					if (selectRowIndex == -1) {
						MessageBox.post("请选择", "", MessageBox.ERROR);
						return;
					}
					// 判断选中行是否已经在配方表中
					MaterialBean selectMaterialBean = materialTableList.get(selectRowIndex);
					TCComponentItemRevision selectItemRev = materialTableItemRevList.get(selectRowIndex);

 					boolean inFormulatorTable = isInFormulatorTable(selectMaterialBean);
					if (inFormulatorTable) {
						MessageBox.post("原材料已经存在配方中", "", MessageBox.INFORMATION);
						return;
					}
					// 将原料表中选中的行信息添加到配方表中
					DefaultTableModel defaultTableModel = (DefaultTableModel) formulaTable.getModel();
					defaultTableModel.addRow(new String[] { selectMaterialBean.objectName, selectMaterialBean.code,
							selectMaterialBean.U8_inventory, selectMaterialBean.bl_quantity, selectMaterialBean.price,
							"", "",
							"" });
					formulatorTableList.add(selectMaterialBean);
					formulatorTabelItemRevList.add(selectItemRev);
					
					// 动态更新配方表中的配比
					initQuality();// 根据投料量变化改变配比
				}
			});

			// 删除
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					// 删除配方表中选中的行
					int selectedRowIndex = formulaTable.getSelectedRow();
					if (selectedRowIndex == -1) {
						MessageBox.post("请选择", "", MessageBox.ERROR);
						return;
					}
					MaterialBean materialBean = formulatorTableList.get(selectedRowIndex);
					TCComponentItemRevision selectItemRev = formulatorTabelItemRevList.get(selectedRowIndex);
					// 要删除的话还要删除在原料表格中的
					DefaultTableModel defaultTableModel = (DefaultTableModel) formulaTable.getModel();
					defaultTableModel.removeRow(selectedRowIndex);// 删除配方表中的
					formulatorTableList.remove(selectedRowIndex);// 删除配方表List中的数据
					formulatorTabelItemRevList.remove(selectedRowIndex);
					// 动态更新配方表中的配比以及合计
					initQuality();// 根据投料量变化改变配比
				}
			});

			// 配方表修改的监听事件
			formulaTable.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent propertychangeevent) {
					// 动态更新配方表中的配比以及合计
					initQuality();// 根据投料量变化改变配比  执行计算等  就是变化的字段

					//更新 某些值保持不变
					for (int i = 0; i < formulatorTableList.size(); i++) {
						//这些事修改后不要变得值
						MaterialBean materialBean = formulatorTableList.get(i);
						formulaTable.setValueAt(materialBean.objectName, i, 0);
						formulaTable.setValueAt(materialBean.code, i, 1);
						formulaTable.setValueAt(materialBean.U8_inventory, i, 2);
						formulaTable.setValueAt(materialBean.bl_quantity, i, 3);
						formulaTable.setValueAt(materialBean.price, i, 4);
					}
				}
			});

			// 创建BOM视图
			createBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					iFormulatorModifyService.createFormulatorBOM(itemRev, formulatorTabelItemRevList,
							formulatorTableList);
					MessageBox.post("OK", "", MessageBox.INFORMATION);
				}
			});

			// 生成营养成分报表
			containBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					// 生成表报
					TCComponentBOMLine cacheTopBomLine = iFormulatorModifyService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
					List<TCComponentBOMLine> materialBomList = iFormulatorModifyService.getMaterialBomList(cacheTopBomLine);
					List<IndexItemBean> indexBeanList = iFormulatorModifyService.getIndexBeanList(cacheTopBomLine);
					iFormulatorModifyService.write2Excel(materialBomList, indexBeanList);
					try {
						logger.fine(cacheTopBomLine.getItem().getProperty("object_name"));
					} catch (TCException e) {
						e.printStackTrace();
					}
					logger.fine(formulatorTabelItemRevList.toString());
				}
			});

			// 取消
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
				}
			});

			// 补足
			supplementBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					Double supplement = Utils.convertStr2Double(supplementEdt.getText());
					int index = formulaTable.getSelectedRow();
					Double inventory = Utils.convertStr2Double(formulaTable.getValueAt(index, 2).toString());
					if (supplement == 0)
						supplement = 1000d;
					if (supplement < 0 || (supplement - (SUM - inventory)) < 0) {
						MessageBox.post("补量有误", "", MessageBox.ERROR);
						return;
					}
					// 计算需要补得量
					inventory = supplement - (SUM - inventory);
					formulaTable.setValueAt(inventory + "", index, 2);
					sumEdt1.setText(supplement + "");
					MaterialBean materialBean = formulatorTableList.get(index);
					materialBean.U8_inventory = Const.CommonCosnt.doubleFormat.format(inventory) + "";
					initQuality();
				}
			});

			// 清除
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
					for (int i = 0; i < materialTableList.size(); i++) {
						model.removeRow(0);
					}
					materialTableList.clear();
					materialTableItemRevList.clear();
				}
			});

			//查询法规
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FindIndexLawFrame findIndexLawFrame = new FindIndexLawFrame(new AbstractCallBack() {
						@Override
						public void setLawAndName(String lawName, TCComponentItemRevision lawRev) {
							super.setLawAndName(lawName, lawRev);
							lawRevision = lawRev;
							lawTextField.setText(lawName);
						}
					});
					findIndexLawFrame.setVisible(true);
				}
			});
			
			
			//做配方的合规性检查
			lawCheckBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(lawRevision==null){//如果法规没选择的话就提示
						MessageBox.post("请选择配方对应的产品法规","",MessageBox.INFORMATION);
						return;
					}
					//先生成作为临时的配方对象的说
					TCComponentBOMLine cacheTopBomLine = iFormulatorModifyService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
					
					checkLawRevList = new ArrayList<>();
					checkLawRevList.add(lawRevision);
					
					//根据配方获取原料的BOM
					waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
					//根据原料的BOM获取对应的Bean
					waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
					
					//根据配方获取指标的BOM
//					waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
					//根据对应的原料的BOM获取对应的指标的Bean
					waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
					
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
		}

		/**
		 * 搜索到的原材料时候包含在原材料表格中
		 * 
		 * @return
		 */
		public boolean isInMaterialTable(MaterialBean materialBean) {
			if (materialTableList.size() == 0)
				return false;

			for (int i = 0; i < materialTableList.size(); i++) {
				if (materialBean.objectName.equals(materialTableList.get(i).objectName)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 选中要添加的原材料是否在配方表格中
		 * 
		 * @param materialBean
		 * @return
		 */
		public boolean isInFormulatorTable(MaterialBean materialBean) {
			if (formulatorTableList.size() == 0)
				return false;

			for (int i = 0; i < formulatorTableList.size(); i++) {
				if (materialBean.objectName.equals(formulatorTableList.get(i).objectName)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 动态更新配方表中的配比以及合计
		 */
		public void initQuality() {
			DefaultTableModel model = (DefaultTableModel) formulaTable.getModel();
			SUM = 0d;// 投料量总和
			Double sumMoney = 0d;// 价钱总和
			for (int i = 0; i < formulatorTableList.size(); i++) {
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}
				SUM += inventory;// 累加求和
			}

			for (int i = 0; i < formulatorTableList.size(); i++) {
				MaterialBean materialBean = formulatorTableList.get(i);
				// =======投料量和百分比的动态变化
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}
				materialBean.U8_inventory = inventory + "";// 让动态改变的投料量随时更新在配方表格的数据结构list中去
				if (SUM == 0) {
					materialBean.bl_quantity = "0";
				} else {
					materialBean.bl_quantity = inventory / SUM * 100 + "";
				}

				formulaTable.setValueAt(materialBean.bl_quantity, i, 3);
				// ==============单位价格动态变化
				String strPrice = (String) model.getValueAt(i, 4);
				Double price = 0d;
				try {
					price = Double.valueOf(strPrice);
				} catch (Exception e) {
					price = 0d;
				}
				materialBean.price = price + "";

				// 如果是菌类的话就需要单独处理
				if (materialBean.objectName.contains("菌")) {
					// TODO:待处理
					continue;
				}

				// ----总价格----
				sumMoney += price * inventory;
				
				//互替说明   替换项  组合说明 
				materialBean.alternate = (String) model.getValueAt(i, 5)+"";//互替说明
				materialBean.alternateItem = (String) model.getValueAt(i, 6)+"";//替换项
				materialBean.groupItem = (String) model.getValueAt(i, 7)+"";//组合说明
				
			}

			// 更新合计三个格子的值
			// sumEdt1 投料量合计
			// sumEdt2 配比合计
			// sumEdt3 价钱合计
			sumEdt1.setText(SUM + "");
			sumEdt2.setText("100");
			sumEdt3.setText(sumMoney + "");

		}
	}

}
