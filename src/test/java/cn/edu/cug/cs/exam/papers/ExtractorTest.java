package cn.edu.cug.cs.exam.papers;


import cn.edu.cug.cs.exam.printer.PaperPrinter;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.QuestionType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtractorTest {

    String filterFile = "dat/paper_filters.txt";
    String scFilterFile = "dat/singlechoice_filters.txt";
    String saFilterFile = "dat/shortanswer_filters.txt";
    String psFilterFile = "dat/problemsolving_filters.txt";
    String syFilterFile = "dat/synthesized_filters.txt";
    String inputFile = "dat/2019A.doc";

    @Test
    public void parsePaper() {
        try {
            //read filters
            Filter filter= new Filter(filterFile);
            filter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
            filter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
            filter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
            filter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
            Extractor extractor = new Extractor(filter);
            //read paper
            Paper ss = extractor.parsePaper(inputFile);
            int  c = ss.getQuestionGroupCount();
            System.out.println(PaperPrinter.toString(ss));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}