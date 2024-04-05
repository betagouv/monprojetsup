/* eslint-disable react/button-has-type */
import { type BoutonProps } from "./Bouton.interface";
import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";

const Bouton = ({ label, type, auClic, taille, variante, désactivé = false, icône, formId }: BoutonProps) => {
  return (
    <button
      disabled={désactivé}
      form={formId}
      onClick={auClic}
      type={type}
    >
      <BoutonSquelette
        icône={icône}
        label={label}
        taille={taille}
        variante={variante}
      />
    </button>
  );
};

export default Bouton;
