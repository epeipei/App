/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author xiao
 * 用于生成报表的模板类
 */
public class Template {
    private String title;//报表的标题
    private String testPath;//被测试文件的路径
    private String criterion;//覆盖标准
    private String date;//测试时间
    private int total=0;//需要被覆盖的总体项
    private int hit=0;//覆盖项
    private int part=0;//部分覆盖

    public Template(String title,String testPath,String criterion) {
        this.title=title;
        this.testPath=testPath;
        this.criterion=criterion;
        Date now=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        this.date=sdf.format(now);
    }

    public String getTitle() {
        return title;
    }

    public String getTestPath() {
        return testPath;
    }

    public String getCriterion() {
        return criterion;
    }

    public String getDate() {
        return date;
    }

    public int getTotal() {
        return total;
    }

    public int getHit() {
        return hit;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }
    
    
    
}
