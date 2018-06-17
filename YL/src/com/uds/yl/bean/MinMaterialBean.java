package com.uds.yl.bean;

import java.util.List;

public class MinMaterialBean {
	public String name;
	public MinMaterialBean materialBean;//小料自身
	public String inventory;//小料的投料量
	public List<MaterialBean> allChildsMaterial;//小料的所有原料孩子
}
