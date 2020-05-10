package cn.edu.cug.cs.exam.filters;

import cn.edu.cug.cs.gtl.common.Pair;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerFilter {
    private ArrayList<Pattern> patterns = new ArrayList<>();

    public AnswerFilter(String filterFile) throws Exception{
        //read filters
        String[] filters= TextExtractor.parseToStrings(filterFile);
        //
        for(String s : filters){
            if(comment(s)==false){
                Pattern p=Pattern.compile(s);
                patterns.add(p);
            }
        }
    }

    /**
     * 判断该字符串是够是试题答案的开始，也就是该行的下一行为正式的试题答案了，
     * 判断的regx字符串为"试题答案.",也即以"试题答案"开头的字符串
     * @param s 试卷答案中的一行文本
     * @return
     */
    public boolean begin(String s){
        return patterns.get(0).matcher(s).find();
    }


    /**
     * 匹配答案行成功，返回答案文本。
     * 如果答案文本字符串为空，则答案文本为多行，采用以"{"开头，以"}"结尾的多行文本构成。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String answerText(String s){
        Pattern p = patterns.get(1);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            String s2 = m.group(2);
            return s2;
        }
        return null;
    }
    /**
     * 匹配难易程度行成功，返回难易程度文本。该文本可能为空字符串。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String difficultyText(String s){
        Pattern p = patterns.get(2);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            return s1;
        }
        return null;
    }
    /**
     * 匹配答案解析行成功，返回文本。该文本可能为空字符串。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String analysisText(String s){
        Pattern p = patterns.get(3);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            return s1;
        }
        return null;
    }

    /**
     * 匹配题型行成功，返回文本。该文本可能为空字符串。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String typeText(String s){
        Pattern p = patterns.get(4);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            return s1;
        }
        return null;
    }

    /**
     * 匹配知识点行成功，返回文本。该文本可能为空字符串。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String knowledgeText(String s){
        Pattern p = patterns.get(5);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            return s1;
        }
        return null;
    }

    /**
     * 匹配所属章节行成功，返回文本。该文本可能为空字符串。
     * 如果返回字符串为null，则匹配不成功.
     * @param s
     * @return
     */
    public String chapterText(String s){
        Pattern p = patterns.get(6);
        Matcher m = p.matcher(s);
        if(m.find()){
            String s1 = m.group(1);
            return s1;
        }
        return null;
    }
    /**
     * 处理试卷答案过滤文件中的注释行，以#为注释行的开始
     * @param s
     * @return
     */
    private boolean comment(String s){
        if(s.isEmpty())
            return false;
        Pattern p = Pattern.compile("#.*");
        if(p.matcher(s).find())
            return true;
        return false;
    }
}
