package com.yl.handlers;



import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.fms.util.formdata.FileUploadMultipartStream;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.controler.CommandOperationManager;
import com.uds.yl.utils.FileUtils;




/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 用于导出的Jar包是否包含新的修改
		System.out.println("----------------------------------");
		System.out.println("2018.05.07 15:22");
		
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			
			TCSession session = (TCSession) app.getSession();
			
			// 获取当前窗口位置信息
			Shell tcShell = Display.getCurrent().getActiveShell();
			Rectangle rectangle = tcShell.getBounds();
			java.awt.Rectangle rectangleObj = new java.awt.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			
			
			//创建temp目录
			FileUtils.createFolder("c:"+File.separator+"temp");
			
			CommandOperationManager opr = new CommandOperationManager(rectangleObj);
			String command = event.getCommand().getId();
			opr.m_commandId = command;
			session.queueOperation(opr);
			
			return null;
	}
}
