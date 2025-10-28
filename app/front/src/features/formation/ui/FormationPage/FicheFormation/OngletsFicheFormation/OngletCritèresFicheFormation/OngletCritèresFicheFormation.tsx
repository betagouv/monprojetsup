import { type OngletCritèresFicheFormationProps } from "./OngletCritèresFicheFormation.interface";
import GraphiqueRépartitionMoyenne from "@/components/GraphiqueRépartitionMoyenne/GraphiqueRépartitionMoyenne";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";
import Titre from "@/components/Titre/Titre";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { Badge } from "@codegouvfr/react-dsfr/Badge";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react/jsx-runtime";

const trierTableauParPourcentage = <T extends { pourcentage: number }>(tableau: T[]): T[] => {
  return [...tableau].sort((a, b) => b.pourcentage - a.pourcentage);
};

const OngletCritèresFicheFormation = ({
  moyenneGénérale,
  répartitionParBac,
  descriptifAttendus,
}: OngletCritèresFicheFormationProps) => {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

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
              <Fragment key={bac.idBac}>
                {bac.pourcentage !== 0 && (
                  <li>
                    <Badge noIcon>
                      {`${référentielDonnées?.bacs.find((élément) => élément.id === bac.idBac)?.nom} ${bac.pourcentage}%`}
                    </Badge>
                  </li>
                )}
              </Fragment>
            ))}
          </ul>
        </div>
      )}

      {moyenneGénérale.centiles.length > 0 && (
        <>
          <hr className="mb-2 mt-8 first:hidden" />
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.MOYENNE_GÉNÉRALE} {moyenneGénérale.nomBac && `(${moyenneGénérale.nomBac})`}
            </Titre>
          </div>
          <GraphiqueRépartitionMoyenne notes={moyenneGénérale.centiles.map((centile) => centile.note)} />
        </>
      )}

      {descriptifAttendus && descriptifAttendus !== "" && (
        <>
          <hr className="mb-2 mt-8 first:hidden" />
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lg"
            >
              {i18n.PAGE_FORMATION.LES_ATTENDUS}
            </Titre>
          </div>
          <TexteTronqué texte={descriptifAttendus} />
        </>
      )}
    </div>
  );
};

export default OngletCritèresFicheFormation;
