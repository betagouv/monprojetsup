import { RechercheSpécialitésProps } from "./RechercheSpécialités.interface";
import useRechercheSpécialités from "./useRechercheSpécialités";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeSpécialitésSuggérées from "@/features/élève/ui/formulaires/ScolaritéForm/Spécialités/ListeDeSpécialitésSuggérées/ListeDeSpécialitésSuggérées";

const RechercheSpécialités = ({ spécialitésBac, bac }: RechercheSpécialitésProps) => {
  const { rechercher, spécialitésSuggérées, rechercheEnCours } = useRechercheSpécialités({ spécialitésBac, bac });

  return (
    <div>
      <Recherche
        description={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.DESCRIPTION}
        label={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL}
        nombreDeCaractèresMaximumRecherche={constantes.SPÉCIALITÉS.NB_CARACTÈRES_MAX_RECHERCHE}
        nombreDeCaractèresMinimumRecherche={constantes.SPÉCIALITÉS.NB_CARACTÈRES_MIN_RECHERCHE}
        nombreDeRésultats={spécialitésSuggérées?.length}
        rechercheCallback={rechercher}
      />
      {rechercheEnCours ? (
        <AnimationChargement />
      ) : (
        <ListeDeSpécialitésSuggérées spécialités={spécialitésSuggérées ?? []} />
      )}
    </div>
  );
};

export default RechercheSpécialités;
