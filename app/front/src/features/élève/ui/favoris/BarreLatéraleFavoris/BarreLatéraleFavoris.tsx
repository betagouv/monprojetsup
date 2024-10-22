import { type BarreLatéraleFavorisProps } from "./BarreLatéraleFavoris.interface";
import useBarreLatéraleFavoris from "./useBarreLatéraleFavoris";
import { i18n } from "@/configuration/i18n/i18n";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import ListeMétiers from "@/features/métier/ui/ListeMétiers/ListeMétiers";
import { SegmentedControl } from "@codegouvfr/react-dsfr/SegmentedControl";

const BarreLatéraleFavoris = ({ métiers, formations }: BarreLatéraleFavorisProps) => {
  const { auChangementDeCatégorie, catégorieAffichée } = useBarreLatéraleFavoris();

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
                defaultChecked: catégorieAffichée === "première",
                onClick: () => auChangementDeCatégorie("première"),
              },
            },
            {
              label: i18n.COMMUN.MÉTIER,
              nativeInputProps: {
                defaultChecked: catégorieAffichée === "seconde",
                onClick: () => auChangementDeCatégorie("seconde"),
              },
            },
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
