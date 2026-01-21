import { i18n } from "@/configuration/i18n/i18n.ts";
import { ModaleParcoursupEventProps } from "@/features/commune/ui/ModaleParcoursupEvent/ModaleParcoursupEvent.interface.tsx";
import useModaleParcoursupEvent from "@/features/commune/ui/ModaleParcoursupEvent/useModaleParcoursupEvent.tsx";

const ModaleParcoursupEvent = ({ modale }: ModaleParcoursupEventProps) => {
  const { boutons, titre } = useModaleParcoursupEvent();

  return (
    <modale.Component
      buttons={boutons}
      title={titre}
    >
      {i18n.PARCOURSUP_MODALE.CONTENU}
    </modale.Component>
  );
};

export default ModaleParcoursupEvent;
