import { type ListeLiensExternesSousFormeBoutonProps } from "./ListeLiensExternesSousFormeBouton.interface";
import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";

const ListeLiensExternesSousFormeBouton = ({ liens }: ListeLiensExternesSousFormeBoutonProps) => {
  return (
    <>
      {liens.length > 0 && (
        <div className="grid grid-flow-col justify-start gap-4">
          {liens.map((lien) => (
            <LienExterne
              ariaLabel={lien.intitulé}
              href={lien.url}
              key={lien.url}
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
      )}
    </>
  );
};

export default ListeLiensExternesSousFormeBouton;
