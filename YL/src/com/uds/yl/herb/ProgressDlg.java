/**************************************************************************************************                                      
 *                                               版权归UDS所有，2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Oct-2015    ChenChun/ZhangYang               Initial
 * 02-Nov-2015    ZhangYang                 open函数中采用DISPLAY.asyncExec
 * 
 **************************************************************************************************/


package com.uds.yl.herb;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressDlg{
	public static final double WIDTH_SCREEN;
	public static final double HEIGHT_SCREEN;
	static{
		Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH_SCREEN=dimension.getWidth();
		HEIGHT_SCREEN=dimension.getHeight();
	}
	/**
	 * 判断是否是否为null或内容为空
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s){
		return s==null||s.equals("");
	}
	//获取屏幕中心点
	public static Point getScreenCenterStartPoint(double width,double height){
		int x=(int) ((WIDTH_SCREEN-width)/2.0);
		int y=(int) ((HEIGHT_SCREEN-height)/2.0);
		return new Point(x,y);
	}
	public static final Display DISPLAY=Display.getDefault();
	//用于显示MessageBox对话框
	public void showMessageBox(final String title,final String content){
		DISPLAY.asyncExec(new Runnable() {
			@Override
			public void run() {
				if(DISPLAY.getActiveShell()!=null&&!(isNullOrEmpty(content)&&isNullOrEmpty(title))){
					MessageBox messageBox=new MessageBox(DISPLAY.getActiveShell(),SWT.TITLE);
					if(title!=null)
						messageBox.setText(title);
					if(content!=null){
						messageBox.setMessage(content);
					}
					messageBox.open();
				}
			}
		});
	}
	/**
	 * 主函数测试
	 * @param args
	 */
	/*public static void main(String[] args) {
		try {
			final ProgressDlg window = new ProgressDlg();
			
			new Thread(){
				private int count;
				public void run() {
					while(count++<100){
						window.updateProgress(count, count%2==0?"进度:"+count:null);
						if(count==50)
							System.out.println(window.getTaskStatus());
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					window.setVisiblility();
					window.showMessageBox(null,"进度展示");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					window.setVisiblility();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					window.dispose();
				};
			}.start();
		System.out.println(window.getTaskStatus());
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	private int width=353;
	private int height=170;
	/**
	 * 窗体设计
	 */
	public void open(final String title) {
		DISPLAY.asyncExec(new Runnable() {
			@Override
			public void run() {
		       // shellProgress = new Shell(DISPLAY,SWT.CLOSE|SWT.ON_TOP);
				//修改设置窗口可最小化，修改时间2016-12-20
				shellProgress = new Shell(DISPLAY,SWT.MIN|SWT.ON_TOP);
				shellProgress.setSize(width,height);
		        shellProgress.setText(title);
		        shellProgress.addShellListener(new ShellAdapter() {
			        @Override
			        public void shellClosed(ShellEvent e) {
				        e.doit=false;
			        }
		        });
				//窗体居中显示
				shellProgress.setLocation(getScreenCenterStartPoint(width, height));
				try{
					shellProgress.setImage(new Image(DISPLAY,this.getClass().getClassLoader().getResourceAsStream("icons/uds.ico")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
		        

				showProgress = new ProgressBar(shellProgress, SWT.NONE);
				showProgress.setBounds(31, 58, 281, 17);

				showProgressInfo = new Label(shellProgress, SWT.NONE);
				showProgressInfo.setBounds(31, 21, 281, 17);
				
				close = new Button(shellProgress, SWT.None);
				close.setBounds(114, 91, 104, 27);
				close.setText("\u4E2D\u6B62");
				close.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent selectionevent) {
						taskStatus=TaskStatus.STOP;
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent selectionevent) {}
				});

				shellProgress.open();
				shellProgress.layout();
				while (!shellProgress.isDisposed()) {
					if (!DISPLAY.readAndDispatch()) {
						DISPLAY.sleep();
					}
				}
			}
		});
	}
	private ProgressBar showProgress;
	private Label showProgressInfo;
	private String previous="";
	private Button close;
	private Shell shellProgress;
	public static  enum TaskStatus{STOP,RUN}; 
	public TaskStatus taskStatus=TaskStatus.RUN;
	/**
	 * 更新进度条
	 * @param value 进度值(0-100)
	 * @param content 显示的内容(为null时显示上次的信息内容)
	 */
	public void updateProgress(final int value,final String content) {
		DISPLAY.asyncExec(new Runnable() {
			@Override
			public void run() {
				showProgress.setSelection(value);
				showProgressInfo.setText(content == null ? previous : content);
				previous = content;
			}
		});
	}
	/**
	 * 获取运行状态
	 * @return
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	/**
	 * 关闭对话框
	 */
	public void dispose(){
		DISPLAY.asyncExec(new Runnable() {
			@Override
			public void run() {
				shellProgress.dispose();
			}
		});
	}
	/**
	 * 设置可见性
	 */
	public void setVisiblility(){
		DISPLAY.asyncExec(new Runnable() {
			@Override
			public void run() {
				if(shellProgress!=null){
					if(shellProgress.isVisible()){
						shellProgress.setVisible(false);
						showProgress.setSelection(0);
						showProgressInfo.setText("");
					}
					else
						shellProgress.setVisible(true);
				}
			}
		});
	}
}
