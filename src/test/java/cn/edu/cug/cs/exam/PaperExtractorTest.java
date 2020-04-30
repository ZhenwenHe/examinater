package cn.edu.cug.cs.exam;

import org.junit.Test;

import java.util.ArrayList;

public class PaperExtractorTest {

    @Test
    public void parseText() {
        try {
            //read filters
            String filterFile = "dat/cug_filters.txt";
            PaperFilter filter= new PaperFilter(filterFile);
            //read paper
            String inputFile = "dat/2019B.doc";
            ArrayList<String> ss = PaperExtractor.parseText(inputFile,filter);
            for(String s:ss){
                System.out.println(s);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}