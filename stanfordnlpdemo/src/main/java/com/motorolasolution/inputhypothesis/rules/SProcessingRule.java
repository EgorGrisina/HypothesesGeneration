package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class SProcessingRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        List<Tree> withoutPOS = new ArrayList<Tree>();

        for (int i = 0; i < inputTrees.size(); i++) {
            withoutPOS.add(getNewTree(removePOS(inputTrees.get(i).deepCopy())));
        }

        withoutPOS = cleanTreeList(withoutPOS);

        /*PrintWriter out = new PrintWriter(System.out);
        for(Tree tree : withoutPOS) {
            tree.pennPrint(out);
            out.flush();
        }*/
        return withoutPOS;
    }

    private Tree removePOS(Tree tree){
        List<Tree> childs = tree.getChildrenAsList();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>1) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);
                if (children.value().equals(CoreNlpConstants.POS)){
                    tree.removeChild(i);
                    childs.remove(i);
                    i--;
                }
            }

        } else {

            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);
                Tree new_children = removePOS(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }


}
