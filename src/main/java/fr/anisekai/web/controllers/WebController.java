package fr.anisekai.web.controllers;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.web.dto.AnimeDto;
import fr.anisekai.web.dto.GroupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Controller
public class WebController {

    private final AnimeService animeService;

    public WebController(AnimeService animeService) {

        this.animeService = animeService;
    }


    @RequestMapping("/")
    public String index(DiscordUser user, Model model) {

        if (user == null) return "login";

        model.addAttribute("logged", true);
        model.addAttribute("user", user);

        if (user.isGuest()) return "forbidden";

        List<Anime>          animes     = this.animeService.getProxy().getRepository().findAll();
        List<String>         groupNames = animes.stream().map(Anime::getGroup).distinct().sorted().toList();
        Collection<GroupDto> groups     = new ArrayList<>();

        for (String groupName : groupNames) {
            GroupDto group = new GroupDto(groupName);
            group.animes = animes.stream()
                                 .filter(anime -> anime.getGroup().equals(groupName))
                                 .sorted(Comparator.comparing(Anime::getOrder))
                                 .map(AnimeDto::new)
                                 .filter(dto -> !dto.episodes.isEmpty())
                                 .toList();

            groups.add(group);
        }

        groups.removeIf(group -> group.animes.isEmpty());

        model.addAttribute("groups", groups);

        return "index";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {

        new SecurityContextLogoutHandler().logout(request, null, null);
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login(DiscordUser user, Model model) {

        if (user == null) return "redirect:/oauth2/authorization/discord";
        return "redirect:/";
    }

    @RequestMapping("/debug")
    public String debug() {

        return "debug";
    }

}
