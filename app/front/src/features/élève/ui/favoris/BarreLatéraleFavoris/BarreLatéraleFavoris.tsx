import { type BarreLatéraleFavorisProps } from "./BarreLatéraleFavoris.interface";
import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import ContrôleSegmenté from "@/components/ContrôleSegmenté/ContrôleSegmenté";
import { i18n } from "@/configuration/i18n/i18n";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import ListeMétiers from "@/features/métier/ui/ListeMétiers/ListeMétiers";
import { useState } from "react";

const BarreLatéraleFavoris = ({ métiers, formations }: BarreLatéraleFavorisProps) => {
  const [catégorieAffichée, setCatégorieAffichée] = useState("formation");
  const { réinitialiserÉlémentAffiché } = actionsListeEtAperçuStore();

  const auChangementDeCatégorie = (catégorieSélectionnée: string) => {
    réinitialiserÉlémentAffiché();
    setCatégorieAffichée(catégorieSélectionnée);
  };

  return (
    <>
      <div className="text-center [&_.fr-segmented>div>div]:w-[170px] [&_.fr-segmented>div]:border [&_.fr-segmented>div]:border-solid [&_.fr-segmented>div]:border-[--background-open-blue-france-hover] [&_.fr-segmented>div]:shadow-none">
        <ContrôleSegmenté
          auClic={auChangementDeCatégorie}
          légende={i18n.PAGE_FAVORIS.CATÉGORIE}
          valeurSélectionnéeParDéfaut="formation"
          éléments={[
            { valeur: "formation", label: i18n.COMMUN.FORMATION },
            { valeur: "métier", label: i18n.COMMUN.MÉTIER },
          ]}
        />
      </div>
      {catégorieAffichée === "formation" ? (
        <ListeFormations
          affichéSurLaPage="favoris"
          formations={formations ?? []}
        />
      ) : (
        <ListeMétiers métiers={métiers ?? []} />
      )}
    </>
  );
};

export default BarreLatéraleFavoris;
