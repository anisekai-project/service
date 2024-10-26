package me.anisekai.globals.tasking.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.Task;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import me.anisekai.globals.tasking.tasks.MessageLogTaskExecutor;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.utils.EmbeddingUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

@Component
public class MessageLogTaskFactory implements TaskFactory<MessageLogTaskExecutor> {

    public static final String         NAME = "audit-log";
    private final       TaskingService service;
    private final       JdaStore       store;

    public MessageLogTaskFactory(TaskingService service, JdaStore store) {

        this.service = service;
        this.store   = store;
    }

    public static Task queue(TaskingService service, MessageEmbed embed) {

        BookshelfJson arguments = new BookshelfJson();
        arguments.put(MessageLogTaskExecutor.EMBED, EmbeddingUtils.toJson(embed));
        return service.queue(NAME, NAME, arguments);
    }

    public static String asTaskName() {

        return NAME;
    }

    @Override
    public Class<MessageLogTaskExecutor> getTaskClass() {

        return MessageLogTaskExecutor.class;
    }

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    @Override
    public String getName() {

        return NAME;
    }

    /**
     * Check if this {@link TaskFactory} allows multiple {@link Task} with the same {@link Task#getName()}.
     *
     * @return True if duplicates are allowed, false otherwise.
     */
    @Override
    public boolean allowDuplicated() {

        return true;
    }

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    @Override
    public MessageLogTaskExecutor create() {

        return new MessageLogTaskExecutor(this.store);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
