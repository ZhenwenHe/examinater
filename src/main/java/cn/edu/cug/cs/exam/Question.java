package cn.edu.cug.cs.exam;

import java.util.ArrayList;

/**
 * 试卷题目基类
 */
public class Question {
    protected ArrayList<String> questionText;//题目问题文本
    protected ArrayList<Figure> questionFigures;//题目中的图
    protected String answerText;//答案文本
    protected ArrayList<Figure> answerFigures;//答案中的图
    protected double difficulty;//难度系数
    protected double score;//分值
    protected String knowledge;//考察的知识点
    protected int    chapter;//知识点所属章节

    public Question(ArrayList<String> questionText, String answerText) {
        this.questionText = questionText;
        this.answerText = answerText;
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
}
