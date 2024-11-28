import useVoeuxOngletToutesLesCommunes from "./useVoeuxOngletToutesLesCommunes";
import { VoeuxOngletToutesLesCommunesProps } from "./VoeuxOngletToutesLesCommunes.interface";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";

const VoeuxOngletToutesLesCommunes = ({ formation }: VoeuxOngletToutesLesCommunesProps) => {
  const { auChangementDesVoeuxSélectionnés, voeuxSuggérés, voeuxSélectionnésParDéfaut, àLaRechercheDUnVoeu } =
    useVoeuxOngletToutesLesCommunes({ formation });

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
          {i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.RAPPEL}{" "}
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
            hash="etude"
            href="/profil"
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
          </LienInterne>
        </p>
      </div>
      <div>
        <SélecteurMultiple
          auChangementOptionsSélectionnées={auChangementDesVoeuxSélectionnés}
          description={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.DESCRIPTION}
          forcerRafraichissementOptionsSélectionnées
          label={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.LABEL}
          messageNbSuggestionsMaxDépassé={
            <>
              {i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.VOIR_PLUS}{" "}
              {formation.lienParcoursSup && (
                <LienExterne
                  ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PARCOURSUP}
                  href={formation.lienParcoursSup}
                  taille="petit"
                  variante="simple"
                >
                  {i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PARCOURSUP}
                </LienExterne>
              )}
            </>
          }
          nombreDeCaractèreMinimumRecherche={constantes.VOEUX.NB_CARACTÈRES_MIN_RECHERCHE}
          nombreDeSuggestionsMax={constantes.VOEUX.NB_VOEUX_PAR_PAGE}
          optionsSuggérées={voeuxSuggérés}
          optionsSélectionnéesParDéfaut={voeuxSélectionnésParDéfaut}
          rechercheSuggestionsEnCours={false}
          texteOptionsSélectionnées={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.SÉLECTIONNÉS}
          àLaRechercheDUneOption={àLaRechercheDUnVoeu}
        />
      </div>
    </div>
  );
};

export default VoeuxOngletToutesLesCommunes;
