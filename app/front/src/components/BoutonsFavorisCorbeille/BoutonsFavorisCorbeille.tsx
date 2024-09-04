import { type BoutonsFavorisCorbeilleProps } from "./BoutonsFavorisCorbeille.interface";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonsFavorisCorbeille = ({ taille }: BoutonsFavorisCorbeilleProps) => {
  return (
    <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
      <Bouton
        // eslint-disable-next-line no-alert
        auClic={() => alert("Pas encore fait")}
        icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
        label={i18n.COMMUN.AJOUTER_À_LA_SÉLECTION}
        taille={taille}
        type="button"
      />
      <Bouton
        // eslint-disable-next-line no-alert
        auClic={() => alert("Pas encore fait")}
        icône={{ position: "gauche", classe: "fr-icon-eye-off-line" }}
        label={i18n.COMMUN.NE_PLUS_VOIR}
        taille={taille}
        type="button"
        variante="secondaire"
      />
    </div>
  );
};

export default BoutonsFavorisCorbeille;
