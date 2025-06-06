import useVoeuxOngletUneCommune from "./useVoeuxOngletUneCommune.ts";
import { type VoeuxOngletUneCommuneProps } from "./VoeuxOngletUneCommune.interface.tsx";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeVoeuxSuggérés from "@/features/formation/ui/Voeux/ListeDeVoeuxSuggérés/ListeDeVoeuxSuggérés.tsx";
import MaSélectionVoeux from "@/features/formation/ui/Voeux/MaSélectionVoeux/MaSélectionVoeux.tsx";

const VoeuxOngletUneCommune = ({ codeCommune }: VoeuxOngletUneCommuneProps) => {
  const { voeux, rayonSélectionné, changerRayonSélectionné, rayons } = useVoeuxOngletUneCommune({
    codeCommune,
  });

  return (
    <div>
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
                rôle="link"
                taille="petit"
                type="button"
                variante="quinaire"
              >{`${rayon}km`}</Bouton>
              {rayon !== rayons.at(-1) && " • "}
            </li>
          ))}
        </ul>
      </div>
      <div>
        <div className="grid gap-6">
          <div>
            <div className="*:mb-2">
              <Titre
                niveauDeTitre="h4"
                styleDeTitre="text--md"
              >
                {i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.TITRE}
              </Titre>
            </div>
            {voeux.length > 0 ? (
              <ListeDeVoeuxSuggérés voeux={voeux} />
            ) : (
              <p className="fr-text--sm mb-0">
                {i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.AUCUN_VOEU_À_PROXIMITÉ} {rayonSélectionné}{" "}
                {i18n.PAGE_FORMATION.CHOIX.VOEUX.PAR_COMMUNE.AUCUN_VOEU_À_PROXIMITÉ_SUITE}
              </p>
            )}
          </div>
          <hr className="pb-[1px]" />
          <div>
            <MaSélectionVoeux />
          </div>
        </div>
      </div>
    </div>
  );
};

export default VoeuxOngletUneCommune;
