package com.uds.yl.common;

import java.text.DecimalFormat;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.kernel.ics.InterfaceActiveObject;

public class Const {
	
	public interface CommonCosnt{//通用的
		DecimalFormat doubleFormat = new DecimalFormat("0.0000");
		String BOM_VIEW_NAME = "视图";
		String Model_File_Path_Preference = "UDSCodeConfigGroup";//模板文件的路径的首选项UDSCodeConfigGroup
		String Model_File_Root_Name = "数据集模板";
		
	}
	
	public interface Formulator{//配方搭建
		String MATERIALBOMNAME = "视图";
		String EXCEL_PATH = "C:\\temp\\配方信息表.xlsx";
		String EXCEL_SHEET1_NAME = "营养成分表";
		String EXCEL_SHEET2_NAME = "配方清单表";
	}
	
	public interface TechStandard{//技术标准
		String BOMNAME = "视图";
	}
	
	public interface Law{//法规
		String BOMNAME = "视图";
		String TEMPLATE_FILEPATH = "C:\\结构化数据模板.xls";
		String SHEET_NAME = "Sheet1";
	}
	
	public interface LawImport{//法规导入
		String USED_IN_LAW = "UsedInLaw";//用来标记法规导入的时候创建的指标或者原料
	}
	public interface ProjectStatistics{//项目统计报表
		String PROJECT_EXCUTE = "项目执行情况";
		String WORK_EXCUTE = "工作执行情况";
		String SCHEDULE_START = "时间段内开始";
		String SCHEDULE_COMPLETE = "时间段内完成";
		
		String PROGRESSING_STATE = "进行中";
		String CLOSED_STATE = "已关闭";
		String NOT_START_STATE = "未开始";
		String COMPLETE_STATE = "完成";
		String BREAK_OFF_STATE = "已中止";
		
		String PROJECTSTATISTICS_EXCEL_PATH = "C:\\temp\\项目情况统计表.xlsx";
		String WORKSTATISTICS_EXCEL_PATH = "C:\\temp\\工作情况统计表.xlsx";
	}
	
	
	public interface FormulatorModify{
		String EXCEL_PATH = "C:\\temp\\配方信息表.xlsx";
		String EXCEL_SHEET1_NAME = "营养成分表";
		String EXCEL_SHEET2_NAME = "配方清单表";
		
	}
	
	public interface Pro_Statistics_Query_Condition{//项目统计报表的查询条件
		 String START_DATE_AFTER = "start_date_after";
		 String START_DATE_BEFORE = "start_date_before";
		 String FINISH_DATE_AFTER = "finish_date_after";
		 String FINISH_DATE_BEFORE = "finish_date_before";
	}
	
	public interface Material_Query_Condition{//原料查询的条件
		String NAME = "名称";
		String CODE = "原料电子代码";
		String SUPPLIER = "供应商";
		String COMMOND_ID = "UDS_YLL__material_use_query";
	}
	
	public interface FormulatorCompare{//配方对比
		String COMMOND_ID = "UDS_YLL__formular_compare";
	}
	
	public interface FormulatorCheck{//配方检查
		String BOMNAME = "视图";
		String QUERY_AREA = "使用区域";
		String FORMULATORCHEKC_EXCEL_PATH = "C:\\temp\\配方检查.xlsx";
		String QUERY_ITME_ID = "ID";
	}
	
	public interface TechStandarModify{//技术标准新增
		String BOMNAME = "视图";
		String QUERY_ITME_ID = "ID";
		String REDCOLOR = "red";
		String GREENCOLOR = "green";
		String QUERY_NAME = "名称";
		String QUERY_REVISION_ID = "版本";
		String QUERY_INDEX_TYPE = "标准指标类别";
		String INDEX_TYPE_LOV = "U8_IndexItemcategoryLOV";
	}
	
	public interface DispatchDocumentToProject{//发文系统

		String SNED_TYPE = "发送";//发送类型
		String REMOVE_TYPE = "撤回";//撤回类型
		String TASK_TARGET_RELATED = "root_target_attachments";//任务的目标下的数据集 关系
		
		String FORM_QUERY_NAME ="名称";//查询Form的条件
		String PROJECT_QUERY = "项目 ID";//查询项目的条件
		String FROM_PROTERTY_ID ="u8_sendadd";//Form中的项目名称的列表
		
		String DATA_TYPE = "yyyy-MM-dd HH:mm:ss";//记录日期的格式
		String LOG_FILE_PATH = "C:\\temp\\DispatchDocumentToProject.log";//发文的指派和移除日志
	}

	public interface PDFConvert{//word、excel转换为
		String TEMP_DIR = "c:"+java.io.File.separator + "temp"+java.io.File.separator + "UDS";;
	}
	
