import useVoeuxOnglet from "./useVoeuxOnglet";
import { type VoeuxOngletProps } from "./VoeuxOnglet.interface";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import useVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useVoeux";
import VoeuLienExterne from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/VoeuLienExterne/VoeuLienExterne.tsx";

const VoeuxOnglet = ({ formation, codeCommune }: VoeuxOngletProps) => {
  const { mettreÀJourUnVoeu, voeuxSélectionnés, key } = useVoeux({ formation });
  const {
    nombreVoeuÀAfficher,
    nombreVoeuxDansLeRayon,
    voeuxÀAfficher,
    afficherPlusDeRésultats,
    rayonSélectionné,
    changerRayonSélectionné,
    rayons,
  } = useVoeuxOnglet({
    formation,
    codeCommune,
  });

  return (
    <div key={key}>
      <div className="fr-text--xs mb-3 grid grid-flow-col items-baseline justify-start gap-1">
        <p className="fr-text--xs mb-0">{i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.RAYON} </p>
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
        <strong className="fr-text--md mb-0">Voeux disponibles</strong>
      </div>
      <div aria-live="polite">
        {voeuxÀAfficher.length > 0 ? (
          <div className="grid gap-6">
            <ul className="m-0 grid grid-flow-row justify-start gap-4 p-0">
              {voeuxÀAfficher.map((voeu) => (
                <li
                  className="grid grid-flow-col justify-between gap-4"
                  key={voeu.id}
                >
                  <VoeuLienExterne
                    estFavoris={voeuxSélectionnés?.some(({ id }) => id === voeu.id)}
                    mettreÀJourUnVoeu={mettreÀJourUnVoeu}
                    voeu={voeu}
                  />
                </li>
              ))}
            </ul>
            {nombreVoeuxDansLeRayon > nombreVoeuÀAfficher && (
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
                      <VoeuLienExterne
                        estFavoris={voeuxSélectionnés?.some(({ id }) => id === voeu.id)}
                        mettreÀJourUnVoeu={mettreÀJourUnVoeu}
                        voeu={voeu}
                      />
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ) : (
          <p className="fr-text--sm mb-0">
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.AUCUN_VOEU_À_PROXIMITÉ} {rayonSélectionné}{" "}
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.AUCUN_VOEU_À_PROXIMITÉ_SUITE}
          </p>
        )}
      </div>
    </div>
  );
};

export default VoeuxOnglet;
