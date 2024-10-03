import { type ÉtablissementsVoeuxProps } from "./ÉtablissementsVoeux.interface";
import useÉtablissementsVoeux from "./useÉtablissementsVoeux";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Onglets from "@/components/Onglets/Onglets";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtablissementsVoeux = ({ formation }: ÉtablissementsVoeuxProps) => {
  const { onglets } = useÉtablissementsVoeux({ formation });

  return (
    <>
      <div>
        <p className="mb-0 font-medium text-[--text-label-grey]">{i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LÉGENDE}</p>
        <div className="ml-1 grid grid-flow-col justify-start gap-6">
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
            href="/profil"
            icône={{ classe: "fr-icon-equalizer-line", position: "gauche" }}
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
          </LienInterne>
          {formation.lienParcoursSup && (
            <LienExterne
              ariaLabel={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PARCOURSUP}
              href={formation.lienParcoursSup}
              taille="petit"
              variante="simple"
            >
              {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PARCOURSUP}
            </LienExterne>
          )}
        </div>
      </div>
      <Onglets
        nomAccessible={i18n.ACCESSIBILITÉ.ONGLETS_VOEUX_ÉTABLISSEMENTS}
        onglets={onglets}
      />
    </>
  );
};

export default ÉtablissementsVoeux;
