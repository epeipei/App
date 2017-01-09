/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.word.editor.core.Contents;
import org.word.editor.core.HtmlViewTopComponent;
import org.word.editor.core.Template;
import org.word.editor.core.WebViewTopComponent;
import org.word.editor.utilty.CovStruct;
import org.word.editor.utilty.FileUtility;
import org.word.editor.utilty.FileUtility.Cond;
import org.word.editor.utilty.HtmlUtility;
import org.word.editor.utilty.LocalExec;
import org.word.editor.utilty.MCDCUtility;
import org.word.editor.utilty.TemplateUtility;

@ActionID(
        category = "Debug",
        id = "org.word.editor.toolbar.RunAction"
)
@ActionRegistration(
        iconBase = "org/word/editor/toolbar/Run_Control_24px_534174_easyicon.net.png",
        displayName = "#CTL_RunAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/Build", position = 325),
    @ActionReference(path = "Shortcuts", name = "F5")
})
@Messages("CTL_RunAction=运行")
public final class RunAction implements ActionListener {
    static Logger logger=Logger.getLogger(RunAction.class);
    public static  File file=null;//被测源文件，与List一起提取覆盖信息
    static InputOutput io=IOProvider.getDefault().getIO("Console",false);
    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("org.word.editor.toolbar.RunAction.actionPerformed()");
        io.getOut().println("org.word.editor.toolbar.RunAction.actionPerformed()");
        if(file==null){//没有被测文件，提示用户需要选择
            JOptionPane.showMessageDialog(null, "请选择被测文件!");
            return ;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                        LocalExec localExec=null;//命令的执行的路径，确定了不同的覆盖标准，在不同的路径下执行命令
        String out="";//覆盖信息的输出路径
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//只有文件的名字无后缀
        //先执行build操作
        //根据选择的覆盖标准 选择到对应的目录，目前先选择到condition
        if(Contents.Cov_Flag.equals(Contents.STATEMENT)){//语句覆盖
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/statement");
            out=Contents.USER_DIR__STRING+"/statement/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                //使用用例 运行结束，使用gcov进行分析

                localExec.exe("gcov "+file.getName());
                //尝试使用lcov工具生成覆盖报告
                localExec.exe("lcov -c -o "+simpleName+".info -d .");
                localExec.exe("genhtml "+simpleName+".info -o result");
                localExec.exe("sed -i \"s/LCOV/Statement/g\" `grep LCOV -rl .`");//将LCOV替换成Statement
                //localExec.exe("sed -i \"s/Generated/Statement/g\" `grep LCOV -rl .`");
                localExec.exe("sed -i '/versionInfo/d' `grep versionInfo *.html -rl .`");//删除带有版本的行
//                String gcov=Contents.USER_DIR__STRING+"/statement/"+simpleName+".c.gcov";//生成的覆盖文件
                //显示覆盖文件使用htmlView
//                HtmlViewTopComponent tp=new HtmlViewTopComponent();
//                tp.setName("Coverage Report For "+file.getName());
//                tp.setText(HtmlUtility.statementCov(gcov));
//                tp.open();
//                tp.requestActive();

//              使用WebView
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WebViewTopComponent tp1=new WebViewTopComponent("file:"+Contents.USER_DIR__STRING+"/statement/result/index.html");
                        tp1.open();
                        tp1.requestActive();
                    }
                });

            }
        }else if(Contents.Cov_Flag.equals(Contents.BRANCH)){//分支覆盖
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch");
            out=Contents.USER_DIR__STRING+"/branch/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                List<CovStruct> covStructs=FileUtility.getBranchStructs(out);
//                for(CovStruct struct:covStructs){
//                    System.out.println(struct.line+" "+struct.result+" "+struct.condition);
//                }
                Map<Integer,FileUtility.Cond[]> maps=FileUtility.convertToBranch(covStructs);
                
                for(Integer key:maps.keySet()){
                    Cond[] conds=maps.get(key);
                    System.out.println(key+" "+conds[0].result+" "+conds[1].result);
                }
                Template template=new Template("Branch Coverage Report",file.getAbsolutePath(),"Branch Info");
                int total=getTotalInfo(Contents.USER_DIR__STRING+"/branch/"+simpleName+".tmp.c")*2;//一个if包好两个分支
                template.setTotal(total);
                
                String code=HtmlUtility.branchCov(maps, file.getAbsolutePath(), template);
//                HtmlViewTopComponent tp=new HtmlViewTopComponent();
//                tp.setName("Coverage Report For "+file.getName());
//                tp.setText(HtmlUtility.branchCov(maps, file.getAbsolutePath()));
//                tp.open();
//                tp.requestActive();
                final String covFile=Contents.USER_DIR__STRING+"/branch/"+simpleName+".html";
                TemplateUtility.branchCov(template, covFile, code);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {//有关界面更新的放在新的线程中
                       WebViewTopComponent webtp=new WebViewTopComponent("file:"+covFile);
                       webtp.open();
                       webtp.requestActive();
                    }
                });

            }
        }else if(Contents.Cov_Flag.equals(Contents.CONDITION)){//条件覆盖的执行
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/condition");
            out=Contents.USER_DIR__STRING+"/condition/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                List<CovStruct> covStruct=FileUtility.getCovStruct(out);
                Map<Integer, Map<Integer, FileUtility.Cond[]>> covMap = FileUtility.convertToCondition(covStruct);
                
                for(Integer key:covMap.keySet()){
                    Map<Integer,Cond[]> map=covMap.get(key);
                    for(Integer k:map.keySet()){
                        Cond[] conds=map.get(k);
                        System.out.println(key+" "+k+" "+conds[0].result+" "+conds[1].result);
                    }
                }
                
//                HtmlViewTopComponent tp=new HtmlViewTopComponent();
//                tp.setName("Coverage Report For "+file.getName());
//                tp.setText(HtmlUtility.conditionCov(covMap, file.getAbsolutePath()));
//                tp.open();
//                tp.requestActive();

                Template template=new Template("Condition Coverage Report",file.getAbsolutePath(),"Condition Info");
                int total=getTotalInfo(Contents.USER_DIR__STRING+"/condition/"+simpleName+".tmp.c");
                template.setTotal(total);
                
                String code=HtmlUtility.conditionCov(covMap, file.getAbsolutePath(), template);//生成代码部分的覆盖信息
                final String covFile=Contents.USER_DIR__STRING+"/condition/"+simpleName+".html";
                TemplateUtility.branchCov(template, covFile, code);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WebViewTopComponent webtp=new WebViewTopComponent("file:"+covFile);
                        webtp.open();
                        webtp.requestActive();
                    }
                });
            }
        }else if(Contents.Cov_Flag.equals(Contents.BRANCH_CONDITION)){//条件分支覆盖
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch_condition");
            //在执行的时候确认的，不需要传入了输出路径
            boolean result=runWithCases(localExec, null);
            if(result){
                String out_branch=Contents.USER_DIR__STRING+"/branch_condition/out_branch.txt";
                String out_cond=Contents.USER_DIR__STRING+"/branch_condition/out_cond.txt";
               //===============输出测试
                Map<Integer, Map<Integer, List<CovStruct>>> mcdc = MCDCUtility.getMCDC_Cond(out_cond);
//                for(Integer key:mcdc.keySet()){
//                    System.out.println("Line: "+key);
//                    for(Integer k:mcdc.get(key).keySet()){
//                        System.out.println("caseNum: "+k);
//                        List<CovStruct> structs=mcdc.get(key).get(k);
//                        for(CovStruct struct:structs){
//                            System.out.println(struct.cs+" "+struct.result+" "+struct.condition);
//                        }
//                    }
//                }
                Map<Integer, Map<Integer, List<CovStruct>>> map1 = MCDCUtility.getMCDC_branch(out_branch);
//                for(Integer key:map1.keySet()){
//                    System.out.println("Line: "+key);
//                    for(Integer k:map1.get(key).keySet()){
//                        System.out.println("caseNum: "+k);
//                        List<CovStruct> structs=map1.get(key).get(k);
//                        for(CovStruct struct:structs){
//                            System.out.println(struct.cs+" "+struct.result+" "+struct.condition);
//                        }
//                    }
//                }
                ///============end -test
                Template template=new Template("Branch/Condition Coverage Report",file.getAbsolutePath(),"Branch/Condition Info");
                int total=getTotalInfo(Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+"_branch.tmp.c")+
                        getTotalInfo(Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+"_cond.tmp.c");
                template.setTotal(total);
                
                String code=HtmlUtility.branchCondCov(map1,mcdc, file.getAbsolutePath(), template);//生成代码部分的覆盖信息
                final String covFile=Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+".html";
                TemplateUtility.branchCov(template, covFile, code);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WebViewTopComponent webtp=new WebViewTopComponent("file:"+covFile);
                        webtp.open();
                        webtp.requestActive();
                    }
                });

            }
        }else if(Contents.Cov_Flag.equals(Contents.MCDC)){//MCDC覆盖
//            localExec=new LocalExec(Contents.USER_DIR__STRING+"/mcdc");
//            boolean result=runWithCases(localExec, null);
//            if(result){
//                String out_branch=Contents.USER_DIR__STRING+"/mcdc/out_branch.txt";
//                String out_cond=Contents.USER_DIR__STRING+"/mcdc/out_cond.txt";
//                
//                Map<Integer, Map<Integer, List<CovStruct>>> mcdc_branch = MCDCUtility.getMCDC_branch(out_branch);
//                Map<Integer, Map<Integer, List<CovStruct>>> mcdc_Cond = MCDCUtility.getMCDC_Cond(out_cond);
//                
//
//                Template template=new Template("MC/DC Coverage Report",
//                        file.getAbsolutePath(),"MC/DC Info");
//                HtmlUtility.mcdcCov(mcdc_Cond, file.getAbsolutePath(), template);
//            } 
// -----------
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch_condition");
            //在执行的时候确认的，不需要传入了输出路径
            boolean result=runWithCases(localExec, null);
            if(result){
                String out_branch=Contents.USER_DIR__STRING+"/branch_condition/out_branch.txt";
                String out_cond=Contents.USER_DIR__STRING+"/branch_condition/out_cond.txt";
               //===============输出测试
                Map<Integer, Map<Integer, List<CovStruct>>> mcdc = MCDCUtility.getMCDC_Cond(out_cond);

                Map<Integer, Map<Integer, List<CovStruct>>> map1 = MCDCUtility.getMCDC_branch(out_branch);

                Template template=new Template("MC/DC Coverage Report",file.getAbsolutePath(),"MC/DC Info");
                int total=getTotalInfo(Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+"_branch.tmp.c")+
                        getTotalInfo(Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+"_cond.tmp.c");
                template.setTotal(total);
                
                String code=HtmlUtility.branchCondCov(map1,mcdc, file.getAbsolutePath(), template);//生成代码部分的覆盖信息
                final String covFile=Contents.USER_DIR__STRING+"/branch_condition/"+simpleName+".html";
                TemplateUtility.branchCov(template, covFile, code);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WebViewTopComponent webtp=new WebViewTopComponent("file:"+covFile);
                        webtp.open();
                        webtp.requestActive();
                    }
                });

            }
