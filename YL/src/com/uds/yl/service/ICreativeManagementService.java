package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;

public interface ICreativeManagementService  {
	
	//判断提案表是通过 并且没有发布状态
	public boolean proposalFormIsInRule(TCComponentForm form)throws TCException ;
	
	//获取关联在提案表关系下的打分表中只有战略分的表的集合
	public List<TCComponentForm> getOneScoreFormList(TCComponentForm proposalForm)throws TCException;
	
	//获取关联在提案表关系下的打分表中有四个分值（战略分为空）的表的集合
	public List<TCComponentForm> getFourScoreFormList(TCComponentForm proposalForm)throws TCException;
	
	//计算总值
	public void computeSumScore(List<TCComponentForm> oneScoreFormList,List<TCComponentForm> fourScoreFormList,TCComponentForm proposalForm)throws TCException;
	
	//看这个提审表是否已经三次机会用完了
	public boolean canComputeSumScore(TCComponentForm proposalForm)throws TCException;
	
	//将Form设置位发布状态
	public void setReleaseForm(TCComponentForm proposalForm)throws TCException;
	
	//可否设置通过不通过的判定
	public boolean proposalFormCanByPass(TCComponentForm form)throws TCException;
}
