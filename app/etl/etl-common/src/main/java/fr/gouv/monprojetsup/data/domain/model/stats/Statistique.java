package fr.gouv.monprojetsup.data.domain.model.stats;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public record Statistique(
        //les frequences cumulees
        int @NotNull [] frequencesCumulees,
        //le middle 50
        @NotNull Middle50 middle50
) implements Serializable {

    public static int last(int[] s) {
        if (s == null || s.length == 0) return 0;
        return s[s.length - 1];
    }

    /**
     * @param compteurs tableau des compteurs
     * @return une statistique construite à partir des compteurs
     */
    public static Statistique getStatistiqueFromCompteurs(int[] compteurs) {
        if (compteurs == null || compteurs.length == 0) {
            throw new RuntimeException("bad compteurs for creating statistique");
        }
        int[] frequencesCumulees = new int[compteurs.length];
        for (int i = 0; i < compteurs.length; i++) {
            frequencesCumulees[i] = compteurs[i] + ((i > 0) ? frequencesCumulees[i - 1] : 0);
        }
        Middle50 md = new Middle50(frequencesCumulees);
        return new Statistique(frequencesCumulees, md);
    }

    public  Statistique updateMiddle50FromFrequencesCumulees() {
        return new Statistique(frequencesCumulees, new Middle50(frequencesCumulees));
    }

    public static Statistique getStatAgregee(List<Statistique> toList) {
        if(toList.stream().anyMatch(e -> e.frequencesCumulees.length != PsupStatistiques.PRECISION_PERCENTILES)) {
            throw new RuntimeException("Cannot merge stats of different sizes");
        }
        int[] frequencesCumulees = new int[PsupStatistiques.PRECISION_PERCENTILES];
        for(int i = 0; i < PsupStatistiques.PRECISION_PERCENTILES; i++) {
            int finalI = i;
            frequencesCumulees[i] = toList.stream().mapToInt(e -> e.frequencesCumulees[finalI]).sum();
        }
        return new Statistique(frequencesCumulees, new Middle50(frequencesCumulees));
    }

    /**
     *
     * @param v note entre 0.0 et 20.0
     * @return rangCentile de la note
     */
    @SuppressWarnings("unused")
    public int rangCentile(double v) {
        int index = Math.toIntExact(Math.round(v / 20.0 * (frequencesCumulees.length-1)));
        index = Math.max(0, Math.min(frequencesCumulees.length - 1, index));
        int l = last(frequencesCumulees);
        if(l > 0) {
            return 100 * frequencesCumulees[index] / l;
        } else {
            return 50;
        }
    }

    public int nb() {
        return frequencesCumulees[PsupStatistiques.PRECISION_PERCENTILES - 1];
    }

    @Override
    public String toString() {
        return middle50.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistique that)) return false;
        if (!middle50.equals(that.middle50)) return false;
        if (frequencesCumulees.length != that.frequencesCumulees.length) return false;
        for (int i = 0; i < frequencesCumulees.length; i++) {
            if (frequencesCumulees[i] != that.frequencesCumulees[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int element : frequencesCumulees) {
            result = 31 * result + element;
        }
        result = 31 * result + middle50.hashCode();
        return result;
    }
}

