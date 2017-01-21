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
public class ECMCDC {
    private String buildStep1;
    private String buildStep2;
    private String buildStep3;
    
    public ECMCDC(){
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
        String pitchName=simpleName+".tmp.c";
        String exeName=simpleName+".exe";
        buildStep1="./ecmcdc.exe < "+file.getAbsolutePath()+" > "+pitchName;
        buildStep2="sed -i \"s/main/main_1/g\" ./lex.yy.c";
        buildStep3="gcc lex.yy.c "+pitchName+" -o "+exeName+" -lfl";
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

    public String getBuildStep3() {
        return buildStep3;
    }

    public void setBuildStep3(String buildStep3) {
        this.buildStep3 = buildStep3;
    }
}
