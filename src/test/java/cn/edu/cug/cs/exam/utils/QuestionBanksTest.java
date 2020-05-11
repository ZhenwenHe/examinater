package cn.edu.cug.cs.exam.utils;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.exam.io.AnswerExtractor;
import cn.edu.cug.cs.exam.io.PaperExtractor;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.QuestionBank;
import cn.edu.cug.cs.gtl.protos.QuestionType;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionBanksTest {
    String filterFile = "dat/paper_filters.txt";
    String scFilterFile = "dat/singlechoice_filters.txt";
    String saFilterFile = "dat/shortanswer_filters.txt";
    String psFilterFile = "dat/problemsolving_filters.txt";
    String syFilterFile = "dat/synthesized_filters.txt";
    String answerFilterFile = "dat/answer_filters.txt";
    String answerFile = "dat/answer_template.doc";
    String paperFile = "dat/paper_template.doc";
    String chaoxingExampleFile = "dat/chaoxing_example.docx";

    @Test
    public void exportToChaoXing() {
        try {
            //read filters
            PaperFilter filter= new PaperFilter(filterFile);
            filter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
            filter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
            filter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
            filter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
            filter.setAnswerFilter(answerFilterFile);

            PaperExtractor paperExtractor=new PaperExtractor(filter);
            Paper questionPaper = paperExtractor.parsePaper(paperFile);
            AnswerExtractor extractor = new AnswerExtractor(filter);
            //read paper and answer
            Paper paper = extractor.parseAnswers(questionPaper,answerFile);
            QuestionBank questionBank= QuestionBanks.importFromPaper(paper,null);
            QuestionBanks.exportToChaoXing(questionBank,chaoxingExampleFile);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}