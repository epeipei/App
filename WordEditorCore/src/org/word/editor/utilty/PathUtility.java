/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author xiao
 */
/*
 在路径覆盖测试时，获取程序的控制流图的cfg，并且对cfg进行处理的类
*/
public class PathUtility {
    private static InputOutput io=IOProvider.getDefault().getIO("Console",false);
    /*
    将使用-fdump-tree-cfg处理过之后的文件传入，构造成Map存储图，ｋｅｙ是每个节点的ｌａｂｅｌ
    */
    public static Map<String,AbstractNode> getCFG(String cfgPath){
        File file=new File(cfgPath);
        BufferedReader br=null;

        Map<String,AbstractNode> map=new HashMap<>();
        try{
            br=new BufferedReader(new FileReader(file));
            String line="";
            AbstractNode currentNode=new NormalNode("start");//声明成Abstract可以接收各种类型节点
            int num=0;
            map.put("start",currentNode);
            while((line=br.readLine())!=null){
                num++;
                line=line.trim();//去除多余的空格；
                if(line.startsWith("return")){//终结,简单处理，默认遇到的第一个韩式是待测函数
                    currentNode.isEnd=true;
                    break;
                }
                if (line.startsWith("<")){//一个block
                    String name=line.substring(0,line.length()-1);
                    AbstractNode node=null;
                    if (map.containsKey(name)){
                        node=map.get(name);//更新当前节点
                    }else{
                        node=new NormalNode(name);
                        map.put(name,node);
                        //currentNode=node;
                    }
                    if(currentNode instanceof NormalNode){
                        if(((NormalNode) currentNode).next==null){
                            ((NormalNode) currentNode).next=node;
                        }
                    }
                    currentNode=node;
                }else if(line.startsWith("if")){//生成分支节点,以行号作为key；并且接连读完整个块
                    String name=String .valueOf(num);
                    BranchNode node=new BranchNode(name);//分支节点不会有重复的lebel 不需要检查map
                    map.put(name,node);
                    //连到分支节点的一定是普通节点，不会有两个分支节点相连的情况,所以强制转化成NormalNode
                    ((NormalNode) currentNode).next=node;
                    currentNode=node;
                    String line1=br.readLine().trim();//if分支的goto
                    br.readLine();
                    String line3=br.readLine().trim();//else分支的goto
                    num+=3;

                    String name1=line1.substring(line1.indexOf("<"),line1.indexOf(">")+1);
                    String name2=line3.substring(line3.indexOf("<"),line3.indexOf(">")+1);

                    AbstractNode tnode=null;
                    AbstractNode fnode=null;
                    if(map.containsKey(name1)){//给真分支赋值
                        tnode=map.get(name1);
                    }else{
                        tnode=new NormalNode(name1);
                        map.put(name1,tnode);
                    }
                    if(map.containsKey(name2)){//给假分支赋值
                        fnode=map.get(name2);
                    }else{
                        fnode=new NormalNode(name2);
                        map.put(name2,fnode);
                    }
                    //给当前节点的真假分支赋值
                    node.tnode=tnode;
                    node.fnode=fnode;
                }else if(line.startsWith("goto")){
                    //跳转到指定block,有时候有这种 goto <bb 5> (<L2>);直接取括号中的名称,使用lastIndexOf

                    String name=line.substring(line.lastIndexOf("<"),line.lastIndexOf(">")+1);//找到跳转到的块
                    AbstractNode next=null;
                    if(map.containsKey(name)){
                        next=map.get(name);
                    }else{
                        next=new NormalNode(name);
                    }
                    ((NormalNode)currentNode).next=next;//在该分支中，只能是从normalNode跳转到该
                }else if(line.startsWith("switch")){//switch (D.2187) <default: <L3>, case 104: <L1>, case 108: <L2>>
                    //switch (x) <default: <L2>, case 1: <L0>, case 2: <L1>>
                    String switch_name="switch_"+num;
                    SwitchNode node=new SwitchNode(switch_name);
                    map.put(switch_name,node);//将switchNode加入到map中
                    if(currentNode instanceof NormalNode){
                        ((NormalNode) currentNode).next=node;
                    }
                    String list=line.substring(line.indexOf("<")+1,line.lastIndexOf(">"));//第一个<，最后一个>
                    System.out.println("switch goto: "+list);
                    String[] parts=list.split(",");
                    for(int i=0;i<parts.length;i++){
                        String next_name=parts[i].substring(parts[i].indexOf("<"),parts[i].indexOf(">")+1);
                        NormalNode next=new NormalNode(next_name);
                        node.nextList.add(next);//加入到switchNode的nextList
                        map.put(next_name,next);//将switch连接的点放入map
                    }
                    currentNode=node;//更新当前节点
                }
                else{

                }
            }//end-while

            printCFG(map);
            System.out.println(getMcCabe(map));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    /*
    计算图的圈复杂度
    */
    public static int getMcCabe(Map<String,AbstractNode> map){
        int edge=-1;//终点不引入边
        for(String key:map.keySet()){
            if (map.get(key) instanceof NormalNode){
                edge++;
            }
            if(map.get(key) instanceof BranchNode){
                edge+=2;
            }
            if (map.get(key) instanceof SwitchNode){
                edge+=((SwitchNode) map.get(key)).nextList.size();
            }
        }
        int node=map.size();
        System.out.println("edge: "+edge+" node: "+node);
        return edge-node+2;
    }
    /*
     打印CFG
    */
    public static void printCFG(Map<String,AbstractNode> map){
        System.out.println("节点数：　"+map.size());
        for(String key:map.keySet()){
            AbstractNode node=map.get(key);
            if(node instanceof NormalNode){//普通节点
                String next="null";
                if(!node.isEnd&&((NormalNode) node).next!=null){
                    next=((NormalNode) node).next.label;
                }
                System.out.println(key+" "+next+" "+node.isEnd);
            }
            if(node instanceof BranchNode){//分支节点
                System.out.println(key+" "+((BranchNode) node).tnode.label+" "
                        +((BranchNode) node).fnode.label+" "+node.isEnd);
            }
            if (node instanceof SwitchNode){//switch节点
                System.out.println("switch: "+node.label);
                for(AbstractNode next:((SwitchNode) node).nextList){
                    System.out.print("    "+next.label);
                }
                System.out.println();
            }
        }
    }
    public static int getCoveragedPath(String outPath){
        BufferedReader br=null;
        Set<String> unique=new HashSet<String>();//存放不同的执行路径
        try {
            br=new BufferedReader(new FileReader(new File(outPath)));
            String line="";
            StringBuilder sb=new StringBuilder();
            while((line=br.readLine())!=null){
                if(line.startsWith("mycov")){//插桩行
                    sb.append(line+" ");
                }
                if(line.contains("CASEEND")){//一个case执行结束
                    unique.add(sb.toString());
                    sb=new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        io.getOut().println("paths:");
        for(String s:unique){
            io.getOut().println(s);
        }
        return unique.size();
    }
    public static void main(String[] args){
        System.out.println("");
    }
} 
