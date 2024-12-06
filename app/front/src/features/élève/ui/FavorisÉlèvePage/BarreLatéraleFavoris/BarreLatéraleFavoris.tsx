import { type BarreLatéraleFavorisProps } from "./BarreLatéraleFavoris.interface";
import useBarreLatéraleFavoris from "./useBarreLatéraleFavoris";
import { i18n } from "@/configuration/i18n/i18n";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import ListeMétiers from "@/features/métier/ui/ListeMétiers/ListeMétiers";
import { SegmentedControl } from "@codegouvfr/react-dsfr/SegmentedControl";

const BarreLatéraleFavoris = ({ métiers, formations }: BarreLatéraleFavorisProps) => {
  const { élémentAffiché, auChangementDeCatégorie } = useBarreLatéraleFavoris({ métiers, formations });

  return (
    <>
      <div className="text-center">
        <SegmentedControl
          hideLegend
          legend={i18n.PAGE_FAVORIS.CATÉGORIE}
          segments={[
            {
              label: i18n.COMMUN.FORMATION,
              nativeInputProps: {
                defaultChecked: élémentAffiché?.type === "formation",
                onClick: () => auChangementDeCatégorie("formations"),
              },
            },
            {
              label: i18n.COMMUN.MÉTIER,
              nativeInputProps: {
                defaultChecked: élémentAffiché?.type === "métier",
                onClick: () => auChangementDeCatégorie("métiers"),
              },
            },
          ]}
        />
      </div>
      {élémentAffiché?.type === "métier" ? (
        <ListeMétiers métiers={métiers ?? []} />
      ) : (
        <ListeFormations
          affichéSurLaPage="favoris"
          formations={formations ?? []}
        />
      )}
    </>
  );
};

export default BarreLatéraleFavoris;
