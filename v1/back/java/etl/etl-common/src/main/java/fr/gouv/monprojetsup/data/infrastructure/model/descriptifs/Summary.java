package fr.gouv.monprojetsup.data.infrastructure.model.descriptifs;

import org.jetbrains.annotations.Nullable;

import java.util.List;


public record Summary(
            List<Choice> choices
    ) {
    @Nullable String getContent() {
        if (choices == null || choices.isEmpty()) return null;
        return choices.get(0).message.content;
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

