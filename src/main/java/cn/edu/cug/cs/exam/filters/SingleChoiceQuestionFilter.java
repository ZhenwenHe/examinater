package cn.edu.cug.cs.exam.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleChoiceQuestionFilter extends QuestionFilter{
    double scorePreQuestion;
    int questionCount;
    double totalScore;

    public double getScorePreQuestion() {
        return scorePreQuestion;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public SingleChoiceQuestionFilter(String filterFile) throws Exception {
        super(filterFile);
    }

    /**
     * 判断该字符串是够是选择题的开始，也就是改行的下一行为正式的选择试题了
     * @param s 试卷中的一行文本
     * @return
     */
    public boolean begin(String s){
        Pattern p = patterns.get(0);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            String s2 = m.group(2);
            String s3 = m.group(3);
            this.questionCount=Integer.parseInt(s1);
            this.scorePreQuestion=Double.parseDouble(s2);
            this.totalScore=Double.parseDouble(s3);
            return true;
        }
        return false;
    }

    /**
     * 提取选择题的题目文本
     * @param s
     * @return
     */
    public String questionText(String s){
        Pattern p = patterns.get(1);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }
    public String choiceAText(String s){
        Pattern p = patterns.get(2);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }
    public String choiceBText(String s){
        Pattern p = patterns.get(3);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }
    public String choiceCText(String s){
        Pattern p = patterns.get(4);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }
    public String choiceDText(String s){
        Pattern p = patterns.get(5);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }
    public boolean end(String s){
        if(s.isEmpty()||s=="\r")
            return true;
        else
            return false;
    }
}
