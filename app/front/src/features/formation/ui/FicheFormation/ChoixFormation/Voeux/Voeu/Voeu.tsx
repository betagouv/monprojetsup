import useVoeu from "./useVoeu";
import parcourSupFavSVG from "@/assets/parcoursup-fav.svg";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { i18n } from "@/configuration/i18n/i18n";
import { VoeuProps } from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/Voeu/Voeu.interface";
import { Toggle } from "@radix-ui/react-toggle";

const Voeu = ({ voeu }: VoeuProps) => {
  const { urlParcoursup, estFavori, estFavoriParcoursup, mettreÀJour } = useVoeu({ voeu });

  return (
    <>
      <div>
        <LienExterne
          ariaLabel={voeu.nom}
          href={urlParcoursup()}
          taille="petit"
          variante="simple"
        >
          {voeu.nom}
        </LienExterne>
      </div>
      {estFavoriParcoursup() ? (
        <img
          alt=""
          className="h-3 self-center"
          src={parcourSupFavSVG}
        />
      ) : (
        <Toggle
          aria-label={i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
          onPressedChange={mettreÀJour}
          pressed={estFavori()}
        >
          <BoutonSquelette
            aria-hidden="true"
            icône={{
              classe: estFavori() ? "fr-icon-heart-fill" : "fr-icon-heart-line",
            }}
            label={i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
            taille="petit"
            variante="tertiaire"
          />
        </Toggle>
      )}
    </>
  );
};

export default Voeu;
