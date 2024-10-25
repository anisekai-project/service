package me.anisekai.modules.toshiko.utils;

import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.interfaces.IUser;
import me.anisekai.modules.shizue.exceptions.AdministratorRequiredException;
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
    public static void requirePrivileges(@NotNull IUser requester) {

        if (!requester.isAdmin()) {
            throw new AdministratorRequiredException();
        }
    }

}
