package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.common.Pair;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.Question;
import cn.edu.cug.cs.gtl.protos.QuestionGroup;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static cn.edu.cug.cs.gtl.protos.QuestionType.*;

/**
 *
 */
public class AnswerExtractor {

    PaperFilter filter=null;

    /**
     * 构造答案提取器
     * @param filter 试卷过滤器，必须包含有答案过滤器
     */
    public AnswerExtractor(PaperFilter filter) {
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
     *为已经存在的试卷匹配答案
     * @param paper 已经存在的试卷
     * @param answerFile 改试卷匹配的答案文件，模板见answer_template.doc
     * @return 返回一个新的完整的试卷
     * @throws Exception
     */
    public  Paper parseAnswers(Paper paper, String answerFile)throws Exception{
        ArrayList<String> text = parseText(answerFile);
        Paper.Builder builder = Paper.newBuilder();
        Pair<Integer,Integer> range = new Pair<>();
        QuestionGroup qg = parseSingleChoiceAnswers(text,range);
        if(qg!=null) {
            text = erase(text,range.first(),range.second());
            if(qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }
        qg = parseMultiChoiceAnswers(text,range);
        if(qg!=null) {
            text = erase(text,range.first(),range.second());
            if(qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }
        qg = parseBlankFillingAnswers(text,range);
        if(qg!=null) {
            text = erase(text,range.first(),range.second());
            if(qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }
        qg = parseTrueFalseAnswers(text,range);
        if(qg!=null) {
            text = erase(text,range.first(),range.second());
            if(qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }

        do {
            qg = parseShortAnswerAnswers(text, range);
            text = erase(text, range.first(), range.second());
            if (qg != null&& qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }while (qg != null && qg.getQuestionCount()>0);

        do {
            qg = parseProblemSolvingAnswers(text, range);
            text=erase(text,range.first(),range.second());
            if (qg != null&& qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        } while (qg != null && qg.getQuestionCount()>0);

        do {
            qg = parseSynthesizedAnswers(text, range);
            text=erase(text,range.first(),range.second());
            if (qg != null&& qg.getQuestionCount()>0) builder.addQuestionGroup(qg);
        }while (qg != null && qg.getQuestionCount()>0);

        Paper answer= builder.build();
        if(paper!=null){
            return mergePaper(paper,answer);
        }
        else
            return answer;
    }

    /**
     * 解析选择题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private  QuestionGroup parseSingleChoiceAnswers(ArrayList<String> text, Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();//(QT_SINGLE_CHOICE);
        qa.setQuestionType(QT_SINGLE_CHOICE);
        //处理一级大题的题干信息
        SingleChoiceQuestionFilter f =(SingleChoiceQuestionFilter) filter.getQuestionFilter(QT_SINGLE_CHOICE);
        if(f==null)  {
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
                qa.setScorePreQuestion(f.getScorePreQuestion());
                qa.setTotalScore(f.getTotalScore());
                break;
            }
        }
        start=i;

        if(i==s) {
            start =i;
            end =Math.min(i+1,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }
        //开始提取每个选择题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            qa.addQuestion(Question
                    .newBuilder()
                    .setQuestionType(QT_SINGLE_CHOICE)
                    .setScore(f.getScorePreQuestion())
                    .setAnswerText(sa.get(0))
                    .setDifficulty(sa.get(1).isEmpty()?5.0:Double.valueOf(sa.get(1)))
                    .setAnalysis(sa.get(2))
                    .setQuestionTypeName(sa.get(3))
                    .setKnowledge(sa.get(4))
                    .setChapter(sa.get(5))
                    .build()
            );
            j++;
        }
        end =Math.min(i,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 解析简答题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private  QuestionGroup parseShortAnswerAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SHORT_ANSWER);
        //处理一级大题的题干信息
        ShortAnswerQuestionFilter f =(ShortAnswerQuestionFilter) filter.getQuestionFilter(QT_SHORT_ANSWER);
        int start =0,end=-1;
        if(f==null){
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取一级题的题干信息，获分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                qa.setScorePreQuestion(f.getScorePreQuestion());
                qa.setTotalScore(f.getTotalScore());
                break;
            }
        }
        if(i==s) {
            start =i;
            end =Math.min(i,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }
        start =i;
        //开始提取每个二级简答题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            qa.addQuestion(Question
                    .newBuilder()
                    .setQuestionType(QT_SHORT_ANSWER)
                    .setScore(f.getScorePreQuestion())
                    .setAnswerText(sa.get(0))
                    .setDifficulty(sa.get(1).isEmpty()?5.0:Double.valueOf(sa.get(1)))
                    .setAnalysis(sa.get(2))
                    .setQuestionTypeName(sa.get(3))
                    .setKnowledge(sa.get(4))
                    .setChapter(sa.get(5))
                    .build()
            );
            j++;
        }
        end =Math.min(i,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 解析应用题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private  QuestionGroup parseProblemSolvingAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        int start=0,end=-1;
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_PROBLEM_SOLVING);
        //处理一级大题的题干信息
        ProblemSolvingQuestionFilter f =(ProblemSolvingQuestionFilter) filter.getQuestionFilter(QT_PROBLEM_SOLVING);
        if(f==null){
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取一级大题的题干信息，获取选择题的分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                qa.setTotalScore(f.getTotalScore());
                break;
            }
        }
        if(i==s) {
            start =i;
            end =Math.min(i,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }
        start =i;
        //开始提取每个二级题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            qa.addQuestion(Question
                    .newBuilder()
                    .setQuestionType(QT_PROBLEM_SOLVING)
                    .setAnswerText(sa.get(0))
                    .setDifficulty(sa.get(1).isEmpty()?5.0:Double.valueOf(sa.get(1)))
                    .setAnalysis(sa.get(2))
                    .setQuestionTypeName(sa.get(3))
                    .setKnowledge(sa.get(4))
                    .setChapter(sa.get(5))
                    .build()
            );
            j++;
        }


        end =Math.min(i,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 解析综合题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private  QuestionGroup parseSynthesizedAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        int start=0,end=-1;
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SYNTHESIZED);
        //处理一级大题的题干信息
        SynthesizedQuestionFilter f =(SynthesizedQuestionFilter) filter.getQuestionFilter(QT_SYNTHESIZED);
        if(f==null) {
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取一级大题的题干信息，获取分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                qa.setTotalScore(f.getTotalScore());
                break;
            }
        }
        if(i==s) {
            start =i;
            end =Math.min(i,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }

        start=i;
        //开始提取二级题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        ArrayList<Question> questionArrayList=new ArrayList<>();
        StringBuilder totalAnswer=new StringBuilder();
        StringBuilder totalAnalysis=new StringBuilder();
        StringBuilder totalKnowledge=new StringBuilder();
        StringBuilder totalChapter=new StringBuilder();
        double totalDifficulty=0,difficulty=0;
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            difficulty=sa.get(1).isEmpty()?5.0:Double.valueOf(sa.get(1));
            questionArrayList.add(
                    Question
                    .newBuilder()
                    .setQuestionType(QT_PROBLEM_SOLVING)
                    .setAnswerText(sa.get(0))
                    .setDifficulty(difficulty)
                    .setAnalysis(sa.get(2))
                    .setQuestionTypeName(sa.get(3))
                    .setKnowledge(sa.get(4))
                    .setChapter(sa.get(5))
                    .build()
            );
            if(!sa.get(0).isEmpty()) {
                totalAnswer.append(sa.get(0));
                totalAnswer.append("\n");
            }
            if(!sa.get(2).isEmpty()){
                totalAnalysis.append(sa.get(2));
                totalAnalysis.append("\n");
            }
            if(!sa.get(4).isEmpty()) {
                totalKnowledge.append(sa.get(4));
                totalKnowledge.append("\n");
            }
            if(!sa.get(5).isEmpty()) {
                totalChapter.append(sa.get(5));
                totalChapter.append("\n");
            }
            totalDifficulty+=difficulty;
            j++;
        }


        qa.addQuestion(Question
                .newBuilder()
                .setQuestionType(QT_SYNTHESIZED)
                .setQuestionTypeName("综合题")
                .setAnswerText(totalAnswer.toString())
                .setAnalysis(totalAnalysis.toString())
                .setKnowledge(totalKnowledge.toString())
                .setChapter(totalChapter.toString())
                .setDifficulty(totalDifficulty/f.getQuestionCount())
                .addAllSubQuestion(questionArrayList)
                .build());

        end =Math.min(i,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 解析多选题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private static QuestionGroup parseMultiChoiceAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        return null;
    }

    /**
     * 解析判断题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private static QuestionGroup parseTrueFalseAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        return null;
    }

    /**
     * 解析填空题答案
     * @param text
     * @param range 返回对应题型答案开始行和结束行[start,end)
     * @return
     */
    private  QuestionGroup parseBlankFillingAnswers(ArrayList<String> text,Pair<Integer,Integer> range){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_BLANK_FILLING);
        //处理一级大题的题干信息
        BlankFillingQuestionFilter f =(BlankFillingQuestionFilter) filter.getQuestionFilter(QT_BLANK_FILLING);
        int start =0,end=-1;
        if(f==null){
            if(range!=null) {
                range.setKey(Integer.valueOf(0));
                range.setValue(Integer.valueOf(0));
            }
            return qa.build();
        }
        //提取一级题的题干信息，获分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                qa.setScorePreQuestion(f.getScorePreQuestion());
                qa.setTotalScore(f.getTotalScore());
                break;
            }
        }
        if(i==s) {
            start =i;
            end =Math.min(i,text.size());
            if(range!=null) {
                range.setKey(Integer.valueOf(start));
                range.setValue(Integer.valueOf(end));
            }
            return qa.build();
        }
        start =i;
        //开始提取每个二级题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            qa.addQuestion(Question
                    .newBuilder()
                    .setQuestionType(QT_BLANK_FILLING)
                    .setScore(f.getScorePreQuestion())
                    .setAnswerText(sa.get(0))
                    .setDifficulty(sa.get(1).isEmpty()?5.0:Double.valueOf(sa.get(1)))
                    .setAnalysis(sa.get(2))
                    .setQuestionTypeName(sa.get(3))
                    .setKnowledge(sa.get(4))
                    .setChapter(sa.get(5))
                    .build()
            );
            j++;
        }
        end =Math.min(i,text.size());
        if(range!=null) {
            range.setKey(Integer.valueOf(start));
            range.setValue(Integer.valueOf(end));
        }

        return qa.build();
    }

    /**
     * 从试卷答案中提取文本信息，该文本每行一个字符串，并去除了无关信息。
     * @param fileDocument
     * @return
     * @throws Exception
     */
    public ArrayList<String> parseText(String fileDocument) throws Exception{
        AnswerFilter answerFilter = this.getFilter().getAnswerFilter();
        if(answerFilter==null)
            return new ArrayList<String>();
        return parseText(fileDocument,answerFilter);
    }

    /**
     * 从试卷答案中提取文本信息，该文本每行一个字符串，并去除了无关信息。
     * @param fileDocument
     * @param filter
     * @return
     * @throws Exception
     */
    public static ArrayList<String> parseText(String fileDocument, AnswerFilter filter) throws Exception{
        String[] paragraphs = TextExtractor.parseToStrings(fileDocument);
        ArrayList<String> ss= new ArrayList<>();
        //查找试题答案开始的前一行，此行向上的部分全部剔除
        int i =0;
        for(i=0;i<paragraphs.length;++i){
            if(filter.begin(paragraphs[i]))
                break;
        }
        //剔除模板文本
        for(int j=i+1;j<paragraphs.length;++j){
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
     * 对一个二级题目的答案进行解析
     * @param text 整个答案文档的字符串行
     * @param i 从第i行开始扫描
     * @param questionCount 需要扫描的答案题目个数
     * @param stringArrayList 返回的扫描结果，按照答案，难易程度，答案解析，题型，知识点和所属章节返回字符串数组
     *         stringArrayList.add(szAnswerText);
     *         stringArrayList.add(szDifficultyText);
     *         stringArrayList.add(szAnalysisText);
     *         stringArrayList.add(szTypeText);
     *         stringArrayList.add(szKnowledgeText);
     *         stringArrayList.add(szChapterText);
     * @return 扫描结束的行
     */
    private int questionAnswer(ArrayList<String> text, int i, int questionCount,ArrayList<String> stringArrayList){
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        String line =null;
        String szAnswerText=null;
        while(szAnswerText==null) {
            line=text.get(i);
            szAnswerText=answerFilter.answerText(line);
            i++;
        }
        int k=i;//答案行的下一行
        String szDifficultyText=null;
        while(szDifficultyText==null) {
            line=text.get(i);
            szDifficultyText=answerFilter.difficultyText(line);
            i++;
        }
        int m=i-1;//难易程度的下一行
        //[k,m)的范围为多行答案文本
        if(m>k){
            StringBuilder sb = new StringBuilder(szAnswerText);
            for(int j=k;j<m;++j)
                sb.append(text.get(j));
            szAnswerText=sb.toString();
        }
        String szAnalysisText=null;
        while(szAnalysisText==null) {
            line=text.get(i);
            szAnalysisText=answerFilter.analysisText(line);
            i++;
        }
        String szTypeText=null;
        while(szTypeText==null) {
            line=text.get(i);
            szTypeText=answerFilter.typeText(line);
            i++;
        }
        String szKnowledgeText=null;
        while(szKnowledgeText==null) {
            line=text.get(i);
            szKnowledgeText=answerFilter.knowledgeText(line);
            i++;
        }
        String szChapterText=null;
        while(szChapterText==null) {
            line=text.get(i);
            szChapterText=answerFilter.chapterText(line);
            i++;
        }
        stringArrayList.add(szAnswerText);
        stringArrayList.add(szDifficultyText);
        stringArrayList.add(szAnalysisText);
        stringArrayList.add(szTypeText);
        stringArrayList.add(szKnowledgeText);
        stringArrayList.add(szChapterText);
        return i;
    }

    /**
     * 将题目与答案合并
     * @param question
     * @param answer
     * @return
     */
    private Question mergeQuestion(Question question, Question answer){
        Question.Builder builder= mergeSimpleQuestion(question,answer);
        if(question.getQuestionType()==QT_SYNTHESIZED){
            int c = question.getSubQuestionCount();
            for(int i=0;i<c;++i){
                Question q= question.getSubQuestion(i);
                Question a= answer.getSubQuestion(i);
                Question r=mergeSimpleQuestion(q,a).build();
                builder.setSubQuestion(i,r);
            }
        }
        return builder.build();
    }

    /**
     * 将题目与答案合并，不处理综合题，供mergeQuestion函数调用。
     * @param question
     * @param answer
     * @return
     */
    private Question.Builder mergeSimpleQuestion(Question question, Question answer){
        return question.
                toBuilder()
                .setAnswerText(answer.getAnswerText())
                .setDifficulty(answer.getDifficulty())
                .setAnalysis(answer.getAnalysis())
                .setQuestionTypeName(answer.getQuestionTypeName())
                .setKnowledge(answer.getKnowledge())
                .setChapter(answer.getChapter());
    }

    /**
     * 将试题组和答案组合并
     * @param question
     * @param answer
     * @return
     */
    private QuestionGroup mergeQuestionGroup(QuestionGroup question, QuestionGroup answer){
        int c = question.getQuestionCount();
        QuestionGroup.Builder builder = question.toBuilder();
        for(int i=0;i<c;++i){
            Question q= question.getQuestion(i);
            Question a= answer.getQuestion(i);
            Question r=mergeQuestion(q,a);
            builder.setQuestion(i,r);
        }
        return builder.build();
    }

    /**
     * 将试卷和答案合并
     * @param question
     * @param answer
     * @return
     */
    private Paper mergePaper(Paper question, Paper answer){
        int c = question.getQuestionGroupCount();
        Paper.Builder builder = question.toBuilder();
        for(int i=0;i<c;++i){
            QuestionGroup q= question.getQuestionGroup(i);
            QuestionGroup a= answer.getQuestionGroup(i);
            QuestionGroup r=mergeQuestionGroup(q,a);
            builder.setQuestionGroup(i,r);
        }
        return builder.build();
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
}
