import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { t } from "i18next";
import { Trans } from "react-i18next";

const BlocAlternanceFicheFormation = () => {
  return (
    <div className="h-full border border-l-4 border-solid border-[--border-default-grey] border-l-[#BAA48A] bg-white px-10 py-6">
      <p className="mb-2">
        <Trans
          i18nKey="ALTERNANCE.TITRE"
          ns="ficheFormation"
        />
      </p>
      <p className="fr-text--sm mb-2">
        <Trans
          i18nKey="ALTERNANCE.TEXTE"
          ns="ficheFormation"
        />
      </p>
      <LienExterne
        ariaLabel={t("ALTERNANCE.LIEN", { ns: "ficheFormation" })}
        href={constantes.LIENS.ALTERNANCE}
        taille="petit"
        variante="simple"
      >
        {t("ALTERNANCE.LIEN", { ns: "ficheFormation" })}
      </LienExterne>
    </div>
  );
};

export default BlocAlternanceFicheFormation;
