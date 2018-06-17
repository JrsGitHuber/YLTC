package com.uds.yl.bean;

import java.util.ArrayList;
import java.util.List;
//技术标准中使用的
public class TechStandarTableBean{
	public String name;//名称
	public String itemId="";//itemId
	public String oldStandard ="";
	public String newStandard= "";
	
	public String unit;//单位
	public String type="";
	public String newWaring="";//新预警值
	public String oldWaring = "";//原预警值
	public String remark = "";//备注
	public List<String> lawStandards = new ArrayList<String>();
	
	public String indexIntroduceString = "";//指标说明
	
	public String testGis="";//检测方法依据
	public String currentMethod = "";//当前检测方法  取自指标BOM行中  U8_testcriterion
	public List<String> allMethodsList = new ArrayList<String>();//所有的检测方法 取自指标的版本中  u8_testmethod2
}