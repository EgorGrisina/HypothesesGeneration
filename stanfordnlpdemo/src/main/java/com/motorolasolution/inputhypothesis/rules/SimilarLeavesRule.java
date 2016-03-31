package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class SimilarLeavesRule extends BaseHypothesisRule{

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);
        //List<InputHypothesis> POSresults = new ArrayList<InputHypothesis>();

        int i = 0;
        while (i < result.size() ) {

            Map<Tree, Double> resultMap = removeSimilarLeaves(result.get(i).getHTree(), result.get(i).getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        return result;
    }


    private Map<Tree, Double> removeSimilarLeaves(Tree tree, double confidence) {

        Map<Tree, Double> changedTree = new HashMap<Tree, Double>();
        //changedTree.put(tree, confidence);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];
            if (children.depth() > 0) {

                Map<Tree, Double> resultMap = removeSimilarLeaves(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (Double) entry.getValue());
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
                        //CONFIDENCE
                        double newConfidence = confidence;
                        changedTree.put(newTree, newConfidence);
                    }
                }
            }
        }

        return changedTree;
    }

}
