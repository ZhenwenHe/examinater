package cn.edu.cug.cs.exam.questions;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.io.Storable;
import cn.edu.cug.cs.gtl.protos.Identifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import static cn.edu.cug.cs.exam.questions.QuestionType.*;

/**
 * 试卷题目基类
 */
public class Question   {

    cn.edu.cug.cs.gtl.protos.Question.Builder builder=cn.edu.cug.cs.gtl.protos.Question.newBuilder();

    private Identifier identifier;//题库中试题的唯一编号
    private ArrayList<String> questionText;//题目问题文本
    private ArrayList<Figure> questionFigures;//题目中的图
    private String answerText;//答案文本
    private ArrayList<Figure> answerFigures;//答案中的图
    private double difficulty;//难度系数
    private double score;//分值
    private ArrayList<Double> detailedScores;//三级小题的分数，一般只有应用题型有这个字段；
    private String knowledge;//考察的知识点
    private String chapter;//知识点所属章节

    public Question(ArrayList<String> questionText, String answerText) {
        //this.questionText = questionText;
        //builder.addAllQuestionText(questionText);
        //this.answerText = answerText;
        //builder.setAnswerText(answerText);
        //builder.setIdentifier(Identifier.newBuilder().build());
    }

    public Identifier getIdentifier() {
        return builder.getIdentifier();
    }

    public ArrayList<Figure> getQuestionFigures() {
        return questionFigures;
    }

    public void setQuestionFigures(ArrayList<Figure> questionFigures) {
        this.questionFigures = questionFigures;
    }

    public ArrayList<Figure> getAnswerFigures() {
        return answerFigures;
    }

    public void setAnswerFigures(ArrayList<Figure> answerFigures) {
        this.answerFigures = answerFigures;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public ArrayList<String> getQuestionText() {
        return questionText;
    }

    public void setQuestionText(ArrayList<String> questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }


    public ArrayList<Double> getDetailedScores() {
        return detailedScores;
    }

    public void setDetailedScores(ArrayList<Double> detailedScores) {
        this.detailedScores = detailedScores;
    }

    public static QuestionType getType(Question qf){
        if(qf instanceof SingleChoiceQuestion)
            return QT_SINGLE_CHOICE;
        if(qf instanceof ShortAnswerQuestion)
            return QT_SHORT_ANSWER;
        if(qf instanceof ProblemSolvingQuestion)
            return QT_PROBLEM_SOLVING;
        if(qf instanceof SynthesizedQuestion)
            return QT_SYNTHESIZED;
        if(qf instanceof BlankFillingQuestion)
            return QT_BLANK_FILLING;
        if(qf instanceof TrueFalseQuestion)
            return QT_TRUE_FALSE;
        if(qf instanceof MultiChoiceQuestion)
            return QT_MULTI_CHOICE;
        return QT_SYNTHESIZED;
    }

}
