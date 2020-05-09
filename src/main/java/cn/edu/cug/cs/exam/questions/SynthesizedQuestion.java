package cn.edu.cug.cs.exam.questions;

import java.util.ArrayList;

/**
 * 综合题，实质为一段文字，加上多个应用题的合成。
 */
public class SynthesizedQuestion extends Question {
    ArrayList<Question> questions=new ArrayList<>();

    public SynthesizedQuestion(ArrayList<String> questionText, ArrayList<Question>qa, String answerText) {
        super(questionText, answerText);
        this.questions.addAll(qa);
    }

    public void addQuestion(Question q){
        this.questions.add(q);
    }

    public ArrayList<Question> getQuestions(){
        return this.questions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getQuestionText().get(0));
        sb.append("\n");
        int i=1;
        for(Question q: questions){
            sb.append(String.valueOf(i));
            sb.append(".");
            sb.append(q.toString());
            i++;
        }
        return sb.toString();
    }
}
