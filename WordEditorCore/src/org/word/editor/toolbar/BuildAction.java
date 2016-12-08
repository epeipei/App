/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.word.editor.core.Branch;
import org.word.editor.core.Condition;
import org.word.editor.core.Contents;
import org.word.editor.core.Statement;
import org.word.editor.utilty.LocalExec;

@ActionID(
        category = "Build",
        id = "org.word.editor.toolbar.BuildAction"
)
@ActionRegistration(
        iconBase = "org/word/editor/toolbar/compile_16px_512879_easyicon.net.png",
        displayName = "#CTL_BuildAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/Build", position = 150),
    @ActionReference(path = "Shortcuts", name = "F6")
})
@Messages("CTL_BuildAction=编译")
public final class BuildAction implements ActionListener {
    static Logger logger=Logger.getLogger(BuildAction.class);
    public static File file=null;
    public static String exeName="";
    //插桩以后生成的文件
    public static String pitchName="";
    
    
//    String gccStep1="gcc -c "+fileName+" -ftest-coverage -fprofile-arcs";
//    String gccStep2="gcc "+objName+" -o "+exeName+" -fprofile-arcs";
    
    public static String step1="";//将生成的插桩之后的文件放入到工程的根目录下的condition文件夹下
    public static String step2="";
    /*
    在用户改变选择的文件时，file字段改变，同时其他的相关字段也要改变
    */
    public static void update(){
      exeName=file.getName().substring(0,file.getName().indexOf("."))+".exe";
    //插桩以后生成的文件
      pitchName=file.getName().substring(0,file.getName().indexOf("."))+".tmp.c";
      step1=Contents.CONDITION_STRING+" < "+file.getAbsolutePath()+" > "
            +pitchName;//将生成的插桩之后的文件放入到工程的根目录下的condition文件夹下
      step2="gcc lex.yy.c "+pitchName+" -o "+exeName+" -lfl ";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("org.word.editor.toolbar.BuildAction.actionPerformed()");
        if(file==null){
            JOptionPane.showMessageDialog(null, "请选择被测文件！");
            return ;
        }
        //根据用户选择的覆盖准则进行编译选项的设置
        if(Contents.Cov_Flag.equals(Contents.STATEMENT)){//语句覆盖
            Statement statement=new Statement();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/statement");
            localExec.exe(statement.getBuildStep1());
            localExec.exe(statement.getBuildStep2());
        }else if(Contents.Cov_Flag.equals(Contents.BRANCH)){//分支覆盖
            Branch branch=new Branch();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch");
            localExec.exe(branch.getBuildStep1());
            localExec.exe(branch.getBuildStep2());
        }else if(Contents.Cov_Flag.equals(Contents.CONDITION)){
            Condition condition=new Condition();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/condition");
            localExec.exe(condition.getBuildStep1());
            localExec.exe(condition.getBuildStep2());
        }
        else if(Contents.Cov_Flag.equals(Contents.ECMCDC)){
             LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/ecmcdc");
             localExec.exe(step1); //生成插桩以后的文件
             //生成可执行文件
             localExec.exe(step2);//将语法分析文件和插桩后的文件编译生成插桩后的可执行文件
        }else {
            
        }
        JOptionPane.showMessageDialog(null, "插桩并且编译完成！");
    }
}
