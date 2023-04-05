package me.anisekai.toshiko;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.apache.logging.log4j.util.Strings;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Texts {

    public static final String ANIME_NOTIFY_ANNOUNCE__DESCRIPTION  = "Envoi une notification d'un anime dans le salon d'annonce";
    public static final String ANIME_NOTIFY_ANNOUNCE__OPTION_ANIME = "Nom de l'anime";

    public static final String ANIME_NOTIFY_REFRESH__DESCRIPTION = "Actualise les messages dans le salon d'annonce";

    public static final String ANIME_ABOUT__DESCRIPTION  = "Afficher les détails d'un anime";
    public static final String ANIME_ABOUT__OPTION_ANIME = "Nom de l'anime";

    public static final String ANIME_STATUS__DESCRIPTION    = "Mettre à jour un anime";
    public static final String ANIME_STATUS__OPTION_ANIME   = "Nom de l'anime";
    public static final String ANIME_STATUS__OPTION_STATUS  = "Nouveau statut de l'anime";
    public static final String ANIME_STATUS__OPTION_WATCHED = "Nombre d'épisodes visionnés";

    public static final String ANIME_INTEREST__DESCRIPTION                         = "Indiquer votre niveau d'intérêt pour un anime";
    public static final String ANIME_INTEREST__OPTION_NAME                         = "Nom de l'anime";
    public static final String ANIME_INTEREST__OPTION_LEVEL                        = "Niveau de l'intérêt";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED     = "Je suis intéressé";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL        = "Je suis suisse ! NEUTRE !";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED = "J'ai vraiment pas envie de voir ça";

    public static final String ANIME_PROGRESS__DESCRIPTION    = "Change la progession de visionnage d'un anime";
    public static final String ANIME_PROGRESS__OPTION_ANIME   = "Nom de l'anime";
    public static final String ANIME_PROGRESS__OPTION_WATCHED = "Nombre d'épisodes visionnés";
    public static final String ANIME_PROGRESS__OPTION_AMOUNT  = "Nombre total d'épisodes";

    public static final String ANIME_REFRESH__DESCRIPTION                     = "Actualise toutes les listes";
    public static final String ANIME_REFRESH__OPTION_TARGET                   = "Cible de l'actualisation";
    public static final String ANIME_REFRESH__OPTION_TARGET__CHOICE_WATCHLIST = "Listes de visionnage";
    public static final String ANIME_REFRESH__OPTION_TARGET__CHOICE_ANNOUNCE  = "Message d'annonces (déjà envoyés uniquement)";
    public static final String ANIME_REFRESH__OPTION_FORCE                    = "(Listes de visionnage uniquement) Force l'actualisation des messages, même s'ils n'existent pas.";

    public static final String ANIME_ADD__DESCRIPTION    = "Ajoute une anime à la liste";
    public static final String ANIME_ADD__OPTION_NAME    = "Nom de l'anime (en Japonais svp)";
    public static final String ANIME_ADD__OPTION_LINK    = "Lien vers la fiche Nautiljon";
    public static final String ANIME_ADD__OPTION_STATUS  = "Statut de publication de l'anime";
    public static final String ANIME_ADD__OPTION_EPISODE = "Nombre total d'épisodes";
    public static final String ANIME_ADD__OPTION_IMAGE   = "Image de l'anime";

    public static final String ANIME_IMPORT__DESCRIPTION = "Importe un anime depuis une chaine JSON.";
    public static final String ANIME_IMPORT__OPTION_JSON = "JSON à importer";

    public static final String USER_ICON__DESCRIPTION = "Changer votre icône de vote";
    public static final String USER_ICON__OPTION_ICON = "Icône de vote";

    private Texts() {}

    public static Command.Choice get(DayOfWeek day) {

        String rawValue = day.getDisplayName(TextStyle.FULL, Locale.FRANCE);
        String value    = rawValue.substring(0, 1).toUpperCase() + rawValue.substring(1);

        return new Command.Choice(value, day.getValue());
    }

    public static String unslugify(String str) {

        List<String> romanNumbers = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");

        String[]     split = str.split("-");
        List<String> parts = new ArrayList<>();

        for (String s : split) {

            if (romanNumbers.contains(s.toUpperCase())) {
                parts.add(s.toUpperCase());
            } else {
                parts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
            }
        }

        return Strings.join(parts, ' ');
    }

    public static String unslugifyExt(String str) {

        return unslugify(str.substring(0, str.lastIndexOf('.')));
    }

    public static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
