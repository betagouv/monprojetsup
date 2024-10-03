import BoutonsActionsFicheFormation from "./BoutonsActionsFicheFormation/BoutonsActionsFicheFormation";
import ExplicationsCorrespondanceFicheFormation from "./ExplicationsCorrespondanceFicheFormation/ExplicationsCorrespondanceFicheFormation";
import { type FicheFormationProps } from "./FicheFormation.interface";
import MétiersAccessiblesFicheFormation from "./MétiersAccessiblesFicheFormation/MétiersAccessiblesFicheFormation";
import OngletsFicheFormation from "./OngletsFicheFormation/OngletsFicheFormation";
import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Badge from "@/components/Badge/Badge";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";

const FicheFormation = ({ id }: FicheFormationProps) => {
  const { data: formation, isFetching: chargementEnCours } = useQuery(récupérerFormationQueryOptions(id));

  if (formation === null) return null;

  if (!formation || chargementEnCours) return <AnimationChargement />;

  return (
    <>
      <Head title={formation.nom} />
      <div className="mb-6 lg:mt-6">
        <Badge
          titre={i18n.COMMUN.FORMATION}
          type="alerte"
        />
      </div>
      <div className="*:mb-6">
        <Titre niveauDeTitre="h1">{formation.nom}</Titre>
      </div>
      <div className="grid grid-flow-row gap-4 text-[--text-mention-grey]">
        {Boolean(formation.admis.total) && (
          <div className="grid grid-flow-col items-start justify-start gap-2">
            <span
              aria-hidden="true"
              className="fr-icon-team-fill fr-icon--sm"
            />
            <p className="mb-0 text-sm">
              <strong>{formation.admis.total} </strong> {i18n.PAGE_FORMATION.ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE}
            </p>
          </div>
        )}
        {formation.communesProposantLaFormation.length > 0 && (
          <div className="grid grid-flow-col items-start justify-start gap-2">
            <span
              aria-hidden="true"
              className="fr-icon-map-pin-2-fill fr-icon--sm"
            />
            <div>
              <p className="mb-2 text-sm">
                {i18n.PAGE_FORMATION.FORMATION_DISPONIBLES} {formation.communesProposantLaFormation.length}{" "}
                {i18n.PAGE_FORMATION.FORMATION_DISPONIBLES_SUITE}
                {formation.explications?.communes && formation.explications.communes.length > 0 && (
                  <>
                    {" "}
                    {i18n.PAGE_FORMATION.FORMATION_DISPONIBLES_SUITE_SI_CORRESPONDANCE}{" "}
                    <strong>{formation.explications?.communes.map((commune) => commune.nom).join(" • ")}</strong>
                  </>
                )}
              </p>
              {formation.lienParcoursSup && (
                <LienExterne
                  ariaLabel={i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
                  href={formation.lienParcoursSup}
                  taille="petit"
                  variante="simple"
                >
                  {i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
                </LienExterne>
              )}
            </div>
          </div>
        )}
      </div>
      <div className="mt-9">
        <BoutonsActionsFicheFormation formation={formation} />
      </div>
      <hr className="mb-9 mt-5" />
      <div className="grid gap-12">
        <OngletsFicheFormation formation={formation} />
        <MétiersAccessiblesFicheFormation métiers={formation.métiersAccessibles} />
        <ExplicationsCorrespondanceFicheFormation explications={formation.explications} />
      </div>
    </>
  );
};

export default FicheFormation;
