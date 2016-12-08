/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

/**
 *
 * @author xiao
 */
    //覆盖分析的结构，将输出的覆盖分析结构处理成这种结构，
/*
当用户分析的是EC-MC/DC时，cs为第几个原子条件
当分析的是分支覆盖时，cs就直接赋值为0，没有意义，condition是整个表达式
*/
public class CovStruct {
   public  int line;//插桩结构的行号
   public  int cs;//条件表达式的内容
   public  int result;//关系条件表达式的结果，1表示大于，-1表示小于，0表示等于
   public String condition;

    public CovStruct(int line,int cs,int result,String condition) {
        this.line=line;
        this.cs=cs;
        this.result=result;
        this.condition=condition;
    }
}
