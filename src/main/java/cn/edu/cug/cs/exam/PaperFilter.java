package cn.edu.cug.cs.exam;

import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class PaperFilter {
    private ArrayList<Pattern>  patterns = new ArrayList<>();

    public PaperFilter(String filterFile) throws Exception{
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
     * 判断该字符串是够是试题的开始，也就是改行的下一行为正式的试题了，
     * 判断的regx字符串为"考试内容.",也即以"考试内容"开头的字符串
     * @param s 试卷中的一行文本
     * @return 试卷内天时候开始出现
     */
    public boolean begin(String s){
        return patterns.get(0).matcher(s).find();
    }

    /**
     * 判断字符串是否为需要剔除的试卷模板信息
     * @param s 试卷中的一行文本
     * @return 判断字符串是否为需要剔除的试卷模板信息，是的话返回true,否则返回false
     */
    public boolean eliminate(String s){
        for(int i=1;i<patterns.size();++i){
            if(patterns.get(i).matcher(s).find())
                return true;
        }
        return false;
    }

    /**
     * 处理试卷模板过滤文件中的注释行，以#为注释行的开始
     * @param s
     * @return
     */
    private boolean comment(String s){
        if(s.isEmpty())
            return false;
        Pattern p = Pattern.compile("#.");
        if(p.matcher(s).find())
            return true;
        return false;
    }
}
