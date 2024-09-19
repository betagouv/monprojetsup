import { type ListeMétiersProps } from "./ListeMétiers.interface";
import {
  actionsListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import CarteMétier from "@/features/métier/ui/CarteMétier/CarteMétier";

const ListeMétiers = ({ métiers }: ListeMétiersProps) => {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { changerÉlémentAffiché, changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();

  const estSélectionnée = (positionDansLaListe: number, métierId: string) => {
    if (positionDansLaListe === 0 && élémentAffiché === undefined) {
      changerÉlémentAffiché({ type: "métier", id: métierId });
      return true;
    }

    return Boolean(élémentAffiché?.type === "métier" && métierId === élémentAffiché?.id);
  };

  return (
    <div className="grid h-full justify-center gap-6 px-2 pb-6 lg:justify-normal lg:overflow-y-auto lg:px-6">
      <ul className="m-0 grid list-none justify-center gap-6 p-0 lg:justify-normal">
        {métiers.map((métier, index) => (
          <li key={métier.id}>
            <CarteMétier
              auClic={() => {
                changerÉlémentAffiché({ type: "métier", id: métier.id });
                changerAfficherBarreLatéraleEnMobile(false);
                scrollTo({ top: 0 });
              }}
              formations={métier.formations}
              id={métier.id}
              key={métier.id}
              sélectionnée={estSélectionnée(index, métier.id)}
              titre={métier.nom}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ListeMétiers;
