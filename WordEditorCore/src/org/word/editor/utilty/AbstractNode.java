package org.word.editor.utilty;

/**
 * Created by xiao on 16-12-6.
 */
public abstract class AbstractNode {
    String label;
    boolean isEnd;
    public AbstractNode(String label){
        this.label=label;
        this.isEnd=false;
    }
}
