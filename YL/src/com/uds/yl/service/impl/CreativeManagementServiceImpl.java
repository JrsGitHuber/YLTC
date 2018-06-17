package com.uds.yl.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentReleaseStatus;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessOperation;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.uds.yl.common.Const;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.utils.StringsUtil;

public class CreativeManagementServiceImpl implements ICreativeManagementService{
	
	private DecimalFormat df = new DecimalFormat("0.00");//������λ
	/* (non-Javadoc)
	 * �᰸��û�з���״̬������ͨ����
	 */
	@Override
	public boolean proposalFormIsInRule(TCComponentForm form) throws TCException {
		TCComponent[] releaseList = form.getReferenceListProperty("release_status_list");
		boolean isCanBeUse = false;
		for(TCComponent component : releaseList){
			String name = component.toDisplayString();
			if("��ɸͨ��".equals(name)){//ֻ�г�ɸͨ���Ĳ��ܴ��
				isCanBeUse = true;
			}
		}
		String status = form.getProperty("u8_status");
		if ( "pass".equals(status)&&isCanBeUse) {
			return true;
		}
		return false;
		
	}

	/* (non-Javadoc)
	 * ��ȡ�������᰸���ϵ�µĴ�ֱ���ֻ��ս�Էֵı�ļ���
	 */
	@Override
	public List<TCComponentForm> getOneScoreFormList(TCComponentForm proposalForm) throws TCException {
		List<TCComponentForm> oneScoreFormList = new ArrayList<>();
		TCComponent[] scoreRelList = proposalForm.getReferenceListProperty("U8_ScoreREL");
		for (TCComponent tcComponent : scoreRelList) {
			if (tcComponent instanceof TCComponentForm) {
				TCComponentForm scoreRelForm = (TCComponentForm) tcComponent;
				// �ҵ��ĸ��Ĵ�ֱ��һ���Ĵ�ֱ�
				String strategic = scoreRelForm.getProperty("u8_strategic");
				if (!StringsUtil.isEmpty(strategic)) {// �����Ϊ�� ˵����ֻ��һ��ֵ
					oneScoreFormList.add(scoreRelForm);
				}
			}
		}
		return oneScoreFormList;
	}

	/* (non-Javadoc)
	 * ��ȡ�������᰸���ϵ�µĴ�ֱ������ĸ���ֵ��ս�Է�Ϊ�գ��ı�ļ���
	 */
	@Override
	public List<TCComponentForm> getFourScoreFormList(TCComponentForm proposalForm) throws TCException {
		List<TCComponentForm> fourScoreFormList = new ArrayList<>();
		TCComponent[] scoreRelList = proposalForm.getReferenceListProperty("U8_ScoreREL");
		
		for (TCComponent tcComponent : scoreRelList) {
			if (tcComponent instanceof TCComponentForm) {
				TCComponentForm scoreRelForm = (TCComponentForm) tcComponent;
				// �ҵ��ĸ��Ĵ�ֱ��һ���Ĵ�ֱ�
				String strategic = scoreRelForm.getProperty("u8_strategic");
				if (StringsUtil.isEmpty(strategic)) {// ������ֵΪ�� ˵�������ĸ�ֵ��Ϊ��
					fourScoreFormList.add(scoreRelForm);
				}
			}
		}
		return fourScoreFormList;
	}

