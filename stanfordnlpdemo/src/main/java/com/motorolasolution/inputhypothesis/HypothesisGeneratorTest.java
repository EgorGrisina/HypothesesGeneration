package com.motorolasolution.inputhypothesis;

import com.motorolasolution.inputhypothesis.rules.BaseHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.AdverbRule;
import com.motorolasolution.inputhypothesis.rules.DatePeriodRule;
import com.motorolasolution.inputhypothesis.rules.INinsideINRule;
import com.motorolasolution.inputhypothesis.rules.INprocessingRule;
import com.motorolasolution.inputhypothesis.rules.JJbeforeNounRule;
import com.motorolasolution.inputhypothesis.rules.NumberProcessingRule;
import com.motorolasolution.inputhypothesis.rules.NumeralRule;
import com.motorolasolution.inputhypothesis.rules.ProperNounRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;
import com.motorolasolution.inputhypothesis.rules.SProcessingRule;
import com.motorolasolution.inputhypothesis.rules.SimilarLeavesRule;

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

        BaseHypothesisRule rulesList[] = new BaseHypothesisRule[8];

        rulesList[0] = new JJbeforeNounRule();
        rulesList[1] = new DatePeriodRule();
        rulesList[2] = new NumeralRule();
        rulesList[3] = new AdverbRule();
        rulesList[4] = new ProperNounRule();
        rulesList[5] = new SimilarLeavesRule();
        rulesList[6] = new INprocessingRule();
        rulesList[7] = new INinsideINRule();

        for (int i = 0; i < rulesList.length; i++ ){
            rulesList[i].setCoreNlpRulesCallback(mCoreNlpRulesCallback);
        }

        out.print("Enter something:");
        out.flush();

        String input = in.readLine();

        while (!input.equals(CoreNlpConstants.EXIT)) {

            List<Tree> inputTrees = new ArrayList<Tree>();
            List<Tree> results = new ArrayList<Tree>();

            out.println("Inpute text:");
            out.println(input);
            out.flush();

            //input = ((PunctuationRule)rulesList[0]).removePunctuation(input);
            /*out.println("");
            out.println("Punctuation rule:");
            out.println(input);
            out.flush();*/

            List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(input);

            if (sentencesTree == null || sentencesTree.size() == 0) {
                out.println("tree is null.Enter something else: ");
                out.flush();
                input = in.readLine();
                continue;
            }
            NumberProcessingRule mNumberProcessingRule = new NumberProcessingRule();
            mNumberProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
            PunctuationRule mPunctuationRule = new PunctuationRule();
            mPunctuationRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
            SProcessingRule mSProcessingRule = new SProcessingRule();
            mSProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);

            results = mNumberProcessingRule.getHypothesis(sentencesTree);
            results = mSProcessingRule.getHypothesis(results);
            for (int i = 0; i < results.size(); i++) {
                inputTrees.add(mCoreNlpPipeline.getTree(
                        mPunctuationRule.removePunctuation(
                                CoreNlpOutput.getSentenceFromTree(results.get(i))
                        )
                ));
            }
            out.println("");
            out.println("Input tree:");
            CoreNlpOutput.printTrees(inputTrees, out);
            out.println("");
            out.flush();

            //results.addAll(sentencesTree);

            for (int i = 0; i < rulesList.length; i++ ){
                results = rulesList[i].getHypothesis(inputTrees);
                out.println("#"+ (i+1) +" " + rulesList[i].getRuleName()+" result:");
                for(int j = 0; j < results.size(); j++){
                    out.println(j + 1 +". " + CoreNlpOutput.getSentenceFromTree(results.get(j)));
                }
                out.println("");
                out.flush();
            }

            out.println("-----------All rules-----------");
            results = rulesList[0].getHypothesis(inputTrees);
            results = rulesList[1].getHypothesis(results);
            results = rulesList[2].getHypothesis(results);
            results = rulesList[3].getHypothesis(results);
            results = rulesList[4].getHypothesis(results);
            results = rulesList[5].getHypothesis(results);
            results = rulesList[6].getHypothesis(results);
            results = rulesList[7].getHypothesis(results);

            out.println("Input:\n0. "+input+"\n\nResult:");

            for(int i = 0; i < results.size(); i++){
                out.println(i + 1 +". " + CoreNlpOutput.getSentenceFromTree(results.get(i)));
            }

            out.flush();
            out.println("-------------------------");
            out.println("Enter something: ");
            out.flush();
            input = in.readLine();
        }
    }
}
