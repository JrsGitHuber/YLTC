package com.uds.yl.bean;


public class FormulatorCheckedBean {
	public String name;//名字  Material的名字或者IndexItem的名字
	public String category;//类别  指标或者添加剂
	public String formulatorValue;//添加剂是投料量  指标是上下限范围A~B
	public String lawName;//法规的名称
	public String lawValue;//法规中对应的 上下限的值
	public String excessiveDesc;//超标说明
	public String wranings;//超标警示语
}