	/* (non-Javadoc)
	 * ����ƽ����ֵ
	 */
	@Override
	public void computeSumScore(List<TCComponentForm> oneScoreFormList, List<TCComponentForm> fourScoreFormList,
			TCComponentForm proposalForm) throws TCException {
		
		Double strategic;//"u8_strategic", strategic
		Double technicalfea;//"u8_technicalfea", technicalfea
		Double statutefea;//"u8_statutefea", statutefea
		Double market;//"u8_marketfea", market
		Double innovativeness;//"u8_innovativeness", innovativeness
		
		Double strategicSum=0d;//"u8_strategic", strategic
		Double technicalfeaSum=0d;//"u8_technicalfea", technicalfea
		Double statutefeaSum=0d;//"u8_statutefea", statutefea
		Double marketSum=0d;//"u8_marketfea", market
		Double innovativenessSum=0d;//"u8_innovativeness", innovativeness
		
		Double sum=0d;//�ܵĺ�  �����ֵ���
		
		for(TCComponentForm fourScoreForm : fourScoreFormList){//�����ĸ�ֵ�ñ�����
			technicalfea = StringsUtil.convertStr2Double(fourScoreForm.getProperty("u8_technicalfea"));
			statutefea = StringsUtil.convertStr2Double(fourScoreForm.getProperty("u8_statutefea"));
			market = StringsUtil.convertStr2Double(fourScoreForm.getProperty("u8_marketfea"));
			innovativeness = StringsUtil.convertStr2Double(fourScoreForm.getProperty("u8_innovativeness"));
			
			technicalfeaSum = technicalfeaSum+technicalfea;
			statutefeaSum = statutefeaSum+statutefea;
			marketSum = marketSum+market;
			innovativenessSum = innovativenessSum+innovativeness;
		}
		if(fourScoreFormList.size()!=0){
			int fourScoreFormSize = fourScoreFormList.size();
			technicalfeaSum = technicalfeaSum*0.2/fourScoreFormSize;
			statutefeaSum = statutefeaSum*0.15/fourScoreFormSize;
			marketSum = marketSum*0.2/fourScoreFormSize;
			innovativenessSum = innovativenessSum*0.15/fourScoreFormSize;
		}
		
		for(TCComponentForm oneScoreForm : oneScoreFormList){//����һ��ֵ�ñ�����
			strategic = StringsUtil.convertStr2Double(oneScoreForm.getProperty("u8_strategic"));
			
			strategicSum = strategicSum+strategic;
		}
		if(oneScoreFormList.size()!=0){
			int oneScoreFormSize = oneScoreFormList.size();
			strategicSum = strategicSum*0.3/oneScoreFormSize;
		}
		
		{//SUM
			sum = technicalfeaSum+statutefeaSum+marketSum+innovativenessSum+strategicSum;
			
		}
		
		{//дֵ
			String sum1 = proposalForm.getProperty("u8_sum1");
			String sum2 = proposalForm.getProperty("u8_sum2");
			String sum3 = proposalForm.getProperty("u8_sum3");
			
			if(StringsUtil.isEmpty(sum1)){
				proposalForm.setProperty("u8_sum",df.format(sum));//��ʾ���µ��ֵܷ�˵
				proposalForm.setProperty("u8_sum1",df.format(sum));
				proposalForm.setProperty("u8_sum20",df.format(strategicSum));//ʱ�̱�ʾ���µķ��� ս���ܷ�
				proposalForm.setProperty("u8_sum21",df.format(strategicSum));
				return;
			}
			
			if(StringsUtil.isEmpty(sum2)){
				proposalForm.setProperty("u8_sum",df.format(sum));
				proposalForm.setProperty("u8_sum2",df.format(sum));
				proposalForm.setProperty("u8_sum20",df.format(strategicSum));
				proposalForm.setProperty("u8_sum22",df.format(strategicSum));
				return;
			}

			if(StringsUtil.isEmpty(sum3)){
				proposalForm.setProperty("u8_sum",df.format(sum));
				proposalForm.setProperty("u8_sum3",df.format(sum));
				proposalForm.setProperty("u8_sum20",df.format(strategicSum));
				proposalForm.setProperty("u8_sum23",df.format(strategicSum));
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * ���Ƿ����δλ���������
	 */
	@Override
	public boolean canComputeSumScore(TCComponentForm proposalForm) throws TCException {
		String sum1 = proposalForm.getProperty("u8_sum1");
		String sum2 = proposalForm.getProperty("u8_sum2");
//		String sum3 = proposalForm.getProperty("u8_sum3");
		if(!StringsUtil.isEmpty(sum1)
				&&!StringsUtil.isEmpty(sum2)){
		
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * ���÷���״̬Ϊ Form
	 */
	@Override
	public void setReleaseForm(TCComponentForm proposalForm) throws TCException {
		AbstractAIFUIApplication mApp =  AIFUtility.getCurrentApplication();
		TCSession mSession = (TCSession) mApp.getSession();
		
		TCComponentTaskTemplateType taskTemplateType = (TCComponentTaskTemplateType) mSession.getTypeComponent("EPMTaskTemplate");
		taskTemplateType.extentTemplates(TCComponentTaskTemplate.PROCESS_TEMPLATE_TYPE);
		String processName=Const.MilkScore.Idea_Judg_Process;
		TCComponentTaskTemplate releaseProcess = taskTemplateType.find(processName, TCComponentTaskTemplate.PROCESS_TEMPLATE_TYPE);
		
		int types[] = { 1 };
		TCComponent components[] = {proposalForm};
		NewProcessOperation newProcessOpt = new NewProcessOperation(mSession, AIFDesktop.getActiveDesktop(), new Date() + "����", "Realeased by programm", releaseProcess, components, types);
		if(newProcessOpt!=null){
			try {
				newProcessOpt.executeOperation();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param form
	 * @return ֮�����Ƿ���״̬�Ĳ��ܹ��޸�ֵ��
	 * @throws TCException
	 */
	@Override
	public boolean proposalFormCanByPass(TCComponentForm form)throws TCException{
		TCComponent[] releaseList = form.getReferenceListProperty("release_status_list");
		boolean isCanBeUse = true;
		for(TCComponent component : releaseList){
			String name = component.toDisplayString();
			if("��ɸͨ��".equals(name)||"����".equals(name)){
				isCanBeUse = false;
			}
		}
		if (releaseList.length == 0 ) {
			return true;
		}
		return false;
	}
}
