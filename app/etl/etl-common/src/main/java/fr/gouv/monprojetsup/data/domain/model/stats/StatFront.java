package fr.gouv.monprojetsup.data.domain.model.stats;

import java.util.stream.IntStream;

public record StatFront(
        /* taille de l'échantillon */
        int nb,
        /* Le rang de la catégorie contenant l'échantillon de rang ceil(0.25*nombre échantillons).
         * Par exemple si on fait 41 catégories, et que rangEch25 vaut 15,
         * alors la note du 25% ième candidat est dans [7.5,8.0[ */
        int rangEch5,
        int rangEch25,
        /* le rang de la valeur de la médiane. */
        int rangEch50,
        /* le rang de la catégorie contenant l'échantillon de rang ceil(0.75*nombre échantillons) */
        int rangEch75,
        int rangEch95,

        int[] frequencesCumulees

) {

    public static int last(int[] s) {
        if (s == null || s.length == 0) return 0;
        return s[s.length - 1];
    }

    public static StatFront getStatistique(int[] frequencesCumulees, boolean minimalForStudent) {
        if (frequencesCumulees == null || frequencesCumulees.length == 0) {
            throw new RuntimeException("bad compteurs for creating statistique");
        }
        final int nb = last(frequencesCumulees);
        return new StatFront(
                nb,
                IntStream.range(0, frequencesCumulees.length).filter(i -> frequencesCumulees[i]  >= Math.max(1,5 * nb / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter(i -> frequencesCumulees[i]  >= Math.max(1,25 * nb / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter(i -> frequencesCumulees[i]  >= Math.max(1,50 * nb / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter(i -> frequencesCumulees[i]  >= Math.max(1,75 * nb / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter(i -> frequencesCumulees[i]  >= Math.max(1,95 * nb / 100)).findFirst().orElse(-1),
                minimalForStudent ? null : frequencesCumulees
        );
    }
}

