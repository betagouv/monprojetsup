package fr.gouv.monprojetsup.suggestions.data.model.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AdmisMatiereBacAnneeStats implements Serializable {
    private final List<AdmisMatiereBacAnnee> stats;

    public AdmisMatiereBacAnneeStats(List<AdmisMatiereBacAnnee> stats) {
        this.stats = stats;
    }

    public AdmisMatiereBacAnneeStats() {
        this(new ArrayList<>());
    }

    public List<AdmisMatiereBacAnnee> stats() {
        return stats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AdmisMatiereBacAnneeStats) obj;
        return Objects.equals(this.stats, that.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stats);
    }

    @Override
    public String toString() {
        return "AdmisMatiereBacAnneeStats[" +
                "stats=" + stats + ']';
    }

}
