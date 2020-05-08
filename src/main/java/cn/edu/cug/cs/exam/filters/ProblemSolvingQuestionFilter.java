package cn.edu.cug.cs.exam.filters;

import cn.edu.cug.cs.gtl.common.Pair;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemSolvingQuestionFilter extends QuestionFilter{
    int questionCount;
    double totalScore;

    public int getQuestionCount() {
        return questionCount;
    }

    public double getTotalScore() {
        return totalScore;
    }

    /**
     * 判断该字符串是应用题的开始，也就是改行的下一行为正式的试题了
     * @param s 试卷中的一行文本
     * @return
     */
    public boolean begin(String s){
        Pattern p = patterns.get(0);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            String s2 = m.group(2);
            this.questionCount=Integer.parseInt(s1);
            this.totalScore=Double.parseDouble(s2);
            return true;
        }
        return false;
    }

    /**
     * 提取二级题目文本，以1.开头的文本
     * @param s
     * @return
     */
    public Pair<String,Double> questionText(String s){
        Pattern p = patterns.get(1);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1= m.group(1);
            String s2=m.group(2);
            Pair<String,Double> r= Pair.create(s1,Double.parseDouble(s2));
            return r;
        }
        else {
            return Pair.create("",0.0);
        }
    }

    /**
     * 提取三级小题的问题文本，以1），2）等开头的文本
     * @param s
     * @param als，存放提取的文本，每个字符串为一个小题文本
     * @return
     */
    public int questionTexts(String s, ArrayList<String> als,ArrayList<Double> scores){
        Pattern p = patterns.get(2);
        Matcher m = p.matcher(s);
        int c = als.size();
        while(m.find()){
            als.add(m.group(1));
            scores.add(Double.valueOf(m.group(2)));
        }
        return als.size()-c;
    }


    public ProblemSolvingQuestionFilter(String filterFile) throws Exception {
        super(filterFile);
    }
}
