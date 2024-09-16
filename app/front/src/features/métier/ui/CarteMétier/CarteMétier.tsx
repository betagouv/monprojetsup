import { type CarteMétierProps } from "./CarteMétier.interface";
import Carte from "@/components/Carte/Carte";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const CarteMétier = ({ id, titre, formations, sélectionnée = false, auClic }: CarteMétierProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);

  const estUnMétierFavori = () => {
    return élève?.métiersFavoris?.includes(id) ?? false;
  };

  return (
    <Carte
      auClic={auClic}
      estFavori={estUnMétierFavori()}
      estMasqué={false}
      sélectionnée={sélectionnée}
      titre={titre}
    >
      {formations.length > 0 && (
        <p className="fr-text--sm mb-0">
          {formations.length} {i18n.CARTE_MÉTIER.FORMATIONS}
        </p>
      )}
    </Carte>
  );
};

export default CarteMétier;
