import BlocAlternanceFicheFormation from "./BlocAlternanceFicheFormation/BlocAlternanceFicheFormation";
import BoutonsActionsFicheFormation from "./BoutonsActionsFicheFormation/BoutonsActionsFicheFormation";
import ExplicationsCorrespondanceFicheFormation from "./ExplicationsCorrespondanceFicheFormation/ExplicationsCorrespondanceFicheFormation";
import { FicheFormationProps } from "./FicheFormation.interface";
import MétiersAccessiblesFicheFormation from "./MétiersAccessiblesFicheFormation/MétiersAccessiblesFicheFormation";
import OngletsFicheFormation from "./OngletsFicheFormation/OngletsFicheFormation";
import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import CommunesProposantLaFormation from "@/features/formation/ui/CommunesProposantLaFormation/CommunesProposantLaFormation";
import { récupérerFicheFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { Badge } from "@codegouvfr/react-dsfr/Badge";
import { useQuery } from "@tanstack/react-query";

const FicheFormation = ({ id }: FicheFormationProps) => {
  const { data: formation, isLoading: chargementEnCours } = useQuery(récupérerFicheFormationQueryOptions(id));

  if (formation === null) return null;

  scrollTo({ top: 0 });

  if (!formation || chargementEnCours) return <AnimationChargement />;

  return (
    <>
      <Head titre={formation.nom} />
      <div className="mb-6 lg:mt-6">
        <Badge
          noIcon
          severity="warning"
        >
          {i18n.COMMUN.FORMATION}
        </Badge>
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
        <CommunesProposantLaFormation
          communes={formation.communesProposantLaFormation}
          lienParcoursSup={formation.lienParcoursSup}
        />
      </div>
      <div className="mt-9">
        <BoutonsActionsFicheFormation formation={formation} />
      </div>
      <hr className="mb-9 mt-5" />
      <div className="grid gap-12">
        <div className="grid gap-4">
          <OngletsFicheFormation formation={formation} />
          {formation.estEnAlternance && <BlocAlternanceFicheFormation />}
        </div>
        <MétiersAccessiblesFicheFormation métiers={formation.métiersAccessibles} />
        <ExplicationsCorrespondanceFicheFormation explications={formation.explications} />
      </div>
    </>
  );
};

export default FicheFormation;
