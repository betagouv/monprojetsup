/* eslint-disable react/button-has-type */
import { type BoutonProps } from "./Bouton.interface";
import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";

const Bouton = ({
  label,
  type,
  auClic,
  taille,
  variante,
  désactivé = false,
  icône,
  formId,
  ariaControls,
  dataFrOpened,
}: BoutonProps) => {
  return (
    <button
      aria-controls={ariaControls}
      data-fr-opened={dataFrOpened}
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
