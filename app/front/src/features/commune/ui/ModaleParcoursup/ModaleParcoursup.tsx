import { i18n } from "@/configuration/i18n/i18n.ts";
import { ModaleParcoursupProps } from "@/features/commune/ui/ModaleParcoursup/ModaleParcoursup.interface.tsx";
import useModaleParcoursup from "@/features/commune/ui/ModaleParcoursup/useModaleParcoursup.tsx";

const ModaleParcoursup = ({ modale }: ModaleParcoursupProps) => {
  const { boutons, titre } = useModaleParcoursup();

  return (
    <modale.Component
      buttons={boutons}
      title={titre}
    >
      {i18n.PARCOURSUP_MODALE.CONTENU}
    </modale.Component>
  );
};

export default ModaleParcoursup;
