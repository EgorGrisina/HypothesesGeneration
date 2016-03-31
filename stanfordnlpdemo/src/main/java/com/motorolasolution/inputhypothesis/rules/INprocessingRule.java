package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class INprocessingRule extends BaseHypothesisRule {

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

        if (tree.getLeaves().size() > 2 && childs.length > 1 && childs[0].value().equals(CoreNlpConstants.IN)) {

            for (int i = 1; i < childs.length; i++) {
                Tree children = childs[i];
                Tree NNchild = getNNchild(children);

                if (NNchild != null) {

                    Tree newTree = tree.deepCopy();

                    if (childs[1].value().equals(CoreNlpConstants.NP)){

                        Tree NPchild = childs[1].deepCopy();
                        int NPchildcount = NPchild.children().length;
                        for (int k = NPchildcount - 1 ; k >= 0; k-- ){
                            NPchild.removeChild(k);
                        }
                        NPchild.addChild(NNchild);
                        for (int j = 1; j < childs.length; j++){
                            newTree.removeChild(j);
                        }
                        newTree.addChild(NPchild);
                        double newConfidence = confidence;
                        changedTree.put(newTree, newConfidence);

                    } else {
                        for (int j = 1; j < childs.length; j++) {
                            newTree.removeChild(j);
                        }
                        newTree.addChild(NNchild);
                        double newConfidence = confidence;
                        changedTree.put(newTree, newConfidence);
                    }
                }
            }
        }

        return changedTree;
    }

    private Tree getNNchild(Tree tree) {

        Tree[] childs = tree.children();
        for (Tree children : childs) {

            for (String nn : CoreNlpConstants.NNlist) {
                if (children.value().equals(nn)) {
                    return children;
                }
            }
            if (children.depth() > 0){
                Tree NNChild = getNNchild(children);
                if (NNChild != null) {
                    return NNChild;
                }
            }
        }

        return null;
    };

}
