package cn.edu.cug.cs.exam.printer;

import cn.edu.cug.cs.gtl.protos.Question;

public class QuestionPrinter {

    public static String toString(Question q){
        String sz="";
        switch (q.getQuestionType()){
            case QT_SINGLE_CHOICE:{
                sz= singleChoiceQuestionToString(q);
                break;
            }
            case QT_SHORT_ANSWER:{
                sz= shortAnswerQuestionToString(q);
                break;
            }
            case QT_PROBLEM_SOLVING:{
                sz= problemSolvingQuestionToString(q);
                break;
            }
            case QT_SYNTHESIZED:{
                sz= synthesizedQuestionToString(q);
                break;
            }
        }
        return sz;
    }

    private static String shortAnswerQuestionToString(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append(q.getQuestionText(0));
        sb.append("\n");
        return sb.toString();
    }

    private static String problemSolvingQuestionToString(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append(q.getQuestionText(0));
        sb.append("(");
        sb.append(String.valueOf(q.getScore()));
        sb.append("分)");
        sb.append("\n");
        int s = q.getQuestionTextCount();
        if (q.getDetailedScoreCount() == 0) {
            for (int i = 1; i < s; ++i) {
                sb.append(String.valueOf(i));
                sb.append(")");
                sb.append(q.getQuestionText(i));
                sb.append("\n");
            }
        } else {
            if (q.getDetailedScoreCount() < q.getQuestionTextCount() - 1) {
                for (int i = 1; i < s; ++i) {
                    sb.append(String.valueOf(i));
                    sb.append(")");
                    sb.append(q.getQuestionText(i));
                    sb.append("\n");
                }
            } else {
                for (int i = 1; i < s; ++i) {
                    sb.append(String.valueOf(i));
                    sb.append(")");
                    sb.append(q.getQuestionText(i));
                    sb.append(" (");
                    sb.append(String.valueOf(q.getDetailedScore(i - 1)));
                    sb.append("分)");
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    private static String singleChoiceQuestionToString(Question q) {

        StringBuilder sb = new StringBuilder();
        sb.append(q.getQuestionText(0));
        sb.append("\n");
        sb.append("A.");
        sb.append(q.getQuestionText(1));
        sb.append("\n");
        sb.append("B.");
        sb.append(q.getQuestionText(2));
        sb.append("\n");
        sb.append("C.");
        sb.append(q.getQuestionText(3));
        sb.append("\n");
        sb.append("D.");
        sb.append(q.getQuestionText(4));
        sb.append("\n");
        return sb.toString();
    }

    private static String synthesizedQuestionToString(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append(q.getQuestionText(0));
        sb.append("\n");
        int i=1;
        for(Question sq: q.getSubQuestionList()){
            sb.append(String.valueOf(i));
            sb.append(".");
            //sb.append(sq.toString());
            sb.append(problemSolvingQuestionToString(sq));
            i++;
        }
        return sb.toString();
    }
}