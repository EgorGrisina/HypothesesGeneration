package com.motorolasolution.inputhypothesis.rules;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class DatePeriodRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);

        PrintWriter out = new PrintWriter(System.out);

        int inputListSize = result.size();
        for (int i = 0; i < inputListSize; i++) {
            result.add(getNewTree(removeDatePeriod(result.get(i).deepCopy())));
        }

        result = cleanTreeList(result);

        for(Tree tree : result) {
            tree.pennPrint(out);
            out.flush();
        }
        return result;
    }

    private Tree removeDatePeriod(Tree tree){

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
                String childrenTextValue = children.value().toLowerCase().replaceAll("\\d", "");

                if (childrenTextValue.equals("s")){
                    String childrenValue = children.value().toLowerCase().replaceAll("[^0-9]","");
                    children.setValue(childrenValue);
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Tree new_children = removeDatePeriod(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }
}
