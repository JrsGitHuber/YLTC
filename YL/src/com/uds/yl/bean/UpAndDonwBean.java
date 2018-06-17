package com.uds.yl.bean;


import com.uds.yl.utils.StringsUtil;

public class UpAndDonwBean{
	public String down="";// ����
	public String up="";// ����
	public String downSymbol="";// ���޷���
	public String upSymbol="";// ���޷���
	public String detectValue="";//���ֵ����
	
	public String resultStr="";//���������޲�ֶ���
	
	public void initUpAndDown(String resultStr){
		this.resultStr = resultStr;
		
		if(StringsUtil.isEmpty(resultStr) || !resultStr.contains("x")){//���Ϊ��
			detectValue = resultStr;
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		
		if (!StringsUtil.isEmpty(resultStr)) {// ��Ϊ��
			String[] result = resultStr.split("x");
			
			if('x' == resultStr.charAt(0)){//x<4���� x>4������ʽ
				if(result[1].contains("��")){//���� <=
					up = result[1].split("��")[1];
					upSymbol = "<=";
				}else if(result[1].contains("��")){//���� <
					up = result[1].split("��")[1];
					upSymbol = "<";
				}else if(result[1].contains("��")){//���� >=
					down = result[1].split("��")[1];
					downSymbol = ">=";
				}else if(result[1].contains("��")){//���� >
					down = result[1].split("��")[1];
					downSymbol = ">";
				}
			}else{ // 3<x<5 ������ʽ
				//����
				if(result[0].contains("��")){
					down = result[0].split("��")[0];//����
					downSymbol = ">=";
				}else if(result[0].contains("��")){
					down = result[0].split("��")[0];//����
					downSymbol = ">";
				}
				
				//����
				if(result.length==2){
					if(result[1].contains("��")){
						up = result[1].split("��")[1];//����
						upSymbol = "<=";
					}else if(result[1].contains("��")) {
						up = result[1].split("��")[1];//����
						upSymbol = "<";
					}
				}
			}

		}else if(resultStr.equals("")||resultStr==null){
			detectValue = "";
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
		}
	}
	
	public void initResult(String up, String down, String upSymbol, String downSymbol,
			String detectValue){
		this.up = up;
		this.down = down;
		this.upSymbol = upSymbol;
		this.downSymbol = downSymbol;
		this.detectValue = detectValue;
		
		
		if (StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// up��down��Ϊ����
			resultStr = detectValue;
		}
		if (!StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// up��down����ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			resultStr = down+downSymbol+"x" + upSymbol + up;// ����һ���Ǵ��ڵ���С�ڵ���
		}
		if (!StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// ֻ��up��ֵ
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			resultStr = "x"+upSymbol + up;
		}
		if (StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// ֻ��down��ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			resultStr = "x" + downSymbol + down;
		}
		
	}
}