/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of Algorithmes-de-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.gouv.monprojetsup.data.carte.algos.filsim;

import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteEntree;
import fr.gouv.monprojetsup.data.carte.algos.Filiere;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFiliereSimilairesDetailsCalculFormationScore;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFilieresSimilairesDetailsCalcul;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFilieresSimilairesDetailsCalculFiliere;
import fr.gouv.monprojetsup.data.carte.algos.talg.Racinisation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.carte.algos.filsim.Recommandation.MAX_PROXIMITE_SEMANTIQUE;


public class AlgoFilieresSimilaires {

    private static final int MAX_NB_RECO = 10;

    /* nombre minimum de voeux communs pour que deux filieres soient
considérées comme proches */
    public static final int NB_CANDIDATS_COMMUNS_MIN = 50;
    static final double CORRELATION_MIN = 0.05;

    private static final int MIN_NB_CANDIDAT_POUR_RECO_FILIERE = 50;

    private static final Logger LOGGER = Logger.getLogger(AlgoFilieresSimilaires.class.getSimpleName());

    public static Map<Integer, AlgoFilieresSimilairesDetailsCalcul> calculerFilieresSimilaires(AlgoCarteEntree entree) {
        Map<Integer,AlgoFilieresSimilairesDetailsCalcul> result = new HashMap<>();
        for(int typeBac = 0; typeBac <=3; typeBac++) {
            result.put(typeBac, calculerFilieresSimilaires(entree, typeBac));
        }
        return result;
    }

    public static AlgoFilieresSimilairesDetailsCalcul calculerFilieresSimilaires(AlgoCarteEntree entree, int typeBac) {
        LOGGER.info("***********************************************************\n" +
                "**********************************************************\n" +
                "**** Filière similaires, type bac " + typeBac + " ********\n" +
                "**********************************************************\n" +
                "************************************************************");

        AlgoFilieresSimilairesDetailsCalcul details = calculerRecommandations(entree, typeBac);

        //on filtre les résultats avec une similarite nulle
        details.resultats.values().forEach( r -> r.recoOnisepCorrelation.removeIf(roc -> roc.score <= 0));

        //on filtre les résultats sans recommandation
        details.resultats.values().removeIf(r -> r.recoOnisepCorrelation.isEmpty());

        //on tronque les listes aux MAX_NB_RECO meilleurs résultats
        details.resultats.values().forEach( r -> {
            r.recoOnisepCorrelation.sort((o1, o2) -> o2.score - o1.score);
            while(r.recoOnisepCorrelation.size() > MAX_NB_RECO)
                r.recoOnisepCorrelation.remove(r.recoOnisepCorrelation.size() - 1);
        });

        return details;
    }

    public static Map<Pair<Integer, Integer>, Double> calculerCorrelations(AlgoCarteEntree entree, int typeBac) {
        Map<Integer, Set<Integer>> candidatsParFiliere = getCandidatsParFiliere(entree, typeBac);

        LOGGER.info("Mise à jour des voeux en commun dans les filieres");
        Map<Integer, CandidatsCommuns> candidatsCommuns = getCandidatsCommuns(candidatsParFiliere);

        //on précalcule  les corrélations
        LOGGER.info("PréCalcul des corrélations");
        return calculerCorrelations(candidatsParFiliere, candidatsCommuns);

    }

