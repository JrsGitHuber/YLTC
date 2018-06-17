package com.uds.yl.interfaces;

import java.io.File;
import java.util.List;

import javax.swing.JTextField;

import org.eclipse.jface.preference.BooleanPropertyAction;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.MaterialBean;


public abstract class AbstractCallBack {
	//查询到的法规和法规的名子
	public void setLawAndName(String lawName,TCComponentItemRevision lawRev) {}
	
	//选择文件
	public void setFilesAndType(String type,File[] files){}
	
	
	//通过不通不过并给意见
	public void setComment(String passOrNot,String comment,
			String box1,String box2,String box3,String box4,String box5)throws TCException{}
	
	//查询原料的回调函数
	public void setMaterialItem(TCComponentItemRevision materialItemRev){}
	
	//冷饮配方搭建器补足用
	public void setCompelemnet(String complementType,String complementContent){	}
	
	//奶粉配方搭建器搜索法规
	public void setLawRev(TCComponentItemRevision lawItemRev){}
	
	
	//奶粉配方搭建器搜索指标
	public void setIndexRev(TCComponentItemRevision indexItemRev){}
	
	
	//查看营养包的界面
	public void modifyNutritionRev(List<TCComponentItemRevision> materialList,List<MaterialBean> materialBeanList){}

	//创建湿法配方
	public void createWetFormulator(boolean selected ,String name){}

	
	//添加基粉
	public void addWetFormulator(TCComponentItemRevision wetFormulator,String name){};
	
	//打分后回写当前用户ID
	public void setUserIdInProposalForm(String userID){};
	
	
	//新增技术标准搭建器中用来调整上下限的回调
	public void setUpAndDownResult(String result){};
	
	//技术标准打搭建器 新增一个新的指标的条目
	public void addNewIndexResult(String indexName,String indexUnit){};

	//技术标准搭建器 修改一个指标的条目
	public void modifyIndexBeanResult(String indexIntroduce,String testGistEdt){};
	
	//产品标准搭建器 修改新内控标准和新预警值
	public void setStandardAndWaringResult(String standardResult,String waringResult,String indexTesStr,String indexIntroduceStr,String remarkStr){}
	
	//奶粉配方搭建器中插入营养包的回调
	public void setNutritionName(String nutritionName){};
	
	//奶粉配方搭建器中插入基粉还有含水量的回调
	public void setFormulatorName(String formulatorName, String warterValue){};
}
