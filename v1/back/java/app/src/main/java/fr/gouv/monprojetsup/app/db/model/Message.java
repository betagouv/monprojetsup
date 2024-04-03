package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.common.Sanitizer;

import java.time.LocalDateTime;

public record Message(String date, String author, String content) {
    public Message(String author, String content) {
        this( LocalDateTime.now().toString(),author, content);
    }

    public Message sanitize() {
        return new Message(
                Sanitizer.sanitize(date),
                Sanitizer.sanitize(author),
                Sanitizer.sanitize(content)
        );
    }
}
