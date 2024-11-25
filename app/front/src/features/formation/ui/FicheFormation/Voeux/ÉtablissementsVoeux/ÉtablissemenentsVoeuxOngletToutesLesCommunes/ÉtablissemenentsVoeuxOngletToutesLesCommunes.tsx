import useÉtablissementsVoeuxOngletToutesLesCommunes from "./useÉtablissementsVoeuxOngletToutesLesCommunes";
import { ÉtablissemenentsVoeuxOngletToutesLesCommunesProps } from "./ÉtablissemenentsVoeuxOngletToutesLesCommunes.interface";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtablissemenentsVoeuxOngletToutesLesCommunes = ({
  formation,
}: ÉtablissemenentsVoeuxOngletToutesLesCommunesProps) => {
  const {
    auChangementDesÉtablissementsSélectionnés,
    établissementsSuggérés,
    établissementsSélectionnésParDéfaut,
    àLaRechercheDUnÉtablissement,
  } = useÉtablissementsVoeuxOngletToutesLesCommunes({ formation });

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
          messageNbSuggestionsMaxDépassé={
            <>
              {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.VOIR_PLUS}{" "}
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
            </>
          }
          nombreDeCaractèreMinimumRecherche={constantes.ÉTABLISSEMENTS.NB_CARACTÈRES_MIN_RECHERCHE}
          nombreDeSuggestionsMax={constantes.ÉTABLISSEMENTS.PAGINATION_ÉTABLISSEMENTS}
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

export default ÉtablissemenentsVoeuxOngletToutesLesCommunes;
