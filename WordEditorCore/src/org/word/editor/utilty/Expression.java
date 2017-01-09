/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.log4j.Logger;

/**
 *
 * @author xiao
 * 求表表达式的覆盖分析，转成逆波兰表达方式
 */
public class Expression {
    static Logger logger=Logger.getLogger(Expression.class);
    private static Map<String,Integer> priority=new HashMap<String, Integer>();

    //静态初始map,初始化各个字符的优先级
    static {
        priority.put("(", 0);
        priority.put("||", 1);
        priority.put("&&", 2);
        priority.put(")", 3);
    }
    /*
     求解逆波兰表达式表示方法，用于求解MCDC覆盖率问题
     传入带有括号的表达式      a+b>c&&b+c>a||a>c 得到的是List 形式处理之后
     */
    public static List<String> getReverseExp(String exp){
        List<String> result=new ArrayList<>();//存放最终的结果的List
        //先把输入的表达式处理成表达式 运算符,括号连接顺序输入的格式
        Stack<String> stack=new Stack();//符号栈

        for(int i=0;i<exp.length();i++){//处理表达式
            char ch=exp.charAt(i);
            StringBuilder sb=new StringBuilder();
            while(ch!='('&&ch!=')'&&ch!='&'&&ch!='|'){//非运算符
                sb.append(ch);
                i++;
                if(i<exp.length()){
                    ch=exp.charAt(i);
                }else {
                    break;
                }
            }
            if (sb.length()>0) {
                result.add(sb.toString());
                //sb=new StringBuilder();
            }
            if (i>=exp.length()) break;
            if(ch=='('){//左括号,直接入栈
                stack.push("(");
            }else if(ch=='&'||ch=='|'){//与逻辑
                String op=ch=='&'?"&&":"||";//运算符
                //System.out.println("-- : "+op+" == "+priority.get(op));

                if(stack.isEmpty()||priority.get(op)>priority.get(stack.peek())){//栈为空 或者是优先级大于栈顶
                    stack.push(op);
                }else{//pop 一直到大于栈顶
                    while (!stack.isEmpty()&&priority.get(op)<=priority.get(stack.peek())) {
                        result.add(stack.pop());//将弄出的元素加入栈顶
                    }//end -while
                    stack.push(op);
                }
                i++;//跳过第二个
            }else if(ch==')'){//右括号
                while(!stack.peek().equals("(")){
                    result.add(stack.pop());
                }
                //将stack顶层的左括号 踢掉
                stack.pop();
               // i++;
            }
            else{
                System.out.println(" else ");
            }
        }//end -for 处理完表达式
        while(!stack.isEmpty()){
            result.add(stack.pop());
        }
        return result;
    }
    /*
    参数map 是该表达式的结果，key 是caseNum； 将其转换成一个条件的每个case的结果对应一列的情况
    返回map 的key是条件的编号，list是其结果列表，按照case顺序排列
    结果返回integer的List 是对有些条件由于短路求值不需要执行，结果可以作为任意值
    0 表示false；1 表示true； 2表示任意值
    */
    public static Map<Integer,List<Integer>> getBoolList(String exp,Map<Integer,List<CovStruct>> map){
        String[] parts=HtmlUtility.split(exp,"&&|\\|\\|");
        Map<Integer,List<Integer>> result=new HashMap<>();//多少个条件对应多少个list，list中存放该条件对应的条件
        for(int i=0;i<parts.length;i++){//该分支表达式一共有多少个条件,获得每一个条件的结果列表
            List<Integer> list=new ArrayList<>();
            result.put(i+1, list);//条件的编号从1开始
        }
        for(Integer key:map.keySet()){
            List<CovStruct> list=map.get(key);// 各个case的结果列表
            for(int i=0;i<parts.length;i++){//对每一个条件
                boolean flag=false;
                for(CovStruct struct:list){
                    if(struct.cs==i+1){
                        flag=true;
                        result.get(struct.cs).add(struct.result);
                        break;
                    }
                }//end -covstruct
                if(!flag){//没有执行到该条件 短路求值
                    result.get(i+1).add(2);//任意值
                }
            }
        }//end - map 循环
        return result;
    }
    /**
     * 生成表达式树的，节点上带有真假值的
     * @param postList 后缀表达式
     * @param resultMap key是条件的编号 从1开始，List是条件的各个case的取值，2代表任意值
     * @return 
     */
    public static TreeNode getExpTree(List<String> postList,Map<Integer,List<Integer>> resultMap){
        int current=0;//当前处理到的条件编号
        Stack<TreeNode> stack=new Stack<>(); //暂存未处理的节点
        for(String s:postList){
            if(s.equals("&&")||s.equals("||")){//运算符节点
                TreeNode right=stack.pop();
                TreeNode left=stack.pop();
                List<Integer> result=computeAndOR(s, left.list, right.list);//计算该节点的结果值
                TreeNode node=new TreeNode(s,left,right,result);
                stack.push(node);
                
            }else{//条件节点
                current++;//第current个条件
                List<Integer> list=resultMap.get(current);
                TreeNode node=new TreeNode(s,null,null,list);
                stack.push(node);
            }
        }
        return stack.pop();
    }
    /**
     * 根据运算符op 计算节点的结果值
     * @param op ： 运算符 && 或者||
     * @param left ： 左子树的结果值
     * @param right ： 右子树的结果值
     * @return 
     */
    private static List<Integer> computeAndOR(String op,List<Integer> left,List<Integer> right){
        List<Integer> result=new ArrayList<>();
        if(left.size()!=right.size()) {
            logger.error("Left Node And Right Node size isn't equal!");
            return null;
        }
        int len=left.size();
        if(op.equals("&&")){
            for(int i=0;i<len;i++){
                int l=left.get(i);
                int r=right.get(i);
                if(l==0||r==0){// 有一个结果是0 最终的结果是0
                    result.add(0);
                }else{
                    result.add(1);
                }
            }
        }else if(op.equals("||")){
            for(int i=0;i<len;i++){
                int l=left.get(i);
                int r=right.get(i);
                if(l==1||r==1){
                    result.add(1);
                }else{
                    result.add(0);
                }
            }
        }else {
            
        }
        return result;
    }
    /**
     * 遍历生成的表达式树
     * @param root  根节点
     */
    public static void traverseTree(TreeNode root){
        if(root!=null){
            System.out.println(root.name);
            for(Integer i: root.list){
                System.out.print(i+" ");
            }
            System.out.println();
            if(root.left!=null) traverseTree(root.left);
            if(root.right!=null) traverseTree(root.right);
        }
    }
    /*
    测试函数
    */
    public static void main(String[] args){
        String exp="a+b>c&&((b+c>a||a>c)&&a>v)";
        List<String> parts=getReverseExp(exp);
        for(String cond:parts){
            System.out.println(cond);
        }
    }

}
