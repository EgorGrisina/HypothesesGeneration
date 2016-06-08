package com.motorolasolution.inputhypothesis.test;

import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolution.inputhypothesis.InputHypothesis;
import com.motorolasolution.inputhypothesis.s2itest.S2iCommunicator;
import com.motorolasolutions.bigdata.vip.controller.request.message.AiResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public class HypNewTester {

    final static String INPUT_FILE_NAME = "testlog/testPositive.txt";
    final static String OUTPUT_FILE_NAME = "testlog/testPositiveResults.txt";

    public static void main(String[] args) throws IOException {

        HypGenerator mHypGenerator = new HypGenerator();

        PrintWriter writer = new PrintWriter(OUTPUT_FILE_NAME, "UTF-8");

        System.out.println("Start testing\n");

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(INPUT_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Can't open input file");
            return;
        }
        InputStreamReader reader = new InputStreamReader( stream );
        BufferedReader buffered_reader = new BufferedReader( reader );

        String inputString;
        String[] phrases;
        String expectedAction;
        String queryS2i;
        List<InputHypothesis> results;
        int i = 0;
        int beforePass = 0;
        int afterPass = 0;
        int totalHyp = 0;
        int totalExpected = 0;
        String LOG = "";

        try {
            while ( (inputString = buffered_reader.readLine()) != null) {

                LOG = "";

                phrases = inputString.split("//");
                if (phrases.length == 0) {
                    continue;
                }

                /*queryS2i = "query="+java.net.URLEncoder.encode(phrases[0], "ISO-8859-1")+"&confidence=1.0";
                AiResponse defaultResponse = S2iCommunicator.query(queryS2i);*/

                results = mHypGenerator.generateHypothesis(phrases[0]);
                /*AiResponse receivedResponse = mHypGenerator.getS2iResponse(results);

                if (defaultResponse.getResult().getAction().equals(expectedAction)) {
                    beforePass++;
                }
                if (receivedResponse.getResult().getAction().equals(expectedAction)) {
                    afterPass++;
                }*/
                LOG += "Input phrase:\n" + phrases[0] + "\n\n";
                LOG += "Expected hypothesis:\n";

                for (int j = 1; j < phrases.length; j++) {
                    phrases[j] = mHypGenerator.mPunctuationRule.removePunctuation(phrases[j]);
                    phrases[j] = phrases[j].toLowerCase();
                    LOG += j+". "+phrases[j] + "\n";
                }

                LOG += "\n";

                /*LOG =
                        "----------------------------TEST " + i + "------------------------------\n"
                                + "Input phrase: " + phrase + "\n\n"
                                + "Expected action: " + expectedAction + "\n"
                                + "Primary action: " + defaultResponse.getResult().getAction() + "\n"
                                + "Primary result: " + (defaultResponse.getResult().getAction().equals(expectedAction) ? "DONE" : "FAIL") + "\n\n"
                                + "Received action: " + receivedResponse.getResult().getAction() + "\n"
                                + "Resolved query: " + receivedResponse.getResult().getResolvedQuery() + "\n"
                                + "Resolved result: " + (receivedResponse.getResult().getAction().equals(expectedAction) ? "DONE" : "FAIL") + "\n\n";
*/

                LOG += "Hypothesis:"+"\n";
                int passCount = 0;

                for(int j = 0; j < results.size(); j++){

                    for (int k = 1; k < phrases.length; k++) {
                        String phrase = phrases[k];
                        String hyp = CoreNlpOutput.getSentenceFromTree(results.get(j).getHTree()).toLowerCase().replaceAll(" ","");
                        if (hyp.equals(phrase.replaceAll(" ",""))){
                            passCount++;
                        }
                    }
                    LOG+= (j + 1 +"."+
                            " w:"+results.get(j).getHConfidence().getWordCount()+
                            " d:"+results.get(j).getHConfidence().getTreeDeep()+
                            " c:"+results.get(j).getHConfidence().getConfidence()+
                            " : " + CoreNlpOutput.getSentenceFromTree(results.get(j).getHTree())+"\n");
                }

                if (passCount > (phrases.length-1)) {
                    passCount = phrases.length -1 ;
                }
                if (passCount > 0) {
                    afterPass++;
                }
                LOG += "GENERATED: "+ passCount+"/"+(phrases.length-1) + "\n";
                totalExpected += phrases.length-1;
                totalHyp += passCount;
                LOG += "---------------------------------------------------------";

                System.out.println(LOG);
                writer.println(LOG);

                i++;
            }

            LOG = "----------------------------TEST RESULTS------------------------------\n"
                    + "TOTAL TESTS: " + (i) + "\n"
                    + "PASS TESTS: " + afterPass + "\n\n"
                    + "TOTAL EXPECTED: " + totalExpected + "\n"
                    + "TOTAL GENERATED: " + totalHyp + "\n";

            System.out.println(LOG);
            writer.println(LOG);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading input file");
            return;
        }

        writer.close();
        System.out.println("\nTesting completed");

    }

}