	public interface MaterialUseQuery{//原料使用情况查询
		String MATERIAL_TYPE_LOV_NAME = "U8_materialcategoryLOV";
	}
	
	public interface ProductFormulaExcel{//生产配方表
		String BOM_NAME = "视图";
		String IN_EXCEL_PATH = "C:\\temp\\液奶生产配方.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\液奶生产配方表.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\液奶复配增稠剂配方.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\液奶复配增稠剂配方.xlsx";
		
		String Template_Dir = "C:\\temp\\";//模板要下载下俩的文件夹
		String Product_Formula_Excel_Input_Path ="C:\\temp\\液奶生产配方.xlsx";//模板下载下来的路径
		String Product_Formula_Excel_Name ="液奶生产配方.xlsx";//模板的名称
		String Product_Formula_Excel_Upload_Name = "液奶生产配方";//上传的名字
		
		String Product_Complex_Excel_Input_Path ="C:\\temp\\液奶复配增稠剂配方.xlsx";//模板下载下来的路径
		String Product_Complex_Excel_Name ="液奶复配增稠剂配方.xlsx";//模板的名称
		String Product_Complex_Excel_Upload_Name = "液奶复配增稠剂配方";//上传的名字
	}
	
	public interface FinanceFormulaExcel{//财务配方表
		String BOM_NAME = "视图";
		String IN_EXCEL_PATH = "C:\\temp\\液奶财务配方.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\液奶财务配方.xlsx";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\液奶订单复配增稠剂配方.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\液奶订单复配增稠剂配方.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		
		String Template_Dir = "C:\\temp\\";//模板要下载下来的文件夹
		String Finance_Formula_Excel_Input_Path ="C:\\temp\\液奶财务配方.xlsx";//模板下载下来的路径
		String Finance_Formula_Excel_Name ="液奶财务配方.xlsx";//模板的名称
		String Finance_Formula_Excel_Upload_Name = "液奶财务配方";//上传的名字
		
		String Finance_Complex_Excel_Input_Path ="C:\\液奶订单复配增稠剂配方.xlsx";//模板下载下来的路径
		String Finance_Complex_Excel_Name ="液奶订单复配增稠剂配方.xlsx";//模板的名称
		String Finance_Complex_Excel_Upload_Name = "液奶订单复配增稠剂配方";//上传的名字
		
		
		
	}
	
	public interface OrderFormulaExcel{//订单配方表
		String BOM_NAME = "视图";
		String IN_EXCEL_PATH = "C:\\temp\\液奶产品订单.xlsx";
		String OUT_EXCEL_PATH = "C:\\temp\\液奶产品订单.xlsx";
		String EXCEL_SHEET1 = "Sheet1";
		String COMPLEX_IN_EXCEL_PATH = "C:\\temp\\液奶订单复配增稠剂配方.xlsx";
		String COMPLEX_OUT_EXCEL_PATH="C:\\temp\\液奶订单复配增稠剂配方.xlsx";
		
		
		String Template_Dir = "C:\\temp\\";//模板要下载下俩的文件夹
		String Order_Formula_Excel_Input_Path ="C:\\temp\\液奶产品订单.xlsx";//模板下载下来的路径
		String Order_Formula_Excel_Name ="液奶产品订单.xlsx";//模板的名称
		String Order_Formula_Excel_Upload_Name = "液奶产品订单";//上传的名字
		
		String Order_Complex_Excel_Input_Path ="C:\\temp\\液奶订单复配增稠剂配方.xlsx";//模板下载下来的路径
		String Order_Complex_Excel_Name ="液奶订单复配增稠剂配方.xlsx";//模板的名称
		String Order_Complex_Excel_Upload_Name = "液奶订单复配增稠剂配方";//上传的名字
		
		
	}
	
	public interface ColdDrinkFormulaExcel{//冷饮生产配方表
		String Template_Dir = "C:\\temp\\";//模板要下载下来的文件夹
		String ColdDrink_Formula_Excel_Input_Path ="C:\\temp\\冷饮配料表.docx";//模板下载下来的路径
		String ColdDrink_Formula_Excel_Name ="冷饮配料表.docx";//模板的名称
		String ColdDrink_Formula_Excel_Upload_Name = "冷饮配料表";//上传的名字
		
		
		String ColdDrink_MinMaterial_Excel_Input_Path = "C:\\temp\\小料配料单.docx";//模板下载下来的路径
		String ColdDrink_MinMaterial_Excel_Name = "小料配料单.docx"; //模板的名称
		String ColdDrink_MinMaterial_Excel_Upload_Name = "小料配料单";//上传的名字
		String BOMNAME = "视图";
		
	}
	
	public interface MaterialorAccIndexStandard{ //原辅料技术标准报表
		String BOMNAME = "视图";
		String TEMPLATE_DIR = "C:\\temp\\";//模板要下载下俩的文件夹
		
