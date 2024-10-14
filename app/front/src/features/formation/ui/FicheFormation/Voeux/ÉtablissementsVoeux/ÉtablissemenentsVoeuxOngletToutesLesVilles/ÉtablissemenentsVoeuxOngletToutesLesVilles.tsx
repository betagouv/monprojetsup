import useÉtablissementsVoeuxOngletToutesLesVilles from "./useÉtablissementsVoeuxOngletToutesLesVilles";
import { type ÉtablissemenentsVoeuxOngletToutesLesVillesProps } from "./ÉtablissemenentsVoeuxOngletToutesLesVilles.interface";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtablissemenentsVoeuxOngletToutesLesVilles = ({ formation }: ÉtablissemenentsVoeuxOngletToutesLesVillesProps) => {
  const {
    auChangementDesÉtablissementsSélectionnés,
    établissementsSuggérés,
    établissementsSélectionnésParDéfaut,
    àLaRechercheDUnÉtablissement,
  } = useÉtablissementsVoeuxOngletToutesLesVilles({ formation });

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
            hash="etude"
            href="/profil"
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
          </LienInterne>
        </p>
      </div>
      <div>
        <SélecteurMultiple
          auChangementOptionsSélectionnées={auChangementDesÉtablissementsSélectionnés}
          description={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.DESCRIPTION}
          forcerRafraichissementOptionsSélectionnées
          label={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.LABEL}
          nombreDeCaractèreMinimumRecherche={3}
          optionsSuggérées={établissementsSuggérés}
          optionsSélectionnéesParDéfaut={établissementsSélectionnésParDéfaut}
          rechercheSuggestionsEnCours={false}
          texteOptionsSélectionnées={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.SÉLECTIONNÉS}
          àLaRechercheDUneOption={àLaRechercheDUnÉtablissement}
        />
      </div>
    </div>
  );
};

export default ÉtablissemenentsVoeuxOngletToutesLesVilles;
