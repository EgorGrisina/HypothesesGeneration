package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public abstract class NumberProcessingRule extends AbstractHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        PrintWriter out = new PrintWriter(System.out);

        int inputListSize = inputTrees.size();
        for (int i = 0; i < inputListSize; i++) {
            inputTrees.add(getNewTree(replaceNo(inputTrees.get(i).deepCopy())));
        }
        for(Tree tree : inputTrees) {
            tree.pennPrint(out);
            out.flush();
        }
        return super.getHypothesis(inputTrees);
    }

    private List<Tree> removeNumberWord(Tree tree){
        return null;
    }

    private Tree replaceNo(Tree tree) {

        PrintWriter out = new PrintWriter(System.out);

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];

                for (String numb : CoreNlpConstants.NUMBList){
                    if (children.value().toLowerCase().equals(numb)){
                        children.setValue(CoreNlpConstants.NUMBER);
                    }
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Tree new_children = replaceNo(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }

    abstract public Tree getNewTree(Tree oldTree);

}
