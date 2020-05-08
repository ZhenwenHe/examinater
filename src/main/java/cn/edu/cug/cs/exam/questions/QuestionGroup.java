package cn.edu.cug.cs.exam.questions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class QuestionGroup extends ArrayList<Question> {

    QuestionType questionType;//题组的类型
    String text; //题组文本
    double scorePreQuestion;//题组中每个小题的分数，如果每个小题的分数不是平均的，则为0
    int questionCount;//题组中试题的个数
    double totalScore;//题组中试题的总分
    String order;//题组序号，如一、二、三、四、五等数字

    public QuestionGroup(QuestionType questionType, int initialCapacity) {
        super(initialCapacity);
        this.questionType=questionType;
        scorePreQuestion=0;
    }

    public QuestionGroup(QuestionType questionType) {
        this.questionType=questionType;
        scorePreQuestion=0;
    }

    public QuestionGroup(QuestionType questionType, @NotNull Collection<? extends Question> c) {
        this.questionType=questionType;
        addAll(c);
        scorePreQuestion=0;
    }

    @Override
    public boolean add(Question question) {
        if(isEmpty())
            return super.add(question);
        else {
            if(this.questionType==Question.getType(question)){
                return super.add(question);
            }
            else
                return false;
        }
    }

    @Override
    public void add(int index, Question element) {
        if(isEmpty())
            super.add(index, element);
        else {
            if(questionType==Question.getType(element)){
                super.add(index, element);
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends Question> c) {
        ArrayList<Question> qa = new ArrayList<>();
        for(Question q: c){
            if(Question.getType(q)==this.questionType)
                qa.add(q);
        }
        return super.addAll(qa);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Question> c) {
        ArrayList<Question> qa = new ArrayList<>();
        for(Question q: c){
            if(Question.getType(q)==this.questionType)
                qa.add(q);
        }
        return super.addAll(index, qa);
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public double getScorePreQuestion() {
        return scorePreQuestion;
    }

    public void setScorePreQuestion(double scorePreQuestion) {
        this.scorePreQuestion = scorePreQuestion;
    }

    public int getQuestionCount() {
        return size();
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.order);
        sb.append("、");
        switch (questionType){
            case QT_SINGLE_CHOICE:{
                sb.append("选择题(");
                sb.append("共");
                sb.append(String.valueOf(getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: this){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    sb.append(q.toString());
                    i++;
                }
                break;
            }
            case QT_SHORT_ANSWER:{
                sb.append("简答题(");
                sb.append("共");
                sb.append(String.valueOf(getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: this){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    sb.append(q.toString());
                    i++;
                }
                break;
            }
            case QT_PROBLEM_SOLVING:{
                sb.append("应用题(");
                sb.append("共");
                sb.append(String.valueOf(getQuestionCount()));
                sb.append("题，共");
                sb.append(String.valueOf(getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: this){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    sb.append(q.toString());
                    i++;
                }
                break;
            }
            case QT_SYNTHESIZED:{
                SynthesizedQuestion sq = (SynthesizedQuestion) get(0);
                sb.append("综合题(");
                sb.append("共");
                sb.append(String.valueOf(sq.getQuestions().size()));
                sb.append("题，共");
                sb.append(String.valueOf(getTotalScore()));
                sb.append("分)\n");
                sb.append(sq.toString());
                sb.append("\n");
                break;
            }
            case QT_MULTI_CHOICE:{
                break;
            }
            case QT_BLANK_FILLING:{
                break;
            }
            case QT_TRUE_FALSE:{
                break;
            }
        }

        return sb.toString();
    }

}
