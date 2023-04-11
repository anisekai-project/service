package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/fs")
public class FileSystemController {

    private final StorageService service;

    public FileSystemController(StorageService service) {

        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String read() {

        return this.service.getDatabase().toString(2);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void regenerate() {

        this.service.cache();
    }
}
