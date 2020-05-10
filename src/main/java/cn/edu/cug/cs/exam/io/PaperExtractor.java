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

public class PaperExtractor {

    PaperFilter filter=null;

    public PaperExtractor(PaperFilter filter) {
        this.filter = filter;
    }

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
        QuestionGroup qb= null;
        qb =parseSingleChoiceQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseMultiChoiceQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseBlankFillingQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseTrueFalseQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseShortAnswerQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseProblemSolvingQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);
        qb= parseSynthesizedQuestions(text);
        if(qb!=null) qa.addQuestionGroup(qb);

        return qa.build();
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
    public ArrayList<QuestionGroup> parseQuestions(ArrayList<String> text){
        ArrayList<QuestionGroup> qa = new ArrayList<>();
        QuestionGroup qb= null;
        qb =parseSingleChoiceQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseMultiChoiceQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseBlankFillingQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseShortAnswerQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseProblemSolvingQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseSynthesizedQuestions(text);
        if(qb!=null) qa.add(qb);
        qb= parseTrueFalseQuestions(text);
        if(qb!=null) qa.add(qb);
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
     * @return
     */
    private QuestionGroup parseSingleChoiceQuestions(ArrayList<String> text){
        QuestionGroup.Builder qa =QuestionGroup.newBuilder();//(QT_SINGLE_CHOICE);
        qa.setQuestionType(QT_SINGLE_CHOICE);
        SingleChoiceQuestionFilter f =(SingleChoiceQuestionFilter) filter.getQuestionFilter(QT_SINGLE_CHOICE);
        if(f==null) return qa.build();
        //提取选择题大题题干信息，获取选择题的分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                System.out.println(line);
                System.out.println(f.getScorePreQuestion());
                System.out.println(f.getQuestionCount());
                System.out.println(f.getTotalScore());
                break;
            }
        }
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
                System.out.println(qt);
                als.add(qt);
                qt="";
                //A text
                while(qt.isEmpty()){
                    i++;
                    line = text.get(i);
                    qt=f.choiceAText(line);
                }
                qt = qt.trim();
                System.out.println(qt);
                als.add(qt);
                //B text
                qt=f.choiceBText(line);
                while(qt.isEmpty()){
                    i++;
                    line = text.get(i);
                    qt=f.choiceBText(line);
                }
                qt = qt.trim();
                System.out.println(qt);
                als.add(qt);
                //C text
                qt=f.choiceCText(line);
                while(qt.isEmpty()){
                    i++;
                    line = text.get(i);
                    qt=f.choiceCText(line);
                }
                qt = qt.trim();
                System.out.println(qt);
                als.add(qt);
                //D text
                qt=f.choiceDText(line);
                while(qt.isEmpty()){
                    i++;
                    line = text.get(i);
                    qt=f.choiceDText(line);
                }
                qt = qt.trim();
                System.out.println(qt);
                als.add(qt);

//                SingleChoiceQuestion q = new SingleChoiceQuestion(als,"");
//                q.setScore(f.getScorePreQuestion());
//                qa.add(q);
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

        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());
        assert qa.getQuestionCount()==f.getQuestionCount();
        return qa.build();
    }

    /**
     * 解析多项选择题
     * @param text
     * @return
     */
    private QuestionGroup parseMultiChoiceQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析判断题
     * @param text
     * @return
     */
    private QuestionGroup parseTrueFalseQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析填空题
     * @param text
     * @return
     */
    private QuestionGroup parseBlankFillingQuestions(ArrayList<String> text){
        return null;
    }

    /**
     * 解析简答题
     * @param text
     * @return
     */
    private QuestionGroup parseShortAnswerQuestions(ArrayList<String> text){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SHORT_ANSWER);
        ShortAnswerQuestionFilter f =(ShortAnswerQuestionFilter) filter.getQuestionFilter(QT_SHORT_ANSWER);
        if(f==null) return qa.build();
        //提取简答题大题题干信息，获取简答题的分值，总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                System.out.println(line);
                System.out.println(f.getScorePreQuestion());
                System.out.println(f.getQuestionCount());
                System.out.println(f.getTotalScore());
                break;
            }
        }
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
                System.out.println(qt);
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

        qa.setScorePreQuestion(f.getScorePreQuestion());
        qa.setTotalScore(f.getTotalScore());
        assert qa.getQuestionCount()==f.getQuestionCount();

        return qa.build();
    }

    /**
     * 解析应用题
     * @param text
     * @return
     */
    private QuestionGroup parseProblemSolvingQuestions(ArrayList<String> text){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_PROBLEM_SOLVING);
        ProblemSolvingQuestionFilter f =(ProblemSolvingQuestionFilter) filter.getQuestionFilter(QT_PROBLEM_SOLVING);
        if(f==null) return qa.build();
        //提取应用题大题题干信息，获取总分值和题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                System.out.println(line);
                System.out.println(f.getQuestionCount());
                System.out.println(f.getTotalScore());
                break;
            }
        }
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
                System.out.println(qt);
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
                //c等于0的时候跳出
//                ProblemSolvingQuestion q = new ProblemSolvingQuestion(als,"");
//                q.setScore(qtp.second().doubleValue());
//                q.setDetailedScores(detailedScores);
//                qa.add(q);
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

        qa.setScorePreQuestion(0);
        qa.setTotalScore(f.getTotalScore());
        assert qa.getQuestionCount()==f.getQuestionCount();

        return qa.build();

    }

    /**
     * 解析综合题
     * @param text
     * @return
     */
    private QuestionGroup parseSynthesizedQuestions(ArrayList<String> text){
        QuestionGroup.Builder qa =  QuestionGroup.newBuilder();
        qa.setQuestionType(QT_SYNTHESIZED);
        ArrayList<String> als_text = new ArrayList<>();
        ArrayList<Question> alq = new ArrayList<>();
        SynthesizedQuestionFilter f =(SynthesizedQuestionFilter) filter.getQuestionFilter(QT_SYNTHESIZED);
        if(f==null){
            return qa.build();
        }
        //提取大题题干信息，获取总分值和二级题目个数
        int i=0;
        int s = text.size();
        String line =null;
        for(i=0;i<s;++i){
            line = text.get(i);
            if(f.begin(line)){
                System.out.println(line);
                System.out.println(f.getQuestionCount());
                System.out.println(f.getTotalScore());
                break;
            }
        }
        if(i<s-1){
            i++;
            line = text.get(i);
            line = line.trim();
            if(line.isEmpty())
                return qa.build();
            als_text.add(line);
        }
        else
            return qa.build();

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
                System.out.println(qt);
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
                //c等于0的时候跳出
//                ProblemSolvingQuestion q = new ProblemSolvingQuestion(als,"");
//                q.setScore(qtp.second().doubleValue());
//                alq.add(q);
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
//            SynthesizedQuestion synthesizedQuestion=new SynthesizedQuestion(als_text,alq,"");
//            synthesizedQuestion.setScore(f.getTotalScore());
//            qa.add(synthesizedQuestion);
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

        return qa.build();
    }




}
