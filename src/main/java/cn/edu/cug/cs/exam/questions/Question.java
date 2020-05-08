package cn.edu.cug.cs.exam.questions;

import cn.edu.cug.cs.exam.filters.*;
import cn.edu.cug.cs.gtl.protos.Identifier;

import java.util.ArrayList;

import static cn.edu.cug.cs.exam.questions.QuestionType.*;

/**
 * 试卷题目基类
 */
public class Question {

    protected Identifier identifier;//题库中试题的唯一编号
    protected ArrayList<String> questionText;//题目问题文本
    protected ArrayList<Figure> questionFigures;//题目中的图
    protected String answerText;//答案文本
    protected ArrayList<Figure> answerFigures;//答案中的图
    protected double difficulty;//难度系数
    protected double score;//分值
    protected ArrayList<Double> detailedScores;//三级小题的分数，一般只有应用题型有这个字段；
    protected String knowledge;//考察的知识点
    protected String chapter;//知识点所属章节

    public Question(ArrayList<String> questionText, String answerText) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.identifier= Identifier.newBuilder().build();
    }

    public Identifier getIdentifier() {
        return identifier;
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
