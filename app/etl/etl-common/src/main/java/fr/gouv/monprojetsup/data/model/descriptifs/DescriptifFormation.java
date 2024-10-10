package fr.gouv.monprojetsup.data.model.descriptifs;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

@Data
public class DescriptifFormation implements Serializable {
    private String presentation;
    private String poursuite;
    private String metiers;
    private @Nullable String url;
    private String error;
    private @NotNull String type;
    private Set<String> multiUrls = new HashSet<>();
    private String summary;
    private String summaryFormation;
    private Map<String, String> mpsData = new HashMap<>();
    private List<String> correctedUrls = new ArrayList<>();

    public DescriptifFormation(
            String presentation,
            String poursuite,
            String metiers,
            @Nullable String url,
            String error,
            @NotNull String type,

            Collection<String> multiUrls,

            String summary,
            String summaryFormation,

            Map<String, String> mpsData
    ) {
        this.presentation = presentation;
        this.poursuite = poursuite;
        this.metiers = metiers;
        this.url = url;
        this.error = error;
        this.type = type;
        if (multiUrls != null) this.multiUrls.addAll(multiUrls);
        this.summary = summary;
        this.summaryFormation = summaryFormation;
        this.mpsData.putAll(mpsData);
    }


    public DescriptifFormation(String presentation, String url, String type) {
        this(presentation, null, null, url, null, type, null, null, null, new HashMap<>());
    }

    public DescriptifFormation(String summary, String summaryFormation, String url, String type) {
        this(null, null, null, url, null, type, null, summary, summaryFormation, new HashMap<>());
    }

    public DescriptifFormation(Map<String, String> mpsData) {
        this(null, null, null, null, null, "summary", null, null, null, mpsData);
    }


    public static DescriptifFormation addToDescriptif(String addendum, DescriptifFormation o) {
        if (o == null) {
            return new DescriptifFormation(addendum, null, "addendum from null");
        }
        if (o.type.equals("error")) {
            return new DescriptifFormation(addendum, o.url, "addedndum from error");
        }
        return new DescriptifFormation(
                addendum + o.presentation,
                o.poursuite,
                o.metiers,
                o.url,
                o.error,
                "addendum + " + o.type,
                o.multiUrls,
                o.summary,
                o.summaryFormation,
                new HashMap<>()
        );
    }

    public static DescriptifFormation setSummary(DescriptifFormation o, String summary) {
        if (o == null) {
            return new DescriptifFormation(summary, null, "summary");
        } else {
            return new DescriptifFormation(
                    o.presentation,
                    o.poursuite,
                    o.metiers,
                    o.url,
                    o.error,
                    o.type,
                    o.multiUrls,
                    summary,
                    null,
                    new HashMap<>()
            );
        }
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isRecherche() {
        return error != null && error.startsWith("recherche");
    }

    public String presentation() {
        return presentation;
    }

    public String metiers() {
        return metiers;
    }

    public @Nullable String url() {
        return url;
    }

    public @NotNull String type() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DescriptifFormation) obj;
        return Objects.equals(this.presentation, that.presentation) &&
                Objects.equals(this.poursuite, that.poursuite) &&
                Objects.equals(this.metiers, that.metiers) &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.error, that.error) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.multiUrls, that.multiUrls) &&
                Objects.equals(this.summary, that.summary) &&
                Objects.equals(this.summaryFormation, that.summaryFormation) &&
                Objects.equals(this.mpsData, that.mpsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(presentation, poursuite, metiers, url, error, type, multiUrls, summary, summaryFormation, mpsData);
    }

    @Override
    public String toString() {
        return "Descriptif[" +
                "presentation=" + presentation + ", " +
                "poursuite=" + poursuite + ", " +
                "metiers=" + metiers + ", " +
                "url=" + url + ", " +
                "error=" + error + ", " +
                "type=" + type + ", " +
                "multiUrls=" + multiUrls + ", " +
                "summary=" + summary + ", " +
                "summaryFormation=" + summaryFormation + ", " +
                "mpsData=" + mpsData + ']';
    }

    public @Nullable String getDescriptifGeneralFront() {
        if (summary != null && !summary.isEmpty()) return summary;
        if (presentation != null && !presentation.isEmpty()) return presentation;
        return null;
    }

    public String getDescriptifDiplomeFront() {
        if (summaryFormation != null && !summaryFormation.isEmpty()) return summaryFormation;
        return null;
    }

}
