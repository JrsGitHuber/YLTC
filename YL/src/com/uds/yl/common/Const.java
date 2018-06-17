package com.uds.yl.common;

import java.text.DecimalFormat;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.kernel.ics.InterfaceActiveObject;

public class Const {
	
	public interface CommonCosnt{//ͨ�õ�
		DecimalFormat doubleFormat = new DecimalFormat("0.0000");
		String BOM_VIEW_NAME = "��ͼ";
		String Model_File_Path_Preference = "UDSCodeConfigGroup";//ģ���ļ���·������ѡ��UDSCodeConfigGroup
		String Model_File_Root_Name = "���ݼ�ģ��";
		
	}
	
	public interface Formulator{//�䷽�
		String MATERIALBOMNAME = "��ͼ";
		String EXCEL_PATH = "C:\\temp\\�䷽��Ϣ��.xlsx";
		String EXCEL_SHEET1_NAME = "Ӫ���ɷֱ�";
		String EXCEL_SHEET2_NAME = "�䷽�嵥��";
	}
	
	public interface TechStandard{//������׼
		String BOMNAME = "��ͼ";
	}
	
	public interface Law{//����
		String BOMNAME = "��ͼ";
		String TEMPLATE_FILEPATH = "C:\\�ṹ������ģ��.xls";
		String SHEET_NAME = "Sheet1";
	}
	
	public interface LawImport{//���浼��
		String USED_IN_LAW = "UsedInLaw";//������Ƿ��浼���ʱ�򴴽���ָ�����ԭ��
	}
	public interface ProjectStatistics{//��Ŀͳ�Ʊ���
		String PROJECT_EXCUTE = "��Ŀִ�����";
		String WORK_EXCUTE = "����ִ�����";
		String SCHEDULE_START = "ʱ����ڿ�ʼ";
		String SCHEDULE_COMPLETE = "ʱ��������";
		
		String PROGRESSING_STATE = "������";
		String CLOSED_STATE = "�ѹر�";
		String NOT_START_STATE = "δ��ʼ";
		String COMPLETE_STATE = "���";
		String BREAK_OFF_STATE = "����ֹ";
		
		String PROJECTSTATISTICS_EXCEL_PATH = "C:\\temp\\��Ŀ���ͳ�Ʊ�.xlsx";
		String WORKSTATISTICS_EXCEL_PATH = "C:\\temp\\�������ͳ�Ʊ�.xlsx";
	}
	
	
	public interface FormulatorModify{
		String EXCEL_PATH = "C:\\temp\\�䷽��Ϣ��.xlsx";
		String EXCEL_SHEET1_NAME = "Ӫ���ɷֱ�";
		String EXCEL_SHEET2_NAME = "�䷽�嵥��";
		
	}
	
	public interface Pro_Statistics_Query_Condition{//��Ŀͳ�Ʊ���Ĳ�ѯ����
		 String START_DATE_AFTER = "start_date_after";
		 String START_DATE_BEFORE = "start_date_before";
		 String FINISH_DATE_AFTER = "finish_date_after";
		 String FINISH_DATE_BEFORE = "finish_date_before";
	}
	
	public interface Material_Query_Condition{//ԭ�ϲ�ѯ������
		String NAME = "����";
		String CODE = "ԭ�ϵ��Ӵ���";
		String SUPPLIER = "��Ӧ��";
		String COMMOND_ID = "UDS_YLL__material_use_query";
	}
	
	public interface FormulatorCompare{//�䷽�Ա�
		String COMMOND_ID = "UDS_YLL__formular_compare";
	}
	
	public interface FormulatorCheck{//�䷽���
		String BOMNAME = "��ͼ";
		String QUERY_AREA = "ʹ������";
		String FORMULATORCHEKC_EXCEL_PATH = "C:\\temp\\�䷽���.xlsx";
		String QUERY_ITME_ID = "ID";
	}
	
	public interface TechStandarModify{//������׼����
		String BOMNAME = "��ͼ";
		String QUERY_ITME_ID = "ID";
		String REDCOLOR = "red";
		String GREENCOLOR = "green";
		String QUERY_NAME = "����";
		String QUERY_REVISION_ID = "�汾";
		String QUERY_INDEX_TYPE = "��׼ָ�����";
		String INDEX_TYPE_LOV = "U8_IndexItemcategoryLOV";
	}
	
	public interface DispatchDocumentToProject{//����ϵͳ

		String SNED_TYPE = "����";//��������
		String REMOVE_TYPE = "����";//��������
		String TASK_TARGET_RELATED = "root_target_attachments";//�����Ŀ���µ����ݼ� ��ϵ
		
		String FORM_QUERY_NAME ="����";//��ѯForm������
		String PROJECT_QUERY = "��Ŀ ID";//��ѯ��Ŀ������
		String FROM_PROTERTY_ID ="u8_sendadd";//Form�е���Ŀ���Ƶ��б�
		
		String DATA_TYPE = "yyyy-MM-dd HH:mm:ss";//��¼���ڵĸ�ʽ
		String LOG_FILE_PATH = "C:\\temp\\DispatchDocumentToProject.log";//���ĵ�ָ�ɺ��Ƴ���־
	}

	public interface PDFConvert{//word��excelת��Ϊ
		String TEMP_DIR = "c:"+java.io.File.separator + "temp"+java.io.File.separator + "UDS";;
	}
	
	public interface MaterialUseQuery{//ԭ��ʹ�������ѯ
		String MATERIAL_TYPE_LOV_NAME = "U8_materialcategoryLOV";
	}
	
	public interface ProductFormulaExcel{//�����䷽��
		String BOM_NAME = "��ͼ";
		String IN_EXCEL_PATH = "C:\\temp\\Һ�������䷽.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\Һ�������䷽��.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\Һ�̸���������䷽.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\Һ�̸���������䷽.xlsx";
		
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		String Product_Formula_Excel_Input_Path ="C:\\temp\\Һ�������䷽.xlsx";//ģ������������·��
		String Product_Formula_Excel_Name ="Һ�������䷽.xlsx";//ģ�������
		String Product_Formula_Excel_Upload_Name = "Һ�������䷽";//�ϴ�������
		
		String Product_Complex_Excel_Input_Path ="C:\\temp\\Һ�̸���������䷽.xlsx";//ģ������������·��
		String Product_Complex_Excel_Name ="Һ�̸���������䷽.xlsx";//ģ�������
		String Product_Complex_Excel_Upload_Name = "Һ�̸���������䷽";//�ϴ�������
	}
	
	public interface FinanceFormulaExcel{//�����䷽��
		String BOM_NAME = "��ͼ";
		String IN_EXCEL_PATH = "C:\\temp\\Һ�̲����䷽.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\Һ�̲����䷽.xlsx";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\Һ�̶�������������䷽.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\Һ�̶�������������䷽.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		String Finance_Formula_Excel_Input_Path ="C:\\temp\\Һ�̲����䷽.xlsx";//ģ������������·��
		String Finance_Formula_Excel_Name ="Һ�̲����䷽.xlsx";//ģ�������
		String Finance_Formula_Excel_Upload_Name = "Һ�̲����䷽";//�ϴ�������
		
		String Finance_Complex_Excel_Input_Path ="C:\\Һ�̶�������������䷽.xlsx";//ģ������������·��
		String Finance_Complex_Excel_Name ="Һ�̶�������������䷽.xlsx";//ģ�������
		String Finance_Complex_Excel_Upload_Name = "Һ�̶�������������䷽";//�ϴ�������
		
		
		
	}
	
	public interface OrderFormulaExcel{//�����䷽��
		String BOM_NAME = "��ͼ";
		String IN_EXCEL_PATH = "C:\\temp\\Һ�̲�Ʒ����.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\Һ�̲�Ʒ����.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\Һ�̶�������������䷽.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\Һ�̶�������������䷽.xlsx";
		
		
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		String Order_Formula_Excel_Input_Path ="C:\\temp\\Һ�̲�Ʒ����.xlsx";//ģ������������·��
		String Order_Formula_Excel_Name ="Һ�̲�Ʒ����.xlsx";//ģ�������
		String Order_Formula_Excel_Upload_Name = "Һ�̲�Ʒ����";//�ϴ�������
		
		String Order_Complex_Excel_Input_Path ="C:\\temp\\Һ�̶�������������䷽.xlsx";//ģ������������·��
		String Order_Complex_Excel_Name ="Һ�̶�������������䷽.xlsx";//ģ�������
		String Order_Complex_Excel_Upload_Name = "Һ�̶�������������䷽";//�ϴ�������
		
		
	}
	
	public interface ColdDrinkFormulaExcel{//���������䷽��
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		String ColdDrink_Formula_Excel_Input_Path ="C:\\temp\\�������ϱ�.docx";//ģ������������·��
		String ColdDrink_Formula_Excel_Name ="�������ϱ�.docx";//ģ�������
		String ColdDrink_Formula_Excel_Upload_Name = "�������ϱ�";//�ϴ�������
		
		
		String ColdDrink_MinMaterial_Excel_Input_Path = "C:\\temp\\С�����ϵ�.docx";//ģ������������·��
		String ColdDrink_MinMaterial_Excel_Name = "С�����ϵ�.docx"; //ģ�������
		String ColdDrink_MinMaterial_Excel_Upload_Name = "С�����ϵ�";//�ϴ�������
		String BOMNAME = "��ͼ";
		
	}
	
	public interface MaterialorAccIndexStandard{ //ԭ���ϼ�����׼����
		String BOMNAME = "��ͼ";
		String TEMPLATE_DIR = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		
		String MATERIAL_INDEXSTANDARD_EXCEL_INPUT_PATH ="C:\\temp\\Һ��ԭ���ϱ�׼ģ��.xlsx";//ģ������������·��
		String MATERIAL_INDEXSTANDARD_EXCEL_NAME ="Һ��ԭ���ϱ�׼ģ��.xlsx";//ģ�������
		String Material_Excel_Name = "ԭ�ϼ�����׼����";//�ϴ�������
		
		String ACCESSORIES_INDEXSTANDARD_EXCEL_PATH ="C:\\temp\\���ϼ�����׼����.xlsx";//ģ������������·��
		String ACCESSORIES_INDEXSTANDARD_EXCEL_NAME ="���ϼ�����׼����.xlsx";//ģ�������
		String Accessories_Excel_Name = "���ϼ�����׼����";//�ϴ�������
		
		String ENDPRO_INDEXSTANDARD_EXCEL_PATH = "C:\\temp\\��Ʒ������׼����.xlsx";//ģ������������·��
		String ENDPRO_INDEXSTANDARD_EXCEL_NAME = "��Ʒ������׼����.xlsx";//ģ�������
		String Product_Excel_Name = "��Ʒ������׼����";//�ϴ�������
		
		
		String HALFPRO_INDEXSTANDARD_EXCEL_PATH = "C:\\temp\\���Ʒ������׼����.xlsx";//ģ������������·��
		String HALFPRO_INDEXSTANDARD_EXCEL_NAME = "���Ʒ������׼����.xlsx";//ģ�������
		String Simi_Finish_Product_Excel_Name = "���Ʒ������׼����";//�ϴ�������
		
	}
	
	public interface LabelGeneratorExcel{//Һ̬��Ӫ����ǩ
		
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		
		String NRV_ZERO_VALUE_EXCEL_INPUT_PATH = "C:\\temp\\Һ̬NRV��0��ֵ.xlsx";//0��ֵ��NRVģ������������·��
		String NRV_ZERO_VALUE__EXCEL_NAME ="Һ̬NRV��0��ֵ.xlsx";//0��ֵ��NRVģ�������
		
		
		
		String LABEL_EXCEL_INPUT_PATH = "C:\\temp\\Һ̬��ǩ��.xlsx";//��ǩģ������������·��
		String LABEL_EXCEL_NAME = "Һ̬��ǩ��.xlsx";//ģ����������������
		String Label_Excel_Upload_Name="Һ̬��ǩ��";//ģ��Ҫ�ϴ�����
		
		
		
		String BOMNAME="��ͼ";
		String[] LABEL_NAME_ARRY  = {"����","������","֬��","����֬�����ᣩ","��ʽ֬�����ᣩ","��������֬�����ᣩ","�಻����֬�����ᣩ",
				"���̴�","̼ˮ������","�ǣ����ǣ�","��ʳ��ά","��","ά����A","ά����D","ά����E",
				"ά����K","ά����B1","ά����B2","ά����B6","ά����B12","ά����C","����","Ҷ��",
				"����","������","����","��","��","þ","��","��","п",
				"��","��","ͭ","��","��"};
		
		String SHEET_NAME = "Sheet1";
		
		String ZERO_SHEET_NAME = "0����ֵ";
		String NRV_SHEET_NAME="NRV";
	}
	
	
	public interface LabelGeneratorSolidExcel{//��̬��Ӫ����ǩ
		
		String Template_Dir = "C:\\temp\\";// ģ��Ҫ�����������ļ���

		String NRV_ZERO_VALUE_EXCEL_INPUT_PATH = "C:\\temp\\��̬NRV��0��ֵ.xlsx";// 0��ֵ��NRVģ������������·��
		String NRV_ZERO_VALUE__EXCEL_NAME = "��̬NRV��0��ֵ.xlsx";// 0��ֵ��NRVģ�������

		String LABEL_EXCEL_INPUT_PATH = "C:\\temp\\��̬��ǩ�� .xlsx";// ��ǩģ������������·��
		String LABEL_EXCEL_NAME = "��̬��ǩ��.xlsx";// ģ����������������
		String Label_Excel_Upload_Name = "��̬��ǩ��";// ģ��Ҫ�ϴ�����

		String BOMNAME = "��ͼ";
		String[] LABEL_NAME_ARRY = { "����", "������", "֬��", "����֬�����ᣩ", "��ʽ֬�����ᣩ", "��������֬�����ᣩ", "�಻����֬�����ᣩ", "���̴�", "̼ˮ������",
				"�ǣ����ǣ�", "��ʳ��ά", "��", "ά����A", "ά����D", "ά����E", "ά����K", "ά����B1", "ά����B2", "ά����B6", "ά����B12", "ά����C", "����",
				"Ҷ��", "����", "������", "����", "��", "��", "þ", "��", "��", "п", "��", "��", "ͭ", "��", "��" };

		String SHEET_NAME = "Sheet1";

		String ZERO_SHEET_NAME = "0����ֵ";
		String NRV_SHEET_NAME = "NRV";
	}
	
	public interface MilkFinManageExcel{//Һ�̼�Ч����ͳ�Ʊ���
		String FORM_QUERY_NAME ="����";//��ѯForm������
		String FROM_PROTERTY_ID ="u8_sendadd";//Form�е���Ŀ���Ƶ��б�
		String EXCEL_PATH = "C:\\temp\\Һ�̼�Ч����ͳ�Ʊ���.xlsx";
		String EXCEL_SHEET1 = "��Ч����ͳ��";

	}

	
	public interface ColdFormula{//�����䷽���
		String BOMNAME = "��ͼ";
	}


	public interface MilkScore{//Һ�̴������
		String Idea_Judg_Process = "YN00_����Ϊ�����ж�����";//�����ж�
		String No_Idea_Judg_Process = "YN00_����Ϊ�ǵ����ж�����";//�ǵ����ж�����
		String Second_Idea_Judg_Process="YN00_����Ϊ�������ж�����";//�����ж�����
	}

	public interface MilkPowderFormulator{//�̷��䷽���
		String  FORMULATOR_TYPE_LOV = "U8_FormulatorTypeLov";//�䷽���͵ľ�̬LOV
		String EXCEL_SHEET1 = "Sheet1";
		
		String Template_Dir = "C:\\temp\\";//ģ��Ҫ�����������ļ���
		String Nutrition_Excel_Input_Path ="C:\\temp\\�̷�Ӫ���ر�׼��.xlsx";//Ӫ���ɷֱ��·��
		String Nutrition_Excel_Name ="�̷�Ӫ���ر�׼��.xlsx";//Ӫ���ɷֱ������
		String Nutrition_Excel_Upload_Name = "�̷�Ӫ���ر�׼��";//�ϴ�������
		
		String Foumulator_Excel_Input_Path ="C:\\temp\\�̷������䷽��.xlsx";//�����䷽���·��
		String Formulator_Excel_Name ="�̷������䷽��.xlsx";//�����䷽�������
		String Formulator_Excel_Upload_Name = "�̷������䷽��";//�ϴ�������
		
		
		String Index_Excel_Input_Path ="C:\\temp\\�̷�Ӫ���ɷֱ�.xlsx";//�����䷽���·��
		String Index_Excel_Name ="�̷�Ӫ���ɷֱ�.xlsx";//�����䷽�������
		String Index_Excel_Upload_Name = "�̷�Ӫ���ɷֱ�";//�ϴ�������
	
	}

	
	
	public interface QueryKey{//��ѯ������˵
		String TYPE = "����";
		String NAME = "����";
		String OWNER = "����Ȩ�û�";
		String DATE_START = "����ʱ������";
		String DATE_END = "����ʱ������";
	}
	
	
	public interface BomViewType{//��ͬ���͵�bom�������ֶ���Ҫд�϶�Ӧ�Ĳ�ͬ���ֶ�
		String LAW = "������ͼ";//����
		String PRODUCT_STANDARD = "��Ʒ��׼��ͼ";//��Ʒ������׼
		String MATERIAL_STANDARD = "ԭ�ϱ�׼��ͼ";//ԭ�ϼ�����׼
		String FORMULATOR = "�䷽��ͼ";//�䷽
		String MATERIAL = "ԭ����ͼ";//����ԭ��
	}
	
	
	public interface IndexType{//������׼���
		String MATERIAL_STANDARD = "ԭ�ϼ�����׼";//ԭ�ϼ�����׼
		String PRODUCT_STANDARD = "��Ʒ������׼";//��Ʒ������׼
		
	}
	
	
	public interface NodeType{//�ڵ������
		String NODE_INDEXITEM = "0";//�ڵ���ָ������
		String NODE_MATERIAL = "1";//�ڵ���ԭ������
		String NODE_NUTRITION = "2";//�ڵ���Ӫ��������
		String NODE_FORMULA = "3";//�ڵ����䷽����
		String NODE_LAW = "4";//�ڵ��Ƿ�������
		String NODE_INDEX = "5";//�ڵ��Ǽ�����׼����
		String NODE_BASE_FORMULATOR = "6";//��������
		
	}
	
	//ϵͳ�е�item�汾������
	public interface ItemRevType{
		String FORMUALTOR_REV = "U8_FormulaRevision";//�䷽�汾
		String MATERIAL_REV = "U8_MaterialRevision";//ԭ�ϰ汾
		String INDEXITEM_REV = "U8_IndexItemRevision";//ָ��汾
		String INDEX_REV = "U8_IndexRevision";//������׼�汾
		String LAW_REV = "U8_LawRevision";//����汾
	}
	
	//ϵͳ�е�item������
	public interface ItemType{
		String FORMUALTOR_ITEM = "U8_Formula";//�䷽
		String MATERIAL_ITEM = "U8_Material";//ԭ��
		String INDEXITEM_ITEM = "U8_IndexItem";//ָ��
		String INDEX_ITEM = "U8_Index";//������׼
		String LAW_REV = "U8_Law";//����
	}
	
	//��ͬ����ͼ���͵Ľз�
	public interface BomType{
		String BOM_VIEW = "��ͼ";
		String BOM_NUTRITION = "����Ӫ����ǩ";
	}


}	
