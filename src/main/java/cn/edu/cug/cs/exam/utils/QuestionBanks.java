package cn.edu.cug.cs.exam.utils;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.exam.io.PaperExtractor;
import cn.edu.cug.cs.exam.io.QuestionPrinter;
import cn.edu.cug.cs.gtl.protos.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.List;

public class QuestionBanks {

    /**
     * 从试卷库中导入题目
     * @param pb
     * @param qb
     * @return
     * @throws Exception
     */
    public static QuestionBank importFromPapers(PaperBank pb, QuestionBank qb) throws Exception{
        QuestionBank.Builder builder=qb.toBuilder();
        for(Paper paper: pb.getPaperList()){
            List<QuestionGroup> lqg=paper.getQuestionGroupList();
            builder.addAllQuestionGroup(lqg);
        }
        return builder.build();
    }

    /**
     * 从多份试卷中导入题目
     * @param papers
     * @param qb
     * @return
     * @throws Exception
     */
    public static QuestionBank importFromPapers(List<Paper> papers,  QuestionBank qb) throws Exception{
        QuestionBank.Builder builder=qb.toBuilder();
        for(Paper paper: papers){
            List<QuestionGroup> lqg=paper.getQuestionGroupList();
            builder.addAllQuestionGroup(lqg);
        }
        return builder.build();
    }

    /**
     * 将Paper对象中的试题导入试题库
     * @param paper
     * @param qb
     */
    public static QuestionBank importFromPaper(Paper paper, QuestionBank qb){
        List<QuestionGroup> lqg=paper.getQuestionGroupList();
        return qb.toBuilder().addAllQuestionGroup(lqg).build();
    }

    /**
     * 批量导出试题（按照超星题库的导入格式导出）
     * @param qb
     * @param file
     * @throws Exception
     */
    public static void exportToChaoXing(QuestionBank qb, String file)throws Exception{
        List<QuestionGroup> qgl=qb.getQuestionGroupList();
        List<Question> ql=null;
        int i=1;
        XWPFDocument xwpfDocument=new XWPFDocument();

        for(QuestionGroup qg: qgl){
            ql=qg.getQuestionList();
            for(Question q: ql){
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(i));
                sb.append("、");
                sb.append(QuestionPrinter.exportToChaoXing(q));
                sb.append("\n");
                xwpfDocument.createParagraph().createRun().setText(sb.toString());
                i++;
            }
        }
        try (FileOutputStream out = new FileOutputStream("file")) {
            xwpfDocument.write(out);
        }
    }

    //试题查询（按照题型、难易程度、知识点等查询）

    /**
     * 加载试题库，从外部文件中加载,qbb
     * @param file
     * @return
     * @throws Exception
     */
    public static QuestionBank load(String file) throws Exception{
        InputStream inputStream=new BufferedInputStream(new FileInputStream(file));
        return QuestionBank.parseFrom(inputStream);
    }


    /**
     * 存储试题库，写入到外部文件,qbb格式
     * @param qb
     * @param file
     * @throws Exception
     */
    public static void store(QuestionBank qb, String file) throws Exception{
       OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
       qb.writeTo(outputStream);
    }
    //按照规则生成试卷
    //去除重复题目

}
