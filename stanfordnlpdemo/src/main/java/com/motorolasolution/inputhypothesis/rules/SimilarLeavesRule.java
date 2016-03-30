package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class SimilarLeavesRule extends BaseHypothesisRule{

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();

        int i = 0;
        while (i < result.size() ) {
            POSresults = removeSimilarLeaves(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(getNewTree(POSresults.get(j)));
            }
            i++;
        }

        result = cleanTreeList(result);

        return result;
    }


    private List<Tree> removeSimilarLeaves(Tree tree) {

        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];
            if (children.depth() > 0) {
                List<Tree> new_children_list = removeSimilarLeaves(children);

                for (int j = 1; j < new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }
            }

        }

        for (int i = 0; i < childs.length; i++) {
            for (int j =0; j < childs.length; j++) {
                if (i != j) {

                    Tree children1 = childs[i];
                    Tree children2 = childs[j];

                    if (children1.value().equals(children2.value())
                            && children1.depth() == children2.depth()) {

                        Tree newTree = tree.deepCopy();
                        newTree.removeChild(j);
                        changedTree.add(newTree);
                    }
                }
            }
        }

        return changedTree;
    }

}
