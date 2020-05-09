package cn.edu.cug.cs.exam.questions;

import java.util.ArrayList;

/**
 * 应用题
 */
public class ProblemSolvingQuestion extends Question {
    public ProblemSolvingQuestion(ArrayList<String> questionText, String answerText) {
        super(questionText, answerText);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getQuestionText().get(0));
        sb.append("(");
        sb.append(String.valueOf(getScore()));
        sb.append("分)");
        sb.append("\n");
        int s = this.getQuestionText().size();
        if(getDetailedScores()==null){
            for(int i=1;i<s;++i){
                sb.append(String.valueOf(i));
                sb.append(")");
                sb.append(this.getQuestionText().get(i));
                sb.append("\n");
            }
        }
        else{
            if(getDetailedScores().size()<getQuestionText().size()-1){
                for(int i=1;i<s;++i){
                    sb.append(String.valueOf(i));
                    sb.append(")");
                    sb.append(this.getQuestionText().get(i));
                    sb.append("\n");
                }
            }
            else{
                for(int i=1;i<s;++i){
                    sb.append(String.valueOf(i));
                    sb.append(")");
                    sb.append(this.getQuestionText().get(i));
                    sb.append(" (");
                    sb.append(String.valueOf(getDetailedScores().get(i-1)));
                    sb.append("分)");
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
