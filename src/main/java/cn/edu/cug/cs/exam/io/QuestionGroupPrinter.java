package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.gtl.protos.Question;
import cn.edu.cug.cs.gtl.protos.QuestionGroup;
import cn.edu.cug.cs.gtl.protos.QuestionType;

import java.util.List;

public class QuestionGroupPrinter {

    /**
     * 将试题组（一级题目，如单选题，多选题，简答题,应用题，综合题等）转成考试试卷字符串，不包含答案
     * @param questionGroup
     * @return
     */
    public static String toString(QuestionGroup questionGroup) {
        StringBuilder sb = new StringBuilder();
        String order = questionGroup.getOrder();
        if(order!=null) {
            if(!order.isEmpty()){
                sb.append(order);
                sb.append("、");
            }
        }
        QuestionType questionType = questionGroup.getQuestionType();
        List<Question> lq=questionGroup.getQuestionList();
        switch (questionType){
            case QT_SINGLE_CHOICE:{
                sb.append("选择题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(questionGroup.getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    //sb.append(q.toString());
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
            case QT_SHORT_ANSWER:{
                sb.append("简答题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(questionGroup.getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    //sb.append(q.toString());
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
            case QT_PROBLEM_SOLVING:{
                sb.append("应用题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    //sb.append(q.toString());
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
            case QT_SYNTHESIZED:{
                Question sq = questionGroup.getQuestion(0);
                sb.append("综合题(");
                sb.append("共");
                sb.append(String.valueOf(sq.getSubQuestionCount()));
                sb.append("题，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                //sb.append(sq.toString());
                sb.append(QuestionPrinter.toString(sq));
                sb.append("\n");
                break;
            }
            case QT_MULTI_CHOICE:{
                sb.append("多选题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(questionGroup.getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
            case QT_BLANK_FILLING:{
                sb.append("填空题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(questionGroup.getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    //sb.append(q.toString());
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
            case QT_TRUE_FALSE:{
                sb.append("判断题(");
                sb.append("共");
                sb.append(String.valueOf(questionGroup.getQuestionCount()));
                sb.append("题，每题");
                sb.append(String.valueOf(questionGroup.getScorePreQuestion()));
                sb.append("分，共");
                sb.append(String.valueOf(questionGroup.getTotalScore()));
                sb.append("分)\n");
                int i=1;
                for(Question q: lq){
                    sb.append(String.valueOf(i));
                    sb.append(".");
                    //sb.append(q.toString());
                    sb.append(QuestionPrinter.toString(q));
                    i++;
                }
                break;
            }
        }

        return sb.toString();
    }
}
