/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.word.editor.core.Contents;
import org.word.editor.core.Template;
import org.word.editor.utilty.FileUtility.Cond;

/**
 *
 * @author xiao
 */
public class HtmlUtility {
    private static Logger logger=Logger.getLogger(HtmlUtility.class);
    static InputOutput io=IOProvider.getDefault().getIO("Console", false);
    //根据覆盖信息List<CovStruct>和被测源文件生成html报告----   EC-MC/DC
    //<用特殊字符&lt;来代替，防止在解析的时候出错。
    /**
     * 
     * @param covs 经过处理之后的插桩信息结构
     * @param sourcePath　覆盖分析的源文件的路径
     * @param covPath　着色显示等动作之后　生成的ｈｔｍｌ覆盖分析报告路径
     * @return 
     */
    public static File generateHtml(Map<Integer,Map<Integer,Cond[]>> covs,String sourcePath,String covPath){
        if(covs.size()==0){//未生成覆盖信息
            logger.info("No coverlay analysis file generated!");
            io.getOut().println("No coverlay analysis file generated!");
        }
        File covHtml=new File(covPath);
//        StringBuilder sb=new StringBuilder("<html><body>");
//        sb.append("<pre name=\"code\" class=\"cpp\">");//该标签会保留定义的缩进信息等
        BufferedReader br=null;
        FileWriter fw=null;
        int currentLine=0;
        int listIndex=0;//遍历的List的当前的index
        try {
            fw=new FileWriter(covHtml);
            String head="<html>\n" +
                            "<head>\n" +
            "<link href=\"http://cdn.bootcss.com/highlight.js/8.0/styles/default.min.css\" rel=\"stylesheet\">\n" +
            "</head>\n" +
            "<body><pre><code class=\"cpp\">";
            fw.write(head);//pre标签保留代码的缩进信息等
            br=new BufferedReader(new FileReader(new File(sourcePath)));//读取源代码信息
            String line="";
            while((line=br.readLine())!=null){
                currentLine++;
                String parts[]=split(line,"&&|\\|\\|");//原来的语句的分割,并且带有分割符
                if(covs.containsKey(currentLine)){ //包含有该行的覆盖信息
                    line="";
                    Map<Integer, Cond[]> map = covs.get(currentLine);//获得该行的覆盖信息
                    for(int i=0;i<parts.length;i++){
                        System.out.println(parts[i]);
                        if(map.containsKey(i+1)){//第i+1个表达式的值
                            Cond[] condition=map.get(i+1);
                            String relational="";
                            String less="/",eq="/",more="/";//初始化三种关系的达到情况
                            if(condition[0].result){//小于关系是否达到
                                relational=condition[0].condition;
                                less="<";
                            }
                            if(condition[1].result){//等于关系是否达到
                                relational=condition[1].condition;
                                eq="=";
                            }
                            if(condition[2].result){//大于关系是否达到
                                relational=condition[2].condition;
                                more=">";
                            }
                            //===三种关系达到的程度 一种8种关系
                            //===end 三种关系
                            String originRel=relational;
                            relational=relational.replace("<", "&lt;");//替换尖括号后的条件表达式
                            //<a onmouseover="{<,/,>}" style="background:red">    </a>
                            String result="{"+less+","+eq+","+more+"}\"";//{<,=,>}
                            StringBuilder tmp=new StringBuilder("<span title=\"");
                            tmp.append(result).append(" style=\"background:#7CCD7C\">");
                            tmp.append(relational).append("</span>");
                            //将parts中的条件表达式　替换成着色并加入动作之后的String
                            parts[i]=parts[i].replace(originRel, 
                            tmp.toString());
                        }else{//不含有该条件的覆盖信息，只做替换尖括号的行为
                            parts[i]=parts[i].replaceAll("<", "&lt;");
                        }
                        line+=parts[i];
                    }//end -for 循环各个条件
                }else{//不含有该行的覆盖信息
                    line=line.replaceAll("<", "&lt;");
                    String trim=line.trim();
                    line=line.replace(trim, "<span>"+trim+"</span>");
                    //line="<span>"+line+"</span>";
                }
                //System.out.println(line);
                //sb.append(line+"\n");
                fw.write(line+"\n");
            }//文件读完了
            String tail="</code>\n" +
                "</pre>\n" +
                "<script src=\"http://cdn.bootcss.com/highlight.js/8.0/highlight.min.js\"></script>\n" +
                "<script >hljs.initHighlightingOnLoad();</script>  \n" +
                "</body></html>";
            fw.write(tail);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }finally{
            try {
                br.close();
                fw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        //sb.append("if(<strong style=\"background:red\">a>b</strong>)");
//        sb.append("</pre></body></html>");\
         
        return covHtml;
    }
    /*带有分割符的分割字符串，防止在分割之后 丢掉了分割字符  */
    /*正则表达式：句子结束符*/
    // String regEx=“&&|\\|\\|”;
    public  static String[] split(String str,String regEx){
        Pattern p =Pattern.compile(regEx);
        Matcher m = p.matcher(str);

	/*按照句子结束符分割句子*/
        String[] words = p.split(str);

	/*将句子结束符连接到相应的句子后*/
        if(words.length > 0)
        {
            int count = 0;
            while(count < words.length)
            {
                if(m.find())
                {
                    //System.out.println("group= "+m.group());
                    words[count] += m.group();
                }
                count++;
            }
        }
        return words;
    }
    /*
    生成有关语句覆盖分析的着色文件
    */
    public static String statementCov(String covPath){
        File file=new File(covPath);
        BufferedReader br=null;
        StringBuilder sb=new StringBuilder("<html><body>");
        sb.append("<pre name=\"code\" class=\"cpp\">");
        try {
            br=new BufferedReader(new FileReader(file));
            String line="";
            while((line=br.readLine())!=null){
                String[] parts=line.split(":",3);//使用：分割，并且最多分成3部分，不能把语句部分拆分了
                if(!parts[1].trim().equals("0")){//不是0表示是代码行，需要显示
                    parts[2]=parts[2].replaceAll("<", "&lt;");
                    if(parts[0].trim().equals("#####")||parts[0].trim().equals("-")){//
                        //未达到的语句
//                        if(Contents.Cov_Flag.equals(Contents.PATH)&&){
//                            
//                        }
//                        sb.append(parts[2]+"\n");
                          sb.append("<strong style=\"background:#FFC0CB\">"+parts[2]+"</strong>\n");
                    }else{//测试用例达到的语句
                        for(int i=0;i<parts[2].length();i++){//防止空白区域着色
                            if(parts[2].charAt(i)==' '){//等于空白
                                sb.append(" ");
                            }else if(parts[2].charAt(i)=='\t'){
                                sb.append("\t");
                            }else{
                                break;
                            }
                        }//end-for
                        sb.append("<strong style=\"background:#7CCD7C\">"+parts[2].trim()+"</strong>\n");
//                          sb.append(parts[2]+"\n");
                    }
                }
               // sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sb.append("</pre></body></html>");
        return sb.toString();
    }
    
    /*
    生成分支覆盖分析的着色文件
    */
    public static String branchCov(Map<Integer,Cond[]> maps,String source,Template template){
        File file=new File(source);
        StringBuilder sb=new StringBuilder("<table cellpadding=0 cellspacing=0 border=0>\n" +
"    <tr>\n" +
"      <td><br></td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td>");
        sb.append("<pre class=\"sourceHeading\">          Line data    Source code</pre>\n" +
"<pre class=\"source\">\n<code>\n<a name=\"1\">");
        BufferedReader br=null;
        int hit=0;
        int part=0;
        try {
            br=new BufferedReader(new FileReader(file));
            String line="";
            int currentLine=0;
            while((line=br.readLine())!=null){
                currentLine++;
                sb.append("<span class=\"lineNum\">       "+currentLine+" </span>  :  ");//行号
                if(maps.containsKey(currentLine)){//该行有覆盖信息
                    Cond[] conds=maps.get(currentLine);
                    String branch="";
                    boolean trueBranch=false;//查看分支的真分支的覆盖情况
                    boolean falseBranch=false;//查看假分支的覆盖情况
                    if(conds[0].result){
                        branch=conds[0].condition;
                        falseBranch=true;
                    }
                    if(conds[1].result){
                        branch=conds[1].condition;
                        trueBranch=true;
                    }
                    String color="";
                    String tip="";
                    if(trueBranch&&falseBranch){//真假分支都达到了,着色为绿色
                        //color="<span class=\"lineCov\""
                        color="#7CCD7C";
                        tip="{T,F}";
                        hit++;
                    }else if(trueBranch){//只有真分支达到了,着色为黄色
                        color="#FFC0CB";
                        tip="{T,/}";
                        part++;
                    }else{//只有假分支达到了，着色为蓝色
                        color="#FFC0CB";
                        tip="{/,F}";
                        part++;
                    }
                    int index=line.indexOf(branch);
                    String pre=line.substring(0,index);
                    String post=line.substring(index+branch.length(),line.length());
                    branch=branch.replaceAll("<", "&lt;");
                    //sb.append(pre).append("<strong style=\"background:").append(color).append(sb)
                    sb.append(pre+"<span title=\""+tip+"\" style=\"background:"+color+"\">"+branch+"</span>"+post+"\n");
                }else{
                    line=line.replaceAll("<", "&lt;");
                    String trim=line.trim();
                    line=line.replace(trim, "<span>"+trim+"</span>\n");
                    sb.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sb.append("</code>\n</pre>\n" +
"      </td>\n" +
"    </tr>\n" +
"  </table>\n" +
"  <br>\n");
        template.setHit(hit*2+part);//hit×2+part是对分支覆盖的部分
        template.setPart(part);//只覆盖了真或者假分支
        return sb.toString();
    }
    /*
    生成条件覆盖的着色文件
    */
    public static String conditionCov(Map<Integer,Map<Integer,Cond[]>> covs,String source,Template template){
        StringBuilder sb=new StringBuilder("<table cellpadding=0 cellspacing=0 border=0>\n" +
"    <tr>\n" +
"      <td><br></td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td>");
        sb.append("<pre class=\"sourceHeading\">          Line data    Source code</pre>\n" +
"<pre class=\"source\">\n<code>\n<a name=\"1\">");
        BufferedReader br=null;
        int hit=0;
        int part=0;
        try {
            int currentLine=0;
            br=new BufferedReader(new FileReader(new File(source)));
            String line="";
            while((line=br.readLine())!=null){
                currentLine++;
                sb.append("<span class=\"lineNum\">       "+currentLine+" </span>  :  ");//行号
                String[] parts=split(line, "&&|\\|\\|");
                if(covs.containsKey(currentLine)){//含有该行的覆盖信息
                    Map<Integer,Cond[]> map=covs.get(currentLine);
                    for(int i=0;i<parts.length;i++){//对每一个条件的处理
                        if(map.containsKey(i+1)){//含有该条件的覆盖信息
                            Cond[] conds=map.get(i+1);
                            boolean tbranch=false;
                            boolean fbranch=false;
                            String condition="";
                            if(conds[0].result){//收集达到真假分支的情况
                                tbranch=true;
                                condition=conds[0].condition;
                            }
                            if(conds[1].result){
                                fbranch=true;
                                condition=conds[1].condition;
                            }
                            String color="";
                            String tip="";
                            if(tbranch&&fbranch){//真/假分支都达到了
                                color="#7CCD7C";
                                tip="Full Coverage:{T,F}";
                                hit++;
                            }else if(tbranch){//
                                color="#FFC0CB";
                                tip="Partial Coverage:{T,/}";
                                part++;
                            }else{
                                color="#FFC0CB";
                                tip="Partial Coverage:{/,F}";
                                part++;
                            }
                            String after=condition.replaceAll("<", "&lt;");
                            parts[i]=parts[i].replace(condition, 
                             "<span title=\""+tip+"\" style=\"background:"+color+"\">"+after+"</span>");
                        }else{//不含有该条件的覆盖信息
//                            parts[i]=parts[i].replace(condition, 
//                             "<span title=\""+tip+"\" style=\"background:"+color+"\">"+after+"</span>");
                            parts[i]=parts[i].replaceAll("<", "&lt;");
                        }
                        sb.append(parts[i]);
                    }//end-for 对每一个条件的处理
                    sb.append("\n");
                }else{//不含有该行的覆盖信息
//                    sb.append(line.replaceAll("<", "&lt;")+"\n");
                    line=line.replaceAll("<", "&lt;");
                    String trim=line.trim();
                    line=line.replace(trim, "<span>"+trim+"</span>");
                    sb.append(line+"\n");
                }
            }//end -while
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sb.append("</code>\n</pre>\n" +
"      </td>\n" +
"    </tr>\n" +
"  </table>\n" +
"  <br>\n");
        template.setHit(hit);
        template.setPart(part);
        return sb.toString();
    }
    
    public static String branchCondCov(Map<Integer,Map<Integer,List<CovStruct>>> branchsMap,
            Map<Integer,Map<Integer,List<CovStruct>>> condsMap,String source,Template template){
        //-----获得每个条件覆盖了多少
        //存放每一个条件的结果,key表示该分支中的第几个表达式
        Map<Integer, Map<Integer, Cond[]>> condsResultMap = FileUtility.convertToCondition(FileUtility.getCovStruct(Contents.USER_DIR__STRING+"/branch_condition/out_cond.txt"));
        //--------
        
        StringBuilder sb=new StringBuilder("<table cellpadding=0 cellspacing=0 border=0>\n" +
"    <tr>\n" +
"      <td><br></td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td>");
        sb.append("<pre class=\"sourceHeading\">          Line data    Source code</pre>\n" +
"<pre class=\"source\">\n<code>\n<a name=\"1\">");
        
        BufferedReader br=null;
        int hit=0;
        int part=0;
        int currentLine=0;
        try {
            br=new BufferedReader(new FileReader(new File(source)));//读取被测源文件
            String line="";
            while((line=br.readLine())!=null){
                currentLine++;
                sb.append("<span class=\"lineNum\">       "+currentLine+" </span>  :  ");//行号
                if(branchsMap.containsKey(currentLine)&&condsMap.containsKey(currentLine)){//均存在该行的覆盖信息
                    String[] parts=split(line, "&&|\\|\\|");
                    Map<Integer,List<CovStruct>> branchResult=branchsMap.get(currentLine);
                    Map<Integer,List<CovStruct>> condsResult=condsMap.get(currentLine);
                    Set<String> sets=new HashSet<String>();//去重用例结果
                    Set<Integer> bResult=new HashSet<>();
                    for(Integer key:branchResult.keySet()){//一个用例一个用例的进行迭代
                        List<CovStruct> result1=branchResult.get(key);
                        List<CovStruct> result2=condsResult.get(key);//
                        StringBuilder sbResult=new StringBuilder("[");//组合条件的结果用大括号括起来
                        for(int i=1;i<=parts.length;i++){//对分支中的每一个条件判断
                            boolean flag=false;//是否找到该条件的标志
                            for(CovStruct struct:result2){//遍历结果列表
                                if(struct.cs==i){//找到了序号相同的条件
                                    flag=true;
                                    if(struct.result==0) sbResult.append("F");
                                    else sbResult.append("T");
                                    break;
                                }
                            }//end-struct-for
                            if(!flag){//未找到
                               sbResult.append("/");
                            }
                            if(i==parts.length){
                                sbResult.append("="+result1.get(0).result+"]");
                                bResult.add(result1.get(0).result);
                            }
                            else sbResult.append(",");
                        }//条件判断结束
                        //一个用例的执行处理结束
                        sets.add(sbResult.toString());
                    }//end-对所有的用例的执行结果处理结束
                    //对分支覆盖 中的hit part记录
                    if(bResult.size()==2) hit++;
                    else part++;
                    StringBuilder title=new StringBuilder();
                    int k=0;
                    for(String s:sets){
                        k++;
                        title.append(s);
                        if(k!=sets.size()) title.append(",");
                    }
                    //对每个条件进行着色 并且显示帮助tip信息
                    Map<Integer, Cond[]> map = condsResultMap.get(currentLine);
                    for(int i=0;i<parts.length;i++){//对每一个条件的处理
                        if(map.containsKey(i+1)){//含有该条件的覆盖信息
                            Cond[] conds=map.get(i+1);
                            boolean tbranch=false;
                            boolean fbranch=false;
                            String condition="";
                            if(conds[0].result){//收集达到真假分支的情况
                                tbranch=true;
                                condition=conds[0].condition;
                            }
                            if(conds[1].result){
                                fbranch=true;
                                condition=conds[1].condition;
                            }
                            String color="";
                            String tip="";
                            if(tbranch&&fbranch){//真/假分支都达到了
                                color="#7CCD7C";
                                tip="Full Coverage: ";
                                hit++;
                            }else if(tbranch){//
                                color="#FFC0CB";
                                tip="Partial Coverage: ";
                                part++;
                            }else{
                                color="#FFC0CB";
                                tip="Partial Coverage: ";
                                part++;
                            }
                            String after=condition.replaceAll("<", "&lt;");
                            parts[i]=parts[i].replace(condition, 
                             "<span title=\""+tip+title+"\" style=\"background:"+color+"\">"+after+"</span>");
                        }else{//不含有该条件的覆盖信息
//                            parts[i]=parts[i].replace(condition, 
//                             "<span title=\""+tip+"\" style=\"background:"+color+"\">"+after+"</span>");
                            parts[i]=parts[i].replaceAll("<", "&lt;");
                        }
                        sb.append(parts[i]);
                    }//end-for 对每一个条件的处理
                    sb.append("\n");//该行处理结束，
                    //------------------------
                }else{//没有该行的覆盖信息
                    line=line.replaceAll("<", "&lt;");
                    String trim=line.trim();
                    line=line.replace(trim, "<span>"+trim+"</span>");
                    sb.append(line+"\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sb.append("</code>\n</pre>\n" +
"      </td>\n" +
"    </tr>\n" +
"  </table>\n" +
"  <br>\n");
        template.setHit(hit);
        template.setPart(part);
        return sb.toString();
    }//end-branchCondCov
    
    /*
    生成MC/DC的覆盖分析文件，对每个条件生成逆波兰表达式，在每个条件标记上各个case的结果；
    并且生成表达式树，树的每个节点上标记上各个条件的正确错误
    */
    public static void mcdcCov(Map<Integer,Map<Integer,List<CovStruct>>> map,String source,Template template){
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(new File(source)));
            String line="";
            int currentLine=0;
            while((line=br.readLine())!=null){
                currentLine++;
               // System.out.println("==========: "+line);
                if(map.containsKey(currentLine)){//含有该行的覆盖信息
                    System.out.println("Contains: "+line);
                    int f=line.indexOf("(");//找到第一个（，确定整个分支的结果
                    int l=line.lastIndexOf(")");
                    String exp=line.substring(f+1,l);
                    System.out.println("EXP== "+exp);
                    List<String> postExp=Expression.getReverseExp(exp);
                    for(String s:postExp){
                        System.out.println(s);
                    }
                        //key是条件在该表达式中的编号，从1 开始,List 中1表示true 2表示任意值
                    Map<Integer, List<Integer>> boolMap = Expression.getBoolList(exp, map.get(currentLine));
                    TreeNode root=Expression.getExpTree(postExp, boolMap);
                    Expression.traverseTree(root);
                    
                    
//                    for(Integer key:boolMap.keySet()){
//                        List<Integer> list=boolMap.get(key);
//                        System.out.println("------- :"+key);
//                        for(Integer i:list){
//                            System.out.print(i+" ");
//                        }
//                        System.out.println();
//                    }
                }else {
                    System.out.println("Not Contains： "+currentLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
/*
<html>
<body>
<div><pre name="code" class="cpp">#include &lt;stdio.h&gt;

void swap(int *a, int *b); //交换两个数

int main()
{
	int     str[10];
	int     i, j;
	//初始化数组为10 9 8 7 6 5 4 3 2 1
	for (i = 0; i &lt; 10; i++)
	{
		str[i] = 10 - i;
	}
	//排序，从a[0]开始排，从小到大
	for (i = 0; i &lt; 10; i++)
	{
		for (j = i + 1; j &lt; 10; j++)
		{
			if (str[i] &gt; str[j])
			{
				swap(&amp;str[i], &amp;str[j]);
			}
		}
	}
        //将十个数输出
	for (i = 0; i &lt; 10; i++)
		printf(&quot;%d\n&quot;, str[i]);
	return    0;
}
void swap(int *a, int *b)
{
	int     c;
	 c = *a;
	*a = *b;
	*b =  c;
}</pre>
<div>
</body>
</html>
*/
