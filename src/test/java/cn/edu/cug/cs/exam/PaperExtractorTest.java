package cn.edu.cug.cs.exam;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.exam.questions.Question;
import cn.edu.cug.cs.exam.questions.QuestionType;
import org.junit.Test;

import java.util.ArrayList;

public class PaperExtractorTest {

    String filterFile = "dat/paper_filters.txt";
    String scFilterFile = "dat/singlechoice_filters.txt";
    String saFilterFile = "dat/shortanswer_filters.txt";
    String psFilterFile = "dat/problemsolving_filters.txt";
    String syFilterFile = "dat/synthesized_filters.txt";
    String inputFile = "dat/2019A.doc";
    @Test
    public void parseText() {
        try {
            //read filters
            PaperFilter filter= new PaperFilter(filterFile);
            //read paper
            ArrayList<String> ss = PaperExtractor.parseText(inputFile,filter);
            for(String s:ss){
                System.out.println(s);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void parseQuestions(){
        try {
            //read filters
            PaperFilter filter= new PaperFilter(filterFile);
            filter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
            filter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
            filter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
            filter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
            PaperExtractor paperExtractor = new PaperExtractor(filter);
            //read paper
            ArrayList<String> ss = paperExtractor.parseText(inputFile);
            ArrayList<Question> qa= paperExtractor.parseQuestions(ss);
            int  c = qa.size();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void parsePaper(){
        try {
            //read filters
            PaperFilter filter= new PaperFilter(filterFile);
            filter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
            filter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
            filter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
            filter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
            PaperExtractor paperExtractor = new PaperExtractor(filter);
            //read paper
            Paper ss = paperExtractor.parsePaper(inputFile);
            int  c = ss.getQuestionGroups().size();
            System.out.println(ss.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}