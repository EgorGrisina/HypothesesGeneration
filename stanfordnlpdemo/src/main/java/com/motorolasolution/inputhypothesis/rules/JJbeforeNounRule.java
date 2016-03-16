package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class JJbeforeNounRule extends AbstractHypothesisRule {



    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {
        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();
        int i = 0;
        while (i < result.size() ) {
            POSresults = removeJJFromTree(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;
        }

        result = cleanTreeList(result);

        PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }
        return result;
    }

    @Override
    protected Tree getNewTree(Tree oldTree) {
        return null;
    }

    private List<Tree> removeJJFromTree(Tree tree) {

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

            for (int i = 0; i < childs.length - 1; i++) {
                Tree children = childs[i];
                Tree next_children = childs[i + 1];
                for (String jj : CoreNlpConstants.JJlist){
                    if (children.label().value().equals(jj)){
                        for (String nn : CoreNlpConstants.NNlist) {
                            if ( next_children.value().equals(nn)){
                                Tree newTree = tree.deepCopy();
                                newTree.removeChild(i);
                                changedTree.add(newTree);
                            }
                        }
                    }
                }
            }

        } else {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                List<Tree> new_children_list = removeJJFromTree(children);

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
