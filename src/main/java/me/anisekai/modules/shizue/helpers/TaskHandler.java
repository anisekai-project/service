package me.anisekai.modules.shizue.helpers;

import me.anisekai.globals.utils.DateTimeUtils;
import org.springframework.scheduling.support.CronExpression;

import java.time.ZonedDateTime;

public abstract class TaskHandler<T> {

    private final String         name;
    private final CronExpression expression;

    private ZonedDateTime next;
    private boolean       exec = false;

    public TaskHandler(String name, String cron) {

        this.name       = name;
        this.expression = CronExpression.parse(cron);
        this.defineNext();
    }

    private void defineNext() {

        this.next = this.expression.next(DateTimeUtils.now());
    }

    protected void tick() {

        ZonedDateTime tickTime = DateTimeUtils.now();

        if (tickTime.equals(this.next) || this.exec) {
            this.execute();
            this.exec = false;
            this.defineNext();
        }
    }

    protected abstract T execute();

    public final String getName() {

        return this.name;
    }

    public void executeOnNextTick() {

        this.exec = true;
    }

}
