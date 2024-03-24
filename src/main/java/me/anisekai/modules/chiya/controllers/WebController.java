package me.anisekai.modules.chiya.controllers;

import jakarta.servlet.http.HttpServletRequest;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.freya.entities.detached.disk.DiskEpisode;
import me.anisekai.modules.freya.entities.detached.disk.DiskSubtitle;
import me.anisekai.modules.freya.services.DiskService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Controller
public class WebController {

    private final DiskService service;

    public WebController(DiskService service) {

        this.service = service;
    }

    @GetMapping("/")
    public String index(DiscordUser user, Model model) {

        if (user == null) {
            return "login";
        }

        model.addAttribute("logged", true);
        model.addAttribute("user", user);

        if (!user.hasWebAccess()) {
            return "forbidden";
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        new SecurityContextLogoutHandler().logout(request, null, null);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(DiscordUser user) {

        if (user == null) {
            return "redirect:/oauth2/authorization/discord";
        }
        return "redirect:/";
    }


    // Files

    @GetMapping("/{anime}/{episode}/video")
    public ResponseEntity<Resource> openAnimeEpisode(DiscordUser user, @PathVariable String anime, @PathVariable String episode) {

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.hasWebAccess()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UUID animeUUID, episodeUUID;

        try {
            animeUUID   = UUID.fromString(anime);
            episodeUUID = UUID.fromString(episode);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<File> file = this.service.findEpisode(animeUUID, episodeUUID).map(DiskEpisode::getPath);
        if (file.isEmpty() || !file.get().exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentDisposition(ContentDisposition.inline().filename(file.get().getName()).build());

        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file.get()));
    }

    @GetMapping("/{anime}/{group}/{episode}/video")
    public ResponseEntity<Resource> openAnimeEpisode(DiscordUser user, @PathVariable String anime, @PathVariable String group, @PathVariable String episode) {

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.hasWebAccess()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UUID animeUUID, groupUUID, episodeUUID;

        try {
            animeUUID   = UUID.fromString(anime);
            groupUUID   = UUID.fromString(group);
            episodeUUID = UUID.fromString(episode);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<File> file = this.service.findEpisode(animeUUID, groupUUID, episodeUUID).map(DiskEpisode::getPath);
        if (file.isEmpty() || !file.get().exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentDisposition(ContentDisposition.inline().filename(file.get().getName()).build());

        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file.get()));
    }

    @GetMapping("/{anime}/{episode}/subs/{sub}")
    public ResponseEntity<Resource> openEpisodeSubs(DiscordUser user, @PathVariable String anime, @PathVariable String episode, @PathVariable String sub) {

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.hasWebAccess()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UUID animeUUID, episodeUUID, subUUID;

        try {
            animeUUID   = UUID.fromString(anime);
            episodeUUID = UUID.fromString(episode);
            subUUID     = UUID.fromString(sub);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<DiskEpisode> diskEpisode = this.service.findEpisode(animeUUID, episodeUUID);
        if (diskEpisode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<File> file = this.service.findSubtitle(diskEpisode.get(), subUUID).map(DiskSubtitle::getPath);
        if (file.isEmpty() || !file.get().exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/plain"));

        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file.get()));
    }

    @GetMapping("/{anime}/{group}/{episode}/subs/{sub}")
    public ResponseEntity<Resource> openAnimeEpisode(DiscordUser user, @PathVariable String anime, @PathVariable String group, @PathVariable String episode, @PathVariable String sub) {

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.hasWebAccess()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UUID animeUUID, groupUUID, episodeUUID, subUUID;

        try {
            animeUUID   = UUID.fromString(anime);
            groupUUID   = UUID.fromString(group);
            episodeUUID = UUID.fromString(episode);
            subUUID     = UUID.fromString(sub);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<DiskEpisode> diskEpisode = this.service.findEpisode(animeUUID, groupUUID, episodeUUID);
        if (diskEpisode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<File> file = this.service.findSubtitle(diskEpisode.get(), subUUID).map(DiskSubtitle::getPath);
        if (file.isEmpty() || !file.get().exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/plain"));

        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file.get()));
    }


}
