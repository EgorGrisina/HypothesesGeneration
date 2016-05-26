package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.HypothesisConfidence;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

public class SimilarLeavesSimpleRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);
        //List<InputHypothesis> POSresults = new ArrayList<InputHypothesis>();

        int i = 0;
        while (i < result.size()) {

            Map<Tree, HypothesisConfidence> resultMap = removeSimilarLeaves(result.get(i).getHTree(), result.get(i).getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        return result;
    }


    private Map<Tree, HypothesisConfidence> removeSimilarLeaves(Tree tree, HypothesisConfidence confidence) {

        Map<Tree, HypothesisConfidence> changedTree = new HashMap<Tree, HypothesisConfidence>();
        //changedTree.put(tree, confidence);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];
            if (children.depth() > 0) {

                Map<Tree, HypothesisConfidence> resultMap = removeSimilarLeaves(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (HypothesisConfidence) entry.getValue());
                }
            }

        }

        for (int i = 0; i < childs.length; i++) {
            for (int j = 0; j < childs.length; j++) {
                if (i != j) {

                    Tree children1 = childs[i];
                    Tree children2 = childs[j];

                    if (children1.value().equals(children2.value())
                            && children1.depth() > 1 && children2.depth() > 1
                            && children1.depth() == children2.depth()) {

                        Tree newTree = tree.deepCopy();
                        newTree.removeChild(j);

                        HypothesisConfidence newConfidence = confidence.copy();
                        updateConfidence(newConfidence, children2, j + 1, childs.length);

                        changedTree.put(newTree, newConfidence);
                    }
                }
            }
        }

        return changedTree;
    }

    void updateConfidence(HypothesisConfidence confidence, Tree children, double chPosition, double chCount) {

        double ruleCoeff = 1.0 - ((CoreNlpConstants.SimilarLeavesMaxDiff / chCount) * chPosition);
        int wordCount = children.getLeaves().size();

        for (CoreLabel leave : children.taggedLabeledYield()) {
            confidence.updateConfidence(1, leave.value(), ruleCoeff);
        }

        confidence.setWordCount(confidence.getWordCount() - wordCount);

    }

}

