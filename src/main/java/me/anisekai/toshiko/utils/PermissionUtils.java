package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.exceptions.AdministratorRequiredException;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to handle permission related methods.
 */
public final class PermissionUtils {

    private PermissionUtils() {}

    /**
     * Require the provided {@link DiscordUser} to have {@link DiscordUser#isAdmin()} to {@code true}.
     *
     * @param requester
     *         The {@link DiscordUser} to check.
     *
     * @throws AdministratorRequiredException
     *         Threw if {@link DiscordUser#isAdmin()} is {@code false}.
     */
    public static void requirePrivileges(@NotNull DiscordUser requester) {

        if (!requester.isAdmin()) {
            throw new AdministratorRequiredException();
        }
    }

}
