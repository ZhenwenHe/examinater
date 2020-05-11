package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.gtl.protos.Question;
import cn.edu.cug.cs.gtl.protos.QuestionType;

public class QuestionPrinter {

    /**
     * 按照超星导入题目格式输出字符串，具体格式说明见quickImportQuestionTemplate.doc
     * @param q
     * @return
     */
    public static String exportToChaoXing(Question q){
        switch (q.getQuestionType()){
            case QT_SINGLE_CHOICE:return singleChoiceToChaoXing(q);
            case QT_MULTI_CHOICE:return multiChoiceToChaoXing(q);
            case QT_BLANK_FILLING:return blankFillingToChaoXing(q);
            case QT_TRUE_FALSE:return tureFalseToChaoXing(q);
            case QT_SHORT_ANSWER:return shortAnswerToChaoXing(q);
            case QT_PROBLEM_SOLVING:problemSolvingToChaoXing(q);
            case QT_SYNTHESIZED:return synthesizedToChaoXing(q);
            default:return "";
        }
    }

    public static String singleChoiceToChaoXing(Question q){
        StringBuilder sb=new StringBuilder(toString(q));
        //题干、选项、答案、难易程度、答案解析、题型，相互之间用回车隔开
        //答案：C
        //难易程度：中
        //答案解析：花间集的答案解析
        //题型：单选题
        sb.append("答案：");
        sb.append(q.getAnswerText());
        sb.append("\n");
        sb.append("难易程度：");
        //难度系数，1-3，易，4-6，中，7-9，难
        sb.append(difficultyToString(q.getDifficulty()));
        sb.append("\n");
        sb.append("答案解析：");
        sb.append(q.getAnalysis());
        sb.append("\n");
        sb.append("题型：");
        //sb.append(typeToString(q.getQuestionType()));
        sb.append("单选题");
        sb.append("\n");
        return sb.toString();
    }

    public static String multiChoiceToChaoXing(Question q){
        return null;
    }

    public static String blankFillingToChaoXing(Question q){
        return null;
    }

    public static String tureFalseToChaoXing(Question q){
        return null;
    }

    public static String shortAnswerToChaoXing(Question q){
        StringBuilder sb=new StringBuilder();
        //题干、选项、答案、难易程度、答案解析、题型，相互之间用回车隔开
        //答案：C
        //难易程度：中
        //答案解析：花间集的答案解析
        //题型：单选题
        sb.append("{\n");
        sb.append(toString(q));
        sb.append("\n}\n");
        sb.append("{\n答案：\n");
        sb.append(q.getAnswerText());
        sb.append("\n}\n");
        sb.append("难易程度：");
        //难度系数，1-3，易，4-6，中，7-9，难
        sb.append(difficultyToString(q.getDifficulty()));
        sb.append("\n");
        sb.append("答案解析：");
        sb.append(q.getAnalysis());
        sb.append("\n");
        sb.append("题型：");
        sb.append(typeToString(q.getQuestionType()));
        sb.append("\n");
        return sb.toString();
    }

    public static String problemSolvingToChaoXing(Question q){
        StringBuilder sb=new StringBuilder();
        //题干、选项、答案、难易程度、答案解析、题型，相互之间用回车隔开
        //答案：C
        //难易程度：中
        //答案解析：花间集的答案解析
        //题型：单选题
        sb.append("{\n");
        sb.append(toString(q));
        sb.append("\n}\n");
        sb.append("{\n答案：\n");
        sb.append(q.getAnswerText());
        sb.append("\n}\n");
        sb.append("难易程度：");
        //难度系数，1-3，易，4-6，中，7-9，难
        sb.append(difficultyToString(q.getDifficulty()));
        sb.append("\n");
        sb.append("答案解析：");
        sb.append(q.getAnalysis());
        sb.append("\n");
        sb.append("题型：");
        //sb.append(typeToString(q.getQuestionType()));
        sb.append("简答题");
        sb.append("\n");
        return sb.toString();
    }


    public static String synthesizedToChaoXing(Question q){
        StringBuilder sb=new StringBuilder();
        //题干、选项、答案、难易程度、答案解析、题型，相互之间用回车隔开
        //答案：C
        //难易程度：中
        //答案解析：花间集的答案解析
        //题型：单选题
        sb.append("{\n");
        sb.append(toString(q));
        sb.append("\n}\n");
        sb.append("{\n答案：\n");
        sb.append(q.getAnswerText());
        sb.append("\n}\n");
        sb.append("难易程度：");
        //难度系数，1-3，易，4-6，中，7-9，难
        sb.append(difficultyToString(q.getDifficulty()));
        sb.append("\n");
        sb.append("答案解析：");
        sb.append(q.getAnalysis());
        sb.append("\n");
        sb.append("题型：");
        //sb.append(typeToString(q.getQuestionType()));
        sb.append("简答题");
        sb.append("\n");
        return sb.toString();
    }
    /**
     * 生成题目字符串，不含答案
     * @param q
     * @return
     */
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

    /**
     * 简答题转成字符串，不含答案
     * @param q
     * @return
     */
    private static String shortAnswerQuestionToString(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append(q.getQuestionText(0));
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 应用题转成字符串，不含答案
     * @param q
     * @return
     */
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

    /**
     * 单选题转成字符串，不含答案
     * @param q
     * @return
     */
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

    /**
     * 综合题转成字符串，不含答案
     * @param q
     * @return
     */
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

    /**
     * 难度系数为1-9，1-3，易，4-6，中，7-9，难
     * @param difficulty
     * @return
     */
    public static String difficultyToString(double difficulty){
        if(difficulty<=3)
            return "易";
        else if(difficulty>=4 && difficulty<=6){
            return "中";
        }
        else {
            return "难";
        }
    }

    /**
     * 题型名称
     * @param type
     * @return
     */
    public static String typeToString(QuestionType type){
        switch (type){
            case QT_SINGLE_CHOICE:
                return "单选题";
            case QT_SHORT_ANSWER:
                return "简答题";
            case QT_PROBLEM_SOLVING:
                return "应用题";
            case QT_SYNTHESIZED:
                return "综合题";
            case QT_MULTI_CHOICE:
                return "多选题";
            case QT_BLANK_FILLING:
                return "填空题";
            case QT_TRUE_FALSE:
                return "判断题";
            default:
                return "综合题";
        }
    }
}