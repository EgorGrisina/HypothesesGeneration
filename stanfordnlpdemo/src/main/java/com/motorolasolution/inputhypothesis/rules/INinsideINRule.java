package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class INinsideINRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);
        List<InputHypothesis> POSresults = new ArrayList<InputHypothesis>();

        /*int i = 0;
        while (i < result.size() ) {
            POSresults = removeINContent(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(getNewTree(POSresults.get(j)));
            }
            i++;
        }*/

        result = cleanHypothesisList(result);

        return result;
    }


    private List<Tree> removeINContent(Tree tree) {

        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1) {

                List<Tree> new_children_list = removeINContent(children);

                for (int j = 1; j < new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }
            }

        }

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1 && isContainIN(children)) {

                List<Tree> new_children_list = removeINinsideIN(children);

                for (int j = 1; j < new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }
            }

        }

        return changedTree;
    }

    private List<Tree> removeINinsideIN(Tree tree) {

        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (isContainIN(children)) {

                Tree newTree = tree.deepCopy();
                newTree.removeChild(i);
                changedTree.add(newTree);

            } else {

                List<Tree> new_children_list = removeINinsideIN(children);

                for (int j = 1; j < new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }
            }

        }

        return changedTree;
    };

    private boolean isContainIN(Tree tree) {
        Tree childs[] = tree.children();
        for (Tree children : childs) {
            if (children.value().equals(CoreNlpConstants.IN)){
                return true;
            }
        }
        return false;
    }
}