//----------------
        }
        else if(Contents.Cov_Flag.equals(Contents.ECMCDC)){//EC-MCDC
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/ecmcdc");
            out=Contents.USER_DIR__STRING+"/condition/output.txt";
            boolean result=runWithCases(localExec, out);//将执行信息写入ｏｕｔ文件了
            if(result){
                /*
                Map<Integer, Map<Integer, List<CovStruct>>> mcdc = MCDCUtility.getMCDC_Cond(out);
                for(Integer key:mcdc.keySet()){
                    System.out.println("Line: "+key);
                    for(Integer k:mcdc.get(key).keySet()){
                        System.out.println("caseNum: "+k);
                        List<CovStruct> structs=mcdc.get(key).get(k);
                        for(CovStruct struct:structs){
                            System.out.println(struct.cs+" "+struct.result+" "+struct.condition);
                        }
                    }
                }*/
                
                //对含有覆盖信息的文件内容按照行排序
                List<CovStruct> covStruct = FileUtility.getCovStruct(out);//处理插桩信息
                //将执行信息处理成Ｍａｐ结构
                Map<Integer, Map<Integer, FileUtility.Cond[]>> covMap = FileUtility.convertTo(covStruct);
                //利用生成的list生成覆盖信息报告
                //                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                //                tp.setText(HtmlUtility.generateHtml(covMap, file.getAbsolutePath()));
                //                tp.open();
                //                tp.requestActive();
                String html=Contents.USER_DIR__STRING+"/ecmcdc/"+file.getName()+".html";
                File covFile=HtmlUtility.generateHtml(covMap, file.getAbsolutePath(), html);
                WebViewTopComponent tp=new WebViewTopComponent("file:"+covFile.getAbsolutePath());
                tp.open();
                tp.requestActive();
                 
            }
        }else if(Contents.Cov_Flag.equals(Contents.PATH)){//简单路径覆盖，不会将复合条件进行拆分
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/path");
            out=Contents.USER_DIR__STRING+"/path/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                localExec.exe("gcov "+file.getName());
                String gcov=Contents.USER_DIR__STRING+"/path/"+simpleName+".c.gcov";//生成的覆盖文件
                //显示覆盖文件
                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                tp.setName("Coverage Report For "+file.getName());
                tp.setText(HtmlUtility.statementCov(gcov));
                tp.open();
                tp.requestActive();
            }
        }else {
            
        }    
            }//end-run
        }).start();

    }//end-actionperformed
    
    private boolean runWithCases(LocalExec localExec,String out){
        io.getOut().println("select the test suite!");
        JFileChooser chooser=new JFileChooser("选择测试用例集");
        int returnVal=chooser.showOpenDialog(null);
        if(returnVal==JFileChooser.APPROVE_OPTION){
            File casesFile=chooser.getSelectedFile();
            String simpleName=file.getName().substring(0,file.getName().indexOf("."));//只有文件的名字无后缀

            //读取cases文件 作为exe的输入;
            BufferedReader br=null;
            try {
                br=new BufferedReader(new FileReader(casesFile));
                String line="";
                if(Contents.Cov_Flag.equals(Contents.BRANCH_CONDITION)
                        ||Contents.Cov_Flag.equals(Contents.MCDC)){//分支条件覆盖标准，需要执行两种类型插桩程序
                    String exeName1=simpleName+"_branch.exe";//在分支条件覆盖时 需要重新定义输出文件的名称
                    String exeName2=simpleName+"_cond.exe";
                    String dir="";
                    if(Contents.Cov_Flag.equals(Contents.BRANCH_CONDITION)){
                        dir="/branch_condition/";
                    }
                    if(Contents.Cov_Flag.equals(Contents.MCDC)){
                        dir="/mcdc/";
                    }
                    String out1=Contents.USER_DIR__STRING+dir+"/out_branch.txt";
                    String out2=Contents.USER_DIR__STRING+dir+"/out_cond.txt";
                    File file1=new File(out1);
                    File file2=new File(out2);
                    if(file1.exists()){//如果输出文件存在的话 需要删除
                        file1.delete();
                    }
                    if(file2.exists()){//第二个输出文件
                        file2.delete();
                    }
                    localExec.exe("chmod +x ./"+exeName1);
                    localExec.exe("chmod +x ./"+exeName2);
                    while((line=br.readLine())!=null){
                        io.getOut().println("Input: "+line);
                        String cmd1="./"+exeName1+" "+line+" >> "+out1;
                        String cmd2="./"+exeName2+" "+line+" >> "+out2;
                        localExec.exe(cmd1);
                        localExec.exe("echo CASEEND >> "+out1);//一个测试用例执行结束标志
                        localExec.exe(cmd2);
                        localExec.exe("echo CASEEND >> "+out2);
                    }
                }else{//其余的覆盖标准
                    String exeName=simpleName+".exe";
                    localExec.exe("chmod +x ./"+exeName);
                    File outFile=new File(out);
                    if(outFile.exists()){
                        outFile.delete();//如果已经存在输出文件，将其删除，写入本次的输出。
                    }
                    while((line=br.readLine())!=null){
                        
                        io.getOut().println("Input: "+line);
                        String cmd="./"+exeName+" "+line+" >> "+out;
                        localExec.exe(cmd);
                        localExec.exe("echo CASEEND >> "+out);
                    }
                }//end-else
                //读取输出文件output.txt，然后获得覆盖率信息
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }finally{
                try {
                    br.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return true;
        }else {
            return false;
        }
    } 
    
    /*
    通过插桩之后的文件信息来计算需要覆盖的部分，比如分支，条件等等,这些信息放在插桩文件的最后一行
    */
    private int getTotalInfo(String pitch){
        File file=new File(pitch);
        BufferedReader br=null;
        int result=0;
        try {
            br=new BufferedReader(new FileReader(file));
            String line="";
            String tmp="";
            boolean flag=false;
            while((line=br.readLine())!=null){
                tmp=br.readLine();
                if(tmp==null){//line 就是最后一行,格式是//5形式
                    //System.err.println("====================="+line);
                    line=line.substring(2,line.length());
                    result=Integer.valueOf(line);
                    flag=true;
                    break;
                }
            }
            if(!flag){//并未给result赋值 tmp是最后一行的元素
                tmp=tmp.substring(2,tmp.length());
                result=Integer.valueOf(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
