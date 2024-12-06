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
          <p className="fr-text--sm mb-0">{nom}</p>
        )}
      </div>{" "}
      <Toggle
        aria-label={ariaLabel}
        className={estFavori ? "*:text-[--artwork-minor-red-marianne]" : ""}
        disabled={désactivé}
        onPressedChange={() => callbackMettreÀJour?.(id)}
        pressed={estFavori}
        title={title}
      >
        {icôneEstFavori === "fr-icon-heart-fill" ? (
          <BoutonSquelette
            aria-hidden="true"
            icône={{
              classe: estFavori ? icôneEstFavori : icôneEstPasFavori,
            }}
            label={i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
            taille="petit"
            variante="tertiaire"
          />
        ) : (
          <div className="fr-btn fr-btn--sm fr-btn--tertiary px-2">
            <img
              alt=""
              className="h-4 w-4"
              src={icôneEstFavori}
            />
          </div>
        )}
      </Toggle>
    </>
  );
};

export default Favori;
