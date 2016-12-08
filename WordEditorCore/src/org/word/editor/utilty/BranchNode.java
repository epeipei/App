package org.word.editor.utilty;

/**
 * Created by xiao on 16-12-6.
 */
public class BranchNode extends AbstractNode{
    AbstractNode tnode;
    AbstractNode fnode;
    public BranchNode(String name){
        super(name);
    }
}
