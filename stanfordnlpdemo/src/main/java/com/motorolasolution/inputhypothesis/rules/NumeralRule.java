package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class NumeralRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int inputTreeCount = result.size();
        int i = 0;
        while (i < result.size() ) {

            Map<Tree, Double> resultMap = removeCDFromTree(result.get(i).getHTree(), result.get(i).getHConfidence(), false);   // from non IN blocks

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }
            i++;
        }

        i = 0;
        while (i < result.size() ) {
            Map<Tree, Double> resultMap = removeCDFromTree(result.get(i).getHTree(), result.get(i).getHConfidence(), true);   // from non IN blocks

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        /*PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }*/
        return result;
    }

    private Map<Tree, Double> removeCDFromTree(Tree tree, Double confidence,  boolean removeFromIn) {

        Map<Tree, Double> changedTree = new HashMap<Tree, Double>();

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
                    double newConfidence = confidence;
                    changedTree.put(newTree, newConfidence);
                }
            }

        } else if (isNoInTree) {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                Map<Tree, Double> resultMap = removeCDFromTree(children, confidence, removeFromIn);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (Double) entry.getValue());
                }

            }
        }

        return changedTree;
    }

}
