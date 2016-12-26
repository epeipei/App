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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author xiao
 */
public class MCDCUtility {
    /*
    处理输出文件，按照一个测试用例一条记录
    将输出文件的格式处理成每一条输入一个记录的格式,一条CovStruct结构,为类似MCDC覆盖准备
    以mycov开头的行是插桩输出行，含有CASEEND的行是一个用例的结束
    Map<Integer,Map<Integer,List<CovStruct>>> 第一个key是行号 第二个key是case编号
    */
    public static Map<Integer,Map<Integer,List<CovStruct>>>  getMCDC_Cond(String outPath){
        int caseNum=1;
        BufferedReader br=null;
        Map<Integer,Map<Integer,List<CovStruct>>> resultMap=new HashMap<>();
        try {
            br=new BufferedReader(new FileReader(new File(outPath)));
            String line="";
            while((line=br.readLine())!=null){
                if(line.startsWith("mycov")){//插桩行
                    String[] parts=line.split(" ");//mycov 28 1 0 argc<4
                    int lineNum=Integer.parseInt(parts[1]);
                    int cs=Integer.parseInt(parts[2]);
                    int result=Integer.parseInt(parts[3]);
                    String condition=parts[4];
                    if (!resultMap.containsKey(lineNum)) {//含有该行的覆盖信息
                        Map<Integer,List<CovStruct>> map=new HashMap<>();
                        resultMap.put(lineNum, map);
                    }
                    Map<Integer,List<CovStruct>> map=resultMap.get(lineNum);
                    if(!map.containsKey(caseNum)){//不含有编号为caseNum
                        List<CovStruct> structs=new ArrayList<>();
                        map.put(caseNum, structs);
                    }
                    CovStruct covStruct=new CovStruct(lineNum, cs, result, condition);
                    map.get(caseNum).add(covStruct);
                }
                if (line.contains("CASEEND")) {//一个用例执行结束
                    caseNum++;
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
        return resultMap;
    }
    /*
    在处理分支条件覆盖的时候，运用了两种覆盖方式，需要按照caseNum处理输出的分支信息，条件信息
    */
    public static Map<Integer,Map<Integer,List<CovStruct>>> getMCDC_branch(String out){
        Map<Integer,Map<Integer,List<CovStruct>>> resultMap=new HashMap<>();
        int caseNum=1;
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(out));
            String line="";
            while((line=br.readLine())!=null){
                if(line.startsWith("mycov")){//一个插桩行 mycov 28 0 argc<4
                    String[] parts=line.split(" ");
                    int lineNum=Integer.parseInt(parts[1]);
                    int result=Integer.parseInt(parts[2]);
                    String branch=parts[3];
                    if(!resultMap.containsKey(lineNum)){
                        Map<Integer,List<CovStruct>> map=new HashMap<>();
                        resultMap.put(lineNum, map);
                    }
                    Map<Integer, List<CovStruct>> map = resultMap.get(lineNum);
                    if(!map.containsKey(caseNum)){
                        List<CovStruct> structs=new ArrayList<>();
                        map.put(caseNum, structs);
                    }
                    CovStruct covStruct=new CovStruct(lineNum,0, result, branch);
                    map.get(caseNum).add(covStruct);
                }
                if(line.contains("CASEEND")){//一个case执行结束了
                    caseNum++;
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
        return resultMap;
    }
}
