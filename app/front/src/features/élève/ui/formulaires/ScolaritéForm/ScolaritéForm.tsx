import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { spécialitésPourUnBacQueryOptions } from "@/features/bac/ui/options";
import { useQuery } from "@tanstack/react-query";

const ScolaritéForm = () => {
  const { data: spécialités } = useQuery(spécialitésPourUnBacQueryOptions("Générale"));

  return (
    <>
      <ListeDéroulante
        description="test desc"
        label="Test label"
        options={[
          { valeur: "1", label: "Label 1" },
          { valeur: "2", label: "Label 2" },
          { valeur: "3", label: "Label 3" },
        ]}
      />
      {spécialités && (
        <SélecteurMultiple
          auChangementOptionsSélectionnées={(valeursOptionsSélectionnées) => console.log(valeursOptionsSélectionnées)}
          description="Commence à taper puis sélectionne des enseignements"
          label="Enseignements de spécialité (EDS) choisis ou envisagés"
          options={spécialités.map((spécialité) => ({ valeur: spécialité.id, label: spécialité.nom }))}
          texteOptionsSélectionnées="Enseignement(s) de spécialité sélectionné(s)"
          valeursOptionsSélectionnéesParDéfaut={["1061", "1063"]}
        />
      )}
    </>
  );
};

export default ScolaritéForm;
