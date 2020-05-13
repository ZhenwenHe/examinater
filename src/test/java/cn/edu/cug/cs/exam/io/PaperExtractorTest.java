package cn.edu.cug.cs.exam.io;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.QuestionType;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PaperExtractorTest {

    String filterFile = "dat/paper_filters.txt";
    String scFilterFile = "dat/singlechoice_filters.txt";
    String mcFilterFile = "dat/multichoice_filters.txt";
    String saFilterFile = "dat/shortanswer_filters.txt";
    String psFilterFile = "dat/problemsolving_filters.txt";
    String syFilterFile = "dat/synthesized_filters.txt";
    String bfFilterFile = "dat/blankfilling_filters.txt";
    String tfFilterFile = "dat/truefalse_filters.txt";
    String answerFilterFile = "dat/answer_filters.txt";
    String answerFile = "dat/answer_template3.doc";
    String paperFile = "dat/paper_template3.doc";


    private PaperFilter initialFilters() throws Exception{
        PaperFilter filter= new PaperFilter(filterFile);
        filter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
        filter.addQuestionFilter(mcFilterFile, QuestionType.QT_MULTI_CHOICE);
        filter.addQuestionFilter(bfFilterFile, QuestionType.QT_BLANK_FILLING);
        filter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
        filter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
        filter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
        filter.addQuestionFilter(tfFilterFile, QuestionType.QT_TRUE_FALSE);
        filter.setAnswerFilter(answerFilterFile);
        return filter;
    }

    @Test
    public void parsePaper() {
        try {
            //read filters
            PaperFilter filter= initialFilters();

            PaperExtractor extractor = new PaperExtractor(filter);
            //read paper
            Paper ss = extractor.parsePaper(paperFile);
            int  c = ss.getQuestionGroupCount();
            System.out.println(PaperPrinter.toString(ss));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void parseText() {
        try {
            //read filters
            PaperFilter filter= initialFilters();

            PaperExtractor extractor = new PaperExtractor(filter);
            //read paper
            ArrayList<String> ss = extractor.parseText(paperFile);
            for(String s:ss)
                System.out.println(s);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}