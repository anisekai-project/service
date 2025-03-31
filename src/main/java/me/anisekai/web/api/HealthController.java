package me.anisekai.web.api;

import me.anisekai.api.json.BookshelfJson;
import me.anisekai.server.enums.TaskState;
import me.anisekai.server.repositories.AnimeRepository;
import me.anisekai.server.repositories.BroadcastRepository;
import me.anisekai.server.repositories.TaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v3/health")
public class HealthController {

    private final AnimeRepository     animeRepository;
    private final BroadcastRepository broadcastRepository;
    private final TaskRepository      taskRepository;

    public HealthController(AnimeRepository animeRepository, BroadcastRepository broadcastRepository, TaskRepository taskRepository) {

        this.animeRepository     = animeRepository;
        this.broadcastRepository = broadcastRepository;
        this.taskRepository      = taskRepository;
    }

    @GetMapping("/")
    public String getHealth() {

        ZonedDateTime now = ZonedDateTime.now();

        BookshelfJson data  = new BookshelfJson();
        BookshelfJson stats = new BookshelfJson();

        BookshelfJson tasks = new BookshelfJson();
        tasks.put("scheduled", this.taskRepository.countTaskByState(TaskState.SCHEDULED));
        tasks.put("executing", this.taskRepository.countTaskByState(TaskState.EXECUTING));
        tasks.put("failed", this.taskRepository.countTaskByState(TaskState.FAILED));

        stats.put("animes", this.animeRepository.count());
        stats.put("upcoming_broadcasts", this.broadcastRepository.countBroadcastByStartingAtAfter(now));
        stats.put("tasks", tasks);

        data.put("status", "ok");
        data.put("stats", stats);

        return data.toString();
    }

}
