package me.anisekai.modules.toshiko.messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public final class GeneralFrenchMessage {

    private GeneralFrenchMessage() {}

    public static EmbedBuilder getRulesEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Règles Générales");

        builder.appendDescription("Voici quelques règles à respecter. Rien de bien méchant.");

        builder.addField(getRuleOne());
        builder.addField(getRuleTwo());
        builder.addField(getRuleThree());

        return builder;
    }

    public static MessageEmbed.Field getRuleOne() {

        return new MessageEmbed.Field(
                "1. Bon sens et courtoisie",
                "> On ne demande pas grand chose, nous sommes civilisés (je pense)",
                false
        );
    }

    public static MessageEmbed.Field getRuleTwo() {

        return new MessageEmbed.Field(
                "2. Ambiance bon enfant",
                "> Les blagues salaces, moquerie et autres chamailleries seront tolérés tant que tout le monde y consent et que cela ne devienne pas du harcèlement, auquel cas nous seront dans l'obligation d'agir.",
                false
        );
    }

    public static MessageEmbed.Field getRuleThree() {

        return new MessageEmbed.Field(
                "3. Pas de pub",
                "> Sauf autorisation spéciale, cela sera sanctionné. Cependant, si cela à un rapport avec un sujet actuellement abordé, l'autorisation sera tacite.",
                false
        );
    }


}
