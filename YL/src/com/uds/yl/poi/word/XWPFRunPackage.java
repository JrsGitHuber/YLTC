package com.uds.yl.poi.word;

import org.apache.poi.xwpf.usermodel.*;

/**
 * Created by GLF on 2017/8/24. Talk is Cheap,Show me the Code!
 */
public class XWPFRunPackage {

    private XWPFRun run;

    public XWPFRunPackage(XWPFRun run){
        this.run = run;
    }

    //璁剧疆绮椾綋
    public XWPFRunPackage setBold(boolean value){
        this.run.setBold(value);
        return this;
    }

    //璁剧疆鍒犻櫎绾�
    public XWPFRunPackage setStrike(boolean value){
        this.run.setStrike(value);
        return this;
    }

    //璁剧疆鏂滀綋
    public XWPFRunPackage setItalic(boolean value){
        this.run.setItalic(value);
        return this;
    }

    //璁剧疆瀛椾綋鐨勫ぇ灏�
    public XWPFRunPackage setFontSize(int size){
        this.run.setFontSize(size);
        return this;
    }

    //璁剧疆鏂囨湰鍐呭
    public XWPFRunPackage setText(String text){
        this.run.setText(text);
        return this;
    }

    //璁剧疆涓嬪垝绾� single 鍗曚笅鍒掔嚎
    public XWPFRunPackage setUnderline(UnderlinePatterns value){
        this.run.setUnderline(value);
        return this;
    }

    //鎹㈣
    public XWPFRunPackage addBreak(){
        this.run.addBreak();
        return this;
    }

}
