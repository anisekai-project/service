package me.anisekai.modules.toshiko;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Texts {

    // <editor-fold desc="@ anime">

    // <editor-fold desc="@ anime/announce">
    public static final String ANIME_NOTIFY_ANNOUNCE__DESCRIPTION  = "\uD83D\uDD12 Créé ou actualise une annonce d'un anime.";
    public static final String ANIME_NOTIFY_ANNOUNCE__OPTION_ANIME = "Anime à annoncer";
    // </editor-fold>

    // <editor-fold desc="@ anime/about">
    public static final String ANIME_ABOUT__DESCRIPTION  = "Permet d'afficher la fiche de présentation d'un anime.";
    public static final String ANIME_ABOUT__OPTION_ANIME = "Anime à afficher";
    // </editor-fold>

    // <editor-fold desc="@ anime/status">
    public static final String ANIME_STATUS__DESCRIPTION   = "\uD83D\uDD12 Met à jour le statut d'un anime.";
    public static final String ANIME_STATUS__OPTION_ANIME  = "Anime à modifier";
    public static final String ANIME_STATUS__OPTION_STATUS = "Nouveau statut de l'anime";
    // </editor-fold>

    // <editor-fold desc="@ anime/interest">
    public static final String ANIME_INTEREST__DESCRIPTION                         = "\uD83E\uDE99 Change ton niveau d'intérêt pour un anime";
    public static final String ANIME_INTEREST__OPTION_NAME                         = "Anime pour lequel le vote sera comptabilisé";
    public static final String ANIME_INTEREST__OPTION_LEVEL                        = "Niveau de l'intérêt";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED     = "Je suis intéressé";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL        = "Je suis suisse ! NEUTRE !";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED = "J'ai vraiment pas envie de voir ça";
    // </editor-fold>

    // <editor-fold desc="@ anime/progress">
    public static final String ANIME_PROGRESS__DESCRIPTION    = "\uD83D\uDD12 Change la progression de visionnage d'un anime";
    public static final String ANIME_PROGRESS__OPTION_ANIME   = "Anime à modifier";
    public static final String ANIME_PROGRESS__OPTION_WATCHED = "Nombre d'épisodes visionnés";
    public static final String ANIME_PROGRESS__OPTION_AMOUNT  = "Nombre total d'épisodes";
    // </editor-fold>

    // <editor-fold desc="@ anime/import">
    public static final String ANIME_IMPORT__DESCRIPTION = "\uD83E\uDE99 Importe un anime depuis une chaine JSON.";
    public static final String ANIME_IMPORT__OPTION_JSON = "JSON à importer";
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="@ broadcast">

    // <editor-fold desc="@ broadcast/schedule">
    public static final String BROADCAST_SCHEDULE__DESCRIPTION      = "\uD83D\uDD12 Programme une ou des diffusions d'épisode(s) d'un anime.";
    public static final String BROADCAST_SCHEDULE__OPTION_ANIME     = "L'anime à programmer";
    public static final String BROADCAST_SCHEDULE__OPTION_FREQUENCY = "Fréquence à laquelle seront programmés les épisodes";
    public static final String BROADCAST_SCHEDULE__OPTION_TIME      = "[HH:MM] Heure à laquelle seront programmés les épisodes";
    public static final String BROADCAST_SCHEDULE__OPTION_AMOUNT    = "Nombre d'épisode(s) par séance de diffusion";
    public static final String BROADCAST_SCHEDULE__OPTION_STARTING  = "[JJ/MM/AAAA] Jour a partir duquel la programmation commence (par défaut à la date du jour)";
    // </editor-fold>

    // <editor-fold desc="@ broadcast/calibrate">
    public static final String BROADCAST_CALIBRATE__DESCRIPTION = "\uD83D\uDD12 Calibre les évènements du serveur.";
    // </editor-fold>

    // <editor-fold desc="@ broadcast/delay">
    public static final String BROADCAST_DELAY__DESCRIPTION     = "\uD83D\uDD12 Décale les évènements d'une plage.";
    public static final String BROADCAST_DELAY__OPTION_DELAY    = "De combien de temps les évènements présent dans l'intervale seront décalés (ex: 1h20m, 2d5h)";
    public static final String BROADCAST_DELAY__OPTION_RANGE    = "Intervale de temps dans lequel les évènements seront décalés. (ex: 1h20m, 2d5h) [Defaut: 6h]";
    public static final String BROADCAST_DELAY__OPTION_STARTING = "Heure à partir de laquelle l'interval de décalage démarre (ex: 22:30, 23:00) [Defaut: (heure actuelle)]";
    // </editor-fold>

    // <editor-fold desc="@ broadcast/cancel">
    public static final String BROADCAST_CANCEL__DESCRIPTION = "\uD83D\uDD12 Annule les évènements actifs.";
    // </editor-fold>


    // <editor-fold desc="@ broadcast/refresh">
    public static final String BROADCAST_REFRESH__DESCRIPTION = "\uD83D\uDD12 Actualise les évènements serveur";
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="@ profile">
    public static final String PROFILE_DESCRIPTION    = "Mettre à jour les données d'un profil utilisateur";
    public static final String PROFILE__OPTION_USER   = "\uD83D\uDD12 Utilisateur pour lequel le profil sera actualisé.";
    public static final String PROFILE__OPTION_ICON   = "Change l'icône de vote";
    public static final String PROFILE__OPTION_ACTIVE = "\uD83D\uDD12 Change le statut d'activité";
    public static final String PROFILE__OPTION_ADMIN  = "\uD83D\uDD12 Change le statut administrateur";
    public static final String PROFILE__OPTION_WEB    = "\uD83D\uDD12 Change le droit d'accès web";
    // </editor-fold>

    // <editor-fold desc="@ top">

    // <editor-fold desc="@ top/anime">
    public static final String TOP_ANIME__DESCRIPTION               = "Affiche un classement des animes basé sur les votes.";
    public static final String TOP_ANIME__OPTION_ORDER              = "Ordre de tri";
    public static final String TOP_ANIME__OPTION_ORDER__CHOICE_ASC  = "Top des moins votés";
    public static final String TOP_ANIME__OPTION_ORDER__CHOICE_DESC = "Top des plus votés";
    public static final String TOP_ANIME__OPTION_LIMIT              = "Nombre d'éléments à afficher dans le classement.";
    // </editor-fold>

    // <editor-fold desc="@ top/user">
    public static final String TOP_USER__DESCRIPTION = "Affiche un classement des utilisateurs par puissance de vote.";
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="@ season/start">
    public static final String SEASON_START__DESCRIPTION = "\uD83D\uDD12 Commence un vote de simulcast pour la prochaine saison";
    public static final String SEASON_START__OPTION_NAME = "Nom de la saison (ex: Hiver 2023)";
    // </editor-fold>

    // <editor-fold desc="@ watchlist/refresh">
    public static final String WATCHLIST_REFRESH__DESCRIPTION = "\uD83D\uDD12 Actualise les listes de visionnage";

    // </editor-fold>

    private Texts() {}

    public static String unslugify(String str) {

        List<String> romanNumbers = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");

        String[]           split = str.split("-");
        Collection<String> parts = new ArrayList<>();

        for (String s : split) {

            if (romanNumbers.contains(s.toUpperCase())) {
                parts.add(s.toUpperCase());
            } else {
                parts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
            }
        }

        return Strings.join(parts, ' ');
    }

    public static String truncate(String str, int maxLength) {

        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

}
