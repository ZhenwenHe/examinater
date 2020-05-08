package cn.edu.cug.cs.exam;

import cn.edu.cug.cs.exam.questions.QuestionGroup;
import cn.edu.cug.cs.gtl.protos.Identifier;

import java.util.ArrayList;
import java.util.Date;

/**
 * 试卷基类
 */
public class Paper {
    private Identifier identifier;//试卷库中的试卷唯一编号
    private String name;//试卷名称
    private Date   date;//试卷时间
    private String file;//试卷文件名，可以是DOC或PDF文件

    ArrayList<QuestionGroup> questionGroups=new ArrayList<>();//一张试卷由一个或多个试题组构成。

    public ArrayList<QuestionGroup> getQuestionGroups() {
        return questionGroups;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(QuestionGroup g: this.questionGroups){
            g.setOrder(indexToOrder(i));
            sb.append(g.toString());
            i++;
        }
        return sb.toString();
    }

    public static String indexToOrder(int i){
        switch (i){
            case 0: return "一";
            case 1: return "二";
            case 2: return "三";
            case 3: return "四";
            case 4: return "五";
            case 5: return "六";
            case 6: return "七";
            case 7: return "八";
            case 8: return "九";
            case 9: return "十";
            case 10: return "十一";
            case 11: return "十二";
            case 12: return "十三";
            case 13: return "十四";
            case 14: return "十五";
            case 15: return "十六";
            case 16: return "十七";
            case 17: return "十八";
            case 18: return "十九";
            case 19: return "二十";
            case 20: return "二十一";
        }
        return "";
    }
}