    private static Map<Pair<Integer, Integer>, Double> calculerCorrelations(
            Map<Integer, Set<Integer>> candidatsParFiliere,
            Map<Integer, CandidatsCommuns> candidatsCommuns
    ) {
        Set<Integer> filieres = new HashSet<>(candidatsParFiliere.keySet());

        Set<Integer> allCandidates = new HashSet<>();
        candidatsParFiliere.values().forEach(allCandidates::addAll);
        long nbCandidats = allCandidates.size();

        Map<Integer, Double> freq = candidatsParFiliere.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> 1.0 * e.getValue().size() / nbCandidats)
        );

        Map<Pair<Integer, Integer>, Double> resultat = new HashMap<>();

        candidatsParFiliere.keySet().forEach(f1 ->
        {
            CandidatsCommuns cc1 = candidatsCommuns.get(f1);
            filieres.stream().filter(f2 -> f2 >= f1).forEach(f2 -> {
                if (cc1.contains(f2)) {
                    Pair<Integer, Integer> p = Pair.of(f1, f2);
                    int nbf1 = candidatsParFiliere.get(f1).size();
                    int nbf2 = candidatsParFiliere.get(f2).size();
                    long nbf1f2 = cc1.nbCandidatsAnneePrecedente(f2);
                    long nbf1pasf2 = nbf1 - nbf1f2;
                    long nbf2pasf1 = nbf2 - nbf1f2;
                    long nbpasf1 = nbCandidats - nbf1;
                    long nbpasf2 = nbCandidats - nbf2;
                    long nbnif1nif2 = nbCandidats - nbf1 - nbf2 + nbf1f2;

                    double xbar = freq.getOrDefault(f1, 0.0);
                    double ybar = freq.getOrDefault(f2, 0.0);
                    double numer =
                            nbf1f2 * (1 - xbar) * (1 - ybar)
                                    + nbf1pasf2 * (1 - xbar) * (0 - ybar)
                                    + nbf2pasf1 * (0 - xbar) * (1 - ybar)
                                    + nbnif1nif2 * (0 - xbar) * (0 - ybar);

                    double denomsqx = nbf1 * (1 - xbar) * (1 - xbar)
                            + nbpasf1 * (0 - xbar) * (0 - xbar);

                    double denomsqy = nbf2 * (1 - ybar) * (1 - ybar)
                            + nbpasf2 * (0 - ybar) * (0 - ybar);

                    double corr = numer / Math.sqrt(denomsqx * denomsqy);
                    resultat.put(p, corr);
                    //on oublie pas le jumeau...
                    resultat.put(Pair.of(f2, f1), corr);

                }

            });
        });


        return resultat;

    }

    //renvoie pour chaque paire de filiere un score de proximite et le détail par mot clés
    private static Map<Pair<Integer, Integer>, Pair<Integer,Map<Integer, Long> > > calculerProximitesSemantiquesHorsApprentissage(
            AlgoCarteEntree entree, Racinisation rac) {

        Map<Integer, List<Integer>> indexRacinesMotsClesParFiliere = getIndexRacinesMotsClesParFiliere(entree, rac);

        //on précalcule le nombre d'occurences de chaque racine dans l'index
        Map<Integer, Long> nbOccurencesRacinesMotCle =
                indexRacinesMotsClesParFiliere.values().stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.groupingBy(index -> index, Collectors.counting()));

        Map<Pair<Integer, Integer>, Pair<Integer,Map<Integer, Long> >> resultat = new HashMap<>();
        for (Filiere f1 : entree.filieres.values()) {
            if (f1.apprentissage) continue;
            entree.filieres.values().forEach(f2 -> {
                if (f1 != f2 && !f2.apprentissage) {
                    Map<Integer, Long> motsClesProximite = new HashMap<>();
                    int proximiteSemantique = calculeProximiteSemantique(
                            f1.cle,
                            f2.cle,
                            indexRacinesMotsClesParFiliere,
                            nbOccurencesRacinesMotCle,
                            motsClesProximite
                    );
                    if(proximiteSemantique > 0) {
                        resultat.put(Pair.of(f1.cle,f2.cle), Pair.of(proximiteSemantique,motsClesProximite));
                    }
                }
            });
        }
        return resultat;
    }
    private AlgoFilieresSimilaires() {
    }

    public static AlgoFilieresSimilairesDetailsCalcul calculerRecommandations(AlgoCarteEntree entree) {
        return calculerRecommandations(entree,-1);
    }

    public static AlgoFilieresSimilairesDetailsCalcul calculerRecommandations(
            AlgoCarteEntree entree,
            int typeBac) {


        Racinisation rac = new Racinisation();


        Map<Integer, Set<Integer>> candidatsParFiliere = getCandidatsParFiliere(entree, typeBac);

        LOGGER.info("Mise à jour des voeux en commun dans les filieres");
        Map<Integer, CandidatsCommuns> candidatsCommuns = getCandidatsCommuns(candidatsParFiliere);

        //on précalcule  les corrélations
        LOGGER.info("PréCalcul des corrélations");
        Map<Pair<Integer, Integer>, Double> corrs = calculerCorrelations(candidatsParFiliere, candidatsCommuns);

        LOGGER.info("PréCalcul des proximité sémantiques, hors apprentissage");
        Map<Pair<Integer, Integer>, Pair<Integer, Map<Integer, Long>>> proximitesSemantique
                = calculerProximitesSemantiquesHorsApprentissage(entree, rac);

        AlgoFilieresSimilairesDetailsCalcul details = new AlgoFilieresSimilairesDetailsCalcul();

        //on fait une première passe
        //en laissant les formations en apprentissage de côté pour l'instant, elles sont post traitées ensuite
        for (Filiere f1 : entree.filieres.values()) {

            //posttraitement plus tard
            if (f1.apprentissage) continue;

            AlgoFilieresSimilairesDetailsCalculFiliere detail = new AlgoFilieresSimilairesDetailsCalculFiliere(f1);
            List<Recommandation> recommandations = new ArrayList<>();

            //on ajoute une reco de la filiere pour elle meme
            recommandations.add(new Recommandation(f1, f1, MAX_PROXIMITE_SEMANTIQUE, 1.0, new HashMap<>()));

            entree.filieres.values().forEach(f2 -> {
                        if (f1.cle != f2.cle && !f2.apprentissage) {

                            double corr = corrs.getOrDefault(Pair.of(f1.cle, f2.cle), 0.0);
                            Pair<Integer, Map<Integer, Long>> data
                                    = proximitesSemantique.getOrDefault(
                                            Pair.of(f1.cle, f2.cle), Pair.of(0,new HashMap<>())
                            );
                            int proximiteSemantique = data.getLeft();

                            if (estAcceptable(proximiteSemantique, corr, f1, f2)) {
                                Map<Integer, Long> motsClesProximite = data.getRight();
                                Map<String, Integer> scores = new HashMap<>();
                                motsClesProximite.forEach((key, value) -> {
                                    String rep = rac.getStem(key);
                                    if (rep != null) {
                                        scores.put(rep, Math.toIntExact(value));
                                    }
                                });
                                recommandations.add(new Recommandation(
                                        f1,
                                        f2,
                                        proximiteSemantique,
                                        corr,
                                        scores
                                ));
                            }
                        }
                    }
            );


            recommandations.sort((Recommandation r1, Recommandation r2) -> r2.correlation - r1.correlation);
            recommandations.forEach(r -> {
                        if (r.correlation > 0) {
                            detail.recoCorrelation.add(
                                    new AlgoFiliereSimilairesDetailsCalculFormationScore(r.filiere2, r.correlation)
                            );
                        }
                    }
            );

            recommandations.sort((Recommandation r1, Recommandation r2) -> r2.proximiteSemantique - r1.proximiteSemantique);
            recommandations.forEach(r -> {
                if (r.proximiteSemantique > 0) {
                    detail.recoMotsCles.add(new AlgoFiliereSimilairesDetailsCalculFormationScore(r.filiere2, r.proximiteSemantique, r.motsClesProximite)
                    );
                }
            });

            recommandations.sort((Recommandation r1, Recommandation r2) -> r2.similarite - r1.similarite);
            recommandations.forEach(r ->
                    detail.recoOnisepCorrelation.add(new AlgoFiliereSimilairesDetailsCalculFormationScore(r.filiere2, r.similarite))
            );

            details.resultats.put(f1.cle, detail);
        }

        LOGGER.info("Traitement des filières en apprentissage");

        AlgoFilieresSimilairesDetailsCalcul app = new AlgoFilieresSimilairesDetailsCalcul();

        Map<Integer, Filiere> appFilieres = new HashMap<>();
        entree.filieres.forEach((key, value) -> {
            if (value.apprentissage) {
                int filIdx = value.cleFiliere;
                //correction manuelle des entrées foireuses
                if (key == 1158 && filIdx == 1158) {
                    filIdx = 158;
                }
                if (key == 11425 && filIdx == 11425) {
                    filIdx = 10425;
                }
                if (key == 691020 && filIdx == 690019) {
                    filIdx = 691019;
                }
                appFilieres.put(filIdx, value);
            }
        });

        details.resultats.values().forEach(detail -> {
            Filiere f3 = appFilieres.get(detail.codeFiliere);
            if (f3 != null) {
                AlgoFilieresSimilairesDetailsCalculFiliere d = new AlgoFilieresSimilairesDetailsCalculFiliere(f3);
                detail.recoOnisepCorrelation.forEach(r -> {
                    Filiere f4 = appFilieres.get(r.filIdx);
                    if (f4 != null) {
                        d.recoOnisepCorrelation.add(new AlgoFiliereSimilairesDetailsCalculFormationScore(f4, r.score));
                    }
                });
                detail.recoCorrelation.forEach(r -> {
                    Filiere f4 = appFilieres.get(r.filIdx);
                    if (f4 != null) {
                        d.recoCorrelation.add(new AlgoFiliereSimilairesDetailsCalculFormationScore(f4, r.score));
                    }
                });
                detail.recoMotsCles.forEach(r -> {
                    Filiere f4 = appFilieres.get(r.filIdx);
                    if (f4 != null) {
                        d.recoMotsCles.add(new AlgoFiliereSimilairesDetailsCalculFormationScore(f4 ,r));

                    }
                });
                app.resultats.put(d.codeFiliere, d);
            }
        });

        details.resultats.putAll(app.resultats);

        return details;
    }

    public static HashMap<Integer, Set<Integer>> getCandidatsParFiliere(AlgoCarteEntree entree, int typeBac) {

        HashMap< Integer, Set<Integer>> result = new HashMap<>();

        Set<Integer> candidatsAvecBonTypeDeBac
                = entree.typesBacCandiats.entrySet()
                .stream().filter(e -> typeBac <= 0 || e.getValue() == typeBac)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        entree.candidatsParFiliere.forEach((flCod, value) -> {
            Set<Integer> s = new HashSet<>();
            result.put(flCod, s);
            value.forEach(
                    gCnCod -> {
                        if (candidatsAvecBonTypeDeBac.contains(gCnCod)) {
                            s.add(gCnCod);
                        }
                    }
            );

        });

        Set<Integer> noCandidats = result.entrySet().stream()
                .filter(e -> e.getValue().size() <= MIN_NB_CANDIDAT_POUR_RECO_FILIERE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        result.keySet().removeAll(noCandidats);

        return result;
    }

    public static Map<Integer, CandidatsCommuns> getCandidatsCommuns(Map<Integer, Set<Integer>> candidatsParFiliere) {


        Map<Integer, CandidatsCommuns> candidatsCommuns = new HashMap<>();

        candidatsParFiliere.forEach((f1, value) -> candidatsParFiliere
                .entrySet().stream().filter(e -> e.getKey() >= f1)
                .forEach(e2 -> {
                    int f2 = e2.getKey();
                    //nombre de candidats dans l'intersection
                    long cnt = value.stream()
                            .filter(gcncod -> e2.getValue().contains(gcncod))
                            .count();
                    if (cnt > 0) {
                        CandidatsCommuns cc1 = candidatsCommuns.computeIfAbsent(f1, z -> new CandidatsCommuns());
                        if (f1 != f2) {
                            CandidatsCommuns cc2 = candidatsCommuns.computeIfAbsent(f2, z -> new CandidatsCommuns());
                            cc1.ajouterFiliereAvecVoeuxCommuns(f2, cnt);
                            cc2.ajouterFiliereAvecVoeuxCommuns(f1, cnt);
                        } else {
                            cc1.ajouterFiliereAvecVoeuxCommuns(f1, cnt);
                        }
                    }
                }));
        return candidatsCommuns;

    }

    /* calcul de la proximite sémantique. Le dernier argument récupère le détail du calcul, pour debug */
    private static int calculeProximiteSemantique(int cle1, int cle2,
                                                  Map<Integer, List<Integer>> indexRacinesMotsClesParFiliere,
                                                  Map<Integer, Long> nbOccurencesRacinesMotCle,
                                                  Map<Integer, Long> motsClesProximite) {

            /* Proximité sémantique:
            somme des poids des différents mots clés Onisep en commun avec une autre filière
            un mot clé a poids 100 si il taggue 2 filières
            un mot clé a poids 66 si il taggue 3 filières
            un mot clé a poids 50 si il taggue 4 filières
            ...
             */
        int proximiteScore = 0;
        indexRacinesMotsClesParFiliere.get(cle1).forEach( m -> motsClesProximite.put(m,0L));
        motsClesProximite.keySet().retainAll(indexRacinesMotsClesParFiliere.get(cle2));
        for (Map.Entry<Integer, Long> e : motsClesProximite.entrySet()) {
            int motCle = e.getKey();
            long nbOccurences = nbOccurencesRacinesMotCle.getOrDefault(motCle, Long.MAX_VALUE);
            long score = (MAX_PROXIMITE_SEMANTIQUE / nbOccurences);
            proximiteScore += score;
            e.setValue(score);
        }
        return proximiteScore;
    }

    private static Map<Integer, List<Integer>> getIndexRacinesMotsClesParFiliere(AlgoCarteEntree entree, Racinisation rac) {
        Map<Integer, Set<String>> motsClesParFiliere = entree.filieres.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> getMotsCles(e.getValue())
                )
        );

        entree.filieres.values().forEach(f -> rac.ajouterLexique(motsClesParFiliere.get(f.cle))
        );
        Map<String, Integer> indexRacinisateur = rac.getIndex();

        return
                motsClesParFiliere.entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().map(indexRacinisateur::get).collect(Collectors.toList())
                        )
                );

    }

    private static boolean estAcceptable(int proximiteSemantique, double corr, Filiere f1, Filiere f2) {

        if(proximiteSemantique > 0) return true;

        //pas assez de candidats en commun
        long nbCandidatsCommuns = f1.candidatsCommuns.nbCandidatsAnneePrecedente(f2.cle);
        if(nbCandidatsCommuns < NB_CANDIDATS_COMMUNS_MIN) return false;

        //corrélation trop faible
        return corr >= CORRELATION_MIN;
    }

    public static Set<String> getMotsCles(Filiere filiere) {
        Set<String> motsCles = new HashSet<>();
        filiere.motsClesOnisepv2.forEach(w -> motsCles.add(w + " (oniv2)"));
        return motsCles;
    }
}

