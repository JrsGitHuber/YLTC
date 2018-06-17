package com.uds.yl.controler;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.eclipse.ui.testing.TestableObject;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.TechStandarTableBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.interfaces.CallBack;
import com.uds.yl.service.ITechStandarModifyService;
import com.uds.yl.service.impl.TechStandarModifyServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.LOVUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.ui.AddNewIndexFrame;
import com.uds.yl.ui.ModifyIndexBeanFrame;
import com.uds.yl.ui.ProgressBarDialog;
import com.uds.yl.ui.StandardAndWaringSelectedFrame;
import com.uds.yl.ui.StandardSelectedFrame;
import com.uds.yl.utils.StringsUtil;

public class TechStandarModifyControler implements BaseControler {
	
	private static TechStandarModify frame;

	private List<TechStandarTableBean> allTableBeans = null;//所有的指标
	private List<TCComponentItemRevision> allItemRevision;// 可能是上个版本+所有的法规的版本
															// 或者没有上个版本
	private TCComponentItemRevision itemRev;//质量技术标准
	private ITechStandarModifyService iTechStandarModifyServcie = new TechStandarModifyServiceImpl();

	private List<TCComponentItemRevision> indexList = new ArrayList<>();// 存放查询到的指标版本
	private List<TCComponentItemRevision> relatedLawList = new ArrayList<>();// 技术标准关联到的所有法规

	private TCComponentItemRevision originRev;// 选中质量技术标准的上一个版本
	private Set<String> namesSet = null;
	
	private List<TechStandarTableBean> indexBeans  = null;//存储选中的技术标准中的所有指标条目
	
	
	public int unitIndex = 1;//单位列的下标
	public int typeIndex = 2 ;//类型下标
	public int newStandardIndex = 3;//新内控标准的下标
	public int oldStandartIndex = 4;//原内控标准下标
	public int newWaringIndex = 5;//新预警的下标
	public int oldWaringIndex = 6;//原预警值下标
	public int indexIntroduceIndex = 7;//指标名称备注下标
	public int methodIndex = 8;//检测方法的下标
	public int methodBasicIndex = 9;//检测方法依据下标
	public int remarkIndex = 10;//备注
	public int lawStartIndex = 11;//法规列的开始下标
	
//	 "指标名称","单位", "类型", "新内控标准","原内控标准","新预警值","原预警值","指标说明","检测方法","检测方法依据"
	
	
	private List<JComboBox<String>> comBoxList = new ArrayList<JComboBox<String>>();//存储下拉框控件
	
	private ProgressBarDialog progressBarDialog=null;
	
	
	

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(frame==null || !frame.isVisible()){
						frame = new TechStandarModify("质量技术标准生成器");
					}
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class TechStandarModify extends JFrame  {

		
		private List<TCComponentItemRevision> searchedRevList;
		StandardSelectedFrame frame;
		StandardAndWaringSelectedFrame standardAndWaringSelectedFrame;
		AddNewIndexFrame addIndexFrame;
		ModifyIndexBeanFrame modifyIndexBeanFrame;
		
		// ========控件	
		private JPanel contentPane;
		private JTextField nameEdit;
		private JTextField revisionEdt;
		private JTextField searchIndexNameEdt;
		private JTable insertTable;//现在是等待插入指标的table
		private JTable indexTable;//技术标准和法规中的指标的合计
		private JButton searchIndexBtn;
		private JButton insertIndexBtn;
		private JButton verificatBtn;
		private JButton outputBtn;
		private JButton obtainBtn;
		private JButton cancleBtn;
		private JButton addBtn;
		private JComboBox indexTypeComBox;//指标类别的combox
		private JButton deleteIndexBtn;//删除一个Bean
		private JButton modifyBtn;//修改一个Bean
		
		
		private int rowAtPoint;
		private int columnAtPoint;

		
		//查询到的法规的名称数组和版本数组
		private List<String> searchNamesList = null;
		private List<TCComponentItemRevision> searchRevsList = null;

		private String selectedRevName;	// 选中技术指标的版本名称
		private String selectedIndexRevNum;// 选中技术指标的版本号
		private String selectedIndexRevItemId;//选中技术标准的itemId
		
		/**
		 * Create the frame.
		 */
		public TechStandarModify(String name) {
			super(name);
			setBounds(100, 100, 1270, 635);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			nameEdit = new JTextField();
			nameEdit.setBounds(156, 28, 208, 25);
			contentPane.add(nameEdit);
			nameEdit.setColumns(10);
			nameEdit.setEditable(false);

			JLabel lblNewLabel = new JLabel("\u540D\u79F0\uFF1A");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
			lblNewLabel.setBounds(80, 28, 54, 25);
			contentPane.add(lblNewLabel);

			JLabel lblNewLabel_1 = new JLabel("\u7248\u672C\uFF1A");
			lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
			lblNewLabel_1.setBounds(478, 28, 54, 25);
			contentPane.add(lblNewLabel_1);

			revisionEdt = new JTextField();
			revisionEdt.setColumns(10);
			revisionEdt.setBounds(539, 28, 106, 25);
			contentPane.add(revisionEdt);
			revisionEdt.setEditable(false);

			JLabel lblNewLabel_2 = new JLabel("指标类别:");
			lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
			lblNewLabel_2.setBounds(80, 90, 75, 25);
			contentPane.add(lblNewLabel_2);

			searchIndexNameEdt = new JTextField();
			searchIndexNameEdt.setColumns(10);
			searchIndexNameEdt.setBounds(449, 90, 106, 25);
			contentPane.add(searchIndexNameEdt);
			
			JLabel label = new JLabel("指标名称:");
			label.setFont(new Font("宋体", Font.PLAIN, 14));
			label.setBounds(341, 90, 98, 25);
			contentPane.add(label);

			searchIndexBtn = new JButton("搜索");
			searchIndexBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			searchIndexBtn.setBounds(736, 90, 93, 25);
			contentPane.add(searchIndexBtn);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(132, 173, 2, 2);
			contentPane.add(scrollPane);

			insertIndexBtn = new JButton("插入");
			insertIndexBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			insertIndexBtn.setBounds(740, 248, 75, 25);
			contentPane.add(insertIndexBtn);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(196, 144, 469, 129);
			contentPane.add(scrollPane_1);

			insertTable = new JTable() {
				public boolean isCellEditable(int rowIndex, int ColIndex) {
					return false;
				}
			};

			insertTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "指标名称","指标单位"}));
			insertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane_1.setViewportView(insertTable);

			JScrollPane scrollPane_2 = new JScrollPane();
			scrollPane_2.setBounds(49, 350, 1090, 146);
			contentPane.add(scrollPane_2);

			indexTable = new JTable() {
				public boolean isCellEditable(int rowIndex, int ColIndex) {
					
					if(ColIndex==methodIndex){
						return true;
					}
					return false;
				}
				
				@Override
				public TableCellEditor getCellEditor(int row, int column) {
					if(column==methodIndex){
						return new DefaultCellEditor(comBoxList.get(row));
					}
					return super.getCellEditor(row, column);
				}
			};
			indexTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "指标名称","单位", "类型", "新内控标准","原内控标准","新预警值","原预警值","指标名称备注","检测方法","检测方法依据","备注"}));
			scrollPane_2.setViewportView(indexTable);

			verificatBtn = new JButton("验证");
			verificatBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			verificatBtn.setBounds(439, 547, 93, 25);
			contentPane.add(verificatBtn);

			outputBtn = new JButton("输出");
			outputBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			outputBtn.setBounds(682, 547, 93, 25);
			contentPane.add(outputBtn);
//			outputBtn.setEnabled(false);

			obtainBtn = new JButton("获取");
			obtainBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			obtainBtn.setBounds(184, 547, 93, 25);
			contentPane.add(obtainBtn);
			obtainBtn.setVisible(false);

			cancleBtn = new JButton("取消");
			cancleBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			cancleBtn.setBounds(891, 547, 93, 25);
			contentPane.add(cancleBtn);

			addBtn = new JButton("+");
			addBtn.setBounds(21, 506, 55, 40);
			contentPane.add(addBtn);
			addBtn.setEnabled(true);
			addBtn.setVisible(false);
			
			indexTypeComBox = new JComboBox();
			indexTypeComBox.setBounds(165, 92, 112, 21);
			contentPane.add(indexTypeComBox);
			
			deleteIndexBtn = new JButton("删除");
			deleteIndexBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			deleteIndexBtn.setBounds(1149, 443, 75, 25);
			contentPane.add(deleteIndexBtn);
			
			
			modifyBtn = new JButton("修改");
			modifyBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			modifyBtn.setBounds(1149, 402, 75, 25);
			contentPane.add(modifyBtn);
			modifyBtn.setVisible(false);
			
			
//			addBtn.setVisible(false);//加号按钮不可见\
			
			//修改一个Bena的按钮
			
			
			
			//添加一个新的指标
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(addIndexFrame!=null&&addIndexFrame.isVisible()){
						return;
					}
					addIndexFrame = new AddNewIndexFrame();
					addIndexFrame.setVisible(true);
					
					addIndexFrame.setCallBack(new AbstractCallBack() {
						@Override
						public void addNewIndexResult(String indexName,
								String indexUnit) {
							super.addNewIndexResult(indexName, indexUnit);
							//重复的指标不要插入
							for(int i=0;i<allTableBeans.size();i++){
								TechStandarTableBean currentBean = allTableBeans.get(i);
								if(currentBean.name.equals(indexName)&&currentBean.unit.equals(indexUnit)){
									MessageBox.post("指标已经存在", "", MessageBox.INFORMATION);
									return;
								}
							}
							
							TechStandarTableBean bean = new TechStandarTableBean();
							bean.name = indexName;
							bean.unit = indexUnit;
							for(int i=0;i<relatedLawList.size();i++){
								bean.lawStandards.add("");
							}
							
							allTableBeans.add(bean);
							refreshTable();
							
						}
					});
					
				}
			});

			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					if(progressBarDialog.isLive()){
						progressBarDialog.stop();
					}
					dispose();
				}
			});
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					//打开进度条
					progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();
					
					//初始化指标类别的combox
					indexTypeComBox.addItem("");//添加一个空为默认的
					
					TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
					List<String> indexTypeList = LOVUtil.getLovDisplayNameList(session, Const.TechStandarModify.INDEX_TYPE_LOV);
					for(String type : indexTypeList){
						indexTypeComBox.addItem(type);
					}
					indexTypeComBox.setSelectedIndex(0);//默认选中的是第一个类别
					
					// 初始化選中的技术标准版本的名字版本id
					try {
						selectedRevName = itemRev.getProperty("object_name");
						nameEdit.setText(selectedRevName);
						selectedIndexRevNum = itemRev.getProperty("item_revision_id");
						selectedIndexRevItemId = itemRev.getProperty("item_id");
						revisionEdt.setText(selectedIndexRevNum);
					} catch (TCException e) {
						e.printStackTrace();
					}
					
					//初始化标准的条目以及相关的法规中对应的指标
					{
						
						List<TCComponentItemRevision> allRelatedLawList = getRelatedLawItemList();
						relatedLawList.addAll(allRelatedLawList);//将标准相关的法规获取到
						
						
						DefaultTableModel indexModel = (DefaultTableModel) indexTable.getModel();
						
						//将法规的名称加入到table中
						for(TCComponentItemRevision lawRev : relatedLawList){
							try {
								String lawName = lawRev.getProperty("object_name");
								String lawId = lawRev.getProperty("item_id");
								indexModel.addColumn(lawId+lawName);
							} catch (TCException e1) {
								e1.printStackTrace();
							}
						}
						
						// 获取之前先如果有内容就清空
						int rowCount = indexModel.getRowCount();
						if (rowCount > 0) {// 不是第一次初始化要清除已存在内容
							for (int i = 0; i < rowCount; i++) {
								indexModel.removeRow(0);
							}
						}
						// 每次获取就是最新的 名称set 去重复后的集合
//						namesSet = iTechStandarModifyServcie.getAllIndexItemNames(itemRev, selectedLawList);
						
						//选中技术标准的版本中的所有指标条目
						indexBeans = iTechStandarModifyServcie.getIndexFormSelectedIndexRev(itemRev,relatedLawList.size());
						
						originRev = iTechStandarModifyServcie.getOriginRev(selectedIndexRevNum, selectedIndexRevItemId);
						allItemRevision = new ArrayList<>();

						if (originRev == null) {// 如果前一个版本获取为空就把值填为空
							allItemRevision.addAll(relatedLawList);
							allTableBeans = iTechStandarModifyServcie.getAllTableBeans(allItemRevision, false,indexBeans);// 第二个参数表示是否有技术指标的前一个版本
						} else {
							allItemRevision.add(originRev);
							allItemRevision.addAll(relatedLawList);
							allTableBeans = iTechStandarModifyServcie.getAllTableBeans(allItemRevision, true,indexBeans);// 第二个参数表示包含了技术指标的前一个版本
						}
						//将选中的技术标准的版本中的BOM结构内容写到bean中，allTableBeans中没有选中的技术标准的信息，顺便将type写进去和单位
//						iTechStandarModifyServcie.getNewStatdard(itemRev, allTableBeans);
						
						// 讲bean写到表格中
						for (int i = 0; i < allTableBeans.size(); i++) {
							final TechStandarTableBean bean = allTableBeans.get(i);
							indexModel.addRow(new String[] { 
									bean.name, // 指标名称
									bean.unit,//单位
									bean.type,// 类型
									bean.newStandard, // 新内控标准
									bean.oldStandard, // 原内控标准
									bean.newWaring,//新预警值
									bean.oldWaring,//原预警值
									bean.indexIntroduceString,//指标说明
							});
							
							indexModel.setValueAt(bean.remark, i, remarkIndex);//备注
							
//							 "指标名称","单位", "类型", "新内控标准","原内控标准","新预警值","原预警值","指标说明","检测方法","检测方法依据","备注"
							
							final JComboBox<String> comBox = new JComboBox<String>();
							comBox.addItemListener(new ItemListener() {
								@Override
								public void itemStateChanged(ItemEvent itemevent) {
									if(comBox.getItemCount()==0||comBox.getSelectedItem()==null){
										return ;
									}else{
										String selectItem = comBox.getSelectedItem().toString();
										bean.currentMethod = selectItem;
									}
								}
							});
							
							
							comBoxList.add(comBox);//添加一个下拉控件

							//初始化检测方法值
							DefaultCellEditor editor = (DefaultCellEditor) indexTable.getCellEditor(i, methodIndex);
							JComboBox<String> comboBox = (JComboBox<String>) editor.getComponent();
							
							if(bean.allMethodsList!=null && bean.allMethodsList.size()>0){
								for(String method : bean.allMethodsList){
									comboBox.addItem(method);
								}
							}
							
							if(!StringsUtil.isEmpty(bean.currentMethod)){
								comboBox.setSelectedItem(bean.currentMethod);
							}
							
							//检测方法依据
							indexTable.setValueAt(bean.testGis, i,methodBasicIndex);
							
							
							//初始化法规值
							if (bean.lawStandards.size() == 0 || bean.lawStandards == null)
								continue;
							for (int j = 0; j < bean.lawStandards.size(); j++) {
								String value = bean.lawStandards.get(j);
								indexTable.setValueAt(value, i, j + lawStartIndex);
							}
						}
						addBtn.setEnabled(true);
						
						//关闭进度
						progressBarDialog.stop();
					}
				}
			}).start();
			
			
			

			
			
			//删除一个指标
			deleteIndexBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					int selectedRow = indexTable.getSelectedRow();
					if(selectedRow==-1){
						MessageBox.post("","请选择要删除的条目",MessageBox.INFORMATION);
						return;
					}
					
					allTableBeans.remove(selectedRow);
					comBoxList.remove(selectedRow);//删除一个下拉控件
					refreshTable();
					
				}
			});
			
			
			// 输出
			outputBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							updateTableInfo2TableBean();//先更新数据到tableBean中
							if (originRev == null) {// 没有上一个版本
								iTechStandarModifyServcie.writeBack2Tc(itemRev, allTableBeans, allItemRevision, false);
							} else {// 有上个版本
								iTechStandarModifyServcie.writeBack2Tc(itemRev, allTableBeans, allItemRevision, true);
							}
							
							progressBarDialog.stop();
						}
					}).start();
					
				}
			});

			

			 //配方表修改的监听事件
			indexTable.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent propertychangeevent) {
//					refreshTable();
				}
			});
		
			//修改原内控标准界面
			indexTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if(e.getClickCount()==2){
						Point point = new Point(e.getX(), e.getY());
						rowAtPoint = indexTable.rowAtPoint(point);
						columnAtPoint = indexTable.columnAtPoint(point);
						String rswStr = indexTable.getValueAt(rowAtPoint, columnAtPoint) == null ? "" : indexTable.getValueAt(rowAtPoint, columnAtPoint).toString();
						String rawStandardStr = indexTable.getValueAt(rowAtPoint, newStandardIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, newStandardIndex).toString();
						String rawWaringStr = indexTable.getValueAt(rowAtPoint, newWaringIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, newWaringIndex).toString();
						String testGistStr = indexTable.getValueAt(rowAtPoint,methodBasicIndex ) == null ? "" : indexTable.getValueAt(rowAtPoint, methodBasicIndex).toString();
						String indexIntroduceStr = indexTable.getValueAt(rowAtPoint, indexIntroduceIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, indexIntroduceIndex).toString();
						String remarkStr = indexTable.getValueAt(rowAtPoint, remarkIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, remarkIndex).toString();
						if(columnAtPoint==newStandardIndex||columnAtPoint==newWaringIndex ||
								columnAtPoint==indexIntroduceIndex || columnAtPoint==methodBasicIndex ||
								columnAtPoint == remarkIndex){//点击的是新内控标准或者新预警值 或者是备注
							callModifyStandardAndWaringJfreme(rawStandardStr,rawWaringStr,testGistStr,indexIntroduceStr,remarkStr);
						}
						
						if(columnAtPoint>=lawStartIndex){
							
							int position;
							if (originRev == null) {// 如果前一个版本获取为空 从0开始
								position = columnAtPoint - lawStartIndex;
							}else{//有上一个版本 从1开始
								 position = columnAtPoint - lawStartIndex + 1;
							}
							
							TCComponentItemRevision lawRevision = allItemRevision.get(position);
							if(!ItemUtil.isModifiable(lawRevision)){//没有权限
								MessageBox.post("对该法规没有修改权限","提示",MessageBox.INFORMATION);
								return ;
							}else {
								callModifyUpAndDownJfreme(rswStr);
							}
						}
						
//						if(columnAtPoint==indexIntroduceIndex || columnAtPoint==methodBasicIndex){
//							if(rowAtPoint==-1||rowAtPoint<0){
//								MessageBox.post("请选择要修改的条目！","提示",MessageBox.INFORMATION);
//								return;
//							}
//							
//							if(modifyIndexBeanFrame!=null&&modifyIndexBeanFrame.isVisible()){
//								return;
//							}
//							final TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
//							
//							modifyIndexBeanFrame = new ModifyIndexBeanFrame();
//							modifyIndexBeanFrame.setVisible(true);
//							modifyIndexBeanFrame.initFrame(bean.testGis, bean.indexIntroduceString);
//							modifyIndexBeanFrame.setCallBack(new AbstractCallBack() {
//								@Override
//								public void modifyIndexBeanResult(String indexIntroduce,
//										String testGist) {
//									super.modifyIndexBeanResult(indexIntroduce,testGist);
//									bean.indexIntroduceString = indexIntroduce;
//									bean.testGis = testGist;
//									refreshTable();
//								}
//							});
//						}
						
//						String name = (String) JOptionPane.showInputDialog(null, "Test：\n", "提示",
//								JOptionPane.PLAIN_MESSAGE, null, null, rowAtPoint+":--:"+columnAtPoint);
					}
					
					
					
				}
			});
			
			

			// 搜索
			searchIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							String searchIndexTypeStr = indexTypeComBox.getSelectedItem().toString();
							String searchIndexNameStr = searchIndexNameEdt.getText();
							DefaultTableModel model = (DefaultTableModel) insertTable.getModel();
							searchedRevList= iTechStandarModifyServcie.getSearchIndexItemRevsionList(searchIndexTypeStr,searchIndexNameStr);
							
							//删除model中的数据
							int rowCount = model.getRowCount();
							for(int i=0;i<rowCount;i++){
								model.removeRow(0);
							}
							
							for(int i=0;i<searchedRevList.size();i++){
								TCComponentItemRevision indexRevision = searchedRevList.get(i);
								try {
									model.addRow(new String[]{
											indexRevision.getProperty("object_name"),
											indexRevision.getProperty("u8_uom")
											});
								} catch (TCException e1) {
									progressBarDialog.stop();
									e1.printStackTrace();
								}
							}
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
					
				}
			});

			// 插入
			insertIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();// 插入到indexTable
					int rowIndex = insertTable.getSelectedRow();
					if (rowIndex == -1){
						MessageBox.post("请选择指标", "", MessageBox.INFORMATION);
						return;
							
					}
					
					TCComponentItemRevision itemRevision = searchedRevList.get(rowIndex);
					try {
						String unit = itemRevision.getProperty("u8_uom");
						String type  = itemRevision.getProperty("u8_category");
						String indexName = itemRevision.getProperty("object_name");
						String itemID = itemRevision.getProperty("item_id");
						
						//重复的指标不要插入
						for(int i=0;i<allTableBeans.size();i++){
							TechStandarTableBean currentBean = allTableBeans.get(i);
							if(currentBean.itemId.equals(itemID)){
								MessageBox.post("指标已经存在", "", MessageBox.INFORMATION);
								return;
							}
						}
						
						//在table中添加一条指标项目
						
						final TechStandarTableBean bean = new TechStandarTableBean();
						bean.name = indexName;
						bean.oldStandard = "";//新内控标准初始化都为空
						bean.newStandard = "";// 新内控标准初始化都为空
						bean.type = type;
						bean.unit = unit;
						bean.itemId = itemID;
						
						//获取检测方法值
						
						bean.allMethodsList = new ArrayList<String>();
						String[] methodSplit = itemRevision.getProperty("u8_testmethod2").split(",");
						for(String method : methodSplit){
							bean.allMethodsList.add(method);
						}
						
						//法规的值
						bean.lawStandards = new ArrayList<>();
						setNewIndexNameResult(bean);
						
						//添加一行 的同时 添加一个控件
						final JComboBox<String> comboBox = new JComboBox<String>();
						comboBox.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent itemevent) {
								if(comboBox.getItemCount()==0||comboBox.getSelectedItem()==null){
									return ;
								}else{
									String selectItem = comboBox.getSelectedItem().toString();
									bean.currentMethod = selectItem;	
								}
							}
						});
						for(String method : bean.allMethodsList){
							comboBox.addItem(method);
						}
						comBoxList.add(comboBox);
						allTableBeans.add(bean);
						
						refreshTable();
						
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					
						
					
				}
			});

			

			// 验证
			verificatBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if (allTableBeans == null)
								return;
							
							updateTableInfo2TableBean();//数据更新到allTableBeans中
							
							boolean vertifyStandardIsOk = iTechStandarModifyServcie.vertifyStandardIsOk(allTableBeans,
									indexTable);
							//功能放开
//							if (!vertifyStandardIsOk) {// 验证没通过      
//								outputBtn.setEnabled(false);
//							} else {
//								outputBtn.setEnabled(true);
//							}
							refreshTable();
							
							
							progressBarDialog.stop();
						}
					}).start();
					
				
				}
			});

		}
		
		
		/**
		 * 将指标的table表中的数据 写到bean中去
		 */
		public void updateTableInfo2TableBean(){
			//重新获取一下bean 从table中
			DefaultTableModel indexModel = (DefaultTableModel) indexTable.getModel();
			
			
//			"指标名称","单位", "类型", "新内控标准","原内控标准","新预警值","原预警值","指标说明","检测方法","检测方法依据"
			
			for(int i=0;i<allTableBeans.size();i++){
				TechStandarTableBean bean = allTableBeans.get(i);
				
				bean.unit = indexTable.getValueAt(i, unitIndex) == null ? "" : indexTable.getValueAt(i, unitIndex).toString();//单位
				bean.type = indexTable.getValueAt(i, typeIndex) == null ? "" : indexTable.getValueAt(i, typeIndex).toString();//类型
				bean.newStandard = indexTable.getValueAt(i, newStandardIndex) == null ? "" : indexTable.getValueAt(i, newStandardIndex).toString();//新内控标准
				bean.oldStandard = indexTable.getValueAt(i, oldStandartIndex) == null ? "" : indexTable.getValueAt(i, oldStandartIndex).toString();//原内控标准
				bean.newWaring = indexTable.getValueAt(i, newWaringIndex) == null ? "" : indexTable.getValueAt(i, newWaringIndex).toString();//新预警值
				bean.oldWaring = indexTable.getValueAt(i, oldWaringIndex) == null ? "" : indexTable.getValueAt(i, oldWaringIndex).toString();//原预警值
				bean.indexIntroduceString = indexTable.getValueAt(i, indexIntroduceIndex) == null ? "" : indexTable.getValueAt(i, indexIntroduceIndex).toString();//指标说明
				bean.currentMethod = indexModel.getValueAt(i, methodIndex)==null ?  "" : indexModel.getValueAt(i, methodIndex).toString();//检测方法
				bean.testGis =indexModel.getValueAt(i, methodBasicIndex)==null ?  "" : indexModel.getValueAt(i, methodBasicIndex).toString();//检测方法依据
				
				if(bean.lawStandards==null){
					bean.lawStandards = new ArrayList<String>();
				}
				
				bean.lawStandards.clear();
				for(int j=0;j<relatedLawList.size();j++){
					String lawStandard = indexModel.getValueAt(i, j+lawStartIndex)==null ?  "" : indexModel.getValueAt(i, j+lawStartIndex).toString();
					bean.lawStandards.add(lawStandard);
				}
				
			}
		}



		/**
		 * 根据选中的质量技术标准获取下面关联的文件夹（相关法规）
		 * 关联的是法规的版本  
		 * 要返回的是法规的版本
		 * 
		 * 以及关联法规中的所关联的法规
		 * 
		 */
		private List<TCComponentItemRevision> getRelatedLawItemList() {
			List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();
			
			Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
			try {
				//初始化队列
				TCComponent[] relatedComponents = itemRev.getRelatedComponents("U8_LawRel");
				for(TCComponent component : relatedComponents){
					if(component instanceof TCComponentItemRevision){
						TCComponentItemRevision lawRev = (TCComponentItemRevision) component;
						queue.offer(lawRev);//将直接关联的法规添加到集合中
						
					}else{
						continue;
					}
				}
				
				//递归遍历队列
				while(!queue.isEmpty()){
					TCComponentItemRevision lawRevsion = queue.poll();
					revList.add(lawRevsion);//所有的法规都会存在这个list中
					
					TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRevsion, "视图");
					if(topBomLine==null){
						topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
					}
					AIFComponentContext[] bomChilds = topBomLine.getChildren();
					for(int i=0;i<bomChilds.length;i++){
						TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i].getComponent();
						
						String indicatorRequire = bomChild.getProperty("U8_indexrequirment");
						String relatedSystemId = bomChild.getProperty("U8_AssociationID");
						if(StringsUtil.isEmpty(relatedSystemId)){//跳过
							continue;
						}
						
						TCComponentItemRevision linkedLawRevision = getLinkedLaw(indicatorRequire, relatedSystemId);
						
						if(linkedLawRevision == null){
							continue;
						}
						queue.offer(linkedLawRevision);
					}
					
				}
				
				
				//去重复
				HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
				for(TCComponentItemRevision lawRevision : revList){
					String id = lawRevision.getProperty("current_id");
					if(revisionMap.containsKey(id)){//id是唯一标识法规的字段
						continue;
					}
					revisionMap.put(id, lawRevision);
				}
				
				
				//重新装入数据
				revList.clear();
				for(TCComponentItemRevision lawRevsion : revisionMap.values()){
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
				if(lawId.startsWith("GB")){//说明合适  是产品标准 不来自2760或者14880
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
		 * 调用获取新内控标准的界面
		 */
		public void callModifyUpAndDownJfreme(String rawStr){
			if(frame!=null&&frame.isVisible()){
				return;
			}
			frame = new StandardSelectedFrame(rawStr);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setCallBack(new AbstractCallBack() {
				@Override
				public void setUpAndDownResult(String result) {
					super.setUpAndDownResult(result);
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
					model.setValueAt(result, rowAtPoint, columnAtPoint);
					
					//跟新到实体类里面
					TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
					if(columnAtPoint==newStandardIndex){
						bean.newStandard = result;
					}
					if(columnAtPoint==newWaringIndex){
						bean.newWaring = result;
					}
					if(columnAtPoint>=lawStartIndex){
						int index = columnAtPoint-lawStartIndex;
						List<String> updateLawStandars = new ArrayList<String>();
						for(int i=0;i<bean.lawStandards.size();i++){
							
							if(index==i){
								updateLawStandars.add(result);
							}else{
								updateLawStandars.add(bean.lawStandards.get(i));
							}
						}
						bean.lawStandards = updateLawStandars;
					}
				}
			});
		}
		
		
		/**
		 * 调用获取新内控标准的界面
		 */
		public void callModifyStandardAndWaringJfreme(String rawStandardStr,String rawWaringStr,String indexTestGist,String indexIntroduce,String indexRemark){
			if(standardAndWaringSelectedFrame!=null&&standardAndWaringSelectedFrame.isVisible()){
				return;
			}
			standardAndWaringSelectedFrame = new StandardAndWaringSelectedFrame(rawStandardStr,rawWaringStr,indexIntroduce,indexTestGist,indexRemark);
			standardAndWaringSelectedFrame.setVisible(true);
			standardAndWaringSelectedFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			standardAndWaringSelectedFrame.setCallBack(new AbstractCallBack() {
				
				@Override
				public void setStandardAndWaringResult(String standardResult,
						String waringResult,String indexTestGist,String indexIntroduce,String indexRemark) {
					
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
					
					model.setValueAt(standardResult, rowAtPoint,newStandardIndex );
					model.setValueAt(waringResult, rowAtPoint,newWaringIndex);
					model.setValueAt(indexTestGist, rowAtPoint,methodBasicIndex);
					model.setValueAt(indexIntroduce, rowAtPoint,indexIntroduceIndex);
					model.setValueAt(indexRemark, rowAtPoint, remarkIndex);
					
					//跟新到实体类里面
					TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
					bean.newStandard = standardResult;
					bean.newWaring = waringResult;
					bean.testGis = indexTestGist;
					bean.indexIntroduceString = indexIntroduce;
					bean.remark = indexRemark;
				}
			});
		}
		

		/**
		 * 刷新一遍最下面的Table表格
		 */
		public void refreshTable() {
			if (allTableBeans == null)
				return;

			DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
			
			//删除数据
			int rowCount = model.getRowCount();
			for(int i=0;i<rowCount;i++){
				model.removeRow(0);
			}
			
//			"指标名称","单位", "类型", "新内控标准","原内控标准","新预警值","原预警值","指标说明","检测方法","检测方法依据"
			// 讲bean写到表格中
			for (int i = 0; i < allTableBeans.size(); i++) {
				model.addRow(new String[]{""});//新加一行数据
				TechStandarTableBean bean = allTableBeans.get(i);
				model.setValueAt(bean.name, i, 0);// 名称
				model.setValueAt(bean.unit, i, unitIndex);// 单位
				model.setValueAt(bean.type, i, typeIndex);// 类型
				model.setValueAt(bean.newStandard, i, newStandardIndex);// 新内控标准
				model.setValueAt(bean.oldStandard, i, oldStandartIndex);// 原内控标准
				model.setValueAt(bean.newStandard, i, newWaringIndex);// 新预警值
				model.setValueAt(bean.oldStandard, i, oldWaringIndex);// 原预警值
				model.setValueAt(bean.indexIntroduceString, i, indexIntroduceIndex);//指标说明
				
				//初始化检测方法值
				DefaultCellEditor editor = (DefaultCellEditor) indexTable.getCellEditor(i, methodIndex);
				JComboBox<String> comboBox = (JComboBox<String>) editor.getComponent();
				
				if(!StringsUtil.isEmpty(bean.currentMethod)){
					int index = bean.allMethodsList.indexOf(bean.currentMethod);
					indexTable.setValueAt(bean.currentMethod, i, methodIndex);
				}
				
				indexTable.setValueAt(bean.testGis, i, methodBasicIndex);//检测方法依据
				
				//初始化法规值
				if (bean.lawStandards.size() == 0 || bean.lawStandards == null)
					continue;
				for (int j = 0; j < bean.lawStandards.size(); j++) {
					String value = bean.lawStandards.get(j);
					indexTable.setValueAt(value, i, j + lawStartIndex);
				}
				
			}
		}


		/**
		 * 回调方法，在Table中添加一行新的数据的回调方法
		 * @param result
		 */
		public void setNewIndexNameResult(TechStandarTableBean bean) {
			//相当于是将界面的功能抽象去给了+符号按钮
			DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
			// 总共有多少个数据
			int count = 10;
			
			String name = bean.name;
			if (name == null || "".equals(name)) {
				return;
			}

			// 初始化bean的内容
			for (int i = 0; i < relatedLawList.size(); i++) {
				bean.lawStandards.add("");
			}

			// 初始化行
			String[] rowStrs = new String[count];
			rowStrs[0] = name;
			for (int i = 1; i < count; i++) {
				rowStrs[i] = "";
			}
			model.addRow(rowStrs);
			
		}
		
		
		
		
	}

	
	
}





