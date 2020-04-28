package cn.edu.cug.cs.hzw;
import cn.edu.cug.cs.gtl.extractor.TextExtractor;

public class CUGExamPaperExtractor implements ExamPaperExtractor{

    @Override
    public String[] parse(String file) throws Exception {
        return TextExtractor.parseToStrings(file);
    }

    @Override
    public String[] getFilters(String fileFilter) throws Exception {
        return new String[0];
    }
}
