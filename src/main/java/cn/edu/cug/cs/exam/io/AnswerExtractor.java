package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.Question;
import cn.edu.cug.cs.gtl.protos.QuestionGroup;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static cn.edu.cug.cs.gtl.protos.QuestionType.*;

public class AnswerExtractor {

    PaperFilter filter=null;

    public AnswerExtractor(PaperFilter filter) {
        this.filter = filter;
    }

    public PaperFilter getFilter() {
        return filter;
    }

    /**
     *
     * @param paper
     * @param answerFile
     * @return
     * @throws Exception
     */
    public  Paper parseAnswers(Paper paper, String answerFile)throws Exception{
        ArrayList<String> text = parseText(answerFile);
        Paper.Builder builder = Paper.newBuilder();
        QuestionGroup qg = parseSingleChoiceAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseMultiChoiceAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseBlankFillingAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseTrueFalseAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseShortAnswerAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseProblemSolvingAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);
        qg = parseSynthesizedAnswers(text);
        if(qg!=null) builder.addQuestionGroup(qg);

        Paper answer= builder.build();
        if(paper!=null){
            return mergePaper(paper,answer);
        }
        else
            return answer;
    }

    private  QuestionGroup parseSingleChoiceAnswers(ArrayList<String> text){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();//(QT_SINGLE_CHOICE);
        qa.setQuestionType(QT_SINGLE_CHOICE);
        //处理一级大题的题干信息
        SingleChoiceQuestionFilter f =(SingleChoiceQuestionFilter) filter.getQuestionFilter(QT_SINGLE_CHOICE);
        if(f==null) return qa.build();
        //提取选择题大题题干信息，获取选择题的分值，总分值和题目个数
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
        if(i==s) return qa.build();
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
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }

    private  QuestionGroup parseShortAnswerAnswers(ArrayList<String> text){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SHORT_ANSWER);
        //处理一级大题的题干信息
        ShortAnswerQuestionFilter f =(ShortAnswerQuestionFilter) filter.getQuestionFilter(QT_SHORT_ANSWER);
        if(f==null) return qa.build();
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
        if(i==s) return qa.build();
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
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }

    private  QuestionGroup parseProblemSolvingAnswers(ArrayList<String> text){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_PROBLEM_SOLVING);
        //处理一级大题的题干信息
        ProblemSolvingQuestionFilter f =(ProblemSolvingQuestionFilter) filter.getQuestionFilter(QT_PROBLEM_SOLVING);
        if(f==null) return qa.build();
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
        if(i==s) return qa.build();
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
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }


    private  QuestionGroup parseSynthesizedAnswers(ArrayList<String> text){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SYNTHESIZED);
        //处理一级大题的题干信息
        SynthesizedQuestionFilter f =(SynthesizedQuestionFilter) filter.getQuestionFilter(QT_SYNTHESIZED);
        if(f==null) return qa.build();
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
        if(i==s) return qa.build();
        //开始提取二级题的答案
        int j=0;
        ++i;
        AnswerFilter answerFilter=this.getFilter().getAnswerFilter();
        ArrayList<Question> questionArrayList=new ArrayList<>();
        while(i<s && j<f.getQuestionCount()){
            ArrayList<String> sa = new ArrayList<>();
            i=questionAnswer(text,i,f.getQuestionCount(),sa);
            questionArrayList.add(
                    Question
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

        qa.addQuestion(Question
                .newBuilder()
                .setQuestionType(QT_SYNTHESIZED)
                .setQuestionTypeName("综合题")
                .addAllSubQuestion(questionArrayList)
                .build());

        return qa.build();
    }

    private static QuestionGroup parseMultiChoiceAnswers(ArrayList<String> text){
        return null;
    }

    private static QuestionGroup parseTrueFalseAnswers(ArrayList<String> text){
        return null;
    }

    private static QuestionGroup parseBlankFillingAnswers(ArrayList<String> text){
        return null;
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
}
