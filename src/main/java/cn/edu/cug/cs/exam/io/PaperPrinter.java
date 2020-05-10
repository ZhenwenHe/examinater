package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.gtl.protos.QuestionGroup;
import cn.edu.cug.cs.gtl.protos.Paper;

public class PaperPrinter {
    public static String toString(Paper paper) {
        StringBuilder sb = new StringBuilder();
        int i=0;
        String sz=null;
        for(QuestionGroup g: paper.getQuestionGroupList()){
            //g.(indexToOrder(i));
            //sb.append(g.toString());
            if(g.getOrder()==null){
                //添加题号
                sz=QuestionGroupPrinter.toString(g);
                sz = indexToOrder(i)+"、"+sz;
            }
            else{
                //添加题号
                if(g.getOrder().isEmpty()){
                    sz=QuestionGroupPrinter.toString(g);
                    sz = indexToOrder(i)+"、"+sz;
                }
                else{
                    //替换题号，这种方法代价挺大的，需要优化
                    sz=QuestionGroupPrinter.toString(g.toBuilder().setOrder(indexToOrder(i)).build());
                }
            }
            sb.append(sz);
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
