package me.anisekai.toshiko.messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public final class GeneralEnglishMessage {

    private GeneralEnglishMessage() {}

    public static EmbedBuilder getRulesEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("General Rules");

        builder.appendDescription("Here's some rules to follow. Nothing fancy.");

        builder.addField(getRuleOne());
        builder.addField(getRuleTwo());
        builder.addField(getRuleThree());

        return builder;
    }

    public static MessageEmbed.Field getRuleOne() {

        return new MessageEmbed.Field(
                "1. Common Sense and courtesy",
                "> We're not asking much, we are civilised (I think)",
                false
        );
    }

    public static MessageEmbed.Field getRuleTwo() {

        return new MessageEmbed.Field(
                "2. Friendly Atmosphere",
                "> Dirty jokes, teasing and other bickering will be tolerated as long as everybody agree with it and it doesn't end up in harassment in which case we will be obliged to intervene.",
                false
        );
    }

    public static MessageEmbed.Field getRuleThree() {

        return new MessageEmbed.Field(
                "3. No advertisement",
                "> Without any authorization, action will be taken. However, if it is related to an ongoing discussion, the authorization will be tacit.",
                false
        );
    }
}
