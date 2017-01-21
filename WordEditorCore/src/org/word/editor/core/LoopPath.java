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
/**
 * 改进的Z-path覆盖循环出口，认为经过的条件的序列一样
 * 就是同样的一条路径
 * @author xiao
 */
public class LoopPath {
    /**
     * 编译过程的三个步骤
     */
    private String buildStep1="";
    private String buildStep2="";
    private String buildStep3="";
    private String buildStep4="";

    public LoopPath() {
        
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
        String pitchName=simpleName+".tmp.c";
        String exeName=simpleName+".exe";
        buildStep1="./condition.exe < "+file.getAbsolutePath()+" > "+pitchName;
        buildStep2="sed -i \"s/main/main_1/g\" ./lex.yy.c";
        buildStep3="gcc lex.yy.c "+pitchName+" -o "+exeName+" -lfl";
        
        //生成cfg，计算该程序的路径数； file.c.012t.cfg
        buildStep4="gcc -fdump-tree-cfg "+file.getAbsolutePath();
    }

    public String getBuildStep1() {
        return buildStep1;
    }

    public String getBuildStep2() {
        return buildStep2;
    }

    public String getBuildStep3() {
        return buildStep3;
    }

    public void setBuildStep1(String buildStep1) {
        this.buildStep1 = buildStep1;
    }

    public void setBuildStep2(String buildStep2) {
        this.buildStep2 = buildStep2;
    }

    public void setBuildStep3(String buildStep3) {
        this.buildStep3 = buildStep3;
    }

    public void setBuildStep4(String buildStep4) {
        this.buildStep4 = buildStep4;
    }

    public String getBuildStep4() {
        return buildStep4;
    }
}
