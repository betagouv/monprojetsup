import { FicheMétierProps } from "./FicheMétier.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import Titre from "@/components/Titre/Titre";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonsActionsFicheMétier from "@/features/métier/ui/BoutonsActionsMétier/BoutonsActionsFicheMétier";
import { récupérerMétierQueryOptions } from "@/features/métier/ui/métierQueries";
import { Badge } from "@codegouvfr/react-dsfr/Badge";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";

const FicheMétier = ({ id }: FicheMétierProps) => {
  const { data: métier, isFetching: chargementEnCours } = useQuery(récupérerMétierQueryOptions(id));
  const [afficherToutesLesFormations, setAfficherToutesLesFormations] = useState(false);

  if (métier === null) return null;

  useEffect(() => {
    window.scrollTo({ top: 0 });
  }, [id]);

  if (!métier || chargementEnCours) return <AnimationChargement />;

  return (
    <>
      <div className="mb-6 lg:mt-6">
        <Badge
          noIcon
          severity="new"
        >
          {i18n.COMMUN.MÉTIER}
        </Badge>
      </div>
      <div className="*:mb-6">
        <Titre niveauDeTitre="h1">{métier.nom}</Titre>
      </div>
      <div className="mt-9">
        <BoutonsActionsFicheMétier
          métier={métier}
          taille="grand"
        />
      </div>
      <hr className="mb-3 mt-5" />
      <p className="grid gap-12 whitespace-pre-line">{métier.descriptif}</p>
      <ListeLiensExternesSousFormeBouton liens={métier.liens} />
      {métier.formations.length > 0 && (
        <div className="mt-12">
          <div className="*:mb-4">
            <Titre
              niveauDeTitre="h2"
              styleDeTitre="text--lead"
            >
              {i18n.PAGE_FAVORIS.FORMATIONS_POUR_UN_MÉTIER}
            </Titre>
          </div>
          <ul
            aria-live="polite"
            className="grid list-none justify-start gap-4 p-0"
          >
            {métier.formations
              .slice(
                0,
                afficherToutesLesFormations
                  ? métier.formations.length
                  : constantes.MÉTIERS.FICHES.NB_FORMATIONS_À_AFFICHER,
              )
              .map((formation) => (
                <li key={formation.id}>
                  <LienInterne
                    ariaLabel={formation.nom}
                    hash={formation.id}
                    href="/formations"
                    variante="simple"
                  >
                    {formation.nom}{" "}
                    <span
                      aria-hidden="true"
                      className="fr-icon-arrow-right-line fr-icon--sm"
                    />
                  </LienInterne>
                </li>
              ))}
            {métier.formations.length > constantes.MÉTIERS.FICHES.NB_FORMATIONS_À_AFFICHER &&
              !afficherToutesLesFormations && (
                <li
                  className="*:p-0"
                  key="bouton-voir-plus"
                >
                  <Bouton
                    auClic={() => setAfficherToutesLesFormations(true)}
                    taille="petit"
                    type="button"
                    variante="secondaire"
                  >
                    {i18n.PAGE_FAVORIS.AFFICHER_FORMATIONS_SUPPLÉMENTAIRES}
                  </Bouton>
                </li>
              )}
          </ul>
        </div>
      )}
    </>
  );
};

export default FicheMétier;
