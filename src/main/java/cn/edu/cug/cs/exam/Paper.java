package cn.edu.cug.cs.exam;

import cn.edu.cug.cs.gtl.protos.Identifier;

import java.util.Date;

/**
 * 试卷基类
 */
public class Paper {
    private Identifier identifier;//试卷库中的试卷唯一编号
    private String name;//试卷名称
    private Date   date;//试卷时间
    private String file;//试卷文件名，可以是DOC或PDF文件

}
