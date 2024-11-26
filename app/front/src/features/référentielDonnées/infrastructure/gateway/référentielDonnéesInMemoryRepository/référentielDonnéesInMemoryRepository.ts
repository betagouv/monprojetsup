/* eslint-disable @typescript-eslint/require-await */
import { type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type RéférentielDonnéesRepository } from "@/features/référentielDonnées/infrastructure/référentielDonnéesRepository.interface";

export class RéférentielDonnéesInMemoryRepository implements RéférentielDonnéesRepository {
  private RÉFÉRENTIEL_DONNÉES: RéférentielDonnées = {
    élève: {
      situations: ["aucune_idee", "quelques_pistes", "projet_precis"],
      classes: ["seconde", "premiere", "terminale"],
      duréesÉtudesPrévue: ["indifferent", "courte", "longue", "aucune_idee"],
      alternances: ["pas_interesse", "indifferent", "interesse", "tres_interesse"],
    },
    bacs: [
      {
        id: "Générale",
        nom: "Bac Général",
        idCarteParcoursup: "1",
        spécialités: [
          {
            id: "mat709",
            nom: "Théâtre-Expression dramatique (TED)",
          },
          {
            id: "mat1066",
            nom: "Littérature et langues et cultures de l'Antiquité: Grec (LLCA)",
          },
          {
            id: "mat1065",
            nom: "Numérique et Sciences Informatiques (NSI)",
          },
          {
            id: "mat10001076",
            nom: "Langues, littératures et cultures étrangères et régionales (LLCE)",
          },
          {
            id: "mat20001076",
            nom: "Anglais Monde Contemporain (AMC)",
          },
          {
            id: "mat1063",
            nom: "Littérature et langues et cultures de l’Antiquité: Latin (LLCA)",
          },
          {
            id: "mat1062",
            nom: "Histoire-Géographie, Géopolitique et Sciences politiques (HGGSP)",
          },
          {
            id: "mat1095",
            nom: "Éducation Physique, Pratiques Et Culture Sportives",
          },
          {
            id: "mat1061",
            nom: "Sciences de l'ingénieur et sciences physiques (SISP)",
          },
          {
            id: "mat4",
            nom: "Sciences de l'ingénieur (SI)",
          },
          {
            id: "mat5",
            nom: "Biologie/Ecologie (BE)",
          },
          {
            id: "mat710",
            nom: "Cinéma-Audiovisuel (CA)",
          },
          {
            id: "mat700",
            nom: "Mathématiques",
          },
          {
            id: "mat1067",
            nom: "Humanités, Littérature et Philosophie (HLP)",
          },
          {
            id: "mat701",
            nom: "Physique-Chimie (PC)",
          },
          {
            id: "mat702",
            nom: "Sciences de la vie et de la Terre (SVT)",
          },
          {
            id: "mat703",
            nom: "Sciences Economiques et Sociales (SES)",
          },
          {
            id: "mat704",
            nom: "Musique",
          },
          {
            id: "mat705",
            nom: "Danse",
          },
          {
            id: "mat706",
            nom: "Arts Plastiques (AP)",
          },
          {
            id: "mat707",
            nom: "Histoire des Arts",
          },
          {
            id: "mat708",
            nom: "Arts du Cirque (AC)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 4,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 7,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 12,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 19,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 28,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 37,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 47,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 57,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 66,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 75,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 82,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 88,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 93,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 96,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STAV",
        nom: "Bac STAV",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat321",
            nom: "Biologie et physiopathologie humaines (BPH)",
          },
          {
            id: "mat1039",
            nom: "Chimie, biologie et physiopathologie humaines (CBPH)",
          },
          {
            id: "mat1079",
            nom: "Territoires et société (TS)",
          },
          {
            id: "mat1078",
            nom: "Territoires et technologie (TT)",
          },
          {
            id: "mat1077",
            nom: "Gestion des ressources et alimentation",
          },
          {
            id: "mat1052",
            nom: "Physique-Chimie pour la santé (PCS)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 4,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 9,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 16,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 25,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 37,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 50,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 62,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 73,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 82,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 89,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 93,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 95,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STHR",
        nom: "Bac STHR",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat1008",
            nom: "Enseignement scientifique alimentation - environnement",
          },
          {
            id: "mat1006",
            nom: "Economie et gestion hôtelière (EGH)",
          },
          {
            id: "mat1051",
            nom: "Sciences et technologies culinaires et services-ESAE (STES)",
          },
          {
            id: "mat1050",
            nom: "Sciences et technologies culinaires et des services (STCS)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 4,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 8,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 13,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 21,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 31,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 42,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 53,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 62,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 72,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 80,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 86,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 92,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 95,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "ST2S",
        nom: "Bac ST2S",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat320",
            nom: "Sciences et techniques sanitaires et sociales (STSS)",
          },
          {
            id: "mat321",
            nom: "Biologie et physiopathologie humaines (BPH)",
          },
          {
            id: "mat1039",
            nom: "Chimie, biologie et physiopathologie humaines (CBPH)",
          },
          {
            id: "mat1052",
            nom: "Physique-Chimie pour la santé (PCS)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 5,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 9,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 15,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 24,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 35,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 47,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 59,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 70,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 79,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 86,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 91,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 95,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 97,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STMG",
        nom: "Bac STMG",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat1038",
            nom: "Droit et Economie (DE)",
          },
          {
            id: "mat887",
            nom: "Gestion et Finance",
          },
          {
            id: "mat888",
            nom: "Systèmes d'information et de Gestion (SIG)",
          },
          {
            id: "mat1054",
            nom: "Management",
          },
          {
            id: "mat1053",
            nom: "Sciences de la gestion et numérique (SGN)",
          },
          {
            id: "mat282",
            nom: "Mercatique",
          },
          {
            id: "mat1009",
            nom: "Ressources humaines et communication (RHC)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 6,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 11,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 19,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 29,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 40,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 53,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 64,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 75,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 83,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 89,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 94,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 96,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STD2A",
        nom: "Bac STD2A",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat2",
            nom: "Physique/Chimie (PC)",
          },
          {
            id: "mat1059",
            nom: "Outils et langages numériques (OLN)",
          },
          {
            id: "mat1058",
            nom: "Design et métiers d'art (DMA)",
          },
          {
            id: "mat1043",
            nom: "Analyse et méthodes en design (AMD)",
          },
          {
            id: "mat1042",
            nom: "Conception et création en design et métiers d'art (CCDMA)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 6,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 10,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 18,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 26,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 35,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 46,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 59,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 69,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 80,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 88,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 93,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 97,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STI2D",
        nom: "Bac STI2D",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat1056",
            nom: "Innovation Technologique (IT)",
          },
          {
            id: "mat1055",
            nom: "Ingénierie et développement durable (IDD)",
          },
          {
            id: "mat1096",
            nom: "Ingénierie, innovation et développement durable (IIDD)",
          },
          {
            id: "mat1040",
            nom: "Physique-Chimie et Mathématiques (PCM)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 7,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 12,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 21,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 31,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 43,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 54,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 66,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 75,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 83,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 89,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 93,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 96,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "STL",
        nom: "Bac STL",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat740",
            nom: "Biotechnologies",
          },
          {
            id: "mat1057",
            nom: "Biochimie-Biologie (BB)",
          },
          {
            id: "mat1041",
            nom: "Biochimie-Biologie-Biotechnologie (BBB)",
          },
          {
            id: "mat1040",
            nom: "Physique-Chimie et Mathématiques (PCM)",
          },
          {
            id: "mat719",
            nom: "Sciences physiques et chimiques en laboratoire (SPCL)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 7,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 12,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 19,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 28,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 38,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 48,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 59,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 69,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 78,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 85,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 90,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 94,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 97,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "S2TMD",
        nom: "Bac S2TMD",
        idCarteParcoursup: "2",
        spécialités: [
          {
            id: "mat1049",
            nom: "Pratique chorégraphiques (PC)",
          },
          {
            id: "mat1048",
            nom: "Culture et sciences chorégraphiques (CSC)",
          },
          {
            id: "mat1047",
            nom: "Pratique musicale (PM)",
          },
          {
            id: "mat1046",
            nom: "Culture et sciences musicale (CSM)",
          },
          {
            id: "mat1045",
            nom: "Culture et sciences théâtrale (CST)",
          },
          {
            id: "mat1044",
            nom: "Pratique théâtrale (PT)",
          },
          {
            id: "mat1060",
            nom: "Economie, droit et environnement du spectacle vivant (EDESV)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 5,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 12,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 18,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 26,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 35,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 43,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 53,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 64,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 70,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 79,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 85,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 91,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 95,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 98,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "P",
        nom: "Bac Professionnel",
        idCarteParcoursup: "3",
        spécialités: [],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 2,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 4,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 8,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 13,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 21,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 31,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 42,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 53,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 65,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 75,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 83,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 90,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 94,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 97,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "PA",
        nom: "Bac Professionnel Agricole",
        idCarteParcoursup: "3",
        spécialités: [],
        statistiquesAdmission: {
          parMoyenneGénérale: [
            {
              moyenne: 0,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 0.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 1.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 2.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 3.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 4.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 5.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 6.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 7.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 8.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 0,
            },
            {
              moyenne: 9,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 1,
            },
            {
              moyenne: 9.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 3,
            },
            {
              moyenne: 10,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 6,
            },
            {
              moyenne: 10.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 11,
            },
            {
              moyenne: 11,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 19,
            },
            {
              moyenne: 11.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 28,
            },
            {
              moyenne: 12,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 38,
            },
            {
              moyenne: 12.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 50,
            },
            {
              moyenne: 13,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 61,
            },
            {
              moyenne: 13.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 71,
            },
            {
              moyenne: 14,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 80,
            },
            {
              moyenne: 14.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 87,
            },
            {
              moyenne: 15,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 92,
            },
            {
              moyenne: 15.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 95,
            },
            {
              moyenne: 16,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 97,
            },
            {
              moyenne: 16.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 17.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 18.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 99,
            },
            {
              moyenne: 19,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
            {
              moyenne: 19.5,
              pourcentageAdmisAyantCetteMoyenneOuMoins: 100,
            },
          ],
        },
      },
      {
        id: "NC",
        nom: "Non-communiqué",
        idCarteParcoursup: "0",
        spécialités: [
          {
            id: "mat1067",
            nom: "Humanités, Littérature et Philosophie (HLP)",
          },
          {
            id: "mat705",
            nom: "Danse",
          },
          {
            id: "mat706",
            nom: "Arts Plastiques (AP)",
          },
          {
            id: "mat707",
            nom: "Histoire des Arts",
          },
          {
            id: "mat708",
            nom: "Arts du Cirque (AC)",
          },
          {
            id: "mat701",
            nom: "Physique-Chimie (PC)",
          },
          {
            id: "mat702",
            nom: "Sciences de la vie et de la Terre (SVT)",
          },
          {
            id: "mat703",
            nom: "Sciences Economiques et Sociales (SES)",
          },
          {
            id: "mat704",
            nom: "Musique",
          },
          {
            id: "mat1077",
            nom: "Gestion des ressources et alimentation",
          },
          {
            id: "mat700",
            nom: "Mathématiques",
          },
          {
            id: "mat740",
            nom: "Biotechnologies",
          },
          {
            id: "mat1038",
            nom: "Droit et Economie (DE)",
          },
          {
            id: "mat1039",
            nom: "Chimie, biologie et physiopathologie humaines (CBPH)",
          },
          {
            id: "mat1078",
            nom: "Territoires et technologie (TT)",
          },
          {
            id: "mat1079",
            nom: "Territoires et société (TS)",
          },
          {
            id: "mat709",
            nom: "Théâtre-Expression dramatique (TED)",
          },
          {
            id: "mat719",
            nom: "Sciences physiques et chimiques en laboratoire (SPCL)",
          },
          {
            id: "mat1043",
            nom: "Analyse et méthodes en design (AMD)",
          },
          {
            id: "mat1044",
            nom: "Pratique théâtrale (PT)",
          },
          {
            id: "mat710",
            nom: "Cinéma-Audiovisuel (CA)",
          },
          {
            id: "mat1041",
            nom: "Biochimie-Biologie-Biotechnologie (BBB)",
          },
          {
            id: "mat1042",
            nom: "Conception et création en design et métiers d'art (CCDMA)",
          },
          {
            id: "mat1040",
            nom: "Physique-Chimie et Mathématiques (PCM)",
          },
          {
            id: "mat282",
            nom: "Mercatique",
          },
          {
            id: "mat1008",
            nom: "Enseignement scientifique alimentation - environnement",
          },
          {
            id: "mat1049",
            nom: "Pratique chorégraphiques (PC)",
          },
          {
            id: "mat1006",
            nom: "Economie et gestion hôtelière (EGH)",
          },
          {
            id: "mat1047",
            nom: "Pratique musicale (PM)",
          },
          {
            id: "mat1048",
            nom: "Culture et sciences chorégraphiques (CSC)",
          },
          {
            id: "mat1045",
            nom: "Culture et sciences théâtrale (CST)",
          },
          {
            id: "mat1046",
            nom: "Culture et sciences musicale (CSM)",
          },
          {
            id: "mat1009",
            nom: "Ressources humaines et communication (RHC)",
          },
          {
            id: "mat888",
            nom: "Systèmes d'information et de Gestion (SIG)",
          },
          {
            id: "mat1054",
            nom: "Management",
          },
          {
            id: "mat1055",
            nom: "Ingénierie et développement durable (IDD)",
          },
          {
            id: "mat1052",
            nom: "Physique-Chimie pour la santé (PCS)",
          },
          {
            id: "mat1096",
            nom: "Ingénierie, innovation et développement durable (IIDD)",
          },
          {
            id: "mat887",
            nom: "Gestion et Finance",
          },
          {
            id: "mat1053",
            nom: "Sciences de la gestion et numérique (SGN)",
          },
          {
            id: "mat20001076",
            nom: "Anglais Monde Contemporain (AMC)",
          },
          {
            id: "mat1050",
            nom: "Sciences et technologies culinaires et des services (STCS)",
          },
          {
            id: "mat320",
            nom: "Sciences et techniques sanitaires et sociales (STSS)",
          },
          {
            id: "mat1095",
            nom: "Éducation Physique, Pratiques Et Culture Sportives",
          },
          {
            id: "mat1051",
            nom: "Sciences et technologies culinaires et services-ESAE (STES)",
          },
          {
            id: "mat321",
            nom: "Biologie et physiopathologie humaines (BPH)",
          },
          {
            id: "mat2",
            nom: "Physique/Chimie (PC)",
          },
          {
            id: "mat10001076",
            nom: "Langues, littératures et cultures étrangères et régionales (LLCE)",
          },
          {
            id: "mat1058",
            nom: "Design et métiers d'art (DMA)",
          },
          {
            id: "mat1059",
            nom: "Outils et langages numériques (OLN)",
          },
          {
            id: "mat1056",
            nom: "Innovation Technologique (IT)",
          },
          {
            id: "mat5",
            nom: "Biologie/Ecologie (BE)",
          },
          {
            id: "mat4",
            nom: "Sciences de l'ingénieur (SI)",
          },
          {
            id: "mat1057",
            nom: "Biochimie-Biologie (BB)",
          },
          {
            id: "mat1065",
            nom: "Numérique et Sciences Informatiques (NSI)",
          },
          {
            id: "mat1066",
            nom: "Littérature et langues et cultures de l'Antiquité: Grec (LLCA)",
          },
          {
            id: "mat1063",
            nom: "Littérature et langues et cultures de l’Antiquité: Latin (LLCA)",
          },
          {
            id: "mat1061",
            nom: "Sciences de l'ingénieur et sciences physiques (SISP)",
          },
          {
            id: "mat1062",
            nom: "Histoire-Géographie, Géopolitique et Sciences politiques (HGGSP)",
          },
          {
            id: "mat1060",
            nom: "Economie, droit et environnement du spectacle vivant (EDESV)",
          },
        ],
        statistiquesAdmission: {
          parMoyenneGénérale: [],
        },
      },
    ],
    centresIntérêts: [
      {
        id: "avoir une activité physique",
        nom: "Avoir une activité physique",
        emoji: "🤸",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci18",
            nom: "Conduire",
            emoji: "🏎",
            description: null,
          },
          {
            id: "ci17",
            nom: "Des sensations fortes",
            emoji: "🔥",
            description: null,
          },
          {
            id: "ci16",
            nom: "Pratiquer une activité sportive",
            emoji: "⛹",
            description: null,
          },
        ],
      },
      {
        id: "communiquer et informer",
        nom: "Communiquer et informer",
        emoji: "🗣",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci6",
            nom: "Communiquer et informer",
            emoji: "🗣",
            description: null,
          },
        ],
      },
      {
        id: "découvrir le monde",
        nom: "Découvrir le monde",
        emoji: "🌎",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci11",
            nom: "Apprendre de nouvelles langues",
            emoji: "🇬🇧",
            description: null,
          },
          {
            id: "ci12",
            nom: "Travailler dans un milieu multiculturel",
            emoji: "🛤",
            description: null,
          },
          {
            id: "ci10",
            nom: "Voyager",
            emoji: "🚅",
            description: null,
          },
        ],
      },
      {
        id: "découvrir, enquêter et rechercher",
        nom: "Découvrir, enquêter et rechercher",
        emoji: "🧐",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci19",
            nom: "Faire des expériences",
            emoji: "🧪",
            description: null,
          },
          {
            id: "ci20",
            nom: "Prêter attention au détail",
            emoji: "🔎",
            description: null,
          },
        ],
      },
      {
        id: "développer les nouvelles technologies",
        nom: "Développer les nouvelles technologies",
        emoji: "💻",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci27",
            nom: "Développer les nouvelles technologies",
            emoji: "💻",
            description: null,
          },
        ],
      },
      {
        id: "faire respecter la loi",
        nom: "Faire respecter la loi",
        emoji: "🧑‍⚖",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci21",
            nom: "Faire respecter la loi",
            emoji: "🧑‍⚖",
            description: null,
          },
        ],
      },
      {
        id: "jongler avec les chiffres",
        nom: "Jongler avec les chiffres",
        emoji: "💯",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci29",
            nom: "Jongler avec les chiffres",
            emoji: "💯",
            description: null,
          },
        ],
      },
      {
        id: "mener une équipe",
        nom: "Mener une équipe",
        emoji: "🚀",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci25",
            nom: "Diriger une équipe",
            emoji: "👍",
            description: null,
          },
          {
            id: "ci26",
            nom: "Organiser les choses",
            emoji: "📑",
            description: null,
          },
        ],
      },
      {
        id: "prendre soin des autres",
        nom: "Prendre soin des autres",
        emoji: "🧡",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci14",
            nom: "Aider les autres",
            emoji: "🫂",
            description: null,
          },
          {
            id: "ci15",
            nom: "Aller vers les gens",
            emoji: "😄",
            description: null,
          },
          {
            id: "ci13",
            nom: "Soigner",
            emoji: "🏥",
            description: null,
          },
        ],
      },
      {
        id: "protéger la nature et les animaux",
        nom: "Protéger la nature et les animaux",
        emoji: "🌱",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci4",
            nom: "Défendre l'écologie",
            emoji: "♻",
            description: null,
          },
          {
            id: "ci3",
            nom: "Travailler au contact de la nature",
            emoji: "🌳",
            description: null,
          },
          {
            id: "ci5",
            nom: "Travailler avec les animaux",
            emoji: "😺",
            description: null,
          },
        ],
      },
      {
        id: "transmettre et m'occuper des plus jeunes",
        nom: "Transmettre et m'occuper des plus jeunes",
        emoji: "🧑‍💻",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci2",
            nom: "Enseigner",
            emoji: "👶",
            description: null,
          },
          {
            id: "ci1",
            nom: "Travailler avec des enfants",
            emoji: "🙋",
            description: null,
          },
        ],
      },
      {
        id: "travailler dans le monde de l'art",
        nom: "Travailler dans le monde de l'art",
        emoji: "🎥",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci23",
            nom: "Découvrir l'envers du décor",
            emoji: "🎭",
            description: null,
          },
          {
            id: "ci24",
            nom: "Écrire ou lire",
            emoji: "✍",
            description: null,
          },
          {
            id: "ci22",
            nom: "Être artiste",
            emoji: "🎨",
            description: null,
          },
        ],
      },
      {
        id: "travailler de mes mains",
        nom: "Travailler de mes mains",
        emoji: "🔨",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci7",
            nom: "Bricoler",
            emoji: "🙌",
            description: null,
          },
          {
            id: "ci8",
            nom: "Créer quelque chose de mes mains",
            emoji: "🪛",
            description: null,
          },
          {
            id: "ci9",
            nom: "Cuisiner",
            emoji: "🧑‍🍳",
            description: null,
          },
        ],
      },
      {
        id: "vendre, développer un commerce",
        nom: "Vendre, développer un commerce",
        emoji: "🤝",
        sousCatégoriesCentreIntérêt: [
          {
            id: "ci28",
            nom: "Vendre, développer un commerce",
            emoji: "🤝",
            description: null,
          },
        ],
      },
    ],
    domainesProfessionnels: [
      {
        id: "agriculture",
        nom: "Agriculture",
        emoji: "🥕",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom1",
            nom: "Agriculture - forêt",
            emoji: "🚜",
            description: null,
          },
          {
            id: "dom2",
            nom: "Élevage - soins aux animaux",
            emoji: "🐮",
            description: null,
          },
        ],
      },
      {
        id: "architecture et construction",
        nom: "Architecture et construction",
        emoji: "🚧",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom8",
            nom: "Aménagement du territoire - urbanisme",
            emoji: "🌄",
            description:
              "Tu souhaites participer à l'organisation du territoire en infrastructures et en services publics et veux contribuer au bon développement de la ville.",
          },
          {
            id: "dom9",
            nom: "Architecture",
            emoji: "🏚️",
            description:
              "Dessiner et créer des nouveaux bâtiment et des maisons individuelles, encadrer une construction, rénover d'anciens immeubles, les possibilités sont vastes.",
          },
          {
            id: "dom10",
            nom: "Bâtiment - construction",
            emoji: "🏗️",
            description:
              "Pour travailler dans le BTP quel que soit le métier qui t'attire (menuisierie, maçonnerie, génie civil, finition, ameublement etc.), c'est par ici !",
          },
        ],
      },
      {
        id: "arts et culture",
        nom: "Arts et culture",
        emoji: "🎨",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom5",
            nom: "Art",
            emoji: "🎨",
            description: "Tu veux peindre, sculpter, dessiner, concevoir des objets de design, ou du graphisme...?",
          },
          {
            id: "dom3",
            nom: "Artisanat - création",
            emoji: "💎",
            description: "Poterie, dorure, bijouterie... si tu veux créer, c'est par ici !",
          },
          {
            id: "dom4",
            nom: "Arts du spectacle",
            emoji: "🎭",
            description: "Pour travailler dans la musique, le théâtre, la danse, la production de spectacles, etc.",
          },
          {
            id: "dom6",
            nom: "Audiovisuel",
            emoji: "🎥",
            description:
              "C'est la voie pour toi si tu veux travailler à la télévision, au cinéma, à la radio, et bien d'autres secteurs encore.",
          },
          {
            id: "dom7",
            nom: "Culture et patrimoine",
            emoji: "🏰",
            description: "Passionné de châteaux, d'histoire et d'histoire de l'art, c'est ton chemin !",
          },
        ],
      },
      {
        id: "commerce et gestion",
        nom: "Commerce et gestion",
        emoji: "💰",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom11",
            nom: "Banque - assurances",
            emoji: "💵",
            description:
              "Si tu veux travailler dans le secteur de la finance, directement au sein d'une banque ou d'une compagnie d'assurance.",
          },
          {
            id: "dom12",
            nom: "Commerce - vente",
            emoji: "🛒",
            description:
              "Tu as envie de faire une école de commerce, de travailler dans l'achat et l'approvisionnement ou encore de faire du marketing ?",
          },
          {
            id: "dom13",
            nom: "Gestion des entreprises - comptabilité",
            emoji: "📈",
            description:
              "Pour te former aux métiers qui permettent à une entreprise d'être bien gérer (RH, secrétariat, comptabilité etc).",
          },
          {
            id: "dom14",
            nom: "Immobilier",
            emoji: "🏤",
            description:
              "Tout ce qui tourne autour de la vente ou de la location d'appartements, de maisons ou de bureaux, et de la gestion d'immeubles pour les particuliers ou les professionnels.",
          },
        ],
      },
      {
        id: "droit",
        nom: "Droit",
        emoji: "⚖️",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom16",
            nom: "Droit",
            emoji: "👩🏽‍⚖️",
            description:
              "Tu veux faire des études de droit et connaître les règles qui régissent les rapports individuels et sociaux de la société ? C'est par ici !",
          },
          {
            id: "dom15",
            nom: "Métiers de la justice",
            emoji: "⚖️",
            description:
              "Si tu veux travailler dans le secteur de la justice (tribunal, administration, police judiciaire et autres).",
          },
        ],
      },
      {
        id: "enseignement",
        nom: "Enseignement",
        emoji: "🎓",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom17",
            nom: "Enseignement - formation",
            emoji: "🎓",
            description: "Si tu souhaites devenir professeur, quel que soit le profil des élèves.",
          },
        ],
      },
      {
        id: "environnement",
        nom: "Environnement",
        emoji: "🌎",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom38",
            nom: "Environnement - développement durable",
            emoji: "♻️",
            description:
              "Si tu as envie de répondre aux enjeux environnementaux et de développer de nouvelles manières de consommer plus respecteuses de la planète.",
          },
          {
            id: "dom37",
            nom: "Énergies",
            emoji: "⚡",
            description:
              "Pour développer les réseaux d'énergie et penser aux solutions pour notre consommation énergétique de demain.",
          },
        ],
      },
      {
        id: "ingénierie et industries",
        nom: "Ingénierie et industries",
        emoji: "🏭",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom18",
            nom: "Industries",
            emoji: "🏭",
            description: null,
          },
          {
            id: "dom19",
            nom: "Logistique - transport",
            emoji: "🚚",
            description:
              "Pour apprendre à gérer les déplacements complexes qui permettent d'acheminer les produits à leurs destinataires.",
          },
          {
            id: "dom20",
            nom: "Télécommunications",
            emoji: "📱",
            description:
              "Pour devenir un ou une pro de la communication à distance (via l'optique, les ondes magnétiques, la radioélectricité, etc).",
          },
        ],
      },
      {
        id: "langues et communication",
        nom: "Langues et communication",
        emoji: "🗣",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom22",
            nom: "Information communication - multimédia",
            emoji: "🎥",
            description:
              "Pour travailler dans la publicité ou le multimédia, mais aussi en bibliothèque, dans l'édition, le journalisme...",
          },
          {
            id: "dom23",
            nom: "Lettres - langues",
            emoji: "📚",
            description:
              "Si tu veux étudier la littérature sous toutes ses formes et apprendre de nouvelles langues et les cultures associées, c'est par ici !",
          },
        ],
      },
      {
        id: "loisirs et tourisme",
        nom: "Loisirs et tourisme",
        emoji: "🥳",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom24",
            nom: "Hôtellerie - restauration",
            emoji: "🍴",
            description:
              "Pour travailler dans tout types de restaurants, brasseries, cafés, en cuisine ou en salle ou dans l'hébergement touristique.",
          },
          {
            id: "dom25",
            nom: "Sport",
            emoji: "⚽️",
            description:
              "Si tu veux transmettre ta passion du sport ou travailler dans les nombreux métiers du secteur, tu es au bon endroit.",
          },
          {
            id: "dom26",
            nom: "Tourisme",
            emoji: "🏖",
            description:
              "Pour devenir un ou une expert(e) du secteur du tourisme, apprendre à gérer ta structure ou animer des activités.",
          },
        ],
      },
      {
        id: "santé et social",
        nom: "Santé et social",
        emoji: "🩺",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom28",
            nom: "Esthétique",
            emoji: "🤩",
            description:
              "Pour travailler dans un institut de soins esthétiques, à ton compte, ou bien dans les structures médicales et paramédicales.",
          },
          {
            id: "dom27",
            nom: "Santé",
            emoji: "🩺",
            description:
              "Si tu veux travailler dans le secteur médical, que ce soit pour soigner des patients ou pour exercer dans les nombreux métiers support du secteur.",
          },
          {
            id: "dom29",
            nom: "Travail social",
            emoji: "🛟",
            description: "Pour accompagner les personnes en situation de détresse et prendre soin des plus fragiles.",
          },
        ],
      },
      {
        id: "sciences et technologie",
        nom: "Sciences et technologie",
        emoji: "🧬",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom30",
            nom: "Biologie",
            emoji: "🔬",
            description: "Pour découvrir tous les secrets du vivant jusqu'au plus petit atome !",
          },
          {
            id: "dom32",
            nom: "Informatique",
            emoji: "💻",
            description:
              "Tu as envie de devenir développeuse ou développeur, de découvrir les langages informatiques et de devenir un(e) pro de l'ordi ?",
          },
          {
            id: "dom33",
            nom: "Mathématiques",
            emoji: "➕",
            description:
              "Si tu aimes l'abstraction et que tu souhaites te plonger dans les mathématiques et leur application pratique, c'est par ici !",
          },
          {
            id: "dom34",
            nom: "Mécanique",
            emoji: "🔧",
            description:
              "Tu as envie de travailler dans l'aéronautique, le secteur naval, l'automobile, ou un secteur similaire ?",
          },
          {
            id: "dom35",
            nom: "Physique - chimie",
            emoji: "🧪",
            description:
              "Si tu as envie de comprendre les propriétés de la matière, les lois de la nature et de l'univers !",
          },
          {
            id: "dom36",
            nom: "Sciences de la Terre et de l'univers",
            emoji: "🔭",
            description: "Pour comprendre comment s'est formée la Terre et ce que l'on sait du reste de l'univers !",
          },
          {
            id: "dom31",
            nom: "Électronique",
            emoji: "🔌",
            description: "Si tu veux apprendre à concevoir, réaliser et réparer des circuits, tu es au bon endroit.",
          },
        ],
      },
      {
        id: "sciences humaines et sociales",
        nom: "Sciences humaines et sociales",
        emoji: "📚",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom39",
            nom: "Histoire - géographie",
            emoji: "🗺️",
            description:
              "Pour devenir un ou une experte de l'Histoire du monde, comprendre la manière dont il s'est construit à travers les temps, savoir où et comment et l'humanité y habite.",
          },
          {
            id: "dom40",
            nom: "Philosophie",
            emoji: "ɸ",
            description:
              "Si tu veux découvrir tous les courants de pensée et la manière dont on peut répondre aux grandes questions de l'Homme.",
          },
          {
            id: "dom41",
            nom: "Psychologie",
            emoji: "🧠",
            description:
              "Pour apprendre la manière dont notre cerveau fonctionne et les mécanismes qui expliquent nos actions et réactions.",
          },
          {
            id: "dom42",
            nom: "Sciences économiques",
            emoji: "📈",
            description:
              "Tu as envie de comprendre comment s'organisent les sociétés pour produire et échanger les biens et les services ? C'est par ici !",
          },
          {
            id: "dom43",
            nom: "Sociologie",
            emoji: "👓",
            description:
              "Si tu veux découvrir comment les sociétés fonctionnent et se transforment, tu es au bon endroit.",
          },
        ],
      },
      {
        id: "sécurité et défense",
        nom: "Sécurité et défense",
        emoji: "👮🏼",
        sousCatégoriesdomainesProfessionnels: [
          {
            id: "dom44",
            nom: "Armée - défense publique",
            emoji: "🎖️",
            description:
              "Si tu veux t'engager pour la défense des citoyens et citoyennes (armée, police, administration publique etc).",
          },
          {
            id: "dom45",
            nom: "Sécurité - prévention",
            emoji: "🦺",
            description:
              "Si tu veux travailler dans les entreprises en lien avec la sécurité, mais aussi tous les métiers de prévention, comme le secourisme.",
          },
        ],
      },
    ],
  };

  public async récupérer(): Promise<RéférentielDonnées | Error> {
    return this.RÉFÉRENTIEL_DONNÉES;
  }
}
