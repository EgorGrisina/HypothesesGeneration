package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class NumberProcessingRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        List<Tree> result = new ArrayList<Tree>();
        //result.addAll(inputTrees);


        for (int i = 0; i < inputTrees.size(); i++) {
            result.add(getNewTree(replaceNo(inputTrees.get(i).deepCopy())));
        }
        List<Tree> withoutNumberResult = new ArrayList<Tree>();
        for (int i = 0; i < result.size(); i++) {
            withoutNumberResult.add(getNewTree(removeNumberWord(result.get(i).deepCopy())));
        }

        withoutNumberResult = cleanTreeList(withoutNumberResult);

        /*PrintWriter out = new PrintWriter(System.out);
        for(Tree tree : result) {
            tree.pennPrint(out);
            out.flush();
        }*/
        return withoutNumberResult;
    }

    private Tree removeNumberWord(Tree tree){
        List<Tree> childs = tree.getChildrenAsList();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);

                    if (children.value().toLowerCase().equals(CoreNlpConstants.NUMBER)){
                        tree.removeChild(i);
                        childs.remove(i);
                        i--;
                    }
            }

        } else {

            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);
                Tree new_children = removeNumberWord(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }

    private Tree replaceNo(Tree tree) {

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

                for (String numb : CoreNlpConstants.NUMBList){
                    if (children.value().toLowerCase().equals(numb)){
                        children.setValue(CoreNlpConstants.NUMBER);
                    }
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Tree new_children = replaceNo(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }

}
