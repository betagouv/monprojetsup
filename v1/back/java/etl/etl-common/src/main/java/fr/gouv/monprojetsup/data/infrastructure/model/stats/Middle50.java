package fr.gouv.monprojetsup.data.infrastructure.model.stats;

import org.apache.commons.lang3.tuple.Triple;

import java.io.Serializable;
import java.util.stream.IntStream;

public record Middle50(
        /* Le rang de la catégorie contenant l'échantillon de rang ceil(0.25*nombre échantillons).
        * Par exemple si on fait 41 catégories, et que rangEch25 vaut 15,
        * alors la note du 25% ième candidat est dans [7.5,8.0[ */
        int rangEch25,
        /* le rang de la valeur de la médiane. */
        int rangEch50,
        /* le rang de la catégorie contenant l'échantillon de rang ceil(0.75*nombre échantillons) */
        int rangEch75,
        int rangEch10,
        int rangEch90,
        int rangMax
) implements Serializable {

    /**
     *
     * @param frequencesCumulees
     *
     */
    public  Middle50(int[] frequencesCumulees) {
        this(
                IntStream.range(0, frequencesCumulees.length).filter( i -> frequencesCumulees[i]  >= Math.max(1,25 * Statistique.last(frequencesCumulees) / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter( i -> frequencesCumulees[i]  >= Math.max(1,50 * Statistique.last(frequencesCumulees) / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter( i -> frequencesCumulees[i]  >= Math.max(1,75 * Statistique.last(frequencesCumulees) / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter( i -> frequencesCumulees[i]  >= Math.max(1,10 * Statistique.last(frequencesCumulees) / 100)).findFirst().orElse(-1),
                IntStream.range(0, frequencesCumulees.length).filter( i -> frequencesCumulees[i]  >= Math.max(1,90 * Statistique.last(frequencesCumulees) / 100)).findFirst().orElse(-1),
                frequencesCumulees.length - 1
        );
    }

    public Triple<Double, Double, Double> getTriple() {
        return Triple.of(
                rangEch25() / 2.0,
                rangEch50() / 2.0,
                rangEch75() / 2.0
        );
    }

}
