package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class JJbeforeNounRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int i = 0;
        while (i < result.size() ) {

            Map<Tree, Double> resultMap = removeJJFromTree(result.get(i).getHTree(), result.get(i).getHConfidence());

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

    private Map<Tree, Double> removeJJFromTree(Tree tree, double confidence) {

        Map<Tree, Double> changedTree = new HashMap<Tree, Double>();

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
                                double newConfidence = confidence;
                                changedTree.put(newTree, newConfidence);
                            }
                        }
                        for (String other_jj : CoreNlpConstants.JJlist) {
                            if ( next_children.value().equals(other_jj)){
                                Tree newTree = tree.deepCopy();
                                newTree.removeChild(i);
                                double newConfidence = confidence;
                                changedTree.put(newTree, newConfidence);
                            }
                        }

                    }
                }
            }

        } else {
            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];

                Map<Tree, Double> resultMap = removeJJFromTree(children, confidence);

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
