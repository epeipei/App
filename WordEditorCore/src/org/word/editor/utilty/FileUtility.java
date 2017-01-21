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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author xiao
 */
public class FileUtility {
    private static Logger logger=Logger.getLogger(FileUtility.class);
    //将覆盖分析结果处理成List的形式，并且按照行号排列，方便着色？
    //如果是三种关系的话好像着色不是最好的方案
    public static List<CovStruct> getCovStruct(String outPath){
        BufferedReader br=null;
        List<CovStruct> result=new ArrayList<>();
        try {
            File file=new File(outPath);
            br = new BufferedReader(new FileReader(file));
            String line="";
                while((line=br.readLine())!=null){
                    if(line.startsWith("mycov")){//插桩信息行 每行的信息格式是mycov line cs rsult
                        String parts[]=line.split(" "); 
                        int lineNum=Integer.valueOf(parts[1]);
                        int cs=Integer.valueOf(parts[2]);
                        int r=Integer.valueOf(parts[3]);
                        String condition=parts[4];
                        CovStruct struct=new CovStruct(lineNum,cs,r,condition);
                        result.add(struct);
                    }
                }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sortList(result);//按照行号 从小到大排列；
        return result;
    }
    //按照lineNum排序插桩信息,
    private static void sortList(List<CovStruct> result){
        Comparator<CovStruct> cmp=new Comparator<CovStruct>() {
            @Override
            public int compare(CovStruct o1, CovStruct o2) {
                if(o1.line!=o2.line){
                    return o1.line-o2.line;
                }else{
                    if(o1.cs!=o2.cs){
                        return o1.cs-o2.cs;
                    }else{
                        return 0;
                    }
                }
            }
        };
        Collections.sort(result,cmp);
    }
    /*去重，将多余的覆盖信息删掉*/
    public static Map<Integer,Map<Integer,Cond[]>> convertTo(List<CovStruct> lists){
        Map<Integer,Map<Integer,Cond[]>> resultMap=new HashMap<>();
        if (!lists.isEmpty()) {//含有覆盖信息
            int currentLine=lists.get(0).line;//初始化当前行为第一个元素
            Map<Integer,Cond[]> map=new HashMap<>();
            for(CovStruct struct:lists){//对于每一条覆盖信息的处理
                System.out.println(struct.line+" "+struct.cs+" "+struct.result+" "+struct.condition);
                if(struct.line!=currentLine){//另外一行的信息
                   resultMap.put(currentLine,map);
                   map=new HashMap<>();
                   currentLine=struct.line;
                }
                    if(!map.containsKey(struct.cs)){//不含有该信息
                        Cond cond[]=new Cond[3];//初始化成false
                        for(int i=0;i<3;i++){
                            cond[i]=new Cond();
                            cond[i].result=false;
                            cond[i].condition="";
                        }
                        map.put(struct.cs, cond);
                    }
                    int r=struct.result;
                    int i=-1;
                    if(r<0){//小于
                       i=0;
                    }else if(r==0){//等于
                       i=1;
                    }else{//大于
                       i=2;
                    }
                    map.get(struct.cs)[i].result=true;
                    map.get(struct.cs)[i].condition=struct.condition;
                    System.out.println("result= "+map.get(struct.cs)[0]+" "+map.get(struct.cs)[1]+" "+
                            map.get(struct.cs)[2]);
            }//end-for
            resultMap.put(currentLine, map);
        }
     return resultMap;
    }
    /*
    根据输出文件获取分支覆盖的信息,out文件格式
    mycov line rsult branch
    mycov是插桩信息的标识
    */
    public static List<CovStruct> getBranchStructs(String outPath){
        List<CovStruct> result=new ArrayList<>();
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(new File(outPath)));
            String line="";
            while((line=br.readLine())!=null){
                if(line.startsWith("mycov")){
                    String[] parts=line.split(" ");
                    int lineNum=Integer.valueOf(parts[1]);
                    int rs=Integer.valueOf(parts[2]);
                    String branch=parts[3];
                    CovStruct struct=new CovStruct(lineNum, 0, rs, branch);
                    result.add(struct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sortList(result);
        return result;
    }
    /*
    分支覆盖去掉重复信息，得到每一行的执行结果key 是行号
    Cond数组下标0标识 假分支；下标1标识 真分支
    结果是true表示达到，结果是false表示未达到
    */
    public static Map<Integer,Cond[]> convertToBranch(List<CovStruct> lists){
        Map<Integer,Cond[]> result=new HashMap<>();
        for(CovStruct struct:lists){
            int line=struct.line;
            int rs=struct.result;
            String branch=struct.condition;
            if(!result.containsKey(line)){//map中不含有该key
                Cond[] conds=new Cond[2];
                conds[0]=new Cond();
                conds[1]=new Cond();
                result.put(line, conds);
            }
            result.get(line)[rs].result=true;//设置达到的真/假分支为true
            result.get(line)[rs].condition=branch;
        }
        return result;
    }
    /*
    转化成条件覆盖的信息，并且去掉重复的信息，对于每一个条件只保留结果为真或者假的情况
    Map<Integer,Map<Integer,Cond[]>> key 是行号，第二个key是每一行的第几个条件
    */
    public static Map<Integer,Map<Integer,Cond[]>> convertToCondition(List<CovStruct> lists){
        Map<Integer,Map<Integer,Cond[]>> result=new HashMap<>();
        for(CovStruct struct:lists){
            int line=struct.line;
            int cs=struct.cs;
            if(!result.containsKey(line)){//不含有该行的覆盖信息
                Map<Integer,Cond[]> map=new HashMap<>();
                result.put(line, map);//将对该该行的map放入map中
            }
            Map<Integer,Cond[]> map=result.get(line);
            if(!map.containsKey(cs)){//不含有该条件的覆盖信息
                Cond[] conds=new Cond[2];
                conds[0]=new Cond();
                conds[1]=new Cond();
                map.put(cs, conds);
            }
            int i=-1;
            String condition="";
            if(struct.result==0){//表示达到了假分支
                i=0;
                condition=struct.condition;
            }else{
                i=1;
                condition=struct.condition;
            }
            /**
             * Cond的下标0表示假分支，1表示真分支；true表示达到相应分支，false表示未达到
             */
            map.get(cs)[i].result=true;
            map.get(cs)[i].condition=condition;
        }
        return result;
    }
    public static class Cond{
        public Boolean result;//条件的结果
        public String condition;

        public Cond() {//初始化成达不到
            this.result=false;
            this.condition="";
        }

        @Override
        public String toString() {
            return result+" "+condition;
        }
    }
}