		String MATERIAL_INDEXSTANDARD_EXCEL_INPUT_PATH ="C:\\temp\\液奶原辅料标准模板.xlsx";//模板下载下来的路径
		String MATERIAL_INDEXSTANDARD_EXCEL_NAME ="液奶原辅料标准模板.xlsx";//模板的名称
		String Material_Excel_Name = "原料技术标准报表";//上传的名字
		
		String ACCESSORIES_INDEXSTANDARD_EXCEL_PATH ="C:\\temp\\辅料技术标准报表.xlsx";//模板下载下来的路径
		String ACCESSORIES_INDEXSTANDARD_EXCEL_NAME ="辅料技术标准报表.xlsx";//模板的名称
		String Accessories_Excel_Name = "辅料技术标准报表";//上传的名字
		
		String ENDPRO_INDEXSTANDARD_EXCEL_PATH = "C:\\temp\\产品技术标准报表.xlsx";//模板下载下来的路径
		String ENDPRO_INDEXSTANDARD_EXCEL_NAME = "产品技术标准报表.xlsx";//模板的名称
		String Product_Excel_Name = "产品技术标准报表";//上传的名字
		
		
		String HALFPRO_INDEXSTANDARD_EXCEL_PATH = "C:\\temp\\半成品技术标准报表.xlsx";//模板下载下来的路径
		String HALFPRO_INDEXSTANDARD_EXCEL_NAME = "半成品技术标准报表.xlsx";//模板的名称
		String Simi_Finish_Product_Excel_Name = "半成品技术标准报表";//上传的名字
		
	}
	
	public interface LabelGeneratorExcel{//液态的营养标签
		
		String Template_Dir = "C:\\temp\\";//模板要下载下俩的文件夹
		
		String NRV_ZERO_VALUE_EXCEL_INPUT_PATH = "C:\\temp\\液态NRV和0界值.xlsx";//0界值和NRV模板下载下来的路径
		String NRV_ZERO_VALUE__EXCEL_NAME ="液态NRV和0界值.xlsx";//0界值和NRV模板的名称
		
		
		
		String LABEL_EXCEL_INPUT_PATH = "C:\\temp\\液态标签表.xlsx";//标签模板下载下来的路径
		String LABEL_EXCEL_NAME = "液态标签表.xlsx";//模板下载下来的名称
		String Label_Excel_Upload_Name="液态标签表";//模板要上传名称
		
		
		
		String BOMNAME="视图";
		String[] LABEL_NAME_ARRY  = {"能量","蛋白质","脂肪","饱和脂肪（酸）","反式脂肪（酸）","单不饱和脂肪（酸）","多不饱和脂肪（酸）",
				"胆固醇","碳水化合物","糖（乳糖）","膳食纤维","钠","维生素A","维生素D","维生素E",
				"维生素K","维生素B1","维生素B2","维生素B6","维生素B12","维生素C","烟酸","叶酸",
				"泛酸","生物素","胆碱","磷","钾","镁","钙","铁","锌",
				"碘","硒","铜","氟","锰"};
		
		String SHEET_NAME = "Sheet1";
		
		String ZERO_SHEET_NAME = "0界限值";
		String NRV_SHEET_NAME="NRV";
	}
	
	
	public interface LabelGeneratorSolidExcel{//固态的营养标签
		
		String Template_Dir = "C:\\temp\\";// 模板要下载下俩的文件夹

		String NRV_ZERO_VALUE_EXCEL_INPUT_PATH = "C:\\temp\\固态NRV和0界值.xlsx";// 0界值和NRV模板下载下来的路径
		String NRV_ZERO_VALUE__EXCEL_NAME = "固态NRV和0界值.xlsx";// 0界值和NRV模板的名称

		String LABEL_EXCEL_INPUT_PATH = "C:\\temp\\固态标签表 .xlsx";// 标签模板下载下来的路径
		String LABEL_EXCEL_NAME = "固态标签表.xlsx";// 模板下载下来的名称
		String Label_Excel_Upload_Name = "固态标签表";// 模板要上传名称

		String BOMNAME = "视图";
		String[] LABEL_NAME_ARRY = { "能量", "蛋白质", "脂肪", "饱和脂肪（酸）", "反式脂肪（酸）", "单不饱和脂肪（酸）", "多不饱和脂肪（酸）", "胆固醇", "碳水化合物",
				"糖（乳糖）", "膳食纤维", "钠", "维生素A", "维生素D", "维生素E", "维生素K", "维生素B1", "维生素B2", "维生素B6", "维生素B12", "维生素C", "烟酸",
				"叶酸", "泛酸", "生物素", "胆碱", "磷", "钾", "镁", "钙", "铁", "锌", "碘", "硒", "铜", "氟", "锰" };

