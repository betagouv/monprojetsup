/* eslint-disable complexity */
import { type ExplicationsCorrespondanceFicheFormationProps } from "./ExplicationsCorrespondanceFicheFormation.interface";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import ExplicationCorrespondanceÉlementFicheFormation from "@/features/formation/ui/FicheFormation/ExplicationsCorrespondanceFicheFormation/ExplicationCorrespondanceÉlementFicheFormation/ExplicationCorrespondanceÉlementFicheFormation";
import ExplicationCorrespondanceListeÉlementsFicheFormation from "@/features/formation/ui/FicheFormation/ExplicationsCorrespondanceFicheFormation/ExplicationCorrespondanceListeÉlementsFicheFormation/ExplicationCorrespondanceListeÉlementsFicheFormation";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";

const ExplicationsCorrespondanceFicheFormation = ({ explications }: ExplicationsCorrespondanceFicheFormationProps) => {
  const utilisateur = useUtilisateur();
  if (!explications) return null;

  if (
    explications.communes.length === 0 &&
    explications.formationsSimilaires.length === 0 &&
    explications.spécialitésChoisies.length === 0 &&
    explications.intérêtsEtDomainesChoisis.domaines.length === 0 &&
    explications.intérêtsEtDomainesChoisis.intérêts.length === 0 &&
    !explications.duréeÉtudesPrévue &&
    !explications.alternance &&
    !explications.duréeÉtudesPrévue &&
    !explications.typeBaccalaureat &&
    !explications.autoEvaluationMoyenne
  )
    return null;

  return (
    <div className="border border-l-4 border-solid border-[--border-default-grey] border-l-[--border-plain-blue-france] px-8 py-6">
      <Titre
        niveauDeTitre="h2"
        styleDeTitre="text--lead"
      >
        {i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.TITRE}
      </Titre>
      <ul className="m-0 grid list-none justify-start gap-6 p-0">
        {(explications.intérêtsEtDomainesChoisis.intérêts.length > 0 ||
          explications.intérêtsEtDomainesChoisis.domaines.length > 0) && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.INTÉRÊTS_ET_DOMAINES}
            éléments={[
              ...explications.intérêtsEtDomainesChoisis.intérêts.map((intérêt) => intérêt.nom),
              ...explications.intérêtsEtDomainesChoisis.domaines.map((domaine) => domaine.nom),
            ]}
          />
        )}
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
        {explications.spécialitésChoisies.length > 0 && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.SPÉCIALITÉS}
            éléments={explications.spécialitésChoisies.map((spécialité) => spécialité.nom)}
          />
        )}
        {explications.duréeÉtudesPrévue && (
          <ExplicationCorrespondanceÉlementFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.DURÉE_FORMATION}
            texteMisEnAvant={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS[
              explications.duréeÉtudesPrévue
            ].LABEL.toLocaleLowerCase()}
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
            éléments={explications.communes.map(
              (commune) => commune.nom + (commune.distanceKm > 0 ? ` (${commune.distanceKm}km)` : ""),
            )}
          />
        )}
        {explications.formationsSimilaires.length > 0 && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.FORMATIONS_SIMILAIRES}
            éléments={explications.formationsSimilaires.map((formation) => formation.nom)}
          />
        )}
        {explications.explicationsCalcul && explications.explicationsCalcul.length > 0 && utilisateur.estExpert && (
          <ExplicationCorrespondanceListeÉlementsFicheFormation
            texteIntroductif={i18n.PAGE_FORMATION.EXPLICATIONS_CORRESPONDANCE_PROFIL.EXPLICATION_CALCUL}
            éléments={explications.explicationsCalcul.map((explication) => explication)}
          />
        )}
      </ul>
    </div>
  );
};

export default ExplicationsCorrespondanceFicheFormation;
