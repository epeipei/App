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
public class BranchCondition {
    private String buildStep1;
    private String buildStep2;
    private String buildStep3;
    private String buildStep4;
    private String buildStep5;
    private String buildStep6;

    public BranchCondition() {
        String simpleName=file.getName().substring(0,file.getName().indexOf("."));//无后缀的文件名称
        String pitchBranchName=simpleName+"_branch.tmp.c";
        String pitchCondName=simpleName+"_cond.tmp.c";
        String exeBranchName=simpleName+"_branch.exe";
        String exeCondName=simpleName+"_cond.exe";
        buildStep1="./branch.exe < "+file.getAbsolutePath()+" > "+pitchBranchName;
        //替换branch.c中的main函数定义，防止编译出错
        buildStep2="sed -i \"s/main/main_1/g\" ./branch.c";
        buildStep3="gcc branch.c "+pitchBranchName+" -o "+exeBranchName+" -lfl";
        //与条件相关
        buildStep4="./condition.exe < "+file.getAbsolutePath()+" > "+pitchCondName;
        //替换condition.c中的main函数定义，防止联合与桩函数编译的时候出错
        buildStep5="sed -i \"s/main/main_1/g\" ./condition.c";
        buildStep6="gcc condition.c "+pitchCondName+" -o "+exeCondName+" -lfl";
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

    public String getBuildStep4() {
        return buildStep4;
    }

    public String getBuildStep5() {
        return buildStep5;
    }

    public String getBuildStep6() {
        return buildStep6;
    }
    
    
}
