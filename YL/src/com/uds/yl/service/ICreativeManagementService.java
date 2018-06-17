package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;

public interface ICreativeManagementService  {
	
	//�ж��᰸����ͨ�� ����û�з���״̬
	public boolean proposalFormIsInRule(TCComponentForm form)throws TCException ;
	
	//��ȡ�������᰸���ϵ�µĴ�ֱ���ֻ��ս�Էֵı�ļ���
	public List<TCComponentForm> getOneScoreFormList(TCComponentForm proposalForm)throws TCException;
	
	//��ȡ�������᰸���ϵ�µĴ�ֱ������ĸ���ֵ��ս�Է�Ϊ�գ��ı�ļ���
	public List<TCComponentForm> getFourScoreFormList(TCComponentForm proposalForm)throws TCException;
	
	//������ֵ
	public void computeSumScore(List<TCComponentForm> oneScoreFormList,List<TCComponentForm> fourScoreFormList,TCComponentForm proposalForm)throws TCException;
	
	//�����������Ƿ��Ѿ����λ���������
	public boolean canComputeSumScore(TCComponentForm proposalForm)throws TCException;
	
	//��Form����λ����״̬
	public void setReleaseForm(TCComponentForm proposalForm)throws TCException;
	
	//�ɷ�����ͨ����ͨ�����ж�
	public boolean proposalFormCanByPass(TCComponentForm form)throws TCException;
}
