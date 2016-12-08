/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author BUAA--XIAO
 */
public class LocalExec {
    static Logger logger=Logger.getLogger(LocalExec.class);
    String[] cmds;
    String path;//执行命令的路径，但是在这里先不指定，可以让用户配置gcc的路径，然后填写到这里
    
    public LocalExec(String filePath){
        this.path=filePath;
        cmds=new String[3];
        cmds[0]="sh";
        cmds[1]="-c";
    }
    public boolean exe(String cmd){
        try {
            cmds[2]="cd "+path+" && "+cmd;
            runCommand(cmds);
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private int runCommand(String[] simpleCmd) throws Exception{
        logger.info(cmds[2]);
        Process process=Runtime.getRuntime().exec(simpleCmd);
        //error message
        StreamGobbler errorGobbler=new StreamGobbler(process.getErrorStream(),"ERROR");
        //output
        StreamGobbler outputGobbler=new StreamGobbler(process.getInputStream(), "OUTPUT");
        //kick them off
        errorGobbler.start();
        outputGobbler.start();
        
        process.waitFor();
        return (process.exitValue());
    }
    
    private class StreamGobbler extends Thread {
        InputStream is;
        String type;

        public StreamGobbler(InputStream is, String type) {
            this.is=is;
            this.type=type;
        }
        
        public void run(){
            try {
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br=new BufferedReader(isr);
                String line=null;
                while((line=br.readLine())!=null){
                    System.out.println("out: "+line); //输出 命令执行结果
                   //logger.info("command out: "+line);
                }
                br.close();
            } catch (IOException ex) {
                logger.error("ERROR", ex);
            }
        }
    }
}
