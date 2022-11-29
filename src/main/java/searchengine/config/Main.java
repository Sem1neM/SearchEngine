package searchengine.config;

import searchengine.models.Lemma;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String SITE_URL = "https://www.lutherancathedral.ru/";
    private final static  Link rootUrl = new Link(SITE_URL.trim());
    public static void main(String[] args) {
        try{
            DBModel.saveFields();
            LinkedHashMap<String, Lemma> lemmas = new LinkedHashMap<>();
            LinkRecursiveTask linkRecursiveTask = new LinkRecursiveTask(rootUrl, rootUrl, lemmas);
            new ForkJoinPool().invoke(linkRecursiveTask);
        }
        catch (IOException e){
            e.printStackTrace();
        }




    }

}
