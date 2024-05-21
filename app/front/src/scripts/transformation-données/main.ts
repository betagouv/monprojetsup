import { JSONHandler } from "./jsonHandler/jsonHandler";
import { type Données } from "./main.interface";
import { SQLHandler } from "./sqlHandler/sqlHandler";
import path from "node:path";

/**
 ***********************************************************************
 *
 *
 * COMMENT LANCER LE SCRIPT :
 *    npx vite-node app/front/src/scripts/transformation-données/main.ts
 *
 ***********************************************************************
 */

const main = async () => {
  let données = {} as Données;
  const requêteSQL: string[] = [];

  const jsonHandler = new JSONHandler();
  const sqlHandler = new SQLHandler();

  await sqlHandler.supprimerFichiersSQL(path.join(__dirname, "sqlDeSortie"));

  const fichiers = await jsonHandler.trouverFichiersJSON(path.join(__dirname, "jsonÀTransformer"));

  for (const fichier of fichiers) {
    const contenuDuFichier = await jsonHandler.lire<{}>(fichier);
    données = { ...données, ...contenuDuFichier };
  }

  // Créer les spécialités
  // const spécialités = Object.entries(données.specialites.specialites).map(([id, label]) => [id, label]);
  // const insertSpécialités = sqlHandler.générerInsert("specialites", ["id", "label"], spécialités);
  // requêteSQL.push(insertSpécialités);

  // Créer les matières
  // const matières = Object.entries(données.matieres).map(([id, label]) => [id, label]);
  // const insertMatières = sqlHandler.générerInsert("matieres", ["id", "label"], matières);
  // requêteSQL.push(insertMatières);

  // Créer les thématiques
  // const thématiques = Object.entries(données.thematiques.thematiques).map(([id]) => [id, données.labels[id]]);
  // const insertThématiques = sqlHandler.générerInsert("thematiques", ["id", "label"], thématiques);
  // requêteSQL.push(insertThématiques);

  // Créer les intérêts
  // const intérêts = Object.entries(données.interets.interets).map(([id]) => [id, données.labels[id]]);
  // const insertIntêrets = sqlHandler.générerInsert("interets", ["id", "label"], intérêts);
  // requêteSQL.push(insertIntêrets);

  // Créer les secteurs
  // const secteurs = Object.entries(données.labels)
  //   .filter(([id]) => id.startsWith("SEC_"))
  //   .map(([id]) => [id, données.labels[id]]);
  // const insertSecteurs = sqlHandler.générerInsert("secteurs", ["id", "label"], secteurs);
  // requêteSQL.push(insertSecteurs);

  // Créer les bacs
  // const MOYENNE_GENERALE_CODE = "-1";
  // const bacs = [
  //   { id: "", label: "Je ne sais pas" },
  //   { id: "Générale", label: "Série Générale" },
  //   { id: "P", label: "Bac Pro" },
  //   { id: "PA", label: "Bac Pro Agricole" },
  //   { id: "S2TMD", label: "Bac Techno S2TMD - Sciences et Techniques du Théâtre de la Musique et de la Danse" },
  //   { id: "ST2S", label: "Bac Techno ST2S - Sciences et technologies de la santé et du social" },
  //   { id: "STAV", label: "Bac Techno STAV - Sciences et Technologies de l\u0027agronomie et du vivant" },
  //   { id: "STD2A", label: "Bac Techno STD2A - Sciences Technologiques du Design et des Arts Appliquées" },
  //   { id: "STHR", label: "Bac Techno STHR - Science et Techniques de l\u0027Hôtellerie et de la Restauration" },
  //   {
  //     id: "STI2D",
  //     label: "Bac Techno STI2D - Sciences et Technologies de l\u0027Industrie et du Développement Durable",
  //   },
  //   { id: "STL", label: "Bac Techno STL - Sciences et technologie de laboratoire" },
  //   { id: "STMG", label: "Bac Techno STMG - Sciences et Technologies du Management et de la Gestion" },
  // ];

  // const bacsAvecInfosSupplémentaires = bacs.map((bac) => {
  //   const statistiques = données.statsAdmis.parGroupe[""].parBac[bac.id].parMatiere[MOYENNE_GENERALE_CODE];

  //   const statsAdmission = {
  //     moyenneGénérale: {
  //       fréquencesCumulées: statistiques.frequencesCumulees,
  //       répartition: statistiques.middle50,
  //     },
  //   };

  //   return [bac.id === "" ? "NC" : bac.id, bac.label, statsAdmission];
  // });

  // const insertBacs = sqlHandler.générerInsert("bacs", ["id", "label", "statsAdmission"], bacsAvecInfosSupplémentaires);
  // requêteSQL.push(insertBacs);

  // Créer les métiers
  const métiers = Object.entries(données.labels)
    .filter(([id]) => id.startsWith("MET_"))
    .map(([métierId, label]) => {
      const descriptif = Object.entries(données.descriptifs.keyToDescriptifs).find(([id]) => id === métierId)?.[1];
      const urls = Object.entries(données.urls).find(([id]) => id === métierId)?.[1];

      return [
        métierId,
        label,
        descriptif?.summary ?? descriptif?.presentation ?? null,
        urls && urls.length > 0 ? urls : null,
      ];
    });

  const insertMétiers = sqlHandler.générerInsert("metier", ["id", "label", "descriptif_general", "urls"], métiers);
  requêteSQL.push(insertMétiers);

  // Créer les formations
  const formations = Object.entries(données.labels)
    .filter(([id]) => id.startsWith("fl") || id.startsWith("fr"))
    .map(([formationId, label]) => {
      const descriptifGénéral = Object.entries(données.descriptifs.keyToDescriptifs).find(
        ([id]) => id === formationId,
      )?.[1];
      const eds = Object.entries(données.eds).find(([id]) => id === formationId)?.[1];
      const motsClefs = Object.entries(données.sources.sources)
        .filter(([_motClef, ids]) => ids.includes(formationId))
        .map(([motClef]) => motClef);
      const urls = Object.entries(données.urls).find(([id]) => id === formationId)?.[1];

      const formationParente = Object.entries(données.groups).find(([id]) => id === formationId)?.[1];

      return [
        formationId,
        label,
        descriptifGénéral?.summary ?? descriptifGénéral?.presentation ?? null,
        eds?.recoEDS ?? null,
        eds?.attendus ?? null,
        motsClefs.length > 0 ? motsClefs : null,
        urls && urls.length > 0 ? urls : null,
        formationParente ?? null,
      ];
    });

  const insertFormations = sqlHandler.générerInsert(
    "formation",
    [
      "id",
      "label",
      "descriptif_general",
      "descriptif_specialites",
      "descriptif_attendu",
      "mots_clefs",
      "urls",
      "formation_parente",
    ],
    formations,
  );

  requêteSQL.push(insertFormations);

  // Créer triple_affectation
  const tripleAffectation = Object.entries(données.psupData.formations.formations).map(([tripleAffectationId, ta]) => {
    return [`ta${tripleAffectationId}`, ta.libelle, ta.commune, ta.codeCommune, [ta.lat, ta.lng], `fl${ta.gFlCod}`];
  });

  const insertTripleAffectation = sqlHandler.générerInsert(
    "triplet_affectation",
    ["id", "nom", "commune", "code_commune", "coordonnees_geographiques", "id_formation"],
    tripleAffectation,
  );

  requêteSQL.push(insertTripleAffectation);

  // Créer bacs_specialites
  // const bacsSpécialités = Object.entries(données.specialites.specialitesParBac).flatMap(([bacId, spécialitéIds]) =>
  //   spécialitéIds.map((spécialitéId) => [bacId === "" ? "NC" : bacId, spécialitéId.toString()]),
  // );
  // const insertBacsSpécialités = sqlHandler.générerInsert(
  //   "bacs_specialites",
  //   ["bac_id", "specialite_id"],
  //   bacsSpécialités,
  //   false,
  // );
  // requêteSQL.push(insertBacsSpécialités);

  // Créer metiers_formations
  const métiersFormations = Object.entries(données.liensMetiersFormations)
    .flatMap(([métierId, formationIds]) => {
      if (!données.labels[métierId]) return undefined;
      return formationIds
        .map((formationId) => {
          if (!données.labels[formationId]) return undefined;
          return [métierId, formationId];
        })
        .filter((métierFormation): métierFormation is string[] => métierFormation !== undefined);
    })
    .filter((métierFormation): métierFormation is string[] => métierFormation !== undefined);

  const insertMétiersFormations = sqlHandler.générerInsert(
    "join_metier_formation",
    ["metier_id", "formation_id"],
    métiersFormations,
    false,
  );
  requêteSQL.push(insertMétiersFormations);

  // Créer metiers_secteurs
  // const métiersSecteurs = Object.entries(données.liensSecteursMetiers).flatMap(([secteurId, métierIds]) =>
  //   métierIds
  //     .map((métierId) => {
  //       if (!données.labels[métierId]) return undefined;
  //       return [secteurId, métierId];
  //     })
  //     .filter((métierSecteur): métierSecteur is string[] => métierSecteur !== undefined),
  // );
  // const insertMétiersSecteurs = sqlHandler.générerInsert(
  //   "metiers_secteurs",
  //   ["secteur_id", "metier_id"],
  //   métiersSecteurs,
  //   false,
  // );
  // requêteSQL.push(insertMétiersSecteurs);

  // Générer fichier final
  sqlHandler.créerFichier("insert", requêteSQL.join("\n"));
};

main();
