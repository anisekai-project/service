package fr.anisekai.server.exceptions.task;

import fr.anisekai.server.tasking.TaskFactory;
import fr.anisekai.wireless.remote.interfaces.TaskEntity;

public class FactoryNotFoundException extends RuntimeException {

    public FactoryNotFoundException(TaskEntity task) {

        super(String.format(
                "The factory of name '%s' does not exist or has not been registered.",
                task.getFactoryName()
        ));
    }

    public FactoryNotFoundException(Class<? extends TaskFactory<?>> factoryClass) {

        super(String.format(
                "The factory of class '%s' does not exist or has not been registered.",
                factoryClass.getSimpleName()
        ));
    }

}
