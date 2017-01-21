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
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.word.editor.core.Branch;
import org.word.editor.core.BranchCondition;
import org.word.editor.core.Condition;
import org.word.editor.core.Contents;
import org.word.editor.core.ECMCDC;
import org.word.editor.core.LoopPath;
import org.word.editor.core.MCDC;
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
    static InputOutput io=IOProvider.getDefault().getIO("Console", false);
    /*
    在用户改变选择的文件时，file字段改变，同时其他的相关字段也要改变,在各个build文件中，将file静态引入
    */
    public static File file=null;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("org.word.editor.toolbar.BuildAction.actionPerformed()");
        io.getOut().println("org.word.editor.toolbar.BuildAction.actionPerformed()");
        if(file==null){
            //InputOutput io=IOProvider.getDefault().getIO("test", true);
            JOptionPane.showMessageDialog(null, "请选择被测文件！");
            return ;
        }
        new Thread(new Runnable() {//在新的线程执行，防止界面卡住
            @Override
            public void run() {
                //设置进度条
                 ProgressHandle handle = ProgressHandle.createHandle("Build"); 
                 handle.start();
         //根据用户选择的覆盖准则进行编译选项的设置
        if(Contents.Cov_Flag.equals(Contents.STATEMENT)){//语句覆盖
            Statement statement=new Statement();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/statement");
            localExec.exe(statement.getBuildStep1());
            localExec.exe(statement.getBuildStep2());
            handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.BRANCH)){//分支覆盖
            Branch branch=new Branch();
            String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch");
            localExec.exe(branch.getBuildStep1());
            /*
            在if.l中嵌入了main函数，会复制到lex.yy.c中，桩函数也带有main函数，不能通过编译，将main替换成main_1
            */
            localExec.exe(branch.getBuildStep2());
            localExec.exe(branch.getBuildStep3());
            handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.CONDITION)){
            Condition condition=new Condition();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/condition");
            localExec.exe(condition.getBuildStep1());
            /*
            在if.l中嵌入了main函数，会复制到lex.yy.c中，桩函数也带有main函数，不能通过编译，将main替换成main_1
            */
            localExec.exe(condition.getBuildStep2());
            localExec.exe(condition.getBuildStep3());
            handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.BRANCH_CONDITION)){//分支条件覆盖
            BranchCondition branch_condition=new BranchCondition();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch_condition");
            localExec.exe(branch_condition.getBuildStep1());
            localExec.exe(branch_condition.getBuildStep2());
            localExec.exe(branch_condition.getBuildStep3());
            localExec.exe(branch_condition.getBuildStep4());
            localExec.exe(branch_condition.getBuildStep5());
            localExec.exe(branch_condition.getBuildStep6());
            handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.MCDC)){//MC/DC覆盖
//            MCDC mcdc=new MCDC();
//            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/mcdc");
//            localExec.exe(mcdc.getBuildStep1());
//            localExec.exe(mcdc.getBuildStep2());
//            localExec.exe(mcdc.getBuildStep3());
//            localExec.exe(mcdc.getBuildStep4());
//            localExec.exe(mcdc.getBuildStep5());
//            localExec.exe(mcdc.getBuildStep6());

            BranchCondition branch_condition=new BranchCondition();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/branch_condition");
            localExec.exe(branch_condition.getBuildStep1());
            localExec.exe(branch_condition.getBuildStep2());
            localExec.exe(branch_condition.getBuildStep3());
            localExec.exe(branch_condition.getBuildStep4());
            localExec.exe(branch_condition.getBuildStep5());
            localExec.exe(branch_condition.getBuildStep6());
            handle.finish();
        }
        else if(Contents.Cov_Flag.equals(Contents.ECMCDC)){
            ECMCDC ecmcdc=new ECMCDC();
             LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/ecmcdc");
             localExec.exe(ecmcdc.getBuildStep1()); //生成插桩以后的文件
             //替换掉文件中的main函数的名字，防止在一起编译的时候main函数冲突
             localExec.exe(ecmcdc.getBuildStep2());
             //生成可执行文件
             localExec.exe(ecmcdc.getBuildStep3());//将语法分析文件和插桩后的文件编译生成插桩后的可执行文件
             handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.PATH)){//简单路径覆盖,不会将条件拆分的
//            Path path=new Path();
//            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/path");
//            localExec.exe(path.getBuildStep1());
              Statement statement=new Statement();
              LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/path");
              localExec.exe(statement.getBuildStep1());
              localExec.exe(statement.getBuildStep2());
              handle.finish();
        }else if(Contents.Cov_Flag.equals(Contents.PATH_LOOP)){//覆盖循环出口的改进
            LoopPath loopPath=new LoopPath();
            LocalExec localExec=new LocalExec(Contents.USER_DIR__STRING+"/loop_path");
            //插桩，并且编译生成插桩之后的代码，生成可执行文件
            localExec.exe(loopPath.getBuildStep1());
            localExec.exe(loopPath.getBuildStep2());
            localExec.exe(loopPath.getBuildStep3());
            //生成cfg
            localExec.exe(loopPath.getBuildStep4());
            handle.finish();
        }else {
            
        }
        
        JOptionPane.showMessageDialog(null, "插桩并且编译完成！");
         }//end-run
            
        }).start();
        
    }

    
}
