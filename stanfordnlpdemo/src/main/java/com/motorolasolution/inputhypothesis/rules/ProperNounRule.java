package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class ProperNounRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {
        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();
        int i = 0;
        while (i < result.size() ) {
            /*POSresults = removeNNP(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;*/
            POSresults.add(getNewTree(removeNNP(result.get(i).deepCopy())));
            i++;
        }

        result.addAll(POSresults);

        result = cleanTreeList(result);

        PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }
        return result;
    }

    private Tree removeNNP(Tree tree) {

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

                for (String nnp : CoreNlpConstants.NNPList) {
                    if (children.label().value().equals(nnp)) {
                        tree.removeChild(i);
                        childs.remove(i);
                        i--;
                    }
                }
            }

        } else {
            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);
                tree.setChild(i, removeNNP(children));
            }
        }

        return tree;
    }

}
