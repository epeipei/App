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
public class Path {
    String buildStep1="";
    String buildStep2="";
    public Path(){
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
//        String pitchName=simpleName+".tmp.c";
//        String exeName=simpleName+".exe";
        buildStep1="gcc "+file.getAbsolutePath()+" -fdump-tree-cfg";
        
    }
    public String getBuildStep1(){
        return this.buildStep1;
    }
    public String getBuildStep2(){
        return this.buildStep2;
    }
    public void setBuildStep1(String step1){
        this.buildStep1=step1;
    }
    public void setBuildStep2(String step2){
        this.buildStep2=step2;
    }
}
