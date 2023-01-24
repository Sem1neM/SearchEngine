package searchengine.search;


import searchengine.index.LemmaFinder;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SearchSystem {
    private static Set<String> lemmas;
    private static LemmaFinder lemmaFinder;
    private static LemmaRepository lemmaRepository;

    private static IndexRepository indexRepository;

    public static void search(String input){
        String[] strings = input.split("\\s");
        for (String s : strings){
            lemmas.addAll(lemmaFinder.getLemmaSet(s));
        }
        List<Lemma> sortedLemmas = new ArrayList<>();
        for (String lemma : lemmas){
            sortedLemmas.add(lemmaRepository.findByLemma(lemma));
        }
       Collections.sort(sortedLemmas);
        for (Lemma lemma : sortedLemmas){
            List<Index> indexList = new ArrayList<>();
            indexList.add(indexRepository.findByLemmaId(lemma.getId()));
            List<Page> pages = new ArrayList<>();
            for(Index index : indexList){
                pages.add(index.getPageId());
            }
        }

    }
}
