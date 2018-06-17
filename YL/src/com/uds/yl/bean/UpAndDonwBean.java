package com.uds.yl.bean;


import com.uds.yl.utils.StringsUtil;

public class UpAndDonwBean{
	public String down="";// 下限
	public String up="";// 上限
	public String downSymbol="";// 下限符号
	public String upSymbol="";// 上限符号
	public String detectValue="";//检测值描述
	
	public String resultStr="";//由于上下限拆分而来
	
	public void initUpAndDown(String resultStr){
		this.resultStr = resultStr;
		
		if(StringsUtil.isEmpty(resultStr) || !resultStr.contains("x")){//如果为空
			detectValue = resultStr;
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		
		if (!StringsUtil.isEmpty(resultStr)) {// 不为空
			String[] result = resultStr.split("x");
			
			if('x' == resultStr.charAt(0)){//x<4或者 x>4这种形式
				if(result[1].contains("≤")){//上限 <=
					up = result[1].split("≤")[1];
					upSymbol = "<=";
				}else if(result[1].contains("＜")){//上限 <
					up = result[1].split("＜")[1];
					upSymbol = "<";
				}else if(result[1].contains("≥")){//下限 >=
					down = result[1].split("≥")[1];
					downSymbol = ">=";
				}else if(result[1].contains("＞")){//下限 >
					down = result[1].split("＞")[1];
					downSymbol = ">";
				}
			}else{ // 3<x<5 这种形式
				//下限
				if(result[0].contains("≤")){
					down = result[0].split("≤")[0];//数字
					downSymbol = ">=";
				}else if(result[0].contains("＜")){
					down = result[0].split("＜")[0];//数字
					downSymbol = ">";
				}
				
				//上限
				if(result.length==2){
					if(result[1].contains("≤")){
						up = result[1].split("≤")[1];//数字
						upSymbol = "<=";
					}else if(result[1].contains("＜")) {
						up = result[1].split("＜")[1];//数字
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
		
		
		if (StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// up和down都为空则
			resultStr = detectValue;
		}
		if (!StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// up和down都有值
			downSymbol = ">=".equals(downSymbol) ? "≤" : "＜";
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			resultStr = down+downSymbol+"x" + upSymbol + up;// 这里一定是大于等于小于等于
		}
		if (!StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// 只有up有值
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			resultStr = "x"+upSymbol + up;
		}
		if (StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// 只有down有值
			downSymbol = ">=".equals(downSymbol) ? "≥" : "＞";
			resultStr = "x" + downSymbol + down;
		}
		
	}
}