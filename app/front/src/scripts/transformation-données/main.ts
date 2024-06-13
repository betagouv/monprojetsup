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
 *
 *
 * SUPPRIMER DONNÉES INUTILES DANS UN JSON
 *  jq 'def walk:
 *  if type == "object" then
 *     with_entries(if .key | test("^ta\\d+") then empty else .value |= walk end)
 *  elif type == "array" then map(walk)
 *  else .
 *  end;
 *  walk' fichierentree.json > fichiersortie.json
 ***********************************************************************
 */

const main = async () => {
  const MOYENNE_GENERALE_CODE = "-1";
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

  /*  BACCALAUREAT
      id         varchar(20)  not null constraint type_baccalaureat_pkey primary key,
      nom        varchar(200) not null,
      id_externe varchar(20)  not null
  */
  const baccalaureat = données.bacs.map((bac) => [bac.id, bac.nom, bac.idExterne]);
  const insertBaccalauréat = sqlHandler.générerInsert("baccalaureat", ["id", "nom", "id_externe"], baccalaureat);
  requêteSQL.push(insertBaccalauréat);
  sqlHandler.créerFichier("baccalaureat", insertBaccalauréat);

  /*  SPECIALITE
      id    varchar(20)  not null primary key,
      label varchar(300) not null
  */
  const spécialités = Object.entries(données.specialites.specialites).map(([id, label]) => [id.trim(), label.trim()]);
  const insertSpécialités = sqlHandler.générerInsert("specialite", ["id", "label"], spécialités);
  requêteSQL.push(insertSpécialités);
  sqlHandler.créerFichier("specialite", insertSpécialités);

  /*  CRITERE_ANALYSE_CANDIDATURE
      id    varchar(20)  not null primary key,
      index integer unique,
      nom   varchar(200) not null
  */
  const critèreAnalyseCandidature = Object.entries(données.grillesAnalyseCandidaturesLabels).map(([id, nom], index) => [
    id.trim(),
    index,
    nom.trim(),
  ]);
  const insertCritèreAnalyseCandidature = sqlHandler.générerInsert(
    "critere_analyse_candidature",
    ["id", "index", "nom"],
    critèreAnalyseCandidature,
  );
  requêteSQL.push(insertCritèreAnalyseCandidature);
  sqlHandler.créerFichier("critere_analyse_candidature", insertCritèreAnalyseCandidature);

  /*  DOMAINE_CATEGORIE
      id    varchar(20)  not null primary key,
      nom   varchar(200) not null,
      emoji varchar(10)  not null
  */
  const domaineCatégories = données.domaines.map((domaineCatégorie) => [
    domaineCatégorie.id.trim(),
    domaineCatégorie.nom.trim(),
    domaineCatégorie.emoji.trim(),
  ]);
  const insertDomaineCatégories = sqlHandler.générerInsert(
    "domaine_categorie",
    ["id", "nom", "emoji"],
    domaineCatégories,
  );
  requêteSQL.push(insertDomaineCatégories);
  sqlHandler.créerFichier("domaine_categorie", insertDomaineCatégories);

  /*  DOMAINE
      id            varchar(30)  not null primary key,
      nom           varchar(200) not null,
      id_categorie varchar(30)  not null references domaine_categorie,
      emoji         varchar(10)  not null
  */
  const domaines = données.domaines.flatMap((domaineCatégorie) =>
    domaineCatégorie.enfants.map((domaine) => [
      domaine.id.trim(),
      domaine.nom.trim(),
      domaine.emoji.trim(),
      domaineCatégorie.id.trim(),
    ]),
  );
  const insertDomaines = sqlHandler.générerInsert("domaine", ["id", "nom", "emoji", "id_categorie"], domaines);
  requêteSQL.push(insertDomaines);
  sqlHandler.créerFichier("domaine", insertDomaines);

  /*  INTERET_CATEGORIE
      id    varchar(20)  not null primary key,
      nom   varchar(200) not null,
      emoji varchar(10)  not null
  */
  const interetCategories = données.intêrets.map((interetCategorie) => [
    interetCategorie.id.trim(),
    interetCategorie.nom.trim(),
    interetCategorie.emoji.trim(),
  ]);
  const insertInteretCategories = sqlHandler.générerInsert(
    "interet_categorie",
    ["id", "nom", "emoji"],
    interetCategories,
  );
  requêteSQL.push(insertInteretCategories);
  sqlHandler.créerFichier("interet_categorie", insertInteretCategories);

  /*  INTERET_SOUS_CATEGORIE
      id            varchar(30)  not null primary key,
      nom           varchar(200) not null,
      id_categorie varchar(30)  not null references interet_categorie,
      emoji         varchar(10)  not null
  */
  const interetSousCategories = données.intêrets.flatMap((interetCategorie) =>
    interetCategorie.sousCatégories.map((sousCatégorie) => [
      sousCatégorie.id.trim(),
      sousCatégorie.nom.trim(),
      sousCatégorie.emoji.trim(),
      interetCategorie.id.trim(),
    ]),
  );
  const insertInteretSousCategories = sqlHandler.générerInsert(
    "interet_sous_categorie",
    ["id", "nom", "emoji", "id_categorie"],
    interetSousCategories,
  );
  requêteSQL.push(insertInteretSousCategories);
  sqlHandler.créerFichier("interet_sous_categorie", insertInteretSousCategories);

  /*  INTERET
      id            varchar(30)  not null primary key,
      nom           varchar(200) not null,
      id_sous_categorie varchar(30)  not null references interet_sous_categorie,
  */
  const intêrets = données.intêrets.flatMap((interetCategorie) =>
    interetCategorie.sousCatégories.flatMap((sousCatégorie) =>
      sousCatégorie.enfants.map((enfant) => [enfant.id.trim(), enfant.nom.trim(), sousCatégorie.id.trim()]),
    ),
  );
  const insertInterets = sqlHandler.générerInsert("interet", ["id", "nom", "id_sous_categorie"], intêrets);
  requêteSQL.push(insertInterets);
  sqlHandler.créerFichier("interet", insertInterets);

  /*  FORMATION
      id                   varchar(20)  not nul primary key,
      label                varchar(300) not null,
      descriptif_general   text,
      descriptif_attendu   text,
      mots_clefs           varchar(300)[],
      descriptif_conseils  text,
      descriptif_diplome   text,
      formations_associees varchar(20)[],
      criteres_analyse     integer[]    not null,
      liens                jsonb[]      not null
  */
  const formations = Object.entries(données.labels)
    .filter(([id]) => id.startsWith("fl") || id.startsWith("fr"))
    .map(([formationId, label]) => {
      const estUneFormationEnfant = Object.entries(données.groups).some(
        ([idFormationEnfant, idFormationParent]) =>
          formationId === idFormationEnfant && formationId !== idFormationParent,
      );

      if (estUneFormationEnfant) return null;

      const descriptif = Object.entries(données.descriptifs.keyToDescriptifs).find(([id]) => id === formationId)?.[1];
      const eds = Object.entries(données.eds).find(([id]) => id === formationId)?.[1];

      const formationsAssociées = Object.entries(données.groups)
        .filter(([_idFormationEnfant, idFormationParent]) => idFormationParent === formationId)
        .map(([idFormationEnfant]) => idFormationEnfant.trim());

      let motsClefs = Object.entries(données.sources.sources)
        .filter(([_motClef, ids]) => ids.includes(formationId))
        .map(([motClef]) => motClef.trim());

      if (formationsAssociées.length > 0) {
        const labelDesFormationsEnfants = formationsAssociées.map((idFormationEnfant) =>
          Object.entries(données.labels)
            .find(([id]) => id === idFormationEnfant)?.[1]
            ?.trim(),
        );

        motsClefs = [
          ...motsClefs,
          ...labelDesFormationsEnfants.filter(
            (labelFormationEnfant): labelFormationEnfant is string => labelFormationEnfant !== undefined,
          ),
        ];
      }

      const critèresAnalyse = critèreAnalyseCandidature.map(
        (critère) => données.grillesAnalyseCandidatures[formationId]?.pcts?.[critère[0]] ?? 0,
      );

      const liens = Object.entries(données.urls)
        .find(([id]) => id === formationId)?.[1]
        ?.map((lien) => ({ label: lien.label.trim(), url: lien.uri.trim() }));

      return [
        formationId.trim(),
        label.trim(),
        descriptif?.summary?.trim() ?? descriptif?.presentation?.trim() ?? null,
        eds?.attendus?.trim() ?? null,
        motsClefs.length > 0 ? [...new Set(motsClefs)] : null,
        eds?.recoEDS?.trim() ?? null,
        descriptif?.summaryFormation?.trim() ?? null,
        formationsAssociées.length > 0 ? formationsAssociées : null,
        critèresAnalyse,
        liens && liens.length > 0 ? JSON.stringify(liens) : JSON.stringify([]),
      ];
    })
    .filter((formation): formation is Array<string | null> => formation !== null);

  const insertFormations = sqlHandler.générerInsert(
    "formation",
    [
      "id",
      "label",
      "descriptif_general",
      "descriptif_attendu",
      "mots_clefs",
      "descriptif_conseils",
      "descriptif_diplome",
      "formations_associees",
      "criteres_analyse",
      "liens",
    ],
    formations,
  );
  requêteSQL.push(insertFormations);
  sqlHandler.créerFichier("formation", insertFormations);

  /*  METIER
      id                 varchar(20)  not null primary key,
      label              varchar(300) not null,
      descriptif_general text,
      liens              jsonb[]      not null
  */
  const métiers = Object.entries(données.labels)
    .filter(([id]) => id.startsWith("MET_"))
    .map(([métierId, label]) => {
      const descriptif = Object.entries(données.descriptifs.keyToDescriptifs).find(([id]) => id === métierId)?.[1];

      const liens = Object.entries(données.urls)
        .find(([id]) => id === métierId)?.[1]
        ?.map((lien) => ({ label: lien.label.trim(), url: lien.uri.trim() }));

      return [
        métierId.trim(),
        label.trim(),
        descriptif?.summary?.trim() ?? descriptif?.presentation?.trim() ?? null,
        liens && liens.length > 0 ? JSON.stringify(liens) : JSON.stringify([]),
      ];
    });
  const insertMétiers = sqlHandler.générerInsert("metier", ["id", "label", "descriptif_general", "liens"], métiers);
  requêteSQL.push(insertMétiers);
  sqlHandler.créerFichier("metier", insertMétiers);

  /*  JOIN_METIER_FORMATION
      metier_id    varchar(20) not null references metier,
      formation_id varchar(20) not null references formation,
  */
  // const métierFormation = Object.entries(données.liensMetiersFormations)
  //   .flatMap(([métierId, formationIds]) => {
  //     if (!données.labels[métierId]) return undefined;

  //     return formationIds
  //       .map((formationId) => {
  //         if (formations.some((formation) => formation[0] === formationId))
  //           return [métierId.trim(), formationId.trim()];

  //         return undefined;
  //       })
  //       .filter((mf): mf is string[] => mf !== undefined);
  //   })
  //   .filter((mf): mf is string[] => mf !== undefined);

  // const insertMétierFormation = sqlHandler.générerInsert(
  //   "join_metier_formation",
  //   ["metier_id", "formation_id"],
  //   métierFormation,
  //   false,
  // );
  // requêteSQL.push(insertMétierFormation);
  // sqlHandler.créerFichier("join_metier_formation", insertMétierFormation);

  /*  TRIPLET_AFFECTATION
      id                        varchar(20)        not null primary key,
      nom                       text               not null,
      commune                   varchar(85)        not null,
      code_commune              varchar(5)         not null,
      coordonnees_geographiques double precision[] not null,
      id_formation              varchar(20)        not null references formation
  */
  const tripletAffectation = Object.entries(données.psupData.formations.formations)
    .map(([tripleAffectationId, ta]) => {
      const formationId = `fl${ta.gFlCod}`;
      let formationIdAssociée = formationId;

      if (!formations.some((formation) => formation[0] === formationId)) {
        const formationParenteId = formations.find((formation) => formation[7]?.includes(formationId))?.[0];

        if (!formationParenteId) return undefined;
        formationIdAssociée = formationParenteId;
      }

      return [
        `ta${tripleAffectationId}`,
        ta.libelle,
        ta.commune,
        ta.codeCommune,
        [ta.lat, ta.lng],
        formationIdAssociée,
      ];
    })
    .filter((ta): ta is string[] => ta !== undefined);

  const insertTripLetAffectation = sqlHandler.générerInsert(
    "triplet_affectation",
    ["id", "nom", "commune", "code_commune", "coordonnees_geographiques", "id_formation"],
    tripletAffectation,
  );
  requêteSQL.push(insertTripLetAffectation);
  sqlHandler.créerFichier("triplet_affectation", insertTripLetAffectation);

  /*  MOYENNE_GENERALE_ADMIS
      annee               varchar(4) not null,
      id_formation        varchar    not null references formation,
      id_bac              varchar    not null references baccalaureat,
      frequences_cumulees integer[]  not null,
  */
  const moyenneGénéraleAdmis = Object.entries(données.repartitionAdmisParBacEtMatiere)
    .flatMap(([formationId, détailParFormation]) => {
      return Object.entries(détailParFormation.parBac).map(([bacId, détailParBac]) => {
        const fréquencesCumulées = détailParBac.parMatiere[MOYENNE_GENERALE_CODE]?.frequencesCumulees;
        if (
          !fréquencesCumulées ||
          !baccalaureat.some((bac) => bac[0] === bacId) ||
          !formations.some((formation) => formation[0] === formationId)
        )
          return undefined;
        return [
          "2023",
          formationId.trim(),
          bacId.trim(),
          détailParBac.parMatiere[MOYENNE_GENERALE_CODE]?.frequencesCumulees,
        ];
      });
    })
    .filter((moyenneGénérale): moyenneGénérale is string[] => moyenneGénérale !== undefined);

  const insertMoyenneGénéraleAdmis = sqlHandler.générerInsert(
    "moyenne_generale_admis",
    ["annee", "id_formation", "id_bac", "frequences_cumulees"],
    moyenneGénéraleAdmis,
    false,
  );
  requêteSQL.push(insertMoyenneGénéraleAdmis);
  sqlHandler.créerFichier("moyenne_generale_admis", insertMoyenneGénéraleAdmis);

  /*  REPARTITION_ADMIS
      annee        varchar(4)   not null,
      id_formation varchar(200) not null references formation,
      id_bac       varchar      not null references baccalaureat,
      nombre_admis integer      not null,
  */
  const répartitionAdmis = Object.entries(données.nombreAdmisParBac)
    .flatMap(([formationId, bacs]) => {
      return Object.entries(bacs).map(([bacId, nbAdmis]) => {
        if (
          !baccalaureat.some((bac) => bac[0] === bacId) ||
          !formations.some((formation) => formation[0] === formationId)
        )
          return undefined;
        return ["2023", formationId.trim(), bacId.trim(), nbAdmis];
      });
    })
    .filter((répartition): répartition is string[] => répartition !== undefined);

  const insertRépartitionAdmis = sqlHandler.générerInsert(
    "repartition_admis",
    ["annee", "id_formation", "id_bac", "nombre_admis"],
    répartitionAdmis,
    false,
  );
  requêteSQL.push(insertRépartitionAdmis);
  sqlHandler.créerFichier("repartition_admis", insertRépartitionAdmis);

  // Créer les matières
  // const matières = Object.entries(données.matieres).map(([id, label]) => [id, label]);
  // const insertMatières = sqlHandler.générerInsert("matieres", ["id", "label"], matières);
  // requêteSQL.push(insertMatières);

  // Créer les secteurs
  // const secteurs = Object.entries(données.labels)
  //   .filter(([id]) => id.startsWith("SEC_"))
  //   .map(([id]) => [id, données.labels[id]]);
  // const insertSecteurs = sqlHandler.générerInsert("secteurs", ["id", "label"], secteurs);
  // requêteSQL.push(insertSecteurs);

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
