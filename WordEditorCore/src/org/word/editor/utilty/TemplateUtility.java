/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import org.openide.util.Exceptions;
import org.word.editor.core.Template;

/**
 *
 * @author xiao
 * 用于生成html的模板类
 */
public class TemplateUtility {
    static DecimalFormat df=new DecimalFormat("0.00");
    /*
    不需要用返回的文件句柄，只需要路径即可
    */
    public static void branchCov(Template template,String outPath,String code){
        File outFile=new File(outPath);
        FileWriter fw=null;
        String cov=df.format(template.getHit()*1.0/template.getTotal()*100);
        String pcov=df.format(template.getPart()*1.0/template.getTotal()*100);
        try {
            fw=new FileWriter(outFile);
            String head="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"\n" +
"<html lang=\"en\">\n" +
"\n" +
"<head>\n" +
"  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
"  <title></title>\n" +
"  <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/gcov.css\">\n" +
"  <link href=\"http://cdn.bootcss.com/highlight.js/8.0/styles/default.min.css\" rel=\"stylesheet\">\n"+
"</head>\n"; //到head段
            String body="<body>\n" +  //到body段
"\n" +
"  <table width=\"100%\" border=0 cellspacing=0 cellpadding=0>\n" +
"    <tr><td class=\"title\">"+template.getTitle()+"</td></tr>\n" +
"    <tr><td class=\"ruler\"><img src=\"./css/glass.png\" width=3 height=3 alt=\"\"></td></tr>\n" +
"\n" +
"    <tr>\n" +
"      <td width=\"100%\">\n" +
"        <table cellpadding=1 border=0 width=\"100%\">\n" +
"          <tr>\n" +
"            <td width=\"10%\" class=\"headerItem\">Current view:</td>\n" +
"            <td width=\"35%\" class=\"headerValue\">"+template.getTestPath()+"</td>\n" +
"            <td width=\"5%\"></td>\n" +
"            <td width=\"15%\"></td>\n" +
"            <td width=\"10%\" class=\"headerCovTableHead\">Covered</td>\n" +
"            <td width=\"10%\" class=\"headerCovTableHead\">Total</td>\n" +
"            <td width=\"15%\" class=\"headerCovTableHead\">Coverage</td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td class=\"headerItem\">Test:</td>\n" +
"            <td class=\"headerValue\">"+template.getCriterion()+"</td>\n" +
"            <td></td>\n" +
"            <td class=\"headerItem\">"+template.getCriterion()+"</td>\n" +
"            <td class=\"headerCovTableEntry\">"+template.getHit()+"</td>\n" +
"            <td class=\"headerCovTableEntry\">"+template.getTotal()+"</td>\n" +
"            <td class=\"headerCovTableEntryMed\">"+cov+"%</td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td class=\"headerItem\">Date:</td>\n" +
"            <td class=\"headerValue\">"+template.getDate()+"</td>\n" +
"            <td></td>\n" +
"            <td class=\"headerItem\">Partial:</td>\n" +//部分覆盖
"            <td class=\"headerCovTableEntry\">"+template.getPart()+"</td>\n" +
"            <td class=\"headerCovTableEntry\">"+template.getTotal()+"</td>\n" +
"            <td class=\"headerCovTableEntryHi\">"+pcov+"%</td>\n" +
"          </tr>\n" +
"          <tr><td><img src=\"./css/glass.png\" width=3 height=3 alt=\"\"></td></tr>\n" +
"        </table>\n" +
"      </td>\n" +
"    </tr>\n" +
"\n" +
"    <tr><td class=\"ruler\"><img src=\"./css/glass.png\" width=3 height=3 alt=\"\"></td></tr>\n" +
"  </table>\n";
            
            fw.write(head);
            fw.write(body);
            fw.write(code);
            
            fw.write("<table width=\"100%\" border=0 cellspacing=0 cellpadding=0>\n" +
"    <tr><td class=\"ruler\"><img src=\"./css/glass.png\" width=3 height=3 alt=\"\"></td></tr>\n" +
"  </table>\n" +
"  <br>\n" +
"\n" +
"<script src=\"http://cdn.bootcss.com/highlight.js/8.0/highlight.min.js\"></script>\n" +
"<script >hljs.initHighlightingOnLoad();</script>  \n" +
"</body>\n" +
"</html>");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                fw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    
}
