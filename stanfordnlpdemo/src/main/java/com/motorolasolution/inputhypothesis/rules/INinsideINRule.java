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

public class INinsideINRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int i = 0;
        while (i < result.size() ) {

            Map<Tree, HypothesisConfidence> resultMap = removeINContent(result.get(i).getHTree(), result.get(i).getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        return result;
    }


    private Map<Tree, HypothesisConfidence> removeINContent(Tree tree, HypothesisConfidence confidence) {

        Map<Tree, HypothesisConfidence> changedTree = new HashMap<Tree, HypothesisConfidence>();

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1) {

                Map<Tree, HypothesisConfidence> resultMap = removeINContent(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (HypothesisConfidence) entry.getValue());
                }
            }

        }

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1 && isContainIN(children)) {

                Map<Tree, HypothesisConfidence> resultMap = removeINinsideIN(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (HypothesisConfidence) entry.getValue());
                }
            }

        }

        return changedTree;
    }

    private Map<Tree, HypothesisConfidence> removeINinsideIN(Tree tree, HypothesisConfidence confidence) {

        Map<Tree, HypothesisConfidence> changedTree = new HashMap<Tree, HypothesisConfidence>();

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (isContainIN(children)) {

                HypothesisConfidence newConfidence = confidence.copy();
                updateConfidence(newConfidence, children);

                Tree newTree = tree.deepCopy();
                newTree.removeChild(i);
                changedTree.put(newTree, newConfidence);

            } else {

                Map<Tree, HypothesisConfidence> resultMap = removeINinsideIN(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (HypothesisConfidence) entry.getValue());
                }
            }

        }

        return changedTree;
    }

    private boolean isContainIN(Tree tree) {
        Tree childs[] = tree.children();
        for (Tree children : childs) {
            if (children.value().equals(CoreNlpConstants.IN)){
                return true;
            }
        }
        return false;
    }

    private void updateConfidence(HypothesisConfidence confidence, Tree children) {

        int wordCount = children.getLeaves().size();

        for (CoreLabel leave : children.taggedLabeledYield()) {
            confidence.updateConfidence(1, leave.value());
        }

        confidence.setWordCount(confidence.getWordCount()-wordCount);
    }
}
