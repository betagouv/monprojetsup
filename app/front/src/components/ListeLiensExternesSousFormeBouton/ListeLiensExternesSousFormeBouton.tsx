import { type ListeLiensExternesSousFormeBoutonProps } from "./ListeLiensExternesSousFormeBouton.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";

const ListeLiensExternesSousFormeBouton = ({ liens }: ListeLiensExternesSousFormeBoutonProps) => {
  if (liens.length === 0) return null;

  return (
    <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
      {liens.map((lien) => (
        <li key={`${lien.url}${lien.intitulé}`}>
          <LienExterne
            ariaLabel={lien.intitulé}
            href={lien.url}
            variante="neutre"
          >
            <BoutonSquelette
              icône={{ position: "droite", classe: "fr-icon-external-link-line" }}
              label={lien.intitulé}
              taille="petit"
              variante="tertiaire"
            />
          </LienExterne>
        </li>
      ))}
    </ul>
  );
};

export default ListeLiensExternesSousFormeBouton;
