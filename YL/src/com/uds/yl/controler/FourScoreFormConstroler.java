package com.uds.yl.controler;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.service.impl.CreativeManagementServiceImpl;
import com.uds.yl.tcutils.FormUtil;
import com.uds.yl.ui.FourScoreFrame;
import com.uds.yl.utils.StringsUtil;
//创意评分四属性
public class FourScoreFormConstroler implements BaseControler {
	private ICreativeManagementService iCreativeManagementService = new CreativeManagementServiceImpl();
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		// TODO Auto-generated method stub

	}

	public void userTask(final TCComponentForm proposalForm, TCSession session) throws TCException {
		boolean hasMatchForm = false;
		
		boolean proposalFormIsInRule = iCreativeManagementService.proposalFormIsInRule(proposalForm);
		if (proposalFormIsInRule) {// 没发布 
																// 并且
																// status是通过
			// 找到所有者为当前用户的打分表 并且未发布
			String currentUserName = session.getUser().getUserId();
			TCComponent[] scoreRelList = proposalForm.getReferenceListProperty("U8_ScoreREL");
			AbstractCallBack callBack = new AbstractCallBack() {
				@Override
				
				public void setUserIdInProposalForm(String userID) {
					super.setUserIdInProposalForm(userID);
					try {
						String desc = proposalForm.getProperty("object_desc");
						if(StringsUtil.isEmpty(desc)){//如果为空 直接写
//							proposalForm.setProperty("object_desc", userID);
						}else{
//							proposalForm.setProperty("object_desc", desc+"#"+userID);
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
				}
			};
			for (TCComponent tcComponent : scoreRelList) {
				if (tcComponent instanceof TCComponentForm) {
					
					TCComponentForm scoreRelForm = (TCComponentForm) tcComponent;
					TCComponentUser user = (TCComponentUser) scoreRelForm.getReferenceProperty("owning_user");
					String owningUserName = user.getUserId();
					
					TCComponent[] referenceListProperty = tcComponent.getReferenceListProperty("release_status_list");
					if (currentUserName.equals(owningUserName) && referenceListProperty.length == 0) {
						hasMatchForm = true;
						// 当前form符合标准 准备填写值
						FourScoreFrame frame = new FourScoreFrame(scoreRelForm,1,callBack);
						frame.setVisible(true);
					}
				}

			}
			// 没有的话就使用当前账户创建一个
			if (!hasMatchForm) {
				// 准备填写值
				FourScoreFrame frame = new FourScoreFrame(proposalForm,0,callBack);
				frame.setVisible(true);
			}
		}else{
			MessageBox.post("没通过或者已发布","",MessageBox.INFORMATION);
		}
	}
}
