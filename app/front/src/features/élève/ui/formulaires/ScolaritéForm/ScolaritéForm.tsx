import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";

const ScolaritéForm = () => {
  return (
    <ListeDéroulante
      description="test desc"
      label="Test label"
      options={[
        { valeur: "1", label: "Label 1" },
        { valeur: "2", label: "Label 2" },
        { valeur: "3", label: "Label 3" },
      ]}
      status={{ type: "erreur", message: "l" }}
    />
  );
};

export default ScolaritéForm;
