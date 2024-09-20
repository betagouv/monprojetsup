import { type ListeLiensExternesSousFormeBoutonProps } from "./ListeLiensExternesSousFormeBouton.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";

const ListeLiensExternesSousFormeBouton = ({ liens }: ListeLiensExternesSousFormeBoutonProps) => {
  if (liens.length === 0) return null;

  return (
    <div className="flex flex-wrap justify-start gap-4">
      {liens.map((lien) => (
        <LienExterne
          ariaLabel={lien.intitulé}
          href={lien.url}
          key={`${lien.url}${lien.intitulé}`}
          variante="neutre"
        >
          <BoutonSquelette
            icône={{ position: "droite", classe: "fr-icon-external-link-line" }}
            label={lien.intitulé}
            taille="petit"
            variante="tertiaire"
          />
        </LienExterne>
      ))}
    </div>
  );
};

export default ListeLiensExternesSousFormeBouton;
