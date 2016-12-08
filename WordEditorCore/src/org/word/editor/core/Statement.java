/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import static org.word.editor.toolbar.BuildAction.file;

/**
 *
 * @author xiao
 */
public class Statement {
   // private File testFile=null;
    private String buildStep1="";
    private String buildStep2="";
    
    public Statement(){
        buildStep1="gcc -c "+file.getAbsolutePath()+" -ftest-coverage -fprofile-arcs";
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
        String objName=simpleName+".o";
        String exeName=simpleName+".exe";
        buildStep2="gcc "+objName+" -o "+exeName+" -fprofile-arcs";
    }
    public String getBuildStep1(){
        return buildStep1;
    }
    public String getBuildStep2(){
        return buildStep2;
    }
    public void setBuildStep1(String step1){
        this.buildStep1=step1;
    }
    public void setBuildStep2(String step2){
        this.buildStep2=step2;
    }
}
