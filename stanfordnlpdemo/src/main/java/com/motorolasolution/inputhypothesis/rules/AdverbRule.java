package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class AdverbRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {
        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();
        int i = 0;
        while (i < result.size() ) {
            POSresults = removeRBFromTree(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;
        }

        result = cleanTreeList(result);

        POSresults = new ArrayList<Tree>();
        for (Tree tree : result){                   //update tree for all results
            POSresults.add(getNewTree(tree));
        }

        PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : POSresults) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }
        return POSresults;
    }

    private List<Tree> removeRBFromTree(Tree tree){
        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>1) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Tree next_children = null;
                if (i < childs.length - 1) {
                    next_children = childs[i + 1];
                }
                if (next_children != null && children.label().value().equals(CoreNlpConstants.RB)
                        && next_children.label().value().equals(CoreNlpConstants.RB)) {

                    Tree newTree = tree.deepCopy();
                    newTree.removeChild(i);
                    changedTree.add(newTree);

                } else {

                    for (String rbNoclear : CoreNlpConstants.RBListNoclear) {
                        if (children.label().value().equals(rbNoclear)) {
                            Tree newTree = tree.deepCopy();
                            newTree.removeChild(i);
                            changedTree.add(newTree);
                        }
                    }
                }
            }

        } else {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                List<Tree> new_children_list = removeRBFromTree(children);

                for (int j = 1; j<new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
                }

            }
        }

        return changedTree;
    };

}
