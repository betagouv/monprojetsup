import { ModaleMétierProps } from "./ModaleMétier.interface";
import useModaleMétier from "./useModaleMétier";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";

const ModaleMétier = ({ métier, modale }: ModaleMétierProps) => {
  const { boutons, titre } = useModaleMétier({ métier });

  return (
    <modale.Component
      buttons={boutons}
      title={titre}
    >
      <div className="grid gap-6">
        <p className="mb-0 whitespace-pre-line">{métier.descriptif}</p>
        <ListeLiensExternesSousFormeBouton liens={métier.liens} />
      </div>
    </modale.Component>
  );
};

export default ModaleMétier;
