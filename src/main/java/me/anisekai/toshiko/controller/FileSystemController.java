package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.io.DiskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/fs")
@RestController
public class FileSystemController {

    private final DiskService service;

    public FileSystemController(DiskService service) {

        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String read() {

        return this.service.getDatabaseCache().toString(2);
    }
}
