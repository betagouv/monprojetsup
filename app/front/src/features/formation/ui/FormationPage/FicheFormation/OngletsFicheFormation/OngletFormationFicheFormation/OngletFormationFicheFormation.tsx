import { type OngletFormationFicheFormationProps } from "./OngletFormationFicheFormation.interface";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";

const OngletFormationFicheFormation = ({ id, texte, liens }: OngletFormationFicheFormationProps) => {
  return (
    <div className="grid gap-6">
      <TexteTronqué texte={texte} />
      <ListeLiensExternesSousFormeBouton
        id={id}
        liens={liens}
      />
    </div>
  );
};

export default OngletFormationFicheFormation;
