package cn.edu.cug.cs.exam.filters;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    /**
     *
     * @param text 试卷文本
     * @param i  选项开始行
     * @param sa 提取的选项文本
     * @return 选项文本结束行
     */
    public int  choiceTexts(ArrayList<String> text, int i, ArrayList<String> sa){
        String line = text.get(i);
        //四个选项在一行
        Pattern p = patterns.get(2);
        Matcher m = p.matcher(line);
        if(m.find()){
            sa.add(m.group(1).trim());
            sa.add(m.group(2).trim());
            sa.add(m.group(3).trim());
            sa.add(m.group(4).trim());
            return i;
        }
        //两个选项一行，总共两行
        p = patterns.get(3);
        m = p.matcher(line);
        if(m.find()){
            sa.add(m.group(1).trim());
            sa.add(m.group(2).trim());
            line=text.get(i+1);
            p = patterns.get(4);
            m = p.matcher(line);
            if(m.find()){
                sa.add(m.group(1).trim());
                sa.add(m.group(2).trim());
                return i+1;
            }
        }

        //每个选项一行，共四行
        String sz="";
        while (sz.isEmpty()){
            sz=choiceAText(line).trim();
            i++;
            line = text.get(i);
        }
        sa.add(sz);

        sz="";
        while (sz.isEmpty()){
            sz=choiceBText(line).trim();
            i++;
            line = text.get(i);
        }
        sa.add(sz);

        sz="";
        while (sz.isEmpty()){
            sz=choiceCText(line).trim();
            i++;
            line = text.get(i);
        }
        sa.add(sz);

        sz="";
        while (sz.isEmpty()){
            sz=choiceDText(line).trim();
            i++;
            line = text.get(i);
        }
        sa.add(sz);

        return i-1;
    }

    /**
     *
     * @param s
     * @return
     */
    private String choiceAText(String s){
        Pattern p = patterns.get(5);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }

    /**
     *
     * @param s
     * @return
     */
    private String choiceBText(String s){
        Pattern p = patterns.get(6);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }

    /**
     *
     * @param s
     * @return
     */
    private String choiceCText(String s){
        Pattern p = patterns.get(7);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }

    /**
     *
     * @param s
     * @return
     */
    private String choiceDText(String s){
        Pattern p = patterns.get(8);
        Matcher m = p.matcher(s);
        if(m.find()){
            return m.group(1);
        }
        else
            return new String("");
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean end(String s){
        if(s.isEmpty()||s=="\r")
            return true;
        else
            return false;
    }
}
