package com.motorolasolution.inputhypothesis;

import com.motorolasolution.inputhypothesis.rules.BaseHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.NumberProcessingRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class TreeCreator {

    public static void main(String[] args) throws IOException {

        PrintWriter out = new PrintWriter(System.out);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final CoreNlpPipeline mCoreNlpPipeline = new CoreNlpPipeline();

        BaseHypothesisRule.CoreNlpRulesCallback mCoreNlpRulesCallback = new BaseHypothesisRule.CoreNlpRulesCallback() {
            @Override
            public Tree getNewTree(Tree oldTree) {
                return mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(oldTree));
            }

            @Override
            public Tree getNewTree(String sentence) {
                return mCoreNlpPipeline.getTree(sentence);
            }
        };

        out.print("Enter something:");
        out.flush();

        String input = in.readLine();

        while (!input.equals(CoreNlpConstants.EXIT)) {

            List<Tree> inputTrees = new ArrayList<Tree>();

            out.println("Inpute text:");
            out.println(input);
            out.flush();

            List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(input);

            if (sentencesTree == null || sentencesTree.size() == 0) {
                out.println("tree is null.Enter something else: ");
                out.flush();
                input = in.readLine();
                continue;
            }

            PunctuationRule mPunctuationRule = new PunctuationRule();
            mPunctuationRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);

            for (int i = 0; i < sentencesTree.size(); i++) {
                inputTrees.add(mCoreNlpPipeline.getTree(
                        mPunctuationRule.removePunctuation(
                                CoreNlpOutput.getSentenceFromTree(sentencesTree.get(i))
                        )
                ));
            }

            for (Tree sent : inputTrees) {
                out.println("");
                out.println("Input sentence:");
                out.println(CoreNlpOutput.getSentenceFromTree(sent));
                out.println("Tree:");
                CoreNlpOutput.printTree(sent, out);
                out.flush();
            }

            out.flush();
            out.println("-------------------------");
            out.println("Enter something: ");
            out.flush();
            input = in.readLine();

        }

    }


}
