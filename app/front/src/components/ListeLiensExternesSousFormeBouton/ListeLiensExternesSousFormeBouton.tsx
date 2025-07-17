import { type ListeLiensExternesSousFormeBoutonProps } from "./ListeLiensExternesSousFormeBouton.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { urlEstValide } from "@/utils/url";

const ListeLiensExternesSousFormeBouton = ({ id, liens }: ListeLiensExternesSousFormeBoutonProps) => {
  if (liens.length === 0) return null;

  return (
    <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
      {liens
          .filter((lien) => urlEstValide(lien.url))
          .map((lien) => (
        <li key={`${lien.url}${lien.intitulé}`}>
          <LienExterne
            ariaLabel={lien.intitulé}
            href={lien.url}
            id={id}
            variante="neutre"
          >
            <BoutonSquelette
              icône={{ position: "droite", classe: "fr-icon-external-link-line" }}
              taille="petit"
              variante="tertiaire"
            >
              {lien.intitulé}
            </BoutonSquelette>
          </LienExterne>
        </li>
      ))}
    </ul>
  );
};

export default ListeLiensExternesSousFormeBouton;
