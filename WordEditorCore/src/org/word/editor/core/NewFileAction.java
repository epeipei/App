/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.word.editor.toolbar.BuildAction;
import org.word.editor.toolbar.FontPanel;
import org.word.editor.toolbar.RunAction;

@ActionID(
        category = "File",
        id = "org.word.editor.core.NewFileAction"
)
@ActionRegistration(
        displayName = "#CTL_NewFileAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "DO-F")
})
@Messages("CTL_NewFileAction=打开文件")
public final class NewFileAction implements ActionListener {
    static Logger logger=Logger.getLogger(NewFileAction.class);
    //打开
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser=new JFileChooser();
//        FileNameExtensionFilter filter=new FileNameExtensionFilter("C Language", "c","C");
//        chooser.setFileFilter(filter);

        
        int returnVal=chooser.showOpenDialog(null);
        if(returnVal==JFileChooser.APPROVE_OPTION){
            File f=chooser.getSelectedFile();
            String filePath=f.getAbsolutePath();//文件的绝对路径
            logger.info("open file: "+f.getName());
            //设置BuildAction,RunAction的FilePath,问题就是我在写入多个文件的时候
            BuildAction.file=f;
            BuildAction.update();
            RunAction.file=f;//被测文件
            

            //新建wordTopcomponent,写入源文件的代码
            WordTopComponent tp= new WordTopComponent(filePath,f.getName());
            tp.open();
            tp.requestActive();
            FontPanel.addObservers(tp);
        }
    }
}
