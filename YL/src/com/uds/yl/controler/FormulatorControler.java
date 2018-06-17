package com.uds.yl.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JButton;
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

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.eclipse.swt.widgets.Tree;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.controler.FormulatorLegalCheckControler_T.FormulaCheckJframe;
import com.uds.yl.herb.ProgressDlg;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.IFormulatorService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.service.impl.FormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.ui.FindIndexLawFrame;
import com.uds.yl.ui.ProgressBarDialog;
import com.uds.yl.utils.DoubleUtil;
import com.uds.yl.utils.StringsUtil;

//配方搭建器
public class FormulatorControler implements BaseControler {

	private static FormulaBomCreate frame;
	
	private Double SUM;// 投料量总和

	private IFormulatorService iFormulatorService = new FormulatorServiceImpl();
	private List<MaterialBean> materialTableList = new ArrayList<>();// 表示了搜索原料表中元素的结构
	private List<MaterialBean> formulatorTableList = new ArrayList<>();// 表示了配方原料表中元素的结构
	private List<TCComponentItemRevision> materialTableItemRevList = new ArrayList<>();// 搜索原料包中的的版本对象
	private List<TCComponentItemRevision> formulatorTabelItemRevList = new ArrayList<>();// 配方原料表中的版本对象
	
	
	//法规合规性检查
	private TCComponentItemRevision lawRevision;//用来作合规性检查的法规
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();

	private List<TCComponentItemRevision> checkLawRevList = new ArrayList<TCComponentItemRevision>();//作为存储等待检查的法规数组
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//等待检查的添加剂Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//等待检查的指标Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//等待检查的添加剂Bom对应的pojo
	private List<IndexItemBean> waitIndexBeanList = null;//等待检查的指标Bom对应的pojo
	
	private List<MaterialBean> checkMaterialBeanList = null;//添加剂法规Bom对应的pojo
	private List<IndexItemBean> checkIndexBeanList = null;//指标法规Bom对应的pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//检查过的要写到excel的数据
	
	private ProgressBarDialog progressBarDialog;//进度条
	
	@Override
	public void userTask(final TCComponentItemRevision itemRev) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if(frame==null || !frame.isVisible()){
						frame = new FormulaBomCreate(itemRev);
					}
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
		private JTextField sumEdt1;//投料量合计
		private JTextField sumEdt2;//配比合计 总共是100
		private JTextField sumEdt3;//内容物理论成本
		private JTextField supplementEdt;
		private JButton cancleBtn;
		private JButton nutritionBtn;//营养成分的信息
		private JButton createBtn;
		private JButton supplementBtn;
		private JButton deleteBtn;
		private JButton addBtn;
		private JButton searchBtn;
		private JButton clearBtn;
		private JButton lawCheckBtn;//法规对比
		private JTextField lawTextField;//显示法规的名称
		private JButton searchLawBtn;//搜索法规
		
		private TCComponentItemRevision mItemRevision;

