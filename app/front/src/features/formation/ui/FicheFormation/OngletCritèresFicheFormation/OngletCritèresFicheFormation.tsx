import { type OngletCritèresFicheFormationProps } from "./OngletCritèresFicheFormation.interface";
import Badge from "@/components/_dsfr/Badge/Badge";
import GraphiqueRépartitionMoyenne from "@/components/GraphiqueRépartitionMoyenne/GraphiqueRépartitionMoyenne";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";

const trierTableauParPourcentage = <T extends { pourcentage: number }>(tableau: T[]): T[] => {
  return [...tableau].sort((a, b) => b.pourcentage - a.pourcentage);
};

const OngletCritèresFicheFormation = ({
  critèresAnalyse,
  moyenneGénérale,
  répartitionParBac,
  descriptifAttendus,
}: OngletCritèresFicheFormationProps) => {
  return (
    <div>
      {répartitionParBac.length > 0 && (
        <div>
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.RÉPARTITION_PAR_BAC}
            </Titre>
          </div>
          <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
            {trierTableauParPourcentage(répartitionParBac).map((bac) => (
              <li key={bac.idBac}>
                <Badge titre={`${bac.nomBac} ${bac.pourcentage}%`} />
              </li>
            ))}
          </ul>
        </div>
      )}
      {critèresAnalyse.length > 0 && (
        <div>
          <hr className="mb-2 mt-8" />
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.CRITÈRES_ANALYSE}
            </Titre>
          </div>
          <ul className="m-0 grid list-none justify-start gap-4 p-0">
            {trierTableauParPourcentage(critèresAnalyse).map((critère) => (
              <li
                className="grid grid-flow-col justify-start gap-2 font-medium"
                key={critère.nom}
              >
                <span
                  aria-hidden="true"
                  className="fr-icon-check-line fr-icon--sm text-[--artwork-minor-green-emeraude]"
                />
                {`${critère.nom} (${critère.pourcentage}%)`}
              </li>
            ))}
          </ul>
        </div>
      )}
      {moyenneGénérale.centilles.length > 0 && (
        <div>
          <hr className="mb-2 mt-8" />
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.MOYENNE_GÉNÉRALE} ({moyenneGénérale.nomBac})
            </Titre>
          </div>
          <GraphiqueRépartitionMoyenne notes={moyenneGénérale.centilles.map((centille) => centille.note)} />
        </div>
      )}
      {descriptifAttendus && descriptifAttendus !== "" && (
        <div>
          <hr className="mb-2 mt-8" />
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.LES_ATTENDUS}
            </Titre>
          </div>
          <TexteTronqué
            nombreDeLigneÀAfficher="4"
            texte={descriptifAttendus}
          />
        </div>
      )}
    </div>
  );
};

export default OngletCritèresFicheFormation;
