package cn.edu.cug.cs.hzw;

import cn.edu.cug.cs.gtl.config.Config;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;
import cn.edu.cug.cs.gtl.io.File;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CUGExamPaperExtractorTest {

    @Test
    public void parse() {
        try {
            String inputFile = "dat/2019A.doc";
            String[] paragraphs = TextExtractor.parseToStrings(inputFile);
            Pattern p=Pattern.compile("第\\s+\\d\\s+页\\s+共\\s+\\d\\s+页");
            for (String s : paragraphs) {
                if(p.matcher(s).find())
                    System.out.println(s);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}