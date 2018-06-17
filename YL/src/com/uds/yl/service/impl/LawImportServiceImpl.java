package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.LawBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.ILawImportService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.DateUtil;
import com.uds.yl.utils.StringsUtil;

public class LawImportServiceImpl implements ILawImportService {
	private HashMap<String,Integer> sortedCloumNameList = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uds.yl.service.ILawImportService#getLawBeansFromExcel(java.lang.
	 * String) �����ļ�·������LawBean�Ļ�ȡ
	 */
	@Override
	public List<LawBean> getLawBeansFromExcel(String filePath) {
		// д��Excel
		List<LawBean> lawBeanList = new ArrayList<>();
		FileInputStream fis = null;
		Workbook wb = null;
		sortedCloumNameList = new HashMap<>();
		try {
			fis = new FileInputStream(new File(filePath));
			wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheet(Const.Law.SHEET_NAME);
			
			Row row = sheet.getRow(0); 
			initCloumNameSort(row,sortedCloumNameList);
			
			for (int i = 1;; i++) {
				row = sheet.getRow(i);
				if (row == null||row.getCell(0)==null || StringsUtil.isEmpty(row.getCell(0).toString())) {// ��ȡ������row�Ļ���������
					break;
				}

				// ÿһ�д���һ��Bean����  ���쳣�׳�˵�����ݸ�ʽ��������
				try {
					initBeanByRow(row, lawBeanList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return lawBeanList;
	}

	/**
	 * ����һ�е�������Ϣ��
	 * @param row
	 * @param sortedCloumNameList
	 */
	private void initCloumNameSort(Row row, HashMap<String, Integer> sortedCloumNameList) {
		for(int i=0;;i++){
			Cell cell = row.getCell(i);
			if(cell==null){
				break;
			}
			String cloumName = cell.getStringCellValue();
			if(StringsUtil.isEmpty(cloumName)){//���Ϊ�վ�����
				break;
			}else{
				sortedCloumNameList.put(cloumName,i);
			}
		}
	}

	/**
	 * ����������ʼ��һ��Bean����
	 * 
	 * @param row
	 */
	private void initBeanByRow(Row row, List<LawBean> lawBeanList) throws Exception {
		LawBean bean = new LawBean();
		int index = 0;
		Cell cell = null;
		index = sortedCloumNameList.get("��ϵ����");cell = row.getCell(index);
		if(cell==null){
			bean.productCategory="";
		}else{
			try {
				bean.productCategory = cell.getStringCellValue();// ��ϵ����
			} catch (Exception e) {
				bean.productCategory = cell.getNumericCellValue()+"";// ��ϵ����
			}
			bean.productCategory.replace(" ", "");//ȥ�ո�
		}
		index = sortedCloumNameList.get("��ϵID");
		cell = row.getCell(index);
		if(cell==null){
			bean.systemId="";
		}else{
			try {
				bean.systemId = (int)cell.getNumericCellValue()+"";//��ϵID
			} catch (Exception e) {
				bean.systemId = cell.getStringCellValue()+"";//��ϵID
			}
		}
		index = sortedCloumNameList.get("��ϵ����");cell = row.getCell(index);
		if(cell==null){
			bean.productCategoryDesc="";
		}else{
			try {
				bean.productCategoryDesc = cell.getStringCellValue();// ��ϵ����
			} catch (Exception e) {
				bean.productCategoryDesc = cell.getNumericCellValue()+"";// ��ϵ����
			}
		}
		index = sortedCloumNameList.get("ָ������");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorName="";
		}else{
			try {
				bean.indicatorName = cell.getStringCellValue();// ָ������
			} catch (Exception e) {
				bean.indicatorName = cell.getNumericCellValue()+"";// ָ������
			}
		}
		index = sortedCloumNameList.get("ָ��Ҫ��");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorRequire="";
		}else{
			try {
				bean.indicatorRequire =cell.getStringCellValue();// ָ��Ҫ��
			} catch (Exception e) {
				bean.indicatorRequire =cell.getNumericCellValue()+"";// ָ��Ҫ��
			}
		}
		index = sortedCloumNameList.get("����ID");cell = row.getCell(index);
		if(cell==null){
			bean.relatedSystemId="";
		}else{
			try {
				bean.relatedSystemId = (int)cell.getNumericCellValue()+"";//����ID
			} catch (Exception e) {
				bean.relatedSystemId = cell.getStringCellValue()+"";//����ID
			}
		}
		index = sortedCloumNameList.get("ָ�����");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorIntroduce="";
		}else{
			try {
				bean.indicatorIntroduce=cell.getStringCellValue();//ָ�����
			} catch (Exception e) {
				bean.indicatorIntroduce=cell.getNumericCellValue()+"";//ָ�����
			}
		}
		index = sortedCloumNameList.get("ָ�굥λ");cell = row.getCell(index);
		if(cell==null){
			bean.unit="";
		}else{
			try {
				bean.unit = cell.getStringCellValue();//ָ�굥λ
			} catch (Exception e) {
				bean.unit = cell.getNumericCellValue()+"";//ָ�굥λ
			}
			bean.unit.replace(" ", "");//ȥ�ո�
		}
		index = sortedCloumNameList.get("ָ�걸ע");cell = row.getCell(index);
		if(cell==null){
			bean.remark="";
		}else{
			try {
				bean.remark = cell.getStringCellValue();//ָ�걸ע
			} catch (Exception e) {
				bean.remark = cell.getNumericCellValue()+"";//ָ�걸ע
			}
		}
		index = sortedCloumNameList.get("��Сֵ");cell = row.getCell(index);
		if(cell==null){
			bean.minValue="";
		}else{
			try {
				bean.minValue = cell.getNumericCellValue()+"";// ��Сֵ
			} catch (Exception e) {
				bean.minValue = cell.getStringCellValue()+"";// ��Сֵ
			}
		}
		index = sortedCloumNameList.get("���ֵ");cell = row.getCell(index);
		if(cell==null){
			bean.maxValue="";
		}else{
			try {
				bean.maxValue = cell.getNumericCellValue()+"";// ���ֵ
			} catch (Exception e) {
				bean.maxValue = cell.getStringCellValue()+"";// ���ֵ
			}
		}
		index = sortedCloumNameList.get("��ⷽ��");cell = row.getCell(index);
		if(cell==null){
			bean.detectionMethod="";
		}else{
			try {
				bean.detectionMethod = cell.getStringCellValue();// ��ⷽ��
			} catch (Exception e) {
				bean.detectionMethod = cell.getNumericCellValue()+"";// ��ⷽ��
			}
		}
		index = sortedCloumNameList.get("��Դ��׼");cell = row.getCell(index);
		if(cell==null){
			bean.sourceStandard="";
		}else{
			try {
				bean.sourceStandard = cell.getStringCellValue();// ��Դ��׼
			} catch (Exception e) {
				bean.sourceStandard = cell.getNumericCellValue()+"";// ��Դ��׼
			}
		}
		index = sortedCloumNameList.get("��Ч��");cell = row.getCell(index);
		if(cell==null){
			bean.effectiveness="";
		}else{
			try {
				bean.effectiveness = cell.getStringCellValue();// ��Ч��
			} catch (Exception e) {
				bean.effectiveness = cell.getNumericCellValue()+"";// ��Ч��
			}
		}
		index = sortedCloumNameList.get("ʵʩ����");cell = row.getCell(index);
		if(cell==null){
			bean.start_date="";
		}else{
			bean.start_date = DateUtil.getDateStr(cell.getDateCellValue());//ʵ������
		}
		index = sortedCloumNameList.get("��ֹ����");cell = row.getCell(index);
		if(cell==null){
			bean.end_date="";
		}else{
			bean.end_date = DateUtil.getDateStr(cell.getDateCellValue());//��ֹ����
		}
		index = sortedCloumNameList.get("��ϵ��ע");cell = row.getCell(index);
		if(cell==null){
			bean.systemNameNote="";
		}else{
			try {
				bean.systemNameNote = cell.getStringCellValue();//��ϵ���Ʊ�ע
			} catch (Exception e) {
				bean.systemNameNote = cell.getNumericCellValue()+"";//��ϵ���Ʊ�ע
			}
		}
		//CNS
		index = sortedCloumNameList.get("CNS");cell = row.getCell(index);
		if(cell==null){
			bean.cns="";
		}else{
			try {
				bean.cns = cell.getStringCellValue();//CNS��
			} catch (Exception e) {
				bean.cns = cell.getNumericCellValue()+"";//CNS��
			}
		}
		//INS
		index = sortedCloumNameList.get("INS");cell = row.getCell(index);
		if(cell==null){
			bean.ins="";
		}else{
			try {
				bean.ins = cell.getStringCellValue();//INS�ֶ�
			} catch (Exception e) {
				bean.ins = cell.getNumericCellValue()+"";//INS�ֶ�
			}
		}
		//���޷��� ���ֵ����
		
		index = sortedCloumNameList.containsKey("���ֵ����") ? sortedCloumNameList.get("���ֵ����") : -1;
		cell = index == -1 ? null : row.getCell(index);
		if(cell==null){
			bean.upOperation="";
		}else{
			try {
				bean.upOperation = cell.getStringCellValue();// ���ֵ����
			} catch (Exception e) {
				bean.upOperation = cell.getNumericCellValue()+"";// ���ֵ����
			}
		}
		//���޷���  ��Сֵ����
		
		index = sortedCloumNameList.containsKey("��Сֵ����") ? sortedCloumNameList.get("��Сֵ����") : -1;
		cell = index == -1 ? null : row.getCell(index);
		if(cell==null){
			bean.downOperation="";
		}else{
			try {
				bean.downOperation = cell.getStringCellValue();// ��Сֵ����
			} catch (Exception e) {
				bean.downOperation = cell.getNumericCellValue()+"";// ��Сֵ����
			}
		}
		lawBeanList.add(bean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ILawImportService#getLawCategoryFromExcel(java.lang.
	 * String) ��ȡҪ�������U8_IndexItem������U8_Material���͵�
	 */
	@Override
	public String getLawCategoryFromExcel(String filePath) {
		String categroyStr = "";
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(new File(filePath));
			wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheet("Sheet1");
			Row row = sheet.getRow(1);
			Cell cell = row.getCell(2);
			categroyStr = cell.getStringCellValue();

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return categroyStr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ILawImportService#createOrUpdateLawBom(java.util.List,
	 * java.util.List, java.lang.String) ����Excel����BOM���޸Ļ��ߴ���
	 */
	@Override
	public void createOrUpdateLawBom(TCComponentBOMLine topBomLine, List<LawBean> lawBeansFromExcel,
			List<TCComponentBOMLine> lawBomLineChilds,String bomType
			,TCComponentFolder indexFolder,TCComponentFolder materialFolder) throws TCException, InstantiationException, IllegalAccessException {
		String excelCategoryStr = bomType;
		
		String category = "";//��ϵ����
		String unit  = "";//ָ�굥λ
		String systemId = "";//��ϵID 
		String systemNote = "";//��ϵ��ע
		for (LawBean bean : lawBeansFromExcel) {
			boolean exitInBOM = false;
			for (TCComponentBOMLine bomLine : lawBomLineChilds) {// �ж��Ƿ���BOM��  Ψһ���ж��ǣ���ϵ����+��ϵID+ָ������+ָ�굥λ
				String name = bomLine.getItemRevision().getProperty("object_name");//ָ������
				category = bomLine.getProperty("U8_category");//��ϵ����
				unit  = bomLine.getProperty("U8_standardunit");//ָ�굥λ
				systemId = bomLine.getProperty("u8_systemid");//��ϵID
				if (bean.indicatorName.equals(name) && bean.productCategory.equals(category) && bean.unit.equals(unit)) {// Ҫ�������Ŀ����bom�и���
					exitInBOM = true;
					AnnotationFactory.setObjectInTC(bean, bomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				} else if (bean.indicatorName.equals(name) && !bean.productCategory.equals(category)) {// ���ڣ���ϵ����ͬ˵��Ҫ�ظ�ʹ��ͬһ��ָ�����Σ���copyһ�֣��޸�����
					exitInBOM = true;
					TCComponentBOMLine addBomLine = topBomLine.add(bomLine.getItem(), bomLine.getItemRevision(), null,
							false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				}else if(bean.indicatorName.equals(name) && !bean.unit.equals(unit)){//���������һ�� ���ǵ�λ��һ��Ҳ����Ϊ������ָ��
					exitInBOM = true;
					TCComponentBOMLine addBomLine = topBomLine.add(bomLine.getItem(), bomLine.getItemRevision(), null,
							false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				}
			}
			
			if (!exitInBOM) {// ���������
				// ������û�оʹ�������ֵ
				TCComponentQuery query = null;
				TCComponent[] resultSearch = null;
				if("U8_Material".equals(bomType)){
					if(StringsUtil.isEmpty(bean.unit)){
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_NULL_USEDINLAW.getValue());	
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "����" },
								new String[] { bean.indicatorName });
					}else{
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_USEDINLAW.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "����","��λ" },
								new String[] { bean.indicatorName ,bean.unit});
					}
					
					
				}else if("U8_IndexItem".equals(bomType)){
					if(StringsUtil.isEmpty(bean.unit)){
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_NULL_USEDINLAW_FOR_IMPORT.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "����"},
								new String[] { bean.indicatorName});
					}else{
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW_FOR_IMPORT.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "����","��λ" },
								new String[] { bean.indicatorName,bean.unit });
					}

				}
				if(resultSearch==null){
					System.out.println(bean.indicatorName+"ָ���ѯ���ΪNULL");
				}
				if (resultSearch.length == 0) {// û���ҵ���Ҫ����
					AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
					TCSession session = (TCSession) app.getSession();
					TCComponentItemType item_type = (TCComponentItemType) session
							.getTypeComponent(excelCategoryStr);

					String newID = item_type.getNewID();
					String newRev = item_type.getNewRev(null);
					String type = excelCategoryStr;
					String name = bean.indicatorName;
					String desc = Const.LawImport.USED_IN_LAW;//������Ƿ��浼���ʱ�򴴽���ָ�����ԭ��
					TCComponentItem newItem = item_type.create(newID, newRev,
							type, name, desc, null);
					
					TCComponentItemRevision newIndexItemRev = newItem.getLatestItemRevision();
					TCComponentBOMLine addBomLine = topBomLine.add(newItem, newItem.getLatestItemRevision(), null, false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					newIndexItemRev.setProperty("u8_uom", bean.unit);//��λ
					
					String  detectionMethod = newItem.getLatestItemRevision().getProperty("u8_testmethod2");
					if(!detectionMethod.contains(bean.detectionMethod)){
						newIndexItemRev.setProperty("u8_testmethod2", detectionMethod+", "+bean.detectionMethod);
					}
					
					 category = bean.productCategory;//��ϵ����
					 systemId = bean.systemId;//��ϵID 
					 systemNote = bean.systemNameNote;//��ϵ��ע
					
					addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");//���з��浼�빦��Ҫ������ָ���ԭ�϶�Ҫ���ΪUsedInLaw
					
					//��Item����ӵ��̶����ļ�����
					if("U8_IndexItem".equals(bomType)){//ָ���ļ���
						indexFolder.add("contents", newItem);
					}else if("U8_Material".equals(bomType)){//ԭ���ļ���
						materialFolder.add("contents", newItem);
					}
					
					
					
				} else {//���ҵ���
					TCComponentItemRevision itemRevision = null;
					for (TCComponent component : resultSearch) {
						if (component instanceof TCComponentItemRevision) {
							String lawDesc = component.getProperty("object_desc");
							if(!Const.LawImport.USED_IN_LAW.equals(lawDesc)){
								//����ҵ������Ͳ���ʹ���ڷ����еľ�ֱ������
								continue;
							}
							itemRevision = (TCComponentItemRevision) component;
							// �ҵ��汾--��Bom�����
							TCComponentBOMLine addBomLine = topBomLine.add(itemRevision.getItem(), itemRevision, null,
									false);
							AnnotationFactory.setObjectInTC(bean, addBomLine);
							if(ItemUtil.isModifiable(itemRevision)){//��дȨ��
								//��Ӱ汾�ϵ����� ��ⷽ��
								String  detectionMethod = addBomLine.getItemRevision().getProperty("u8_testmethod2");
								if(!detectionMethod.contains(bean.detectionMethod)){
									addBomLine.getItemRevision().setProperty("u8_testmethod2", detectionMethod+", "+bean.detectionMethod);
								}
							}
							
							category = bean.productCategory;//��ϵ����
							systemId = bean.systemId;//��ϵID 
							systemNote = bean.systemNameNote;//��ϵ��ע
							
							break;// ���ҵ�һ���ͺ���ʱ
						}
					}
				}
			}
		}
		
		TCComponentItemRevision lawRevision = topBomLine.getItemRevision();
		//�ر�
		try {
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			topBomLine.refresh();
			bomWindow.save();
			bomWindow.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		//д�汾��bomRevision����
		lawRevision.setProperty("u8_systemid",systemId);//��ϵid
		lawRevision.setProperty("u8_category",category);//��ϵ����
		lawRevision.setProperty("u8_systemnamenote",systemNote);//��ϵ��ע

		
	}

	@Override
	public TCComponentItem searchLawItemByID(String id){
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"ID"}, new String[]{id});
		
		if(searchResult.length!=1){
			return null;
		}
		TCComponentItem lawItem = (TCComponentItem) searchResult[0];
		return lawItem;
		
	};
	
	//����Ƿ��ж���64������128���ֽڵ�����
	@Override
	public boolean isCanImportByName(List<LawBean> lawBeansFromExcel){
		for(LawBean bean : lawBeansFromExcel){
			String name = bean.indicatorName;
			if(name.toCharArray().length>64){
				return false;
						
			}
		}
		return true;
		
	}

	/* (non-Javadoc)
	 * ����һ���µķ���
	 * �����ID
	 * ���������
	 * ����汾�İ汾��
	 * 
	 * ��󽫰汾����
	 */
	@Override
	public TCComponentItemRevision createLawItem(TCComponentFolder folder,String lawID, String lawName, String lawRevNum,Logger logger) {
		String name = lawName.substring(0,lawName.length()>42?40:lawName.length());
		String desc = lawName;
		TCComponentItem lawItem = ItemUtil.createtLawItemWithRevNum(lawID, name, lawRevNum,desc);
		if(lawItem==null){   
			//���excel�������Ƿ���ȷ
			logger.fine("����"+lawID+"-"+lawRevNum+lawName+"-�ļ������Ƿ����");
			MessageBox.post("����"+lawID+"-"+lawRevNum+lawName+"  ������","",MessageBox.INFORMATION);
			return null;
		}
		try {
			folder.add("contents", lawItem);
			TCComponentItemRevision latestItemRevision = lawItem.getLatestItemRevision();
			return latestItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * �����Ѿ����ڷ�����°汾
	 * ����Item
	 * Ҫ���µİ汾��
	 * 
	 * ��󽫰汾����
	 */
	@Override
	public TCComponentItemRevision updateLawItem(TCComponentItem lawItem, String lawRevNum) {
		try {
			TCComponentItemRevision itemRev = lawItem.getLatestItemRevision();
			String revNum = itemRev.getProperty("item_revision_id");
			if(lawRevNum.equals(revNum)){
				return null;
			}
			TCComponentItemRevision newItemRev = itemRev.saveAs(lawRevNum);
			return newItemRev;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TCComponentFolder getIndexFolder() {
		
		TCComponentFolder indexFolder = null;
		TCComponentFolder homeFolder = null;
		String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����"}, new String[]{"_INDEX", "_INDEX", "Folder"} );
		if(searchResult.length>0){
			indexFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(indexFolder==null){//û���ҵ� ����
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����Ȩ�û�"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//�ҵ�����Ҫ�ҵ��ļ���
					homeFolder = (TCComponentFolder) component;
				}
			}
			
			
			try {
				indexFolder = ItemUtil.createFolder("_INDEX", "_INDEX");
				homeFolder.add("contents", indexFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return indexFolder;
	}

	@Override
	public TCComponentFolder getMaterialFolder() {
		
		TCComponentFolder materialFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����"}, new String[]{"WN_MATERIAL","WN_MATERIAL","Folder"} );
		if(searchResult.length>0){
			materialFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(materialFolder==null){//û���ҵ� ����
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
			searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����Ȩ�û�"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//�ҵ�����Ҫ�ҵ��ļ���
					homeFolder = (TCComponentFolder) component;
				}
			}
			try {
				materialFolder = ItemUtil.createFolder("WN_MATERIAL", "WN_MATERIAL");
				homeFolder.add("contents", materialFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return materialFolder;
	}
}
