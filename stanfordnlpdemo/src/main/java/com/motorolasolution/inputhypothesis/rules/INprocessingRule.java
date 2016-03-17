package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class INprocessingRule extends BaseHypothesisRule {

    @Override
    public List<Tree> getHypothesis(List<Tree> inputTrees) {

        List<Tree> result = new ArrayList<Tree>();
        result.addAll(inputTrees);
        List<Tree> POSresults = new ArrayList<Tree>();

        int i = 0;
        while (i < result.size() ) {
            POSresults = removeINContent(result.get(i));
            for (int j = 1; j < POSresults.size(); j++) {
                result.add(POSresults.get(j));
            }
            i++;
        }

        result = cleanTreeList(result);

        PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }
        return result;
    }


    private List<Tree> removeINContent(Tree tree) {

        PrintWriter out = new PrintWriter(System.out);

        List<Tree> changedTree = new ArrayList<Tree>();
        changedTree.add(tree);

        Tree[] childs = tree.children();

        for (int i = 0; i < childs.length; i++) {

            Tree children = childs[i];
            if (children.depth() > 1) {
                List<Tree> new_children_list = removeINContent(children);

                for (int j = 1; j < new_children_list.size(); j++) {
                    Tree newTree = tree.deepCopy();
                    newTree.setChild(i, new_children_list.get(j));
                    changedTree.add(newTree);
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
                        changedTree.add(newTree);
                    } else {
                        for (int j = 1; j < childs.length; j++) {
                            newTree.removeChild(j);
                        }
                        newTree.addChild(NNchild);
                        changedTree.add(newTree);
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
