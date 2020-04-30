package cn.edu.cug.cs.exam;

import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class PaperExtractor {
    /**
     * 从试卷中提取文本信息，该文本每行一个字符串，并去除了题目无关信息。
     * @param fileDocument
     * @param filter
     * @return
     * @throws Exception
     */
    public static ArrayList<String> parseText(String fileDocument, PaperFilter filter) throws Exception{
        String[] paragraphs = TextExtractor.parseToStrings(fileDocument);
        ArrayList<String> ss= new ArrayList<>();
        //查找试题开始的前一行，此行向上的部分全部剔除
        int i =0;
        for(i=0;i<paragraphs.length;++i){
            if(filter.begin(paragraphs[i]))
                break;
        }
        //剔除模板文本
        for(int j=i+1;j<paragraphs.length;++j){
            if(filter.eliminate(paragraphs[j])==false)
                ss.add(paragraphs[j]);
        }
        //剔除连续空行
        int c = ss.size();
        Pattern p = Pattern.compile("^\\s+");
        for(int j=0;j<c;++j){
            while(j+1<c && p.matcher(ss.get(j)).find() && p.matcher(ss.get(j+1)).find()){
                ss.remove(j+1);
                c--;
            }
        }
        return ss;
    }

    /**
     * 从试卷文本中解析题目，并返回。
     * @param text
     * @return
     */
    public static ArrayList<Question> parseQuestions(ArrayList<String> text){
        ArrayList<Question> qa = new ArrayList<>();
        ArrayList<Question> qb= null;
        qb =parseSingleChoiceQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseMultiChoiceQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseBlankFillingQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseShortAnswerQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseProblemSolvingQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseSynthesizedQuestions(text);
        if(qb!=null) qa.addAll(qb);
        qb= parseTrueFalseQuestions(text);
        if(qb!=null) qa.addAll(qb);
        return qa;
    }

    /**
     * 解析单项选择题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseSingleChoiceQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析多项选择题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseMultiChoiceQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析判断题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseTrueFalseQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析填空题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseBlankFillingQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析简答题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseShortAnswerQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析应用题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseProblemSolvingQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析综合题
     * @param text
     * @return
     */
    private static ArrayList<Question> parseSynthesizedQuestions(ArrayList<String> text){
        return null;
    }



}
