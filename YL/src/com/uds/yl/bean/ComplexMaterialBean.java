package com.uds.yl.bean;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class ComplexMaterialBean {
	public MaterialBean rootMaterial;
	public TCComponentBOMLine rootBomLine;
	public List<MaterialBean> allChildsMaterial;
	public List<TCComponentBOMLine> allMaterialBomLine;
}
