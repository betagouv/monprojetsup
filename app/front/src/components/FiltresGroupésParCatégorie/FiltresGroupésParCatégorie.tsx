import { type FiltresGroupésParCatégorieProps } from "./FiltresGroupésParCatégorie.interface";
import TagFiltre from "@/components/_dsfr/TagFiltre/TagFiltre";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import * as Accordion from "@radix-ui/react-accordion";
import { useEffect, useState } from "react";

const FiltresGroupésParCatégorie = ({
  catégories,
  auChangementFiltresSélectionnés,
  filtreIdsSélectionnésParDéfaut,
  niveauDeTitre,
}: FiltresGroupésParCatégorieProps) => {
  const [filtreIdsSélectionnés, setFiltreIdsSélectionnés] = useState(filtreIdsSélectionnésParDéfaut ?? []);

  const supprimerFiltreIdSélectionné = (filtreId: string) => {
    const nouveauxFiltreIdsSélectionnés = filtreIdsSélectionnés.filter(
      (filtreIdSélectionné) => filtreIdSélectionné !== filtreId,
    );
    setFiltreIdsSélectionnés(nouveauxFiltreIdsSélectionnés);
  };

  const ajouterFiltreIdSélectionné = (filtreId: string) => {
    const nouveauxFiltreIdsSélectionnés = [...filtreIdsSélectionnés, filtreId];
    setFiltreIdsSélectionnés(nouveauxFiltreIdsSélectionnés);
  };

  const auChangementAppuieDUnFiltre = (estAppuyé: boolean, filtreId: string) => {
    if (estAppuyé) ajouterFiltreIdSélectionné(filtreId);
    else supprimerFiltreIdSélectionné(filtreId);
  };

  const catégoriesOuvertesParDéfaut = catégories
    .filter((catégorie) => catégorie.filtres.some((filtre) => filtreIdsSélectionnés.includes(filtre.id)))
    .map((catégorie) => catégorie.nom);

  useEffect(() => {
    auChangementFiltresSélectionnés(filtreIdsSélectionnés);
  }, [auChangementFiltresSélectionnés, filtreIdsSélectionnés]);

  return (
    <Accordion.Root
      className="grid gap-6"
      defaultValue={catégoriesOuvertesParDéfaut}
      type="multiple"
    >
      {catégories.map((catégorie) => (
        <Accordion.Item
          key={catégorie.nom}
          value={catégorie.nom}
        >
          <Accordion.Header
            asChild
            className="w-full rounded-2xl border border-solid border-[--border-default-grey] px-8 py-4 text-left hover:border-[--background-open-blue-france-active] hover:!bg-[--background-open-blue-france] aria-expanded:rounded-b-none"
          >
            <Accordion.Trigger className="group grid grid-flow-col items-center justify-between">
              <div className="*:mb-0">
                <Titre
                  niveauDeTitre={niveauDeTitre}
                  styleDeTitre="h6"
                >
                  <span
                    aria-hidden
                    className="pr-4"
                  >
                    {catégorie.emoji}
                  </span>{" "}
                  {catégorie.nom}
                </Titre>
              </div>

              <svg
                className="rotate-180 transition-all duration-200 group-aria-expanded:rotate-0"
                fill="none"
                height="8"
                viewBox="0 0 14 8"
                width="14"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  clipRule="evenodd"
                  d="M6.99974 2.828L2.04974 7.778L0.635742 6.364L6.99974 0L13.3637 6.364L11.9497 7.778L6.99974 2.828Z"
                  fill="#CFCFCF"
                  fillRule="evenodd"
                />
              </svg>
            </Accordion.Trigger>
          </Accordion.Header>
          <Accordion.Content className="border border-t-0 border-solid border-[--border-default-grey] p-8 pt-6">
            <p className="fr-text--sm mb-4 text-[--text-mention-grey]">{i18n.COMMUN.PRÉCISER_CATÉGORIES}</p>
            <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
              {catégorie.filtres.map((filtre) => (
                <li key={filtre.id}>
                  <TagFiltre
                    appuyéParDéfaut={filtreIdsSélectionnés.includes(filtre.id)}
                    auClic={(estAppuyé) => auChangementAppuieDUnFiltre(estAppuyé, filtre.id)}
                    libellé={filtre.nom}
                  />
                </li>
              ))}
            </ul>
          </Accordion.Content>
        </Accordion.Item>
      ))}
    </Accordion.Root>
  );
};

export default FiltresGroupésParCatégorie;
