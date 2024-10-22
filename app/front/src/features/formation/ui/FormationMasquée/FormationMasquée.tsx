import Bouton from "@/components/Bouton/Bouton.tsx";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import CommunesProposantLaFormation from "@/features/formation/ui/CommunesProposantLaFormation/CommunesProposantLaFormation.tsx";
import { FormationMasquéeProps } from "@/features/formation/ui/FormationMasquée/FormationMasquée.interface";
import useFormationMasquée from "@/features/formation/ui/FormationMasquée/useFormationMasquée.tsx";
import NombreAffinité from "@/features/formation/ui/NombreAffinité/NombreAffinité";

const FormationMasquée = ({ formation }: FormationMasquéeProps) => {
  const { démasquerUneFormation } = useFormationMasquée({ formation });

  return (
    <div className="grid grid-flow-col items-center justify-between gap-8">
      <div className="grid grid-flow-row items-baseline justify-between gap-2">
        <div className="*:mb-0 *:text-[--text-label-blue-france]">
          <Titre niveauDeTitre="h5">{formation.nom}</Titre>
        </div>
        <NombreAffinité affinité={formation.affinité} />
        <CommunesProposantLaFormation
          communes={formation.communesProposantLaFormation}
          explications={formation.explications}
        />
      </div>
      <Bouton
        auClic={démasquerUneFormation}
        icône={{ classe: "fr-icon-arrow-go-back-line", position: "gauche" }}
        label={i18n.PAGE_PROFIL.FORMATIONS_MASQUÉES.BOUTON_NE_PLUS_MASQUER}
        type="button"
        variante="secondaire"
      />
    </div>
  );
};

export default FormationMasquée;
