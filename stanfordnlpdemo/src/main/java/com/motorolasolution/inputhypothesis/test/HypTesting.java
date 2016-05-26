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

public class HypTesting {

    final static String INPUT_FILE_NAME = "testlog/testInput.txt";
    final static String OUTPUT_FILE_NAME = "testlog/testS2iResults.txt";

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
        String phrase;
        String expectedAction;
        String queryS2i;
        List<InputHypothesis> results;
        int i = 1;
        int beforePass = 0;
        int afterPass = 0;
        String LOG = "";

        try {
            while ( (inputString = buffered_reader.readLine()) != null) {

                LOG = "";

                phrase = inputString.substring(
                        inputString.indexOf('[')+1,inputString.indexOf(']'));
                expectedAction = inputString.substring(
                        inputString.lastIndexOf('[')+1,inputString.lastIndexOf(']'));

                queryS2i = "query="+java.net.URLEncoder.encode(phrase, "ISO-8859-1")+"&confidence=1.0";
                AiResponse defaultResponse = S2iCommunicator.query(queryS2i);

                results = mHypGenerator.generateHypothesis(phrase);
                AiResponse receivedResponse = mHypGenerator.getS2iResponse(results);

                if (defaultResponse.getResult().getAction().equals(expectedAction)) {
                    beforePass++;
                }
                if (receivedResponse.getResult().getAction().equals(expectedAction)) {
                    afterPass++;
                }

                LOG =
                          "----------------------------TEST " + i + "------------------------------\n"
                        + "Input phrase: " + phrase + "\n\n"
                        + "Expected action: " + expectedAction + "\n"
                        + "Primary action: " + defaultResponse.getResult().getAction() + "\n"
                        + "Primary result: " + (defaultResponse.getResult().getAction().equals(expectedAction) ? "DONE" : "FAIL") + "\n\n"
                        + "Received action: " + receivedResponse.getResult().getAction() + "\n"
                        + "Resolved query: " + receivedResponse.getResult().getResolvedQuery() + "\n"
                        + "Resolved result: " + (receivedResponse.getResult().getAction().equals(expectedAction) ? "DONE" : "FAIL") + "\n\n";

                LOG += "Hypothesis:"+"\n";

                for(int j = 0; j < results.size(); j++){
                    LOG+= (j + 1 +"."+
                            " w:"+results.get(j).getHConfidence().getWordCount()+
                            " d:"+results.get(j).getHConfidence().getTreeDeep()+
                            " c:"+results.get(j).getHConfidence().getConfidence()+
                            " : " + CoreNlpOutput.getSentenceFromTree(results.get(j).getHTree())+"\n");
                }

                LOG += "\n"+CoreNlpOutput.getS2iQuery(results) + "\n";

                System.out.println(LOG);
                writer.println(LOG);

                i++;
            }
            LOG = "----------------------------TEST RESULTS------------------------------\n"
                    + "TOTAL TESTS: " + (i-1) + "\n"
                    + "PASS BEFORE: " + beforePass + "/" + (i-1) + "\n"
                    + "PASS AFTER: " + afterPass + "/" + (i-1) + "\n";

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
