import useÉtablissementsVoeuxOnglet from "./useÉtablissementsVoeuxOnglet";
import { type ÉtablissementsVoeuxOngletProps } from "./ÉtablissementsVoeuxOnglet.interface";
import Bouton from "@/components/Bouton/Bouton";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import TagFiltre from "@/components/TagFiltre/TagFiltre";
import { i18n } from "@/configuration/i18n/i18n";
import useÉtablissementsVoeux from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/useÉtablissementsVoeux";
import { Fragment } from "react/jsx-runtime";

const ÉtablissementsVoeuxOnglet = ({ formation, codeCommune }: ÉtablissementsVoeuxOngletProps) => {
  const { mettreÀJourUnVoeu, voeuxSélectionnés, key } = useÉtablissementsVoeux({ formation });
  const {
    nombreÉtablissementÀAfficher,
    nombreÉtablissementsDansLeRayon,
    établissementsÀAfficher,
    rayonSélectionné,
    changerRayonSélectionné,
    rayons,
  } = useÉtablissementsVoeuxOnglet({
    formation,
    codeCommune,
  });

  return (
    <div key={key}>
      <p className="fr-text--xs mb-3">
        {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.RAYON}{" "}
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
      </p>
      <div aria-live="polite">
        {établissementsÀAfficher.length > 0 ? (
          <div className="grid gap-6">
            <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
              {établissementsÀAfficher.map((établissement) => (
                <li
                  className="*:text-left *:leading-6"
                  key={établissement.id}
                >
                  <TagFiltre
                    appuyéParDéfaut={voeuxSélectionnés?.includes(établissement.id)}
                    ariaLabel={établissement.nom}
                    auClic={() => mettreÀJourUnVoeu(établissement.id)}
                  >
                    {établissement.nom.split(" - ").map((it, index) => (
                      <Fragment key={it}>
                        {index === 0 ? <strong>{it}</strong> : it}
                        {it !== établissement.nom.split(" - ").at(-1) && " - "}
                      </Fragment>
                    ))}
                  </TagFiltre>
                </li>
              ))}
            </ul>
            {nombreÉtablissementsDansLeRayon > nombreÉtablissementÀAfficher && (
              <p className="fr-text--sm mb-0">
                {nombreÉtablissementsDansLeRayon} {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.VOIR_PLUS}{" "}
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
              </p>
            )}
          </div>
        ) : (
          <p className="fr-text--sm mb-0">
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ} {rayonSélectionné}{" "}
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ_SUITE}
          </p>
        )}
      </div>
    </div>
  );
};

export default ÉtablissementsVoeuxOnglet;
