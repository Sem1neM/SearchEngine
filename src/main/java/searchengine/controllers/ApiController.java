package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IIndexService;
import searchengine.services.StatisticsService;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IIndexService indexService;

    public ApiController(StatisticsService statisticsService, IIndexService iIndexService) {
        this.statisticsService = statisticsService;
        this.indexService = iIndexService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    @GetMapping("/startIndexing")
    public HashMap<String, String> startIndexing(){
       return (HashMap<String, String>) indexService.startIndexing();
    }

    @GetMapping("/stopIndexing")
    public HashMap<String, String> stopIndexing(){
        return (HashMap<String, String>) indexService.stopIndexing();
    }

    @GetMapping("/search")
    public HashMap<String, String> search(){return (HashMap<String, String>) indexService.search();}


}
