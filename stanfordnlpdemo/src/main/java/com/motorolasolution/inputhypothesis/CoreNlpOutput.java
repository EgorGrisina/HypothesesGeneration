package com.motorolasolution.inputhypothesis;

import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

public class CoreNlpOutput {

    public static String getSentenceFromTree(Tree tree) {
        String sentence = "";
        List<CoreLabel> list = tree.taggedLabeledYield();
        for (CoreLabel label : list) {
            sentence+=label.word() + " ";
        }
        return sentence;
    }


    static void printTree(Tree tree, PrintWriter out){
        tree.pennPrint(out);
        out.flush();
    }

    static void printTrees(List<Tree> trees, PrintWriter out){
        for (Tree tree : trees) {
            out.println("Sentence: "+getSentenceFromTree(tree));
            tree.pennPrint(out);
            out.println("");
        }
        out.flush();
    }

}