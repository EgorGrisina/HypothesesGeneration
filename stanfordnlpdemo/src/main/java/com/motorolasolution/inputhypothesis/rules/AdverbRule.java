package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.HypothesisConfidence;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class AdverbRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int i = 0;
        while (i < result.size() ) {

            Map<Tree, HypothesisConfidence> resultMap =
                    removeRBFromTree(result.get(i).getHTree(), result.get(i).getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        /*PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : POSresults) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }*/
        return result;
    }

    private Map<Tree, HypothesisConfidence> removeRBFromTree(Tree tree, HypothesisConfidence confidence) {

        Map<Tree, HypothesisConfidence> changedTree = new HashMap<Tree, HypothesisConfidence>();

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth() > 1) {
                isSimpleChilds = false;
            }
        }


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

                HypothesisConfidence newConfidence = confidence.copy();
                newConfidence.updateConfidence(1, children.value());

                changedTree.put(newTree, newConfidence);

            } else {

                for (String rbNoclear : CoreNlpConstants.RBListNoclear) {
                    if (children.label().value().equals(rbNoclear)) {

                        Tree newTree = tree.deepCopy();
                        newTree.removeChild(i);

                        HypothesisConfidence newConfidence = confidence.copy();
                        newConfidence.updateConfidence(1, children.value());

                        changedTree.put(newTree, newConfidence);
                    }
                }
            }
        }

        if (!isSimpleChilds) {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];

                Map<Tree, HypothesisConfidence> resultMap = removeRBFromTree(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (HypothesisConfidence) entry.getValue());
                }

            }
        }

        return changedTree;
    }

}
