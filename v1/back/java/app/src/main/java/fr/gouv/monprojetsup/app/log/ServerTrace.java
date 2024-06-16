package fr.gouv.monprojetsup.app.log;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

public record ServerTrace(String timestamp, String origin, String event, Object param)
{
    public ServerTrace(String origin, String event, Object o) {
        this(LocalDateTime.now().toString(),
                origin,
                event,
                o); }

    public boolean isFiliereRecoofPoc2() {
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

    public boolean isSeeingDetailsOfPoc2() {
        return event != null && event.contains("doOpenDetailsModal");
    }

    public boolean isTransitionOfPoc3() {
        return event != null && event.contains("doTransition");
    }

    public boolean isDebutInscriptionPoc3() {
        return event != null && event.contains("--> inscription_tunnel_statut");
    }

    public boolean isFinInscriptionPoc3() {
        return event != null && event.contains("--> inscription_tunnel_felicitations");
    }

    public @Nullable String fromScreen() {
        if(event == null) return null;
        int j = event.indexOf("doTransition");
        if(j < 0) return null;
        String s = event.substring(j + "doTransition".length());
        int i = s.indexOf("-->");
        if( i <= 0) return null;
        return s.substring(0, i).trim();
    }

    public @Nullable String toScreen() {
        if(event == null) return null;
        int i = event.indexOf("-->");
        if( i <= 0) return null;
        return event.substring(i + "-->".length()).trim();
    }

    public boolean isSuggestionsUpdate() {
        boolean answer =  event != null
                && event.contains("UpdateProfileService")
                && param instanceof Map
                && ((Map<?, ?>) param).containsKey("suggestions")
                /*
                && ((Map<?, ?>) param).get("suggestions") instanceof List
                && ((List<?>) ((Map<?, ?>) param).get("suggestions"))
                        .stream()
                        .anyMatch(o -> o instanceof SuggestionDTO && ( (SuggestionDTO) o).status() == SuggestionDTO.SUGG_APPROVED)
                 */
                ;
        return answer;
    }
}
