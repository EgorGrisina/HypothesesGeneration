package com.motorolasolution.inputhypothesis.s2itest;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolutions.bigdata.vip.controller.request.message.AiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class S2iTester {

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

        out.print("Enter query string:");
        out.flush();

        String input = "query=describe+male+witness+in+case+1112&query=describe+witness+in+case+1112&query=describe+witness+in+case&confidence=0.75&confidence=0.64&confidence=0.45";
        out.println(input);
        while (!input.equals(CoreNlpConstants.EXIT)) {

            out.println("");
            AiResponse response = S2iCommunicator.query(input);
            if (response == null) {
                out.println("No response");
            } else {
                out.println("ResolvedQuery: "+response.getResult().getResolvedQuery());
                out.println("Action: "+response.getResult().getAction());
                out.println("");
            }

            out.flush();
            out.println("-------------------------");
            out.println("Enter query string: ");
            out.flush();
            input = in.readLine();
        }

    }
}

