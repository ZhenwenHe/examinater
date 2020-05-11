package cn.edu.cug.cs.exam.utils;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.exam.io.PaperExtractor;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.PaperBank;
import cn.edu.cug.cs.gtl.protos.QuestionBank;

import java.io.*;

/**
 *
 */
public class PaperBanks {
    /**
     * 从试卷导入，试卷模板见2019A.doc和2019B.doc,为CUG的2014年模板
     * 如果其他学校的模板，需要在paper_filters.txt中调整需要剔除字符串模板
     * 所谓的提出字符串，指的是和试题无关的信息，如学校名称、装订线等
     * 最简单的试卷模板见paper_template.doc
     * @param paperFiles
     * @param filter
     * @param pb
     * @throws Exception
     */
    public static PaperBank importPapers(String[] paperFiles, PaperFilter filter, PaperBank pb) throws Exception{
        PaperBank.Builder builder = pb.toBuilder();
        for(String pf: paperFiles){
            PaperExtractor paperExtractor=new PaperExtractor(filter);
            Paper paper=paperExtractor.parsePaper(pf);
            builder.addPaper(paper);
        }
        return builder.build();
    }

    /**
     * 从试卷导入，试卷模板见2019A.doc和2019B.doc,为CUG的2014年模板
     * 如果其他学校的模板，需要在paper_filters.txt中调整需要剔除字符串模板
     * 所谓的提出字符串，指的是和试题无关的信息，如学校名称、装订线等
     * 最简单的试卷模板见paper_template.doc
     * 答案文件模板见answer_template.doc
     * @param paperFiles
     * @param answerFiles
     * @param filter
     * @param pb
     * @throws Exception
     */
    public static PaperBank importPapers(String[] paperFiles,String[] answerFiles, PaperFilter filter, PaperBank pb) throws Exception{
        PaperBank.Builder builder = pb.toBuilder();
        for(int i=0;i<paperFiles.length;++i){
            builder.addPaper(importPaper(paperFiles[i],answerFiles[i],filter));
        }
        return builder.build();
    }

    /**
     * 从试卷导入，试卷模板见2019A.doc,为CUG的2014年模板
     * 如果其他学校的模板，需要在paper_filters.txt中调整需要剔除字符串模板
     * 所谓的提出字符串，指的是和试题无关的信息，如学校名称、装订线等
     * 最简单的试卷模板见paper_template.doc
     * 答案文件模板见answer_template.doc
     * @param paperFile
     * @param answerFile
     * @param filter
     * @return
     * @throws Exception
     */
    public static Paper importPaper(String paperFile, String answerFile, PaperFilter filter) throws Exception{
        PaperExtractor paperExtractor=new PaperExtractor(filter);
        Paper paper=paperExtractor.parsePaper(paperFile);
        return importAnswer(paper,answerFile);
    }

    /**
     * 加载试卷库，从外部文件中加载,pbb
     * @param file
     * @return
     * @throws Exception
     */
    public static PaperBank load(String file) throws Exception{
        InputStream inputStream=new BufferedInputStream(new FileInputStream(file));
        int len = (int) new File(file).length();
        byte[] bs = new byte[len];
        inputStream.read(bs);
        PaperBank pb= PaperBank.parseFrom(bs);
        inputStream.close();
        return pb;
    }


    /**
     * 存储试题库，写入到外部文件,pbb格式
     * @param qb
     * @param file
     * @throws Exception
     */
    public static void store(PaperBank qb, String file) throws Exception{
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bs = qb.toByteArray();
        outputStream.write(bs);
        outputStream.close();
    }

    /**
     * 为已有的试卷匹配答案
     * @param paper
     * @param answerFile
     * @return
     * @throws Exception
     */
    public static Paper importAnswer(Paper paper, String answerFile) throws Exception{
        //TODO : 以 answer_template.doc为例
        return paper;
    }
}
