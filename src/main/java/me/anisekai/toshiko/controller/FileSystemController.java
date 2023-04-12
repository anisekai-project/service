package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/fs")
@RestController
public class FileSystemController {

    private final StorageService service;

    public FileSystemController(StorageService service) {

        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String read() {

        return this.service.getDatabase().toString(2);
    }
}
