package fr.gouv.monprojetsup.data.domain.model.psup;

import org.apache.commons.lang3.tuple.Pair;

record CorrelationPaireFiliere(int fl1, int fl2, double corr) {

    public CorrelationPaireFiliere(Pair<Integer, Integer> integerIntegerPaire, Double aDouble) {
        this(integerIntegerPaire.getLeft(), integerIntegerPaire.getRight(), aDouble);
    }
}
