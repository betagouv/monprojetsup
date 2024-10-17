import avisSVG from "@/assets/avis.svg";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import CarteSecondaireTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/CarteSecondaireTableauDeBordÉlève/CarteSecondaireTableauDeBordÉlève";

const CarteAvisÉlève = () => {
  return (
    <CarteSecondaireTableauDeBordÉlève
      illustration={avisSVG}
      sousTitre={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.SOUS_TITRE}
      titre={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.TITRE}
    >
      <LienExterne
        ariaLabel={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.BOUTON}
        href={constantes.LIENS.DONNER_SON_AVIS}
        variante="neutre"
      >
        <BoutonSquelette
          icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
          label={i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.BOUTON}
          taille="grand"
        />
      </LienExterne>
    </CarteSecondaireTableauDeBordÉlève>
  );
};

export default CarteAvisÉlève;
