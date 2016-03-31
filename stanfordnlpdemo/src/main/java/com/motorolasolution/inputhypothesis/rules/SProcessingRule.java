package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class SProcessingRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> withoutPOS = new ArrayList<InputHypothesis>();

        for (int i = 0; i < inputHypothesisList.size(); i++) {

            Map<Tree, Double> resultMap = removePOS(
                    inputHypothesisList.get(i).getHTree().deepCopy(),
                    inputHypothesisList.get(i).getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                withoutPOS.add(new InputHypothesis(getNewTree((Tree)entry.getKey()), (Double)entry.getValue()));
            }
        }

        withoutPOS = cleanHypothesisList(withoutPOS);

        /*PrintWriter out = new PrintWriter(System.out);
        for(Tree tree : withoutPOS) {
            tree.pennPrint(out);
            out.flush();
        }*/
        return withoutPOS;
    }

    private Map<Tree, Double> removePOS(Tree tree, double confidence){

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
                if (children.value().equals(CoreNlpConstants.POS)){
                    tree.removeChild(i);
                    childs.remove(i);
                    i--;
                }
            }

        } else {

            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);
                Map<Tree, Double> result = removePOS(children, confidence);

                for (Map.Entry entry : result.entrySet()) {
                    confidence = (Double) entry.getValue();
                    tree.setChild(i, (Tree) entry.getKey());
                }


            }
        }

        Map<Tree, Double> resultMap = new HashMap<Tree, Double>();
        resultMap.put(tree, confidence);
        return resultMap;
    }


}
