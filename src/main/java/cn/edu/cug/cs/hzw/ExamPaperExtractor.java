package cn.edu.cug.cs.hzw;

import java.io.IOException;

public interface ExamPaperExtractor {
    String[] parse(String fileDocument) throws Exception;
    String[] getFilters(String fileFilter) throws Exception;
}