		String SHEET_NAME = "Sheet1";

		String ZERO_SHEET_NAME = "0界限值";
		String NRV_SHEET_NAME = "NRV";
	}
	
	public interface MilkFinManageExcel{//液奶绩效管理统计报表
		String FORM_QUERY_NAME ="名称";//查询Form的条件
		String FROM_PROTERTY_ID ="u8_sendadd";//Form中的项目名称的列表
		String EXCEL_PATH = "C:\\temp\\液奶绩效管理统计报表.xlsx";
		String EXCEL_SHEET1 = "绩效管理统计";

	}

	
	public interface ColdFormula{//冷饮配方搭建器
		String BOMNAME = "视图";
	}


	public interface MilkScore{//液奶打分流程
		String Idea_Judg_Process = "YN00_结论为点子判定流程";//点子判定
		String No_Idea_Judg_Process = "YN00_结论为非点子判定流程";//非点子判定流程
		String Second_Idea_Judg_Process="YN00_结论为待二次判定流程";//二次判定流程
	}

	public interface MilkPowderFormulator{//奶粉配方搭建器
		String  FORMULATOR_TYPE_LOV = "U8_FormulatorTypeLov";//配方类型的静态LOV
		String EXCEL_SHEET1 = "Sheet1";
		
		String Template_Dir = "C:\\temp\\";//模板要下载下俩的文件夹
		String Nutrition_Excel_Input_Path ="C:\\temp\\奶粉营养素标准表.xlsx";//营养成分表的路径
		String Nutrition_Excel_Name ="奶粉营养素标准表.xlsx";//营养成分表的名称
		String Nutrition_Excel_Upload_Name = "奶粉营养素标准表";//上传的名称
		
		String Foumulator_Excel_Input_Path ="C:\\temp\\奶粉生产配方表.xlsx";//生成配方表的路径
		String Formulator_Excel_Name ="奶粉生产配方表.xlsx";//生产配方表的名称
		String Formulator_Excel_Upload_Name = "奶粉生产配方表";//上传的名字
		
		
		String Index_Excel_Input_Path ="C:\\temp\\奶粉营养成分表.xlsx";//生成配方表的路径
		String Index_Excel_Name ="奶粉营养成分表.xlsx";//生产配方表的名称
		String Index_Excel_Upload_Name = "奶粉营养成分表";//上传的名字
	
	}

	
	
	public interface QueryKey{//查询条件的说
		String TYPE = "类型";
		String NAME = "名称";
		String OWNER = "所有权用户";
		String DATE_START = "创建时间晚于";
		String DATE_END = "创建时间早于";
	}
	
	
	public interface BomViewType{//不同类型的bom的描述字段中要写上对应的不同的字段
		String LAW = "法规视图";//法规
		String PRODUCT_STANDARD = "产品标准视图";//产品技术标准
		String MATERIAL_STANDARD = "原料标准视图";//原料技术标准
		String FORMULATOR = "配方视图";//配方
		String MATERIAL = "原料视图";//复配原料
	}
	
	
	public interface IndexType{//技术标准类别
		String MATERIAL_STANDARD = "原料技术标准";//原料技术标准
		String PRODUCT_STANDARD = "产品技术标准";//产品技术标准
		
	}
	
	
	public interface NodeType{//节点的类型
		String NODE_INDEXITEM = "0";//节点是指标类型
		String NODE_MATERIAL = "1";//节点是原料类型
		String NODE_NUTRITION = "2";//节点是营养包类型
		String NODE_FORMULA = "3";//节点是配方类型
		String NODE_LAW = "4";//节点是法规类型
		String NODE_INDEX = "5";//节点是技术标准类型
		String NODE_BASE_FORMULATOR = "6";//基粉类型
		
	}
	
	//系统中的item版本的类型
	public interface ItemRevType{
		String FORMUALTOR_REV = "U8_FormulaRevision";//配方版本
		String MATERIAL_REV = "U8_MaterialRevision";//原料版本
		String INDEXITEM_REV = "U8_IndexItemRevision";//指标版本
		String INDEX_REV = "U8_IndexRevision";//技术标准版本
		String LAW_REV = "U8_LawRevision";//法规版本
	}
	
	//系统中的item的类型
	public interface ItemType{
		String FORMUALTOR_ITEM = "U8_Formula";//配方
		String MATERIAL_ITEM = "U8_Material";//原料
		String INDEXITEM_ITEM = "U8_IndexItem";//指标
		String INDEX_ITEM = "U8_Index";//技术标准
		String LAW_REV = "U8_Law";//法规
	}
	
	//不同的视图类型的叫法
	public interface BomType{
		String BOM_VIEW = "视图";
		String BOM_NUTRITION = "冷饮营养标签";
	}


}	
