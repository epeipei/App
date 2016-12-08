package org.word.editor.utilty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 16-12-7.
 */
public class SwitchNode extends AbstractNode{
    List<AbstractNode> nextList;
    public SwitchNode(String name){
        super(name);
        nextList=new ArrayList<>();
    }
}
