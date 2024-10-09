import { type ÉtablissemenentsVoeuxOngletToutesLesVillesProps } from "./ÉtablissemenentsVoeuxOngletToutesLesVilles.interface";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtablissemenentsVoeuxOngletToutesLesVilles = ({
  établissements,
  formationId,
}: ÉtablissemenentsVoeuxOngletToutesLesVillesProps) => {
  // eslint-disable-next-line  no-console
  console.log(établissements, formationId);

  return (
    <div className="grid gap-6">
      <div className="grid grid-flow-col items-center justify-start gap-4">
        <span
          aria-hidden="true"
          className=""
        >
          🏙️
        </span>
        <p className="fr-text--sm mb-0">
          {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.RAPPEL}{" "}
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
            href="/profil"
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
          </LienInterne>
        </p>
      </div>
    </div>
  );
};

export default ÉtablissemenentsVoeuxOngletToutesLesVilles;
