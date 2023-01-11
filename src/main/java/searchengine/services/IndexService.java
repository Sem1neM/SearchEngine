package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.dto.index.Link;
import searchengine.dto.index.LinkRecursiveTask;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.models.Field;
import searchengine.models.Lemma;
import searchengine.models.SiteEntity;
import searchengine.models.SiteStatus;
import searchengine.repositories.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Async
public class IndexService implements IIndexService{
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final LinkedHashMap<String, Lemma> allLemmas = new LinkedHashMap<>();
    private final SitesList sites;
    private final List<SiteEntity> siteEntityList;
    private final SiteRepository siteRepository;
    private final IndexRepository indexRepository;
    private final FieldRepository fieldRepository;
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final List<Thread> threads;

    @Override
    public Map<String, String> startIndexing() {
        saveFields();
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
                        SiteEntity siteEntity = createSiteEntity(site);
                        siteEntityList.add(siteEntity);
                        siteRepository.save(siteEntity);
                        String siteUrl = site.getUrl().trim().replaceAll("www\\.", "");
                        siteUrl = siteUrl.endsWith("/")? siteUrl: siteUrl + "/";
                        Link rootUrl = new Link(siteUrl, siteEntity);
                        LinkRecursiveTask linkRecursiveTask = new LinkRecursiveTask(rootUrl, rootUrl, allLemmas, indexRepository, siteRepository, pageRepository);
                        forkJoinPool.invoke(linkRecursiveTask);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                threads.add(thread);
                thread.start();
            });
        }
        return response;
    }

    public SiteEntity createSiteEntity(Site site){
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setName(site.getName());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setLastError("");
        siteEntity.setStatus(SiteStatus.INDEXING);
        siteEntity.setStatusTime(new Date());
        return siteEntity;
    }
    @Override
    public Map<String, String> stopIndexing() {

        HashMap<String,String> response = new HashMap<>();
        AtomicBoolean isActive = new AtomicBoolean(false);
        threads.forEach(thread -> {
            if (thread.isAlive()) {
                isActive.set(true);
            }
        });
        if (isActive.get()){
            forkJoinPool.shutdownNow();
            threads.forEach(Thread::interrupt);
            response.put("result", "true");
            siteEntityList.forEach(site -> site.setStatus(SiteStatus.FAILED));
            siteRepository.saveAll(siteEntityList);
        }
        else {
            response.put("result", "false");
            response.put("error", "Индексация не запущена");
        }
        return response;
    }

    public void saveFields(){
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        fieldRepository.save(title);
        fieldRepository.save(body);
    }
}
