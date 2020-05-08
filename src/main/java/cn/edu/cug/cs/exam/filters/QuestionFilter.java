package cn.edu.cug.cs.exam.filters;

import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class QuestionFilter {
    protected ArrayList<Pattern> patterns = new ArrayList<>();

    public QuestionFilter(String filterFile) throws Exception{
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
     * 判断该字符串是够是一种类型试题的开始，也就是改行的下一行为正式的试题了
     * @param s 试卷中的一行文本
     * @return
     */
    public boolean begin(String s){
        return patterns.get(0).matcher(s).find();
    }

    /**
     * 处理过滤文件中的注释行，以#为注释行的开始
     * @param s
     * @return
     */
    protected boolean comment(String s){
        if(s.isEmpty())
            return false;
        Pattern p = Pattern.compile("#.*");
        if(p.matcher(s).find())
            return true;
        return false;
    }
}
