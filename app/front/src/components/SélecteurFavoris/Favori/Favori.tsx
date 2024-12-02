import { FavoriProps } from "./Favori.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { i18n } from "@/configuration/i18n/i18n";
import { Toggle } from "@radix-ui/react-toggle";

const Favori = ({
  id,
  nom,
  estFavori,
  ariaLabel = i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI,
  url,
  title = "",
  désactivé = false,
  icôneEstFavori = "fr-icon-heart-fill",
  icôneEstPasFavori = "fr-icon-heart-line",
  callbackMettreÀJour,
}: FavoriProps) => {
  return (
    <>
      <div>
        {url ? (
          <LienExterne
            ariaLabel={nom}
            href={url}
            taille="petit"
            variante="simple"
          >
            {nom}
          </LienExterne>
        ) : (
          nom
        )}
      </div>
      <Toggle
        aria-label={ariaLabel}
        className={désactivé ? "*:text-gray-600 *:opacity-75" : ""}
        disabled={désactivé}
        onPressedChange={() => callbackMettreÀJour?.(id)}
        pressed={estFavori}
        title={title}
      >
        <BoutonSquelette
          aria-hidden="true"
          icône={{
            classe: estFavori ? icôneEstFavori : icôneEstPasFavori,
          }}
          label={i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
          taille="petit"
          variante="tertiaire"
        />
      </Toggle>
    </>
  );
};

export default Favori;
