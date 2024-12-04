import { ModaleParcourSupProps } from "./ModaleParcourSup.interface";
import useModaleParcourSup from "./useModaleParcourSup";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";

const ModaleParcourSup = ({ modale }: ModaleParcourSupProps) => {
  const { boutons } = useModaleParcourSup();

  return (
    <modale.Component
      buttons={boutons}
      title={i18n.ÉLÈVE.TABLEAU_DE_BORD.MODALE_PARCOURSUP.TITRE}
    >
      <p className="mb-0 whitespace-pre-line">
        {i18n.ÉLÈVE.TABLEAU_DE_BORD.MODALE_PARCOURSUP.CONTENU}{" "}
        <LienExterne
          ariaLabel={constantes.CONTACT.EMAIL}
          href={`mailto:${constantes.CONTACT.EMAIL}`}
        >
          {constantes.CONTACT.EMAIL}
        </LienExterne>
      </p>
    </modale.Component>
  );
};

export default ModaleParcourSup;
