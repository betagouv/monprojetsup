import { ListeDeVoeuxProps } from "./ListeDeVoeux.interface";
import Bouton from "@/components/Bouton/Bouton.tsx";
import useListeDeVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/ListeDeVoeux/useListeDeVoeux.ts";
import Voeu from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/Voeu/Voeu";

const ListeDeVoeux = ({ voeux }: ListeDeVoeuxProps) => {
  const { nombreVoeuxAffichés, voeuxAffichés, afficherPlusDeRésultats } = useListeDeVoeux({ voeux });

  return (
    <div
      id="liste-voeux"
      tabIndex={-1}
    >
      <ul className="m-0 grid grid-flow-row justify-start gap-2 p-0">
        {voeuxAffichés.map((voeu) => (
          <li
            className="grid grid-flow-col justify-between gap-2 p-0"
            key={voeu.id}
          >
            <Voeu voeu={voeu} />
          </li>
        ))}
      </ul>
      {voeux.length > nombreVoeuxAffichés && (
        <Bouton
          auClic={() => afficherPlusDeRésultats()}
          label="Plus de résultats"
          taille="petit"
          type="button"
          variante="quinaire"
        />
      )}
    </div>
  );
};

export default ListeDeVoeux;
