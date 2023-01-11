package searchengine.dto.search;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import searchengine.dto.index.HibernateSessionFactoryUtils;
import searchengine.dto.index.LemmaFinder;
import searchengine.repositories.LemmaRepository;

import java.util.Set;

public class SearchSystem {
    private static Set<String> lemmas;
    private static LemmaFinder lemmaFinder;
    private static LemmaRepository lemmaRepository;

    public static void search(String input){
        String[] strings = input.split("\\s");
        for (String s : strings){
            lemmas.addAll(lemmaFinder.getLemmaSet(s));
        }


    }

    public void getSortLemmas(){
//        lemmaRepository.findAllByOrderByFrequencyDesk();
    }
}
