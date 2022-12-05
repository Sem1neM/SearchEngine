package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.index.Link;
import searchengine.dto.index.LinkRecursiveTask;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.models.Lemma;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class IndexService implements IIndexService{
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final LinkedHashMap<String, Lemma> allLemmas = new LinkedHashMap<>();
    private final SitesList sites;

    @Autowired
    private final IndexRepository indexRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final PageRepository pageRepository;

    @Override
    public Map<String, String> startIndexing() {
        HashMap<String, String> response = new HashMap<>();
        if(forkJoinPool.hasQueuedSubmissions()){
            response.put("result" , "false");
            response.put("error", "Индексация уже запущена");
        }
        else{
            response.put("result", "true");
            List<Site> siteList = sites.getSites();
            siteList.forEach(site -> {
                Thread thread = new Thread(() -> {
                    try {
                        String siteUrl = site.getUrl().trim().replaceAll("www\\.", "");
                        siteUrl = siteUrl.endsWith("/")? siteUrl: siteUrl + "/";
                        Link rootUrl = new Link(siteUrl);
                        LinkRecursiveTask linkRecursiveTask = new LinkRecursiveTask(rootUrl, rootUrl, allLemmas);
                        forkJoinPool.invoke(linkRecursiveTask);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            });
        }
        return response;
    }

    @Override
    public Map<String, String> stopIndexing() {
        HashMap<String,String> response = new HashMap<>();
        if (forkJoinPool.hasQueuedSubmissions()){
            forkJoinPool.shutdown();
            response.put("result", "true");
        }
        else {
            response.put("result", "false");
            response.put("error", "Индексация не запущена");
        }
        return response;
    }
}
