package fr.gouv.monprojetsup.app.log;

import java.time.LocalDateTime;

public record ServerTrace(String timestamp, String origin, String event, Object param)
{
    public ServerTrace(String origin, String event, Object o) {
        this(LocalDateTime.now().toString(),
                origin,
                event,
                o); }

    public boolean isFiliereReco() {
        return event != null && (
                event.contains("front reloadSuggestions fl")
                || event.contains("front reloadSuggestions fr")
                || event.contains("front reloadSuggestions formations")
        );
    }

    public boolean isPsupUrl() {
        return event != null && event.contains("openUrl") && event.contains("parcoursup");
    }

    public boolean isOnisepUrl() {
        return event != null && event.contains("openUrl") && (event.contains("onisep") || event.contains("avenirs"));
    }

    public String toSummary() {
        return timestamp + " - " + event;
    }

    public boolean isSeeingDetails() {
        return event != null && event.contains("doOpenDetailsModal");
    }
}
