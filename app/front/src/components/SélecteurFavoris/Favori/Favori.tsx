import {FavoriProps} from "./Favori.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import {i18n} from "@/configuration/i18n/i18n";
import {Toggle} from "@radix-ui/react-toggle";
import Bouton from "@/components/Bouton/Bouton.tsx";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";
import ModaleParcourSup from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteParcourSupÉlève/ModaleParcourSup/ModaleParcourSup.tsx";
import IconeMPS from "@/assets/icone-mps.svg";

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

    const modaleParcourSup = useMemo(() => createModal({
        id: `modale-parcoursup`,
        isOpenedByDefault: false,
    }), []);

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
                        icône={{
                            classe: estFavori ? icôneEstFavori : icôneEstPasFavori,
                        }}
                        taille="petit"
                        variante="tertiaire"
                        ariaHidden={true}
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

            <Bouton
                auClic={modaleParcourSup.open}
                taille="petit"
                variante="tertiaire"
            >
                <img src={IconeMPS} alt="Icon" width={14} height={14}/>
            </Bouton>

            <ModaleParcourSup modale={modaleParcourSup} />
        </>
    );
};

export default Favori;
