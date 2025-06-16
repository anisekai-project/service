package fr.anisekai.server.exceptions.task;

import fr.anisekai.server.enums.TaskPipeline;
import fr.anisekai.server.tasking.TaskFactory;

public class FactoryAlreadyRegisteredException extends RuntimeException {

    private final TaskPipeline   pipeline;
    private final TaskFactory<?> factory;

    public FactoryAlreadyRegisteredException(TaskPipeline pipeline, TaskFactory<?> factory) {

        super(String.format(
                "Factory '%s' is already registered in '%s' pipeline.",
                factory.getName(),
                pipeline.name()
        ));

        this.pipeline = pipeline;
        this.factory  = factory;
    }

    public TaskFactory<?> getFactory() {

        return this.factory;
    }

    public TaskPipeline getPipeline() {

        return this.pipeline;
    }

}
