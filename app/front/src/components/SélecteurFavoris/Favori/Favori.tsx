import { FavoriProps } from "./Favori.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { i18n } from "@/configuration/i18n/i18n";
import ModaleParcourSup from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteParcourSupÉlève/ModaleParcourSup/ModaleParcourSup.tsx";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { Toggle } from "@radix-ui/react-toggle";
import { useMemo } from "react";

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
  idDeLonglet = "",
}: FavoriProps) => {
  const modaleParcourSup = useMemo(
    () =>
      createModal({
        id: `modale-parcoursup-${id}-${idDeLonglet}`,
        isOpenedByDefault: false,
      }),
    [id, idDeLonglet],
  );

  return (
    <>
      <div>
        {url ? (
          <LienExterne
            ariaLabel={nom || "favori"}
            href={url}
            taille="petit"
            variante="simple"
          >
            {nom}
          </LienExterne>
        ) : (
          <p className="fr-text--sm mb-0">{nom}</p>
        )}
      </div>

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
            ariaHidden
            icône={{
              classe: estFavori ? icôneEstFavori : icôneEstPasFavori,
            }}
            taille="petit"
            variante="tertiaire"
          >
            {i18n.ACCESSIBILITÉ.METTRE_EN_FAVORI}
          </BoutonSquelette>
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

      <ModaleParcourSup modale={modaleParcourSup} />
    </>
  );
};

export default Favori;
