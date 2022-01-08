package edu.estu;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class App
{
    public static void main(String[] args) {
        MyOptions myOptions= new MyOptions();
        CmdLineParser parser = new CmdLineParser(myOptions);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar myprogram.jar [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
        List<String> people = new ArrayList<String>();

        /** jsoup parse paragraph*/
        Document document = null;
        try {
            document = Jsoup.connect(myOptions.url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String paragraph="";

        Element body = document.body();
        Elements paragraphs1 = body.getElementsByClass("sect1");

        for (Element line : paragraphs1) {
            paragraph+=line.text()+". ";
        }

        /**sentence detection*/
        SentenceModel model=null;
        try (InputStream modelIn = new FileInputStream("src/main/resources/opennlp-en-ud-ewt-sentence-1.0-1.9.3 .bin")) {
            model = new SentenceModel(modelIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String sentences[] = sentenceDetector.sentDetect(paragraph);

        /** tokenizer*/
        InputStream inputStream2 = null;
        try {
            inputStream2 = new FileInputStream("src/main/resources/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TokenizerModel tokenModel = null;
        try {
            tokenModel = new TokenizerModel(inputStream2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TokenizerME tokenizer = new TokenizerME(tokenModel);

        for (String sentence :sentences){
            String tokens[] = tokenizer.tokenize(sentence);

            /** name finder*/

            InputStream inputStreamNameFinder = null;
            try {
                inputStreamNameFinder = new
                        FileInputStream("src/main/resources/en-ner-person.bin");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            TokenNameFinderModel model2 = null;
            try {
                model2 = new TokenNameFinderModel(inputStreamNameFinder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            NameFinderME nameFinder = new NameFinderME(model2);

            Span nameSpans[] = nameFinder.find(tokens);

            String[] spanns = Span.spansToStrings(nameSpans, tokens);
            for (int i = 0; i < spanns.length; i++) {
                people.add(spanns[i]);
            }

        }

        for(int i=0;i<people.size();i++){
            System.out.println(people.get(i));
        }
    }

    }



