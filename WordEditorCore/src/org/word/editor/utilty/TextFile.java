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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BUAA--XIAO
 */
public class TextFile extends ArrayList<String>{
    //将文件作为一个String读入
    public static String read(String fileName){
        StringBuilder sb=new StringBuilder();
        try {
            BufferedReader in=new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()));
            try {
                String s;
                while((s=in.readLine())!=null){
                    sb.append(s);
                    sb.append("\n");
                }
            }finally{
                    in.close();
                }
            } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        return sb.toString();
    }
    //读入用正则表达式分割的某个文件
    public TextFile(String fileName,String splitter){
        super(Arrays.asList(read(fileName).split(splitter)));
        if(get(0).equals("")) remove(0);
    }
    //正常的按照行读入
    public TextFile(String fileName){
        this(fileName,"\n");
    }
}