		/**
		 * Create the frame.
		 */
		public FormulaBomCreate(final TCComponentItemRevision itemRev) {
		
			{
				this.mItemRevision = itemRev;
				setBounds(100, 100, 915, 636);
				contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				setContentPane(contentPane);
				contentPane.setLayout(null);

				JLabel label = new JLabel("配方管理器");
				label.setBounds(10, 0, 85, 25);
				contentPane.add(label);

				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel.setBounds(10, 25, 879, 563);
				contentPane.add(panel);
				panel.setLayout(null);

				JLabel lblNewLabel = new JLabel("配方：");
				lblNewLabel.setBounds(19, 10, 48, 23);
				panel.add(lblNewLabel);

				formulaEdtName = new JTextField();
				formulaEdtName.setBounds(87, 10, 88, 23);
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
				searchEdt_1.setBounds(87, 53, 88, 23);
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
				searchEdt_3.setEditable(false);

				searchBtn = new JButton("搜索");
				searchBtn.setBounds(600, 53, 88, 23);
				panel.add(searchBtn);

				JLabel lblNewLabel_2 = new JLabel("可选原料表：");
				lblNewLabel_2.setBounds(19, 101, 97, 23);
				panel.add(lblNewLabel_2);

				addBtn = new JButton("添加");
				addBtn.setBounds(694, 234, 88, 23);
				panel.add(addBtn);

				JLabel lblNewLabel_3 = new JLabel("配方表：");
				lblNewLabel_3.setBounds(13, 267, 54, 15);
				panel.add(lblNewLabel_3);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(42, 132, 586, 125);
				panel.add(scrollPane);

				materialTable = new JTable() {
					public boolean isCellEditable(int rowIndex, int ColIndex) {
						return false;
					}
				};
				materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				materialTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "原料名", "电子代码", "供应商", "单位成本(元/千克)", "基准单位" }));
				scrollPane.setViewportView(materialTable);
				materialTable.setBorder(new LineBorder(new Color(0, 0, 0)));

				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel_1.setBounds(23, 292, 846, 211);
				panel.add(panel_1);
				panel_1.setLayout(null);

				JScrollPane scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(10, 10, 621, 149);
				panel_1.add(scrollPane_1);

				formulaTable = new JTable(){
					public boolean isCellEditable(int rowIndex, int ColIndex) {
						
						if(ColIndex==2){
							return true;
						}
						return false;
					}
				};
				formulaTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				formulaTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "原料名", "电子代码", "投料量(千克)", "配比(%)", "内容物/理论成本" }));
				formulaTable.getColumnModel().getColumn(2).setPreferredWidth(103);
				formulaTable.getColumnModel().getColumn(4).setPreferredWidth(136);
				scrollPane_1.setViewportView(formulaTable);

				deleteBtn = new JButton("删除");
				deleteBtn.setBounds(730, 163, 88, 23);
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

				JLabel lblNewLabel_5 = new JLabel("元/吨");
				lblNewLabel_5.setBounds(527, 179, 54, 21);
				panel_1.add(lblNewLabel_5);

				JLabel lblNewLabel_6 = new JLabel("定额:");
				lblNewLabel_6.setBounds(641, 72, 54, 23);
				panel_1.add(lblNewLabel_6);

				supplementEdt = new JTextField();
				supplementEdt.setBounds(728, 72, 90, 23);
				panel_1.add(supplementEdt);
				supplementEdt.setColumns(10);

				supplementBtn = new JButton("补足");
				supplementBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				supplementBtn.setBounds(730, 120, 88, 23);
				panel_1.add(supplementBtn);

				createBtn = new JButton("创建");
				createBtn.setBounds(151, 530, 88, 23);
				panel.add(createBtn);

				nutritionBtn = new JButton("营养成分");
				nutritionBtn.setBounds(528, 530, 100, 23);
				panel.add(nutritionBtn);

				cancleBtn = new JButton("取消");
				cancleBtn.setBounds(703, 530, 79, 23);
				panel.add(cancleBtn);
				
				clearBtn = new JButton("清除");
				clearBtn.setBounds(694, 201, 88, 23);
				panel.add(clearBtn);
				
				lawCheckBtn = new JButton("法规对比");
				lawCheckBtn.setBounds(340, 530, 100, 23);
				panel.add(lawCheckBtn);
				
				JLabel label_4 = new JLabel("法规:");
				label_4.setBounds(409, 86, 66, 23);
				panel.add(label_4);
				label_4.setVisible(false);
				
				lawTextField = new JTextField();
				lawTextField.setColumns(10);
				lawTextField.setBounds(495, 87, 88, 23);
				lawTextField.setEditable(false);
				panel.add(lawTextField);
				lawTextField.setVisible(false);
				
				searchLawBtn = new JButton("搜索法规");
				searchLawBtn.setBounds(600, 86, 140, 23);
				panel.add(searchLawBtn);
				searchLawBtn.setVisible(false);
			}
			

			// 设置输入框回车焦点消失
			setEditTextFocuse();
			 
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();

					// 配方名字和版本的EditText初始化
					try {
						String formulatorName = itemRev.getProperty("object_name");
						String formulatorRevision = itemRev.getProperty("item_revision_id");
						formulaEdtName.setText(formulatorName);
						formulaEdtRevision.setText(formulatorRevision);
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					
				
					
					

					// 如果有BOM视图的话配方表里面的Table是有初始值的
					List<MaterialBean> initBean = iFormulatorService.getInitBean(itemRev);
					List<TCComponentItemRevision> initItemRevLst = iFormulatorService.getInitMaterialItemRevList(itemRev);
					formulatorTabelItemRevList.addAll(initItemRevLst);

					DefaultTableModel formulaModel = (DefaultTableModel) formulaTable.getModel();
					for (MaterialBean materialBean : initBean) {
						formulatorTableList.add(materialBean);// 汇总到配方表格中
						formulaModel.addRow(new String[] { materialBean.objectName, materialBean.code,
								materialBean.U8_inventory, materialBean.bl_quantity, materialBean.price });
					}
					if (initBean.size() > 0) {
						formulaTable.setRowSelectionInterval(0, 0);// 默认选中第一行
					}

					initQuality();// 根据投料量变化改变配比
					
					progressBarDialog.stop();
					
				}
			}).start();
			

			
			
			// 搜索原料
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							// 汇总搜索条件 :原料名称、电子代码、供应商
							String materialName = "*"+searchEdt_1.getText().toString()+"*";
							String materialCode = searchEdt_2.getText().toString();
							String materialSupplier = searchEdt_3.getText().toString();

							
							// 搜索原材料
							List<TCComponentItemRevision> searchItemRevisionList = iFormulatorService.getSearchItemRev(materialName,
									materialCode, materialSupplier);
							
							List<MaterialBean> searchBeans = iFormulatorService.getSearchBean(searchItemRevisionList);
							if (searchBeans == null) {
								return;
							}
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
							
							progressBarDialog.stop();
							
						}
					}).start();
					

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
							selectMaterialBean.U8_inventory, selectMaterialBean.bl_quantity,
							selectMaterialBean.price });
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
					initQuality();// 根据投料量变化改变配比
					//
					for (int i = 0; i < formulatorTableList.size(); i++) {
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
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							iFormulatorService.createFormulatorBOM(itemRev, formulatorTabelItemRevList, formulatorTableList);
							
							//投料量、内容物理论成本回写到配方版本属性u8_dosagebase2、u8_price 写回到版本
							try {
								String sumInventoryStr = sumEdt1.getText().toString();
								itemRev.setProperty("u8_dosagebase2", sumInventoryStr);
								
								String sumContentStr = sumEdt3.getText().toString();
								itemRev.setProperty("u8_price", sumContentStr);
							} catch (TCException e) {
								e.printStackTrace();
							}
							
							progressBarDialog.stop();
							MessageBox.post("OK", "", MessageBox.INFORMATION);
							
						}
					}).start();
				}
			});

			// 生成营养成分报表
			nutritionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							// 生成表报 
							TCComponentBOMLine cacheTopBomLine = iFormulatorService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
							List<TCComponentBOMLine> materialBomList = iFormulatorService.getMaterialBomList(cacheTopBomLine);
							List<IndexItemBean> indexBeanList = iFormulatorService.getIndexBeanList(cacheTopBomLine);

							iFormulatorService.write2Excel(materialBomList, indexBeanList);
							progressBarDialog.stop();
						}
					}).start();
				}
			});

			// 取消
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
					if(progressBarDialog.isLive()){
						progressBarDialog.stop();
					}
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
					if (supplement < 0||(supplement - (SUM - inventory))<0){
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

			//清除
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
					for(int i=0;i<materialTableList.size();i++){
						model.removeRow(0);
					}
					materialTableList.clear();
					materialTableItemRevList.clear();
				}
			});
			
			//法规对比
			lawCheckBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
