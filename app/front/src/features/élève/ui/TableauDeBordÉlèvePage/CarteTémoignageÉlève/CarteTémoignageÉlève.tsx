import { type CarteTémoignageÉlèveProps } from "./CarteTémoignageÉlève.interface";
import temoignageSVG from "@/assets/temoignage.svg";

const CarteTémoignageÉlève = ({ children, auteur, rôle }: CarteTémoignageÉlèveProps) => {
  return (
    <div className="h-full border border-l-4 border-solid border-[--border-default-grey] border-l-[#FB926B] bg-white px-10 py-8 md:px-14 md:py-12">
      <div className="grid grid-flow-row gap-2">
        <img
          alt=""
          className="w-[32px]"
          src={temoignageSVG}
        />
        <p className="fr-text--lead mb-0 font-bold text-[--text-active-grey]">« {children} »</p>
        <div className="grid grid-flow-row gap-1">
          <p className="fr-text mb-0 font-bold text-[--text-active-grey]">{auteur}</p>
          <p className="fr-text--xs mb-0 text-[--text-mention-grey]">{rôle}</p>
        </div>
      </div>
    </div>
  );
};

export default CarteTémoignageÉlève;
