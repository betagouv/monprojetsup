package fr.gouv.monprojetsup.app.db.model;

public record Classe(
        String index,
        String name,
        Niveau niveau,
        boolean isPro,
        boolean isTechno,
        String serie,

        String expeENSGroup) {

    public enum Niveau {seconde, premiere, terminale}
}
