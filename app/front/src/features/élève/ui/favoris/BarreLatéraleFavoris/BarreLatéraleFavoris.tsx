import { type BarreLatéraleFavorisProps } from "./BarreLatéraleFavoris.interface";
import useBarreLatéraleFavoris from "./useBarreLatéraleFavoris";
import ContrôleSegmenté from "@/components/ContrôleSegmenté/ContrôleSegmenté";
import { i18n } from "@/configuration/i18n/i18n";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import ListeMétiers from "@/features/métier/ui/ListeMétiers/ListeMétiers";

const BarreLatéraleFavoris = ({ métiers, formations }: BarreLatéraleFavorisProps) => {
  const { auChangementDeCatégorie, catégorieAffichée } = useBarreLatéraleFavoris();

  return (
    <>
      <div className="text-center [&_.fr-segmented>div>div]:w-[170px] [&_.fr-segmented>div]:border [&_.fr-segmented>div]:border-solid [&_.fr-segmented>div]:border-[--background-open-blue-france-hover] [&_.fr-segmented>div]:shadow-none">
        <ContrôleSegmenté
          auClic={auChangementDeCatégorie}
          légende={i18n.PAGE_FAVORIS.CATÉGORIE}
          valeurSélectionnéeParDéfaut="première"
          éléments={[
            { valeur: "première", label: i18n.COMMUN.FORMATION },
            { valeur: "seconde", label: i18n.COMMUN.MÉTIER },
          ]}
        />
      </div>
      {catégorieAffichée === "première" ? (
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
