package com.motorolasolution.inputhypothesis.rules;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class JJafterNounRule extends AbstractHypothesisRule {

    private List<Tree> results;

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {
        results = new ArrayList<Tree>();
        results.add(inputTrees.get(0));
        removePOSFromTree(inputTrees.get(0), true);

        PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : results) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }
        return inputTrees;
    }

    Tree removePOSFromTree(Tree tree, boolean firstRun) {

        Tree[] childs = tree.children();

        PrintWriter out = new PrintWriter(System.out);

        boolean isSimpleChilds = true;
        for (Tree children : childs){
            if (children.children().length > 1){
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {
            /*out.println("isSimpleChilds");
            tree.pennPrint(out);
            out.flush();*/
            for (int i = 0; i < childs.length-1; i++) {
                Tree children = childs[i];
                Tree next_children = childs[i+1];
                if ( children.label().value().equals("JJ") && next_children.value().equals("NN")){
                    tree.removeChild(i);
                    return tree;
                }
            }

        } else {
        /*    out.println("not isSimpleChilds");
            tree.pennPrint(out);
            out.flush();*/
            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                int leaves_cout = children.getLeaves().size();
                Tree old_tree = tree.deepCopy();
                Tree new_children = removePOSFromTree(children, false);

                out.println("new tree");
                tree.pennPrint(out);
                out.println("old tree");
                old_tree.pennPrint(out);
                out.flush();

                if (old_tree.getLeaves().size() != tree.getLeaves().size()) {
                    tree.setChild(i, new_children);
                    if (firstRun) {
                        results.add(old_tree);
                    }
                    return tree;
                }
            }
        }
        return tree;
    }
}
