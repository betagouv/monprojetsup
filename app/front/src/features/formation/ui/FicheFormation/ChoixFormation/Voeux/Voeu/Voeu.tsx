import useVoeu from "./useVoeu";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { i18n } from "@/configuration/i18n/i18n";
import { VoeuProps } from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/Voeu/Voeu.interface";
import { Toggle } from "@radix-ui/react-toggle";

const Voeu = ({ voeu }: VoeuProps) => {
  const { urlParcoursup, estFavori, estFavoriParcoursup, mettreÀJour, icône } = useVoeu({ voeu });

  return (
    <>
      <div>
        <LienExterne
          ariaLabel={voeu.nom}
          href={urlParcoursup}
          taille="petit"
          variante="simple"
        >
          {voeu.nom}
        </LienExterne>
      </div>
      <Toggle
        aria-label={estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
        className={estFavoriParcoursup ? "*:text-gray-600 *:opacity-75" : ""}
        disabled={estFavoriParcoursup}
        onPressedChange={mettreÀJour}
        pressed={estFavori}
        title={estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : ""}
      >
        <BoutonSquelette
          aria-hidden="true"
          icône={{
            classe: icône,
          }}
          label={i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
          taille="petit"
          variante="tertiaire"
        />
      </Toggle>
    </>
  );
};

export default Voeu;
