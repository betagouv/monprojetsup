import ModaleParcourSup from "./ModaleParcourSup/ModaleParcourSup";
import parcourSupMPSSVG from "@/assets/parcoursup-mps.svg";
import Bouton from "@/components/Bouton/Bouton";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import CarteSecondaireTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/CarteSecondaireTableauDeBordÉlève/CarteSecondaireTableauDeBordÉlève";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

const CarteParcourSupÉlève = () => {
  const { data: élève } = useQuery(élèveQueryOptions);

  const modaleParcourSup = useMemo(
    () =>
      createModal({
        id: "modale-parcoursup",
        isOpenedByDefault: false,
      }),
    [],
  );

  return (
    <>
      <CarteSecondaireTableauDeBordÉlève
        altIllustration={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.ALT_ILLUSTRATION}
        illustration={parcourSupMPSSVG}
        sousTitre={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SOUS_TITRE}
        titre={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.TITRE}
      >
        {élève?.compteParcoursupAssocié ? (
          <p className="mb-0 whitespace-pre-line">
            {i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SI_SYNCHRO.SOUS_TITRE}{" "}
            <LienExterne
              ariaLabel={constantes.EMAIL_CONTACT}
              href={`mailto:${constantes.EMAIL_CONTACT}`}
            >
              {constantes.EMAIL_CONTACT}
            </LienExterne>
          </p>
        ) : (
          <Bouton
            auClic={modaleParcourSup.open}
            icône={{ position: "droite", classe: "fr-icon-refresh-line" }}
            label={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.BOUTON}
            taille="grand"
            type="button"
          />
        )}
      </CarteSecondaireTableauDeBordÉlève>
      <ModaleParcourSup modale={modaleParcourSup} />
    </>
  );
};

export default CarteParcourSupÉlève;
