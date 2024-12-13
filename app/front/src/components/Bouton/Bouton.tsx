/* eslint-disable react/button-has-type */
import { type BoutonProps } from "./Bouton.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";

const Bouton = ({
  children,
  type,
  auClic,
  taille,
  variante,
  désactivé = false,
  icône,
  formId,
  ariaControls,
  dataFrOpened,
  rôle = "button",
}: BoutonProps) => {
  return (
    <button
      aria-controls={ariaControls}
      data-fr-opened={dataFrOpened}
      disabled={désactivé}
      form={formId}
      onClick={auClic}
      role={rôle}
      type={type}
    >
      <BoutonSquelette
        icône={icône}
        taille={taille}
        variante={variante}
      >
        {children}
      </BoutonSquelette>
    </button>
  );
};

export default Bouton;
