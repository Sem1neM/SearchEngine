package searchengine.services;

import java.util.Map;

public interface IIndexService {
    Map<String, String> startIndexing();
    Map<String, String> stopIndexing();
}
