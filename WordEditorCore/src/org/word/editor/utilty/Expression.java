/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiao
 * 求表表达式的覆盖分析，转成逆波兰表达方式
 */
public class Expression {
   /*
    求解逆波兰表达式表示方法，用于求解MCDC覆盖率问题
    */
    public static List<String> getReverseExp(String exp){
        List<String> result=new ArrayList<>();
        //先把输入的表达式处理成表达式 运算符,括号连接顺序输入的格式
        HtmlUtility.split(exp, "&&|\\|\\|");
        return result;
    }
}
