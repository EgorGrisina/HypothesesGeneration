package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class INinsideINRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int i = 0;
        while (i < result.size() ) {

            Map<Tree, Double> resultMap = removeINContent(result.get(i).getHTree(), result.get(i).getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }
            i++;
        }

        result = cleanHypothesisList(result);

        return result;
    }


    private Map<Tree, Double> removeINContent(Tree tree, double confidence) {

        Map<Tree, Double> changedTree = new HashMap<Tree, Double>();

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1) {

                Map<Tree, Double> resultMap = removeINContent(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (Double) entry.getValue());
                }
            }

        }

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (children.depth() > 1 && isContainIN(children)) {

                Map<Tree, Double> resultMap = removeINinsideIN(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (Double) entry.getValue());
                }
            }

        }

        return changedTree;
    }

    private Map<Tree, Double> removeINinsideIN(Tree tree, double confidence) {

        Map<Tree, Double> changedTree = new HashMap<Tree, Double>();

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];

            if (isContainIN(children)) {

                Tree newTree = tree.deepCopy();
                newTree.removeChild(i);
                double newConfidence = confidence;
                changedTree.put(newTree, confidence);

            } else {

                Map<Tree, Double> resultMap = removeINinsideIN(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, (Tree) entry.getKey());
                    changedTree.put(newTree, (Double) entry.getValue());
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
}
