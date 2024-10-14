import avisSVG from "@/assets/avis.svg";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";

const CarteAvisÉlève = () => {
  return (
    <div className="border border-solid border-[--border-default-grey] bg-white p-8 md:px-[4.5rem] md:py-10">
      <div className="grid grid-flow-row items-center justify-items-center gap-6 text-center lg:grid-cols-[auto_1fr_auto] lg:justify-items-start lg:text-left">
        <img
          alt=""
          className="w-[88px]"
          src={avisSVG}
        />
        <div className="text-[--text-label-grey]">
          <p className="fr-h3 mb-2">{i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.TITRE}</p>
          <p className="fr-text--lead mb-0">{i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.AVIS.SOUS_TITRE}</p>
        </div>
        <div className="pt-2">
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
        </div>
      </div>
    </div>
  );
};

export default CarteAvisÉlève;
