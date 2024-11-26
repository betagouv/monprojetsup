import useÉtablissementsVoeuxOnglet from "./useÉtablissementsVoeuxOnglet";
import { type ÉtablissementsVoeuxOngletProps } from "./ÉtablissementsVoeuxOnglet.interface";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import useÉtablissementsVoeux from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/useÉtablissementsVoeux";
import ÉtablissementLienExterne from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/ÉtablissementLienExterne/ÉtablissementLienExterne";

const ÉtablissementsVoeuxOnglet = ({ formation, codeCommune }: ÉtablissementsVoeuxOngletProps) => {
  const { mettreÀJourUnVoeu, voeuxSélectionnés, key } = useÉtablissementsVoeux({ formation });
  const {
    nombreÉtablissementÀAfficher,
    nombreÉtablissementsDansLeRayon,
    établissementsÀAfficher,
    afficherPlusDeRésultats,
    rayonSélectionné,
    changerRayonSélectionné,
    rayons,
  } = useÉtablissementsVoeuxOnglet({
    formation,
    codeCommune,
  });

  return (
    <div key={key}>
      <div className="fr-text--xs mb-3 grid grid-flow-col items-baseline justify-start gap-1">
        <p className="fr-text--xs mb-0">{i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.PAR_COMMUNE.RAYON} </p>
        <ul className="m-0 inline-flex list-none flex-wrap justify-start gap-1 p-0">
          {rayons.map((rayon) => (
            <li
              className={`${rayonSélectionné === rayon ? "[&_.fr-btn]:font-bold" : "[&_.fr-btn]:font-normal"} [&_button]:p-0`}
              key={rayon}
            >
              <Bouton
                auClic={() => changerRayonSélectionné(rayon)}
                label={`${rayon}km`}
                taille="petit"
                type="button"
                variante="quinaire"
              />
              {rayon !== rayons.at(-1) && " • "}
            </li>
          ))}
        </ul>
      </div>
      <div className="mb-2">
        <strong className="fr-text--md mb-0">Établissements disponibles</strong>
      </div>
      <div aria-live="polite">
        {établissementsÀAfficher.length > 0 ? (
          <div className="grid gap-6">
            <ul className="m-0 grid grid-flow-row justify-start gap-4 p-0">
              {établissementsÀAfficher.map((établissement) => (
                <li
                  className="flex w-full items-center justify-between gap-4"
                  key={établissement.id}
                >
                  <ÉtablissementLienExterne
                    estFavoris={voeuxSélectionnés?.some(({ id }) => id === établissement.id)}
                    mettreÀJourUnVoeu={mettreÀJourUnVoeu}
                    établissement={établissement}
                  />
                </li>
              ))}
            </ul>
            {nombreÉtablissementsDansLeRayon > nombreÉtablissementÀAfficher && (
              <Bouton
                auClic={() => afficherPlusDeRésultats()}
                label="Plus de résultats"
                taille="petit"
                type="button"
                variante="quinaire"
              />
            )}
            {voeuxSélectionnés.length > 0 && (
              <div className="">
                <strong className="fr-text--md">Ma sélection</strong>
                <ul className="mt-2 flex list-none flex-wrap justify-start gap-4 p-0">
                  {voeuxSélectionnés.map((voeu) => (
                    <li
                      className="flex items-center gap-2"
                      key={voeu.id}
                    >
                      <ÉtablissementLienExterne
                        estFavoris={voeuxSélectionnés?.some(({ id }) => id === voeu.id)}
                        mettreÀJourUnVoeu={mettreÀJourUnVoeu}
                        établissement={voeu}
                      />
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ) : (
          <p className="fr-text--sm mb-0">
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.PAR_COMMUNE.AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ} {rayonSélectionné}{" "}
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.PAR_COMMUNE.AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ_SUITE}
          </p>
        )}
      </div>
    </div>
  );
};

export default ÉtablissementsVoeuxOnglet;
