import { type ListeMétiersProps } from "./ListeMétiers.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import CarteMétier from "@/features/métier/ui/CarteMétier/CarteMétier";
import { useLocation } from "@tanstack/react-router";

const ListeMétiers = ({ métiers }: ListeMétiersProps) => {
  const { hash } = useLocation();

  return (
    <div
      className="grid h-full justify-center gap-6 px-2 pb-6 lg:justify-normal lg:overflow-y-auto lg:px-6"
      id={constantes.ACCESSIBILITÉ.LISTE_CARTES_ID}
    >
      <ul
        aria-label={i18n.ACCESSIBILITÉ.LISTE_MÉTIERS}
        className="m-0 grid list-none justify-center gap-6 p-0 lg:justify-normal"
      >
        {métiers.map((métier) => (
          <li key={métier.id}>
            <CarteMétier
              formations={métier.formations}
              id={métier.id}
              key={métier.id}
              sélectionnée={hash !== "" && hash === métier.id}
              titre={métier.nom}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ListeMétiers;
