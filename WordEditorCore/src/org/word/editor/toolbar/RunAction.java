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
import org.apache.log4j.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.word.editor.core.Contents;
import org.word.editor.core.HtmlViewTopComponent;
import org.word.editor.utilty.CovStruct;
import org.word.editor.utilty.FileUtility;
import org.word.editor.utilty.FileUtility.Cond;
import org.word.editor.utilty.HtmlUtility;
import org.word.editor.utilty.LocalExec;

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
    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("org.word.editor.toolbar.RunAction.actionPerformed()");
        if(file==null){//没有被测文件，提示用户需要选择
            JOptionPane.showMessageDialog(null, "请选择被测文件!");
            return ;
        }
        LocalExec localExec=null;//命令的执行的路径，确定了不同的覆盖标准，在不同的路径下执行命令
        String out="";//覆盖信息的输出路径
        //先执行build操作
        //根据选择的覆盖标准 选择到对应的目录，目前先选择到condition
        if(Contents.Cov_Flag.equals(Contents.STATEMENT)){//语句覆盖
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/statement");
            out=Contents.USER_DIR__STRING+"/statement/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                //使用用例 运行结束，使用gcov进行分析
                String simpleName=file.getName().substring(0,file.getName().indexOf("."));//只有文件的名字无后缀
                localExec.exe("gcov "+file.getName());
                String gcov=Contents.USER_DIR__STRING+"/statement/"+simpleName+".c.gcov";//生成的覆盖文件
                //显示覆盖文件
                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                tp.setName("Coverage Report For "+file.getName());
                tp.setText(HtmlUtility.statementCov(gcov));
                tp.open();
                tp.requestActive();
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
                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                tp.setName("Coverage Report For "+file.getName());
                tp.setText(HtmlUtility.branchCov(maps, file.getAbsolutePath()));
                tp.open();
                tp.requestActive();
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
                
                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                tp.setName("Coverage Report For "+file.getName());
                tp.setText(HtmlUtility.conditionCov(covMap, file.getAbsolutePath()));
                tp.open();
                tp.requestActive();
            }
        }
        else if(Contents.Cov_Flag.equals(Contents.ECMCDC)){//EC-MCDC
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/ecmcdc");
            out=Contents.USER_DIR__STRING+"/condition/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
               //对含有覆盖信息的文件内容按照行排序
                List<CovStruct> covStruct = FileUtility.getCovStruct(out);
                //FileUtility.sortList(covStruct);
                Map<Integer, Map<Integer, FileUtility.Cond[]>> covMap = FileUtility.convertTo(covStruct);
            
           
                //利用生成的list生成覆盖信息报告
                HtmlViewTopComponent tp=new HtmlViewTopComponent();
                tp.setText(HtmlUtility.generateHtml(covMap, file.getAbsolutePath()));
                tp.open();
                tp.requestActive();
            }
        }else if(Contents.Cov_Flag.equals(Contents.PATH)){//简单路径覆盖，不会将复合条件进行拆分
            localExec=new LocalExec(Contents.USER_DIR__STRING+"/path");
            out=Contents.USER_DIR__STRING+"/path/output.txt";
            boolean result=runWithCases(localExec, out);
            if(result){
                String simpleName=file.getName().substring(0,file.getName().indexOf("."));//只有文件的名字无后缀
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
    }//end-actionperformed
    
    private boolean runWithCases(LocalExec localExec,String out){
        JFileChooser chooser=new JFileChooser("选择测试用例集");
        int returnVal=chooser.showOpenDialog(null);
        if(returnVal==JFileChooser.APPROVE_OPTION){
            File casesFile=chooser.getSelectedFile();
            String exeName=file.getName().substring(0,file.getName().indexOf("."))+".exe";
            File outFile=new File(out);
            if(outFile.exists()){
                outFile.delete();//如果已经存在输出文件，将其删除，写入本次的输出。
            }
            //读取cases文件 作为exe的输入;
            BufferedReader br=null;
            try {
                br=new BufferedReader(new FileReader(casesFile));
                String line="";
                while((line=br.readLine())!=null){
                    String cmd="chmod +x ./"+exeName+" && ./"+exeName+" "+line+" >> "+out;
                    localExec.exe(cmd);
                }
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
}
