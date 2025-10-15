package com.example.k8smysqlbykowski;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestService {

    private final NewsRepo newsRepo;

    public RestService(NewsRepo newsRepo) {
        this.newsRepo = newsRepo;
    }

    @GetMapping("/news")
    public String get() {
        return newsRepo.findAll().toString();
    }

    @PostMapping("/news")
    public void post(@RequestBody News news) {
        newsRepo.save(news);
    }
}