//							if(lawRevision==null){//如果法规没选择的话就提示
//								MessageBox.post("请选择配方对应的产品法规","",MessageBox.INFORMATION);
//								progressBarDialog.stop();
//								return;
//							}
							
							//先生成作为临时的配方对象的说  需要根据不同的结构 将多层的原料投料量写进去
							TCComponentBOMLine cacheTopBomLine = iFormulatorService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
							
							checkLawRevList = getRelatedLawItemList();
							
							
							//根据配方获取原料的BOM
							waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
							//根据原料的BOM获取对应的Bean
							waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//根据配方获取指标的BOM
//							waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
							//根据对应的原料的BOM获取对应的指标的Bean
							waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
							
							//计算下最终的指标上下限制的值
							Double sum =getSumInventory();
							for(int i=0;i<waitIndexBeanList.size();i++){
								IndexItemBean indexBean = waitIndexBeanList.get(i);
								Double up  =  StringsUtil.convertStr2Double(indexBean.up) /sum;
								Double down  =  StringsUtil.convertStr2Double(indexBean.down) /sum;
								
								indexBean.up = up+"";
								indexBean.down = down+"";
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
							allCheckedBeanList.clear();//先清空
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
							
							progressBarDialog.stop();
							
							//关闭bom
							TCComponentBOMWindow bomWindow = cacheTopBomLine.getCachedWindow();
							try {
								bomWindow.refresh();
								bomWindow.save();
								bomWindow.close();
								cacheTopBomLine.refresh();
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					
					
				}
			});
			
			
			//搜索法规
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					FindIndexLawFrame findIndexLawFrame = new FindIndexLawFrame(new AbstractCallBack() {
						@Override
						public void setLawAndName(String lawName, TCComponentItemRevision lawRev) {
							super.setLawAndName(lawName, lawRev);
							lawRevision = lawRev;
							lawTextField.setText(lawName);
							
//							checkLawRevList.add(lawRevision);
						}
					});
					findIndexLawFrame.setVisible(true);
				}
			});
		}

		
		/**
		 * 输入框根据回车失去焦点
		 */
		private void setEditTextFocuse() {

			searchEdt_1.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
					{
						searchEdt_1.setFocusable(false);
						searchEdt_1.setFocusable(true);
					}
				}
			});
			searchEdt_2.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
					{
						searchEdt_2.setFocusable(false);
						searchEdt_2.setFocusable(true);
					}
				}
			});
			searchEdt_3.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
					{
						searchEdt_3.setFocusable(false);
						searchEdt_3.setFocusable(true);
					}
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
				if (materialBean.itemID.equals(formulatorTableList.get(i).itemID)) {
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
				MaterialBean materialBean = formulatorTableList.get(i);
				
				// 判断是否使菌
				String isbacteria = materialBean.getIsbacteria();
				if(isbacteria != null && "是".equals(isbacteria)){
					continue;
				}
				
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}

				// 如果是菌类的话总的投料量里面就不再增加了
				// if(!materialBean.price.contains("kg")&&!materialBean.price.contains("g")&&!materialBean.price.contains("ml")){
				// //显示的投料量为0
				// inventory = 0d;
				// }

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
					materialBean.bl_quantity = DoubleUtil.formatNumber(materialBean.bl_quantity);
				} else {
					materialBean.bl_quantity = inventory / SUM * 100 + "";
					materialBean.bl_quantity = DoubleUtil.formatNumber(materialBean.bl_quantity);
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

				// ----总价格----
				sumMoney += price * inventory;
			}
			//总价格要按照每吨计算
			sumMoney = sumMoney/1000;

			// 更新合计三个格子的值
			// sumEdt1 投料量合计
			// sumEdt2 配比合计
			// sumEdt3 价钱合计
			sumEdt1.setText(DoubleUtil.formatNumber(SUM) + "");
			sumEdt2.setText("100");
			sumEdt3.setText(DoubleUtil.formatNumber(sumMoney) + "");

		}
	
		
		/**
		 * 根据选中的配方版本获取下面关联的文件夹（相关法规）
		 * 关联的是法规的版本
		 * 要返回的是法规的版本
		 * 
		 */
		private List<TCComponentItemRevision> getRelatedLawItemList() {
			List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();

			Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
			try {
				// 初始化队列
				TCComponent[] relatedComponents = mItemRevision
						.getRelatedComponents("U8_LawRel");
				for (TCComponent component : relatedComponents) {
					if (component instanceof TCComponentItemRevision) {
						TCComponentItemRevision lawRev = (TCComponentItemRevision) component;
						queue.offer(lawRev);// 将直接关联的法规添加到集合中

					} else {
						continue;
					}
				}

				// 递归遍历队列
				while (!queue.isEmpty()) {
					TCComponentItemRevision lawRevsion = queue.poll();
					revList.add(lawRevsion);// 所有的法规都会存在这个list中

					TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(
							lawRevsion, "视图");
					if (topBomLine == null) {
						topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
					}
					AIFComponentContext[] bomChilds = topBomLine.getChildren();
					for (int i = 0; i < bomChilds.length; i++) {
						TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i]
								.getComponent();

						String indicatorRequire = bomChild
								.getProperty("U8_indexrequirment");
						String relatedSystemId = bomChild
								.getProperty("U8_AssociationID");
						if (StringsUtil.isEmpty(relatedSystemId)) {// 跳过
							continue;
						}

						TCComponentItemRevision linkedLawRevision = getLinkedLaw(
								indicatorRequire, relatedSystemId);

						if (linkedLawRevision == null) {
							continue;
						}
						queue.offer(linkedLawRevision);
					}

				}

				// 去重复
				HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
				for (TCComponentItemRevision lawRevision : revList) {
					String id = lawRevision.getProperty("current_id");
					if (revisionMap.containsKey(id)) {// id是唯一标识法规的字段
						continue;
					}
					revisionMap.put(id, lawRevision);
				}

				// 重新装入数据
				revList.clear();
				for (TCComponentItemRevision lawRevsion : revisionMap.values()) {
					revList.add(lawRevsion);
				}

			} catch (TCException e) {
				e.printStackTrace();
			}
			return revList;
		}

		
		
		/**
		 * 根据关联体系id
		 * 
		 * 抽取出来的GB 2199 名字
		 * 
		 * 作为法规 id 进行查询
		 * @param indicatorRequire
		 * @param relatedSystemId
		 * @return
		 */
		private TCComponentItemRevision getLinkedLaw(String indicatorRequire,String relatedSystemId) {
			//根据连接的法规的ID找到法规
			String[] splitsLawIds = indicatorRequire.split("#");
			String relatedIds = relatedSystemId;
			
			for(String lawId : splitsLawIds){//技术标准比较特殊 只需要指标的法规
				if(lawId.startsWith("GB")&&(lawId.contains("2760")||lawId.contains("14880"))){//说明合适  是产品标准  来自2760或者14880
					//搜索找到法规
					TCComponentItemRevision itemRevision = null;
					String lawID = relatedIds+" "+lawId;
					TCComponentItemRevision lawRevision = getLawRevisionById(lawID);
					
					if(lawRevision == null){
						return null;
					}
					return lawRevision;
				}
			}
			
			return null;
		}
		
		
		/**
		 * @param area
		 * @return 根据法规ID查询法规
		 */
		public TCComponentItemRevision getLawRevisionById(String lawId) {
			TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
			TCComponent[] result = QueryUtil.getSearchResult(query, new String[] { Const.FormulatorCheck.QUERY_ITME_ID },
					new String[] { lawId });

			if (result == null || result.length == 0 ) {
				return null;
			}
			
			TCComponentItemRevision itemRevision = null;
			try {
				itemRevision = ((TCComponentItem) result[0]).getLatestItemRevision();
			} catch (TCException e) {
				e.printStackTrace();
			}
			return itemRevision;
		}
		
		
		
		/**
		 * 计算配方中的和
		 * @return
		 */
		public Double getSumInventory(){
			Double sum = 0d;
			for(int i=0;i<formulatorTableList.size();i++){
				MaterialBean bean = formulatorTableList.get(i);
				sum += StringsUtil.convertStr2Double(bean.U8_inventory);
			}
			return sum;
		}
		
	}

}
