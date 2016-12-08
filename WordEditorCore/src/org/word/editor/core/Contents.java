/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

/**
 *
 * @author xiao
 * 程序运行过程中的常量等的数据;
 * coverage flag 标识用户当前选择的覆盖标准
 */
public final class Contents {
    public static final String USER_DIR__STRING=System.getProperty("user.dir");
    public static final String BRANCH_STRING=USER_DIR__STRING+"/branch/branch.exe";
   // public static final String BRANCH_LEX_STRING=
    public static final String CONDITION_STRING=USER_DIR__STRING+"/condition/condition.exe";
    
    
    public static final String STATEMENT="statement coverage";
    public static final String BRANCH="branch coverage";
    public static final String CONDITION="condition coverage";
    public static final String BRANCH_CONDITION="branch/condition coverage";
    public static final String MCDC="MC/DC";
    public static final String PATH="path coverage";
    
    public static final String ECMCDC="EC-MC/DC";
    public static final String PATH_LOOP="path-loop coverage";
//    public static final String 
    
    public static String Cov_Flag=STATEMENT;//全局标识当前选中的覆盖标准,开始默认选择语句覆盖
    
    
}
