package fr.gouv.monprojetsup.data.model.descriptifs;

import org.jetbrains.annotations.Nullable;

import java.util.List;


public record Summary(
            List<Choice> choix
    ) {
    @Nullable String getContent() {
        if (choix == null || choix.isEmpty()) return null;
        return choix.get(0).message.content;
    }

    public record Choice(
            Message message
    ) {
    }

    public record Message(
            String content
    ) {
    }
}

