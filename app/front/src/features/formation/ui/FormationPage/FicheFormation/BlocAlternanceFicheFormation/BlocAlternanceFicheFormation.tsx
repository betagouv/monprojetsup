import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";

const BlocAlternanceFicheFormation = () => {
  return (
    <div className="h-full border border-l-4 border-solid border-[--border-default-grey] border-l-[#BAA48A] bg-white px-10 py-6">
      <p className="mb-2">{i18n.PAGE_FORMATION.ALTERNANCE.TITRE}</p>
      <p className="fr-text--sm mb-2">{i18n.PAGE_FORMATION.ALTERNANCE.TEXTE}</p>
      <LienExterne
        ariaLabel={i18n.PAGE_FORMATION.ALTERNANCE.LIEN}
        href={constantes.LIENS.ALTERNANCE}
        taille="petit"
        variante="simple"
      >
        {i18n.PAGE_FORMATION.ALTERNANCE.LIEN}
      </LienExterne>
    </div>
  );
};

export default BlocAlternanceFicheFormation;
