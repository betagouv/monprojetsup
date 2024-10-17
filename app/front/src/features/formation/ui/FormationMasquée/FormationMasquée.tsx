import Bouton from "@/components/Bouton/Bouton.tsx";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import { FormationMasquéeProps } from "@/features/formation/ui/FormationMasquée/FormationMasquée.interface";
import NombreRaisonsDInteret from "@/features/formation/ui/NombreRaisonsDInteret/NombreRaisonsDInteret";
import NombreVillesDontFavorites from "@/features/formation/ui/NombreVillesDontFavortites/NombreVillesDontFavorites.tsx";
import { type Élève } from "@/features/élève/domain/élève.interface.ts";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries.tsx";
import { useMutation, useQuery } from "@tanstack/react-query";

const FormationMasquée = ({ formation }: FormationMasquéeProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });

  const modifierFormationsMasquées = async (formationsMasquées: Élève["formationsMasquées"]) => {
    if (!élève) return;
    await mutationÉlève.mutateAsync({
      ...élève,
      formationsMasquées,
    });
  };

  const masquerUneFormation = async () => {
    const formationsMasquées = élève?.formationsMasquées ?? [];
    await modifierFormationsMasquées([formation.id, ...formationsMasquées]);
    const nouvellesFormationsMasquées = formationsMasquées?.filter(
      (idFormationMasquée) => idFormationMasquée !== formation.id,
    );
    await modifierFormationsMasquées(nouvellesFormationsMasquées);
  };

  return (
    <div className="grid grid-flow-col items-center justify-between gap-8">
      <div className="grid grid-flow-row items-baseline justify-between gap-2">
        <div className="*:mb-0">
          <Titre
            couleurDeTitre="--text-label-blue-france"
            niveauDeTitre="h5"
            styleDeTitre="h5"
          >
            {formation.nom}
          </Titre>
        </div>
        <NombreRaisonsDInteret affinité={formation.affinité} />
        <NombreVillesDontFavorites
          communesProposantLaFormation={formation.communesProposantLaFormation}
          explications={formation.explications}
        />
      </div>
      <Bouton
        auClic={() => masquerUneFormation()}
        icône={{ classe: "fr-icon-arrow-go-back-line", position: "gauche" }}
        label={i18n.FORMATIONS_MASQUÉES.BOUTON_NE_MASQUER}
        type="button"
        variante="secondaire"
      />
    </div>
  );
};

export default FormationMasquée;
