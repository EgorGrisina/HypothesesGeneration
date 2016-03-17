package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class NumeralRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {
        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();
        int inputTreeCount = result.size();
        int i = 0;
        while (i < result.size() ) {
            POSresults = removeCDFromTree(result.get(i), false);   // from non IN blocks
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;
        }

        i = 0;
        while (i < result.size() ) {
            POSresults = removeCDFromTree(result.get(i), true);    // from all blocks
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;
        }

        result = cleanTreeList(result);

        /*PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }*/
        return result;
    }

    private List<Tree> removeCDFromTree(Tree tree, boolean removeFromIn) {

        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>1) {
                isSimpleChilds = false;
            }
        }

        boolean isNoInTree = true;
        if (!removeFromIn) {
            for (Tree children : childs) {
                if (children.value().equals(CoreNlpConstants.IN)) {
                    isNoInTree = false;
                }
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                if (children.value().equals(CoreNlpConstants.NUMERAL)) {
                    Tree newTree = tree.deepCopy();
                    newTree.removeChild(i);
                    changedTree.add(newTree);
                }
            }

        } else if (isNoInTree) {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                List<Tree> new_children_list = removeCDFromTree(children, removeFromIn);

                for (int j = 1; j<new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }

            }
        }

        return changedTree;
    }

}
