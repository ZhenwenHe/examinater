package cn.edu.cug.cs.exam.filters;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.protos.QuestionType;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 试卷提取中使用的过滤器
 */
public class PaperFilter {
    private ArrayList<Pattern>  patterns = new ArrayList<>();

    private ArrayList<QuestionFilter> questionFilters=new ArrayList<>();

    private AnswerFilter  answerFilter=null;

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


    public QuestionFilter getQuestionFilter(QuestionType questionType) {
        for(QuestionFilter qf: this.questionFilters){
            switch (questionType){
                case QT_SINGLE_CHOICE:{
                    if(qf instanceof SingleChoiceQuestionFilter)
                        return qf;
                    break;
                }
                case QT_MULTI_CHOICE:{
                    if(qf instanceof MultiChoiceQuestionFilter)
                        return qf;
                    break;
                }
                case QT_TRUE_FALSE:{
                    if(qf instanceof TrueFalseQuestionFilter)
                        return qf;
                    break;
                }
                case QT_BLANK_FILLING:{
                    if(qf instanceof BlankFillingQuestionFilter)
                        return qf;
                    break;
                }
                case QT_SHORT_ANSWER:{
                    if(qf instanceof ShortAnswerQuestionFilter)
                        return qf;
                    break;
                }
                case QT_PROBLEM_SOLVING:{
                    if(qf instanceof ProblemSolvingQuestionFilter)
                        return qf;
                    break;
                }
                default:{//
                    if(qf instanceof SynthesizedQuestionFilter)
                        return qf;
                    break;
                }
            }
        }
        return null;
    }


    public QuestionFilter addQuestionFilter(String filterFile, QuestionType questionType) {
        try {
            QuestionFilter qf=null;
            switch (questionType){
                case QT_SINGLE_CHOICE:{
                    qf =new SingleChoiceQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                case QT_MULTI_CHOICE:{
                    qf= new MultiChoiceQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                case QT_TRUE_FALSE:{
                    qf= new TrueFalseQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                case QT_BLANK_FILLING:{
                    qf= new BlankFillingQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                case QT_SHORT_ANSWER:{
                    qf= new ShortAnswerQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                case QT_PROBLEM_SOLVING:{
                    qf= new ProblemSolvingQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
                default:{//
                    qf= new SynthesizedQuestionFilter(filterFile);
                    questionFilters.add(qf);
                    return qf;
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     *
     * @param filterFile
     * @throws Exception
     */
    public void setAnswerFilter(String filterFile) throws Exception{
        this.answerFilter=new AnswerFilter(filterFile);
    }

    /**
     *
     * @return
     */
    public AnswerFilter getAnswerFilter(){
        return this.answerFilter;
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
        Pattern p = Pattern.compile("#.*");
        if(p.matcher(s).find())
            return true;
        return false;
    }
}
