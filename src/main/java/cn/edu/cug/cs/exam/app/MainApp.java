package cn.edu.cug.cs.exam.app;

import cn.edu.cug.cs.exam.filters.PaperFilter;
import cn.edu.cug.cs.exam.io.AnswerExtractor;
import cn.edu.cug.cs.exam.utils.PaperBanks;
import cn.edu.cug.cs.exam.utils.QuestionBanks;
import cn.edu.cug.cs.gtl.protos.Paper;
import cn.edu.cug.cs.gtl.protos.PaperBank;
import cn.edu.cug.cs.gtl.protos.QuestionBank;
import cn.edu.cug.cs.gtl.protos.QuestionType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class MainApp extends JFrame implements ActionListener {

    public static void main(String[] args){
        MainApp mainApp = new MainApp();
        mainApp.setSize(800,600);
        mainApp.show();
    }

    private JMenuBar menuBar=new JMenuBar();
    private JMenu menuQuestionBank=new JMenu("试题库");
    private String  szMenuItemOpenQuestionBank="打开试题库...";
    private String  szMenuItemSaveQuestionBank="保存试题库...";
    private String  szMenuItemExportChaoXing="导出超星格式...";
    private JMenuItem menuItemOpenQuestionBank=new JMenuItem(szMenuItemOpenQuestionBank);
    private JMenuItem menuItemSaveQuestionBank=new JMenuItem(szMenuItemSaveQuestionBank);
    private JMenuItem menuItemExportChaoXing=new JMenuItem(szMenuItemExportChaoXing);
    private JMenu menuPaperBank=new JMenu("试卷库");
    private String szMenuItemOpenPaperBank="打开试卷库...";
    private String szMenuItemSavePaperBank="保存试卷库...";
    private String szMenuItemImportPaper="导入试卷...";
    private JMenuItem menuItemOpenPaperBank=new JMenuItem(szMenuItemOpenPaperBank);
    private JMenuItem menuItemSavePaperBank=new JMenuItem(szMenuItemSavePaperBank);
    private JMenuItem menuItemImportPaper=new JMenuItem(szMenuItemImportPaper);
    private PaperBank paperBank=null;
    private QuestionBank questionBank=null;


    private PaperFilter paperFilter=null;


    public MainApp() throws HeadlessException {
        super();
        initializeCustomUI();
        initializeConfigure();
    }

    public MainApp(GraphicsConfiguration gc) {
        super(gc);
        initializeCustomUI();
        initializeConfigure();
    }

    public MainApp(String title) throws HeadlessException {
        super(title);
        initializeCustomUI();
        initializeConfigure();
    }

    public MainApp(String title, GraphicsConfiguration gc) {
        super(title, gc);
        initializeCustomUI();
        initializeConfigure();
    }

    private void initializeCustomUI(){
        this.setJMenuBar(menuBar);
        menuQuestionBank.add(menuItemOpenQuestionBank);
        menuQuestionBank.add(menuItemSaveQuestionBank);
        menuQuestionBank.add(menuItemExportChaoXing);
        menuBar.add(menuQuestionBank);
        menuPaperBank.add(menuItemOpenPaperBank);
        menuPaperBank.add(menuItemSavePaperBank);
        menuPaperBank.add(menuItemImportPaper);
        menuBar.add(menuPaperBank);

        menuItemExportChaoXing.addActionListener(this);
        menuItemOpenQuestionBank.addActionListener(this);
        menuItemSaveQuestionBank.addActionListener(this);
        menuItemOpenPaperBank.addActionListener(this);
        menuItemSavePaperBank.addActionListener(this);
        menuItemImportPaper.addActionListener(this);
    }

    private void initializeConfigure(){
        String filterFile = "dat/paper_filters.txt";
        String scFilterFile = "dat/singlechoice_filters.txt";
        String mcFilterFile = "dat/multichoice_filters.txt";
        String bfFilterFile = "dat/blankfilling_filters.txt";
        String tfFilterFile = "dat/truefalse_filters.txt";
        String saFilterFile = "dat/shortanswer_filters.txt";
        String psFilterFile = "dat/problemsolving_filters.txt";
        String syFilterFile = "dat/synthesized_filters.txt";
        String answerFilterFile = "dat/answer_filters.txt";

        try {
            paperFilter= new PaperFilter(filterFile);
            paperFilter.addQuestionFilter(scFilterFile, QuestionType.QT_SINGLE_CHOICE);
            paperFilter.addQuestionFilter(mcFilterFile, QuestionType.QT_MULTI_CHOICE);
            paperFilter.addQuestionFilter(bfFilterFile, QuestionType.QT_BLANK_FILLING);
            paperFilter.addQuestionFilter(tfFilterFile, QuestionType.QT_TRUE_FALSE);
            paperFilter.addQuestionFilter(saFilterFile, QuestionType.QT_SHORT_ANSWER);
            paperFilter.addQuestionFilter(psFilterFile, QuestionType.QT_PROBLEM_SOLVING);
            paperFilter.addQuestionFilter(syFilterFile, QuestionType.QT_SYNTHESIZED);
            paperFilter.setAnswerFilter(answerFilterFile);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public PaperBank getPaperBank() {
        return paperBank;
    }

    public void setPaperBank(PaperBank paperBank) {
        this.paperBank = paperBank;
    }

    public PaperBank.Builder getPaperBankBuilder() {
        if(questionBank==null)
            return PaperBank.newBuilder();
        else
            return paperBank.toBuilder();
    }

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public QuestionBank.Builder getQuestionBankBuilder() {
        if(questionBank==null)
            return QuestionBank.newBuilder();
        else
            return questionBank.toBuilder();
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }


    public PaperFilter getPaperFilter() {
        return paperFilter;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //菜单响应
        if(actionEvent.getSource() instanceof JMenuItem){
            if(actionEvent.getActionCommand().equals(szMenuItemImportPaper)) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("试卷文件","doc"));
                int r= jFileChooser.showOpenDialog(this);
                String paperFile=null;
                if(r==JFileChooser.APPROVE_OPTION){
                    paperFile=jFileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println(paperFile);
                }
                else
                    return;

                jFileChooser.setFileFilter(new FileNameExtensionFilter("答案文件","doc"));
                r= jFileChooser.showOpenDialog(this);
                String answerFile=null;
                if(r==JFileChooser.APPROVE_OPTION){
                    answerFile=jFileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println(paperFile);
                }
                else
                    return;

                if(answerFile!=null && paperFile!=null){
                    try{
                        Paper paper = PaperBanks.importPaper(paperFile,answerFile,getPaperFilter());
                        PaperBank.Builder b1 = getPaperBankBuilder();
                        this.paperBank = b1.addPaper(paper).build();
                        this.questionBank=QuestionBanks.importFromPaper(paper,this.questionBank);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else if(actionEvent.getActionCommand().equals(szMenuItemExportChaoXing)) {
                if (this.questionBank != null) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("超星试题", "docx"));
                    int r = jFileChooser.showSaveDialog(this);
                    if(r==JFileChooser.APPROVE_OPTION){
                        File f = jFileChooser.getSelectedFile();
                        String szChaoXingFile=f.getAbsolutePath();
                        if(szChaoXingFile.endsWith("docx")==false)
                            szChaoXingFile+=".docx";

                        try{
                            QuestionBanks.exportToChaoXing(getQuestionBank(),szChaoXingFile);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this,"试题库为空!请先通过试卷库导入一些试卷！");
                }
            }
            else if(actionEvent.getActionCommand().equals(szMenuItemSaveQuestionBank)) {
                if (this.questionBank != null) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("试题库", "qbx"));
                    int r = jFileChooser.showSaveDialog(this);
                    if(r==JFileChooser.APPROVE_OPTION){
                        File f = jFileChooser.getSelectedFile();
                        String szFile=f.getAbsolutePath();
                        if(szFile.endsWith("qbx")==false)
                            szFile+=".qbx";

                        try{
                            QuestionBanks.store(getQuestionBank(),szFile);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this,"试题库为空!请先通过试卷库导入一些试卷，或者加载试题库文件！");
                }
            }
            else if(actionEvent.getActionCommand().equals(szMenuItemSavePaperBank)) {
                if (this.getPaperBank() != null) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("试卷库", "pbx"));
                    int r = jFileChooser.showSaveDialog(this);
                    if(r==JFileChooser.APPROVE_OPTION){
                        File f = jFileChooser.getSelectedFile();
                        String szFile=f.getAbsolutePath();
                        if(szFile.endsWith("pbx")==false)
                            szFile+=".pbx";

                        try{
                            PaperBanks.store(getPaperBank(),szFile);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this,"试卷库为空!请先通过试卷库导入一些试卷，或者加载试卷库文件，或从试题库中生成试卷！");
                }
            }
            else if(actionEvent.getActionCommand().equals(szMenuItemOpenPaperBank)) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("试卷库文件","pbx"));
                int r= jFileChooser.showOpenDialog(this);
                String szFile=null;
                if(r==JFileChooser.APPROVE_OPTION){
                    szFile=jFileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println(szFile);
                    try{
                        setPaperBank(PaperBanks.load(szFile));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                    return;
            }
            else if(actionEvent.getActionCommand().equals(szMenuItemOpenQuestionBank)) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("试题库文件","qbx"));
                int r= jFileChooser.showOpenDialog(this);
                String szFile=null;
                if(r==JFileChooser.APPROVE_OPTION){
                    szFile=jFileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println(szFile);
                    try{
                        setQuestionBank(QuestionBanks.load(szFile));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                    return;
            }
        }

    }
}
