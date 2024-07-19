import { type ExplicationsCorrespondanceFicheFormationProps } from "./ExplicationsCorrespondanceFicheFormation.interface";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { duréeÉtudesPrévueVersTraduction } from "@/features/élève/domain/élève";
import ExplicationCorrespondanceÉlementFicheFormation from "@/features/formation/ui/FicheFormation/ExplicationCorrespondanceÉlementFicheFormation/ExplicationCorrespondanceÉlementFicheFormation";
import ExplicationCorrespondanceListeÉlementsFicheFormation from "@/features/formation/ui/FicheFormation/ExplicationCorrespondanceListeÉlementsFicheFormation/ExplicationCorrespondanceListeÉlementsFicheFormation";

const ExplicationsCorrespondanceFicheFormation = ({ explications }: ExplicationsCorrespondanceFicheFormationProps) => {
  return (
    <div className="border border-l-4 border-solid border-[--border-default-grey] border-l-[--border-plain-blue-france] px-8 py-6">
      <Titre
        niveauDeTitre="h2"
        styleDeTitre="text--lead"
      >
        {i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.TITRE}
      </Titre>
      <ul className="m-0 grid list-none justify-start gap-6 p-0">
        {explications.typeBaccalaureat && (
          <ExplicationCorrespondanceÉlementFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.ADMISSION_BAC}
            texteMisEnAvant={`${explications.typeBaccalaureat.pourcentageAdmisAnnéePrécédente}% ${i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.ADMISSION_BAC_SUITE} ${explications.typeBaccalaureat.nom}`}
          />
        )}
        {explications.autoEvaluationMoyenne && (
          <ExplicationCorrespondanceÉlementFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.MOYENNE}
            texteMisEnAvant={`${explications.autoEvaluationMoyenne.nomBacUtilisé} ${i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.MOYENNE_SUITE} [${explications.autoEvaluationMoyenne.intervalBas},${explications.autoEvaluationMoyenne.intervalHaut}]`}
          />
        )}
        {explications.duréeÉtudesPrévue && (
          <ExplicationCorrespondanceÉlementFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.DURÉE_FORMATION}
            texteMisEnAvant={duréeÉtudesPrévueVersTraduction(explications.duréeÉtudesPrévue).toLocaleLowerCase()}
          />
        )}
        {explications.alternance && explications.alternance !== "pas_interesse" && (
          <ExplicationCorrespondanceÉlementFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.ALTERNANCE}
            texteMisEnAvant={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.ALTERNANCE_SUITE}
          />
        )}
        {explications.communes.length > 0 && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.COMMUNES}
            éléments={explications.communes.map((commune) => `${commune.nom} (${commune.distanceKm}km)`)}
          />
        )}
        {explications.formationsSimilaires.length > 0 && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.FORMATIONS_SIMILAIRES}
            éléments={explications.formationsSimilaires.map((formation) => formation.nom)}
          />
        )}
        {explications.spécialitésChoisies.length > 0 && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.SPÉCIALITÉS}
            éléments={explications.spécialitésChoisies.map((spécialité) => spécialité.nom)}
          />
        )}
        {(explications.intêretsEtDomainesChoisis.intêrets.length > 0 ||
          explications.intêretsEtDomainesChoisis.domaines.length > 0) && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.INTÊRETS_ET_DOMAINES}
            éléments={[
              ...explications.intêretsEtDomainesChoisis.intêrets.map((intêret) => intêret.nom),
              ...explications.intêretsEtDomainesChoisis.domaines.map((domaine) => domaine.nom),
            ]}
          />
        )}
      </ul>
    </div>
  );
};

export default ExplicationsCorrespondanceFicheFormation;
