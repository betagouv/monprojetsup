import ModaleParcourSup from "./ModaleParcourSup/ModaleParcourSup";
import parcourSupMPSSVG from "@/assets/parcoursup-mps.svg";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import CarteSecondaireTableauDeBordÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteSecondaireTableauDeBordÉlève/CarteSecondaireTableauDeBordÉlève";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";

const CarteParcourSupÉlève = () => {
  const { élèveAAssociéSonCompteParcoursup } = useÉlève();

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
        sousTitre={
          élèveAAssociéSonCompteParcoursup
            ? i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SI_SYNCHRO.SOUS_TITRE
            : i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SOUS_TITRE
        }
        titre={
          élèveAAssociéSonCompteParcoursup
            ? i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SI_SYNCHRO.TITRE
            : i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.TITRE
        }
      >
        {élèveAAssociéSonCompteParcoursup ? (
          <Bouton
            auClic={modaleParcourSup.open}
            icône={{ position: "droite", classe: "fr-icon-refresh-line" }}
            taille="grand"
            type="button"
            variante="secondaire"
          >
            {i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.SI_SYNCHRO.BOUTON}
          </Bouton>
        ) : (
          <Bouton
            auClic={modaleParcourSup.open}
            icône={{ position: "droite", classe: "fr-icon-refresh-line" }}
            taille="grand"
            type="button"
            variante="secondaire"
          >
            {i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PARCOURSUP.BOUTON}
          </Bouton>
        )}
      </CarteSecondaireTableauDeBordÉlève>
      <ModaleParcourSup modale={modaleParcourSup} />
    </>
  );
};

export default CarteParcourSupÉlève;
