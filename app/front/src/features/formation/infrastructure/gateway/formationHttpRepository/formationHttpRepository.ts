import {
  LiensFormationRéponseHTTP,
  type RécupérerFichesFormationsRéponseHTTP,
  RécupérerFormationsRéponseHTTP,
  type RécupérerSuggestionsFormationsRéponseHTTP,
} from "./formationHttpRepository.interface";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type FicheFormation, Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";
import { RessourceNonTrouvéeErreurHttp } from "@/services/erreurs/erreursHttp";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";
import { components } from "@/types/api-mps";

type ProfilÉlèveHTTP = components["schemas"]["ProfilDTO"];

export class formationHttpRepository implements FormationRepository {
  private _ENDPOINT = "/api/v1/public/formations" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérerUneFiche(formationId: string): Promise<FicheFormation | Error> {
    const réponse = await this.récupérerPlusieursFiches([formationId]);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp) {
      return new RessourceNonTrouvéeErreur();
    }

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse?.[0];
  }

  public async récupérerPlusieursFiches(formationIds: string[]): Promise<FicheFormation[] | Error> {
    const body = {
      ids: formationIds,
      profil: this._récupérerProfilLocal(),
      numeroDePage: 1,
    };

    const réponse = await this._mpsApiHttpClient.post<RécupérerFichesFormationsRéponseHTTP>(
      `${this._ENDPOINT}/fiches`,
      body,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperFicheFormationVersLeDomaine(formation));
  }

  public async récupérerPlusieurs(formationIds: string[]): Promise<Formation[] | Error> {
    const body = {
      ids: formationIds,
      profil: this._récupérerProfilLocal(),
      numeroDePage: 1,
    };

    const réponse = await this._mpsApiHttpClient.post<RécupérerFormationsRéponseHTTP>(this._ENDPOINT, body);

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperFormationVersLeDomaine(formation));
  }

  public async rechercherFichesFormations(recherche: string): Promise<FicheFormation[] | Error> {
    const body = {
      recherche,
      profil: this._récupérerProfilLocal(),
      numeroDePage: 1,
    };

    const réponse = await this._mpsApiHttpClient.post<RécupérerFichesFormationsRéponseHTTP>(
      `${this._ENDPOINT}/recherche/detaillee`,
      body,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperFicheFormationVersLeDomaine(formation));
  }

  public async rechercherFormations(recherche: string): Promise<Formation[] | Error> {
    const body = {
      recherche,
      profil: this._récupérerProfilLocal(),
      numeroDePage: 1,
    };

    const réponse = await this._mpsApiHttpClient.post<RécupérerFormationsRéponseHTTP>(
      `${this._ENDPOINT}/recherche/succincte`,
      body,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperFormationVersLeDomaine(formation));
  }

  public async suggérer(): Promise<FicheFormation[] | Error> {
    const body = {
      profil: this._récupérerProfilLocal(),
      numeroDePage: 1,
    };

    const réponse = await this._mpsApiHttpClient.post<RécupérerSuggestionsFormationsRéponseHTTP>(
      `${this._ENDPOINT}/suggestions`,
      body,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperFicheFormationVersLeDomaine(formation));
  }

  private _mapperFormationVersLeDomaine(
    formationHttp: RécupérerFormationsRéponseHTTP["formations"][number],
  ): Formation {
    return {
      id: formationHttp.id,
      nom: formationHttp.nom,
    };
  }

  private _mapperFicheFormationVersLeDomaine(
    formationHttp: RécupérerFichesFormationsRéponseHTTP["formations"][number],
  ): FicheFormation {
    const lienParcourSup = formationHttp.formation.liens.find((lien) => /Parcoursup/u.exec(lien.nom));
    const lienParcourSupAvecCommunesFavorites = lienParcourSup
      ? {
          nom: lienParcourSup.nom,
          url: this._générerLeLienParcourSupAvecCommunesFavorites(
            lienParcourSup.url,
            formationHttp.formation.communesFavoritesAvecLeursVoeux,
          ),
        }
      : null;

    return {
      id: formationHttp.formation.id,
      nom: formationHttp.formation.nom,
      descriptifs: {
        formation: formationHttp.formation.descriptifFormation ?? null,
        détails: formationHttp.formation.descriptifDiplome ?? null,
        attendus: formationHttp.formation.descriptifAttendus ?? null,
        conseils: formationHttp.formation.descriptifConseils ?? null,
      },
      estEnAlternance: formationHttp.formation.apprentissage,
      lienParcoursSup: lienParcourSupAvecCommunesFavorites?.url ?? null,
      liens: this._mapperLiensVersLeDomaine(formationHttp.formation.liens, lienParcourSupAvecCommunesFavorites),
      admis: {
        moyenneGénérale: {
          idBac: formationHttp.formation.moyenneGeneraleDesAdmis?.baccalaureat?.id ?? null,
          nomBac: formationHttp.formation.moyenneGeneraleDesAdmis?.baccalaureat?.nom ?? null,
          centiles:
            formationHttp.formation.moyenneGeneraleDesAdmis?.centiles.map((centile) => ({
              centile: centile.centile,
              note: centile.note,
            })) ?? [],
        },
        répartition: {
          parBac:
            formationHttp.formation.repartitionAdmisAnneePrecedente?.parBaccalaureat.map((répartition) => ({
              idBac: répartition.baccalaureat.id,
              nomBac: répartition.baccalaureat.nom,
              nombre: répartition.nombreAdmis,
              pourcentage: Math.round(
                (répartition.nombreAdmis / (formationHttp.formation.repartitionAdmisAnneePrecedente?.total ?? 100)) *
                  100,
              ),
            })) ?? [],
        },
        total: formationHttp.formation.repartitionAdmisAnneePrecedente?.total ?? null,
      },
      formationsAssociées: formationHttp.formation.idsFormationsAssociees,
      critèresAnalyse: formationHttp.formation.criteresAnalyseCandidature.map((critère) => ({
        nom: critère.nom,
        pourcentage: critère.pourcentage,
      })),
      voeux: formationHttp.formation.voeux.map((voeu) => ({
        id: voeu.id,
        nom: voeu.nom,
        commune: { nom: voeu.commune.nom, code: voeu.commune.codeInsee },
      })),
      voeuxParCommuneFavorites: formationHttp.formation.communesFavoritesAvecLeursVoeux.map((commune) => ({
        commune: {
          code: commune.commune.codeInsee,
          nom: commune.commune.nom,
        },
        voeux: commune.voeuxAvecDistance.map((voeu) => ({
          id: voeu.voeu.id,
          nom: voeu.voeu.nom,
          distanceEnKm: voeu.distanceKm,
        })),
      })),
      communesProposantLaFormation: formationHttp.formation.communes.map((commune) => commune.nom),
      métiersAccessibles: formationHttp.formation.metiers.map((métier) => ({
        id: métier.id,
        nom: `${métier.nom[0].toUpperCase()}${métier.nom.slice(1)}`,
        descriptif: métier.descriptif ?? null,
        liens: métier.liens.map((lien) => ({ intitulé: lien.nom, url: lien.url })),
      })),
      explications: this._mapperExplicationsVersLeDomaine(formationHttp.explications),
      affinité: this._calculerNombrePointsAffinité(formationHttp.explications),
    };
  }

  private _mapperExplicationsVersLeDomaine = (
    explications: RécupérerFichesFormationsRéponseHTTP["formations"][number]["explications"],
  ): FicheFormation["explications"] => {
    if (!explications) {
      return null;
    }

    return {
      communes:
        explications.geographique.map((commune) => ({
          nom: commune.nomVille,
          distanceKm: commune.distanceKm,
        })) ?? [],
      duréeÉtudesPrévue: explications.dureeEtudesPrevue ?? null,
      alternance: explications.alternance ?? null,
      choixÉlève: {
        choix:
          explications.choixEleve?.choix.map((choix) => ({
            id: choix.id,
            nom: choix.nom,
          })) ?? [],
      },
      spécialitésChoisies: explications.specialitesChoisies.map((spécialité) => ({
        nom: spécialité.nomSpecialite,
        pourcentageAdmisAnnéePrécédente: spécialité.pourcentage,
      })),
      typeBaccalaureat: explications.typeBaccalaureat
        ? {
            id: explications.typeBaccalaureat.baccalaureat.id,
            nom: explications.typeBaccalaureat.baccalaureat.nom,
            pourcentageAdmisAnnéePrécédente: explications.typeBaccalaureat?.pourcentage,
          }
        : null,
      explicationsCalcul: explications.detailsCalculScore?.details ?? null,
      autoEvaluationMoyenne: null,
    };
  };

  private _calculerNombrePointsAffinité = (
    explications: RécupérerFichesFormationsRéponseHTTP["formations"][number]["explications"],
  ): number => {
    if (!explications) {
      return 0;
    }

    const conditionsDeValidationExplication = [
      explications.geographique.length > 0,
      explications.dureeEtudesPrevue,
      explications.alternance,
      explications.choixEleve && explications.choixEleve.choix.length > 0,
      explications.specialitesChoisies.length > 0,
      explications.typeBaccalaureat,
    ];

    let points = 0;

    for (const condition of conditionsDeValidationExplication) {
      if (condition) {
        points++;
      }
    }

    return points;
  };

  private _ajouterDesIdsVoeuxÀUrlParcourSup(voeuxIds: string[], lien: string) {
    const lienParsé = new URL(lien);
    const paramètresDeRecherche = new URLSearchParams(lienParsé.search);
    paramètresDeRecherche.set("center_on_interests", voeuxIds.join(","));

    return `${lienParsé.origin}${lienParsé.pathname}?${decodeURIComponent(paramètresDeRecherche.toString())}`;
  }

  private _générerLeLienParcourSupAvecCommunesFavorites(
    lien: string,
    voeuxParCommuneFavorites: RécupérerFichesFormationsRéponseHTTP["formations"][number]["formation"]["communesFavoritesAvecLeursVoeux"],
  ): string {
    const uneCommuneFavorite = voeuxParCommuneFavorites.length === 1;
    const plusieursCommunesFavorites = voeuxParCommuneFavorites.length > 1;

    if (uneCommuneFavorite) {
      const voeuxDeLaCommune = voeuxParCommuneFavorites[0].voeuxAvecDistance;

      const pasDeVoeuxÀProximitéCommune = voeuxDeLaCommune.length === 0;
      const unVoeuÀProximitéCommune = voeuxDeLaCommune.length === 1;

      if (pasDeVoeuxÀProximitéCommune) return lien;

      const idVoeuLePlusProcheDeLaCommune = voeuxDeLaCommune[0]?.voeu.id;
      const idVoeuLePlusLoinDeLaCommune = voeuxDeLaCommune?.[voeuxDeLaCommune.length - 1]?.voeu.id;
      if (unVoeuÀProximitéCommune) return this._ajouterDesIdsVoeuxÀUrlParcourSup([idVoeuLePlusProcheDeLaCommune], lien);

      return this._ajouterDesIdsVoeuxÀUrlParcourSup([idVoeuLePlusProcheDeLaCommune, idVoeuLePlusLoinDeLaCommune], lien);
    } else if (plusieursCommunesFavorites) {
      const communesAvecAuMoinsUnVoeu = voeuxParCommuneFavorites.filter((voeu) => voeu.voeuxAvecDistance.length > 0);
      const aucuneCommuneAvecVoeu = communesAvecAuMoinsUnVoeu.length === 0;
      const uneCommuneAvecVoeu = communesAvecAuMoinsUnVoeu.length === 1;

      if (aucuneCommuneAvecVoeu) {
        return lien;
      }

      if (uneCommuneAvecVoeu) {
        const voeuxDeLaCommune = communesAvecAuMoinsUnVoeu[0].voeuxAvecDistance;
        const unVoeuÀProximitéCommune = voeuxDeLaCommune.length === 1;
        const idVoeuLePlusProcheDeLaCommune = voeuxDeLaCommune[0]?.voeu.id;
        const idVoeuLePlusLoinDeLaCommune = voeuxDeLaCommune?.[voeuxDeLaCommune.length - 1]?.voeu.id;

        if (unVoeuÀProximitéCommune) return this._ajouterDesIdsVoeuxÀUrlParcourSup([voeuxDeLaCommune[0].voeu.id], lien);

        return this._ajouterDesIdsVoeuxÀUrlParcourSup(
          [idVoeuLePlusProcheDeLaCommune, idVoeuLePlusLoinDeLaCommune],
          lien,
        );
      }

      // eslint-disable-next-line unicorn/no-array-reduce
      const idsDesVoeuxDesCommunes = communesAvecAuMoinsUnVoeu.reduce<string[]>((idsDesVoeux, commune) => {
        const idVoeuÀAjouter = commune.voeuxAvecDistance.find(
          (voeuAvecDistance) => idsDesVoeux.includes(voeuAvecDistance.voeu.id) === false,
        )?.voeu.id;

        if (idVoeuÀAjouter) {
          idsDesVoeux.push(idVoeuÀAjouter);
        }

        return idsDesVoeux;
      }, []);

      return this._ajouterDesIdsVoeuxÀUrlParcourSup(idsDesVoeuxDesCommunes, lien);
    }

    return lien;
  }

  private _mapperLiensVersLeDomaine(
    liens: LiensFormationRéponseHTTP,
    lienParcourSup: LiensFormationRéponseHTTP[number] | null,
  ): FicheFormation["liens"] {
    return liens.map((lien) => {
      if (lienParcourSup && lienParcourSup.nom === lien.nom) {
        return { intitulé: lienParcourSup.nom, url: lienParcourSup.url };
      }

      return { intitulé: lien.nom, url: lien.url };
    });
  }

  private _récupérerProfilLocal() {
    return this._mapperVersLApiMps(dépendances.récupérerProfilLocalUseCase.run());
  }

  private _mapperVersLApiMps(élève: Élève | null): ProfilÉlèveHTTP | undefined {
    if (élève === null) {
      return undefined;
    }

    return {
      situation: élève.situation ?? undefined,
      compteParcoursupAssocie: élève.compteParcoursupAssocié ?? undefined,
      classe: élève.classe ?? undefined,
      baccalaureat: élève.bac ?? undefined,
      specialites: élève.spécialités ?? undefined,
      domaines: élève.domaines ?? undefined,
      centresInterets: élève.centresIntérêts ?? undefined,
      metiersFavoris: élève.métiersFavoris ?? undefined,
      dureeEtudesPrevue: élève.duréeÉtudesPrévue ?? undefined,
      alternance: élève.alternance ?? undefined,
      communesFavorites: élève.communesFavorites ?? undefined,
      corbeilleFormations: élève.formationsMasquées ?? undefined,
      formationsFavorites:
        élève.formations?.map((idFormation) => ({
          idFormation,
          niveauAmbition: élève.ambitions?.find((ambition) => ambition.idFormation === idFormation)?.ambition ?? 0,
          priseDeNote: élève.notesPersonnelles?.find((note) => note.idFormation === idFormation)?.note ?? undefined,
        })) ?? undefined,
      voeuxFavoris:
        élève.voeuxFavoris?.map((voeuFavori) => ({
          idVoeu: voeuFavori.id,
          estFavoriParcoursup: voeuFavori.estParcoursup,
        })) ?? undefined,
    };
  }
}
