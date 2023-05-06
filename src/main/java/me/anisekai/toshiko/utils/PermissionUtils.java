package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.exceptions.ActivityRequiredException;
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

    /**
     * Require the provided {@link DiscordUser} to have {@link DiscordUser#isActive()} to {@code true}.
     *
     * @param requester
     *         The {@link DiscordUser} to check.
     *
     * @throws ActivityRequiredException
     *         Threw if {@link DiscordUser#isActive()} is {@code false}.
     */
    public static void requireActivity(@NotNull DiscordUser requester) {

        if (!requester.isActive() && !requester.isAdmin()) {
            throw new ActivityRequiredException();
        }
    }
}
