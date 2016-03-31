package com.motorolasolution.inputhypothesis.rules;


import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class DatePeriodRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        int inputListSize = result.size();

        for (int i = 0; i < inputListSize; i++) {
            Map<Tree, Double> resultMap = removeDatePeriod(result.get(i).getHTree().deepCopy(), result.get(i).getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }
        }

        result = cleanHypothesisList(result);

        /*
        PrintWriter out = new PrintWriter(System.out);
        for(Tree tree : result) {
            tree.pennPrint(out);
            out.flush();
        }*/
        return result;
    }

    private Map<Tree, Double> removeDatePeriod(Tree tree, double confidence){

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                String childrenTextValue = children.value().toLowerCase().replaceAll("\\d", "");

                if (childrenTextValue.equals("s")){
                    String childrenValue = children.value().toLowerCase().replaceAll("[^0-9]","");
                    children.setValue(childrenValue);
                    confidence = confidence; //!!!!!!!!!!!!!!!!
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];
                Map<Tree, Double> resultMap = removeDatePeriod(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
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
