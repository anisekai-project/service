package me.anisekai.toshiko.messages;

import net.dv8tion.jda.api.EmbedBuilder;

public final class AnimeNightMessages {

    private AnimeNightMessages() {}

    public static EmbedBuilder getIntroduction() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bienvenue sur la partie Anime du serveur !");
        builder.setDescription("Ce salon vous expliquera en détail l'organisation des soirées anime et le fonctionnement du bot !");

        builder.addField(
                "Information Générales",
                """
                Sur ce serveur se trouve la grande et merveilleuse <@973021581410459649>, bot suprême qui règnera sur tous les autres...
                                
                **Le but du bot:** Permettre l'organisation d'une liste d'anime à regarder / en cours de visionnage pour organiser nos soirées animes !
                                
                Lorsqu'un anime est ajouté, sa fiche est postée dans le salon <#960633496173412402> avec 3 boutons vous permettant d'indiquer votre niveau d'intérêt pour ce dernier.""",
                false
        );

        return builder;
    }

    public static EmbedBuilder getVoteSystem() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Les niveaux d'intérêt, ça sert à quoi exactement ?");

        builder.setDescription("""
                               Les niveaux d'intérêt s'apparentent à des votes, pour lesquels vous accumulerez de la *puissance de vote*.
                               
                               A l'exception des votes neutre, chaque vote vous apportera un peu de *puissance de vote*, qui n'est autre que le pourcentage que représente vos votes sur la globalité du serveur !
                                                              
                               **Exemple:** Seulement deux personnes possèdent des votes sur 3 animes différents.
                               Il y a donc 6 votes en tout, chaque personne ayant 3 votes, donc 50%.
                               La puissance de vote de ces deux personnes sera donc de 50.
                               En d'autre termes: **pour ces deux personnes, chaque vote vaudra 50pts sur un anime !**
                                                              
                               Le score d'un anime est donc défini par les points apportés par chaque vote. Un vote positif viendra s'ajouter au score tandis qu'un vote négatif viendra s'y soustraire. Il est donc avantageux de voter sur le plus d'anime possible pour faire entendre sa voix !
                                                              
                               Cela dit, toute personne qui vote n'aura pas forcément une puissance de vote, car cette personne doit être considérée comme active !
                                                              
                               Chaque personne possède une *icône de vote* qui n'est autre qu'un simple *emoji* ! Cela permet d'un coup d'oeil de savoir qui a voté positivement ou négativement sur un anime !
                                                              
                               Il y a deux façons de voter: Soit en utilisant les 3 boutons disponibles sur une fiche d'anime (via la commande `/anime about` ou soit dans le salon d'annonce), soit via la commande `/anime interest`.
                                                              
                               *P.S.: Pour voter, il faut avoir une icône de vote. Plus d'information dans la section d'utilisation du bot.*
                               """);

        return builder;
    }

    public static EmbedBuilder getActivity() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Comment est-on considéré comme actif ? Quand est ce qu'on ne l'est plus ?");

        builder.setDescription("""
                               Il n'y a pas vraiment de critère défini car cela se fait beaucoup plus au feeling.
                               Mais il va sans dire que si vous ne participez pas à plusieurs séances anime à la suite, vous perdrez votre statut de *personne active*, et donc vos votes n'affecteront plus les scores (ils seront toujours sauvegardé cependant !).
                                                              
                               L'activité pour les votes n'a donc rien à voir avec votre activité concernant les autres activités du serveur, tel que les discussions ou les jeux.""");

        return builder;
    }

    public static EmbedBuilder getAnimeNights() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Ok d'accord, du coup comment ça se passe les séances de visionnage là ?");

        builder.setDescription("""
                               Tout d'abord, les séances de visionnages sont disponibles depuis les évènements serveur (quand <@149279150648066048> n'a pas la flemme de faire la programmation cela dit).
                               De manière générale, les séances démarrent à 22h30 et se terminent aux alentours de minuit.
                                                              
                               **De 22:30 à 23:00:** Généralement 1 épisode d'un simulcast
                               **De 23:00 à 00:00:** Généralement 3 épisodes d'un anime
                                                              
                               Si vous souhaitez connaitre les prochaines séances (qui ne sont pas dans les évènements serveur), vous pouvez jeter un coup d'oeil aux listes présente dans le salon <#749324195267215571> (principalement **\\👀 En cours de visionnage** et **\\🕘 Visionnage en simulcast**)
                                                              
                               Le déroulement d'une séance visionnage est simple: <@149279150648066048> démarrera un partage d'écran pour diffuser le ou les épisodes de la séance.
                               
                               Il est toléré de discuter pendant l'anime, mais il ne faut pas abuser non plus pour ne pas gâcher l'expérience de visionnage.""");
        return builder;
    }

    public static EmbedBuilder getDelayOrCancel() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Je ne peux pas être présent ou bien je serais en retard... C'est grave docteur ?");

        builder.setDescription("""
                               Nous ne sommes pas trop exigeant là dessus, si vous ne pouvez pas être présent à une séance ou alors si vous savez que vous aurez du retard, prévenez à l'avance et on pourra réorganiser la séance pour plus tard !
                                                              
                               Si vous êtes absent pour une durée prolongée (ex: vacances), il est possible de nous avertir et dès lors, nous remanierons totalement les prochaines séances. Généralement il s'agit simplement de nous indiquer quels animes ne vous intéresse pas trop.
                               
                               Nous reprogrammerons les séances pour faire passer en priorité les animes qui ne vous intéresse pas ou bien que vous voulez bien qu'on regarde sans vous avant de reprendre le cours normal des séances à votre retour.
                                                              
                               A l'inverse, des absences/retard à répétition vous feront perdre le statut **actif**: On ne vous attendra plus, et vos votes ne compterons plus.
                               
                               *Nous n'aimons pas en arriver là, mais on a une liste d'anime à vider, nous ne pouvons pas repousser indéfiniment...*""");
        return builder;
    }

    public static EmbedBuilder getSimulcast() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Est-ce que les simulcasts sont soumis au système de vote aussi ?");

        builder.setDescription("""
                               Oui et non.
                                                              
                               Vous pouvez effectivement voter sur des simulcasts (et profiter du gain de puissance de vote), mais ces votes n'influenceront pas le choix des simulcasts pour la prochaine saison.
                                                              
                               A chaque début de saison, toutes les personnes actives pourront choisir un simulcast qui sera visionné au rythme d'un épisode par semaine. Si plusieurs simulcast sortent le même jour, ceux-ci seront redistribué sur d'autre jour de la semaine.
                                                              
                               > Que se passe-t-il dans le cas ou il y a plus de 7 personnes actives ?
                                                              
                               C'est jamais arrivé, mais dans ce cas, nous prendrons les 7 personnes avec le plus de puissance de vote.
                                                              
                               > D'accord, et maintenant si il y a moins de 7 personnes ?
                                                              
                               C'est très au cas-par-cas. Par exemple si nous sommes 3 personnes actives, chacun aura le droit à 2 simulcasts. On en aura pas 7 en tout, mais c'est pas si dramatique !
                                                              
                               Une fois la saison passée, les simulcasts non visionnés repasseront dans une autre liste et la règle des votes reprendra son cour.
                               """);

        return builder;
    }

    public static EmbedBuilder getAnimeImport() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Je veux ajouter un anime qui m'intéresse, comment dois-je procéder ?");

        builder.setDescription("""
                               Premièrement, il est bon de savoir combien de personnes participant aux séances de visionnage ont déjà vu l'anime que vous souhaitez ajouter. Si la majorité l'ont déjà vu, est-il vraiment nécessaire de l'ajouter ?
                                                              
                               Le bot utilise les informations de Nautiljon pour remplir une fiche d'anime. Cependant, Nautiljon n'a pas apprécié qu'un bot 'vole' leur jolie fiche et c'est donc désormais impossible d'automatiser l'opération.
                               
                               Nous utilisons donc désormais une extension navigateur (Chrome/Edge) permettant d'extraire les informations utile de la fiche pour ensuite importer l'anime via une commande.
                                                              
                               Si vous voulez obtenir cette extension et les explications d'import, merci de contacter <@149279150648066048>.""");

        return builder;
    }

    public static EmbedBuilder getBotUsage() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bon, comment on utilise le bot là ?");

        builder.setDescription("""
                               Rendez-vous dans le salon <#961291908884365322> et entrez le caractère `/` dans la zone de texte. Vous verrez une liste de commande s'afficher.
                               De là, cliquez sur l'image de profil du bot dans la barre latérale, à gauche de toutes les commandes.
                                                             
                               Seul 5 commandes vous seront vraiment utiles:
                               `/anime about`, `/top user`, `/top anime`, `/anime interest` et `/user icon set`.
                                                             
                               Chaque commande vient avec sa description et ses paramètres (qui ont eux aussi leur description, et même des fois de l'auto-complétion, incroyable technologie je vous assure), ce qui aide énormément à la compréhension.
                               
                               Si pour autant il y a quelque chose qui vous échappe, contactez <@149279150648066048> ou même <@233305307235745792>.
                                                             
                               **Pour la commande `/user icon set`**
                               Cette commande permet de choisir votre icône de vote. Pour obtenir une liste d'emoji et simplifier le copier/coller, je recommande [ce site](https://unicode.org/emoji/charts/full-emoji-list.html).
                               """);

        return builder;
    }

}
