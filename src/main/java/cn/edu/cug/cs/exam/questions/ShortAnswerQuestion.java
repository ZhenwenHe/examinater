package cn.edu.cug.cs.exam.questions;

import java.util.ArrayList;

/**
 * 简答题
 */
public class ShortAnswerQuestion extends Question{
    public ShortAnswerQuestion(ArrayList<String> questionText, String answerText) {
        super(questionText, answerText);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.questionText.get(0));
        sb.append("\n");
        return sb.toString();
    }
}
