package com.motorolasolution.inputhypothesis;

import com.motorolasolution.inputhypothesis.rules.AbstractHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.DatePeriodRule;
import com.motorolasolution.inputhypothesis.rules.JJafterNounRule;
import com.motorolasolution.inputhypothesis.rules.NumberProcessingRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class HypothesisGeneratorTest {

    public static void main(String[] args) throws IOException {

        PrintWriter out = new PrintWriter(System.out);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final CoreNlpPipeline mCoreNlpPipeline = new CoreNlpPipeline();

        AbstractHypothesisRule rulesList[] = new AbstractHypothesisRule[4];
        rulesList[0] = new PunctuationRule();
        rulesList[1] = new JJafterNounRule();
        rulesList[2] = new NumberProcessingRule() {
            @Override
            public Tree getNewTree(Tree oldTree) {
                return mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(oldTree));
            }
        };
        rulesList[3] = new DatePeriodRule() {
            @Override
            protected Tree getNewTree(Tree oldTree) {
                return mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(oldTree));
            }
        };

        out.print("Enter something:");
        out.flush();

        String input = in.readLine();

        while (!input.equals(CoreNlpConstants.EXIT)) {


            //out.println(input);
           // out.flush();

            //input = ((PunctuationRule)rulesList[0]).removePunctuation(input);

            List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(input);

            if (sentencesTree == null || sentencesTree.size() == 0) {
                out.println("tree is null.Enter something else: ");
                out.flush();
                input = in.readLine();
                continue;
            }

            CoreNlpOutput.printTrees(sentencesTree, out);
            out.println("");
            out.flush();

            List<Tree> results = new ArrayList<Tree>();
            results.addAll(sentencesTree);

            results = rulesList[2].getHypothesis(results);
            results = rulesList[1].getHypothesis(results);
            results = rulesList[3].getHypothesis(results);

            out.println("Input:");
            out.println("0. "+input);
            out.println("Result:");
            for(int i = 0; i < results.size(); i++){
                out.println(i + 1 +". " + CoreNlpOutput.getSentenceFromTree(results.get(i)));
            }
            out.flush();
            out.println("Enter something: ");
            out.flush();
            input = in.readLine();
        }
    }
}
