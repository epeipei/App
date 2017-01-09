/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.utilty;

import java.util.List;

/**
 *
 * @author xiao
 */
public class TreeNode {
    String name;//节点名称，条件节点就是条件，逻辑运算符就是逻辑运算&& ||
    TreeNode left;
    TreeNode right;
    List<Integer> list;//该节点的结果值列表

    public TreeNode() {
    }

    public TreeNode(String name,TreeNode left,TreeNode right, List<Integer> list) {
        this.name = name;
        this.left = left;
        this.right = right;
        this.list = list;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }

    public List<Integer> getList() {
        return list;
    }
    
}
