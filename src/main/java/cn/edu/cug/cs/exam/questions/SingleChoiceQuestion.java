package cn.edu.cug.cs.exam.questions;

import java.util.ArrayList;

/**
 * 单选题
 * 支持两种类型文本的输入：
 * 1、SQL语言通常称为()。
 * A.结构化查询语言           B.结构化控制语言
 * C.结构化定义语言           D.结构化操纵语言
 * 或者
 * 1、SQL语言通常称为()。
 * A.结构化查询语言
 * B.结构化控制语言
 * C.结构化定义语言
 * D.结构化操纵语言
 * 其中，"、"与"."可以互换使用。
 */
public class SingleChoiceQuestion extends Question {
    public SingleChoiceQuestion(ArrayList<String> questionText, String answerText) {
        super(questionText, answerText);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getQuestionText().get(0));
        sb.append("\n");
        sb.append("A.");
        sb.append(this.getQuestionText().get(1));
        sb.append("\n");
        sb.append("B.");
        sb.append(this.getQuestionText().get(2));
        sb.append("\n");
        sb.append("C.");
        sb.append(this.getQuestionText().get(3));
        sb.append("\n");
        sb.append("D.");
        sb.append(this.getQuestionText().get(4));
        sb.append("\n");
        return sb.toString();
    }
}
