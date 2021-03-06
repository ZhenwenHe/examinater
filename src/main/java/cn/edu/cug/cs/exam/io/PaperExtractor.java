package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.protos.Question;
import cn.edu.cug.cs.gtl.protos.QuestionGroup;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.common.Pair;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static cn.edu.cug.cs.gtl.protos.QuestionType.*;

/**
 *
 */
public class PaperExtractor {

    PaperFilter filter=null;

    /**
     * 构造试卷提取器
     * @param filter 试卷过滤器，可以不包含答案过滤器
     */
    public PaperExtractor(PaperFilter filter) {
        this.filter = filter;
    }

    /**
     * 获取试卷过滤器
     * @return
     */
    public PaperFilter getFilter() {
        return filter;
    }


    /**
     * 从试卷文本中解析题目，并组成试卷返回。
     * @param fileDocument
     * @return
     */
    public Paper parsePaper(String fileDocument) throws Exception{
        ArrayList<String> text = parseText(fileDocument);
        return parsePaper(text);
    }

    /**
     * 从试卷文本中解析题目，并返回。
     * @param text
     * @return
     */
    public Paper parsePaper(ArrayList<String> text){
        Paper.Builder qa = Paper.newBuilder();
        ArrayList<QuestionGroup> a = parseQuestions(text);
        return qa.addAllQuestionGroup(a).build();
    }
    /**
     * 从试卷中提取文本信息，该文本每行一个字符串，并去除了题目无关信息。
     * @param fileDocument
     * @return
     * @throws Exception
     */
    public ArrayList<String> parseText(String fileDocument) throws Exception{
        return parseText(fileDocument,this.filter);
    }

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
        //剔除连续空格行
        int c = ss.size();
        Pattern p = Pattern.compile("^\\s+");
        for(int j=0;j<c;++j){
            while(j+1<c && p.matcher(ss.get(j)).find() && p.matcher(ss.get(j+1)).find()){
                ss.remove(j+1);
                c--;
            }
        }
        //删除空行
        ss=eraseEmptyLine(ss);
        return ss;
    }

    /**
     * 从试卷文本中解析题目，并返回。
     * @param text
     * @return
     */
    public ArrayList<QuestionGroup> parseQuestions(ArrayList<String> text){
        ArrayList<QuestionGroup> qa = new ArrayList<>();
        QuestionGroup qb= null;
        Pair<Integer,Integer> range=new Pair<>();
        qb =parseSingleChoiceQuestions(text,range);
        if(qb!=null) {
            text=erase(text,range.first(),range.second());
            if(qb.getQuestionCount()>0) qa.add(qb);
        }
        qb= parseMultiChoiceQuestions(text,range);
        if(qb!=null) {
            text=erase(text,range.first(),range.second());
            if(qb.getQuestionCount()>0) qa.add(qb);
        }
        qb= parseBlankFillingQuestions(text,range);
        if(qb!=null) {
            text=erase(text,range.first(),range.second());
            if(qb.getQuestionCount()>0) qa.add(qb);
        }
        qb= parseTrueFalseQuestions(text,range);
        if(qb!=null) {
            text=erase(text,range.first(),range.second());
            if(qb.getQuestionCount()>0) qa.add(qb);
        }

        //可能会出现多道一级简答大题
        do {
            qb= parseShortAnswerQuestions(text,range);
            text=erase(text,range.first(),range.second());
            if(qb!=null&& qb.getQuestionCount()>0) qa.add(qb);
        }while (qb!=null && qb.getQuestionCount()>0) ;

        //可能会出现多道一级应用大题
        do{
            qb= parseProblemSolvingQuestions(text,range);
            text=erase(text,range.first(),range.second());
            if(qb!=null&& qb.getQuestionCount()>0) qa.add(qb);
        }while (qb!=null && qb.getQuestionCount()>0);
        //可能会出现多道一级综合题
        do{
            qb= parseSynthesizedQuestions(text,range);
            text=erase(text,range.first(),range.second());
            if(qb!=null&& qb.getQuestionCount()>0) qa.add(qb);
        }while (qb!=null && qb.getQuestionCount()>0);

        return qa;
    }

    /**
     * 解析单项选择题
     * 单选题的开始标志为：
     * 一、选择题（共10小题，每小题3分，共30分）。
     * 1、SQL语言通常称为()。
     * A.结构化查询语言           B.结构化控制语言
     * C.结构化定义语言           D.结构化操纵语言
     *
     * 2、SQL语言通常称为()。
     * A.结构化查询语言
     * B.结构化控制语言
     * C.结构化定义语言
     * D.结构化操纵语言
     *
     * 其中，"、"与"."可以互换使用。
     *
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseSingleChoiceQuestions(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();//(QT_SINGLE_CHOICE);
        qa.setQuestionType(QT_SINGLE_CHOICE);
        SingleChoiceQuestionFilter f =(SingleChoiceQuestionFilter) filter.getQuestionFilter(QT_SINGLE_CHOICE);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取选择题大题题干信息，获取选择题的分值，总分值和题目个数
        int i=0;
        int start, end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start=i;
        //开始提取每个选择题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            String qt = f.questionText(line);
            if(qt.isEmpty()==false){
                ArrayList<String> als = new ArrayList<>();
                qt = qt.trim();
                als.add(qt);
                i=f.choiceTexts(text,i+1,als);
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_SINGLE_CHOICE)
                        .setScore(f.getScorePreQuestion())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );

                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }

    /**
     * 解析多项选择题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseMultiChoiceQuestions(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_MULTI_CHOICE);
        MultiChoiceQuestionFilter f =(MultiChoiceQuestionFilter) filter.getQuestionFilter(QT_MULTI_CHOICE);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取选择题大题题干信息，获取选择题的分值，总分值和题目个数
        int i=0;
        int start, end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start=i;
        //开始提取每个选择题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            String qt = f.questionText(line);
            if(qt.isEmpty()==false){
                ArrayList<String> als = new ArrayList<>();
                qt = qt.trim();
                als.add(qt);
                i=f.choiceTexts(text,i+1,als);
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_MULTI_CHOICE)
                        .setScore(f.getScorePreQuestion())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );

                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }

    /**
     * 解析判断题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseTrueFalseQuestions(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_TRUE_FALSE);
        TrueFalseQuestionFilter f =(TrueFalseQuestionFilter) filter.getQuestionFilter(QT_TRUE_FALSE);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取简答题大题题干信息，获取简答题的分值，总分值和题目个数
        int i=0;
        int start,end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start =i;
        //开始提取每个填空题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            String qt = f.questionText(line);
            if(qt.isEmpty()==false){
                ArrayList<String> als = new ArrayList<>();
                qt = qt.trim();
                als.add(qt);
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_TRUE_FALSE)
                        .setScore(f.getScorePreQuestion())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );
                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());

        return qa.build();
    }

    /**
     * 解析填空题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseBlankFillingQuestions(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_BLANK_FILLING);
        BlankFillingQuestionFilter f =(BlankFillingQuestionFilter) filter.getQuestionFilter(QT_BLANK_FILLING);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取简答题大题题干信息，获取简答题的分值，总分值和题目个数
        int i=0;
        int start,end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start =i;
        //开始提取每个填空题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            String qt = f.questionText(line);
            if(qt.isEmpty()==false){
                ArrayList<String> als = new ArrayList<>();
                qt = qt.trim();
                als.add(qt);
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_BLANK_FILLING)
                        .setScore(f.getScorePreQuestion())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );
                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());

        return qa.build();
    }

    /**
     * 解析简答题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseShortAnswerQuestions(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SHORT_ANSWER);
        ShortAnswerQuestionFilter f =(ShortAnswerQuestionFilter) filter.getQuestionFilter(QT_SHORT_ANSWER);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取简答题大题题干信息，获取简答题的分值，总分值和题目个数
        int i=0;
        int start,end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start =i;
        //开始提取每个简答题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            String qt = f.questionText(line);
            if(qt.isEmpty()==false){
                ArrayList<String> als = new ArrayList<>();
                qt = qt.trim();
                als.add(qt);
//                ShortAnswerQuestion q = new ShortAnswerQuestion(als,"");
//                q.setScore(f.getScorePreQuestion());
//                qa.add(q);
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_SHORT_ANSWER)
                        .setScore(f.getScorePreQuestion())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );
                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());

        return qa.build();
    }

    /**
     * 解析应用题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseProblemSolvingQuestions(ArrayList<String> text,Pair<Integer,Integer> range ){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_PROBLEM_SOLVING);
        ProblemSolvingQuestionFilter f =(ProblemSolvingQuestionFilter) filter.getQuestionFilter(QT_PROBLEM_SOLVING);
        if(f==null){
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取应用题大题题干信息，获取总分值和题目个数
        int i=0;
        int start,end=-1;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start=i;
        //开始提取每个应用题
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取题干信息
            Pair<String,Double> qtp = f.questionText(line);
            String qt = qtp.first();
            if(qt.isEmpty()==false){
                qt = qt.trim();
                ArrayList<String> als = new ArrayList<>();
                ArrayList<Double> detailedScores=new ArrayList<>();
                als.add(qt);
                int c=1;
                while(c>0&&s-1>i){
                    ++i;
                    line = text.get(i);
                    c = f.questionTexts(line,als,detailedScores);
                    if(c==0 && !line.isEmpty()) i--;
                }
                qa.addQuestion(Question
                        .newBuilder()
                        .setQuestionType(QT_PROBLEM_SOLVING)
                        .setScore(qtp.second().doubleValue())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .addAllDetailedScore(detailedScores)
                        .build()
                );

                j++;
            }
        }
        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }
        qa.setScorePreQuestion(0);
        qa.setTotalScore(f.getTotalScore());

        return qa.build();

    }

    /**
     * 解析综合题
     * @param text
     * @param range 返回对应题型开始行和结束行[start,end)
     * @return
     */
    private QuestionGroup parseSynthesizedQuestions(ArrayList<String> text, Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SYNTHESIZED);
        ArrayList<String> als_text = new ArrayList<>();
        ArrayList<Question> alq = new ArrayList<>();
        SynthesizedQuestionFilter f =(SynthesizedQuestionFilter) filter.getQuestionFilter(QT_SYNTHESIZED);
        if(f==null){
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取大题题干信息，获取总分值和二级题目个数
        int i=0;
        int start ,end;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                break;
            }
        }
        start =i;
        end=-1;
        if(i<s-1){
            i++;
            line = text.get(i);
            line = line.trim();
            if(line.isEmpty()) {
                end =Math.min(i+1,text.size());
                if(range!=null) {
                    range.setKey(Integer.valueOf(start));
                    range.setValue(Integer.valueOf(end));
                }
                return qa.build();
            }
            als_text.add(line);
        }
        else {
            end =Math.min(i+1,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }

        //开始提取二级小题，每个二级小题为一个应用题的格式
        int j=0;
        while(i<s && j<f.getQuestionCount()){
            i++;
            line = text.get(i);
            //获取二级题干信息
            Pair<String,Double> qtp = f.questionText(line);
            String qt = qtp.first();
            if(qt.isEmpty()==false){
                qt = qt.trim();
                ArrayList<String> als = new ArrayList<>();
                als.add(qt);
                int c=1;
                while(c>0&&s-1>i){
                    ++i;
                    line = text.get(i);
                    c = f.questionTexts(line,als);
                    if(!line.isEmpty()&&c==0)
                        i--;
                }

                alq.add(Question.newBuilder()
                        .setQuestionType(QT_PROBLEM_SOLVING)
                        .setScore(qtp.second().doubleValue())
                        .setAnswerText("")
                        .addAllQuestionText(als)
                        .build()
                );
                j++;
            }
        }

        if(alq.size()>0 && als_text.size()>0){
            qa.addQuestion(Question
                    .newBuilder()
                    .setQuestionType(QT_SYNTHESIZED)
                    .setScore(f.getTotalScore())
                    .setAnswerText("")
                    .addAllQuestionText(als_text)
                    .addAllSubQuestion(alq)
                    .build()
            );
        }


        qa.setScorePreQuestion(0);
        qa.setTotalScore(f.getTotalScore());

        end =Math.min(i+1,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 删除数组中的指定元素，并返回新数组
     * @param text
     * @param start
     * @param end
     * @return
     */
    private static ArrayList<String> erase(ArrayList<String> text, int start, int end){
        int c = end-start;
        int s = text.size();
        if(c>0){
            int i=0;
            while (i<c && start<s){
                text.remove(start);
                s = text.size();
                ++i;
            }
        }
        return text;
    }

    private static ArrayList<String> eraseEmptyLine(ArrayList<String> text){
        int s = text.size();
        int i=0;
        Pattern p = Pattern.compile("^\\s+\\r");
        while (i<s){
            if(text.get(i).isEmpty()||text.get(i).equals("\r")||text.get(i).equals("\n"))
                text.remove(i);
            else if(p.matcher(text.get(i)).find()) {
                text.remove(i);
            }
            else {
                i++;
            }
            s = text.size();
        }
        return text;
    }
}
