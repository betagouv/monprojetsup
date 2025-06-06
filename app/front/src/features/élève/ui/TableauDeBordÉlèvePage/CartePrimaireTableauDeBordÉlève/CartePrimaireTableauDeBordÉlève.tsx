import { type CartePrimaireTableauDeBordÉlèveProps } from "./CartePrimaireTableauDeBordÉlève.interface";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";

const CartePrimaireTableauDeBordÉlève = ({
  titre,
  sousTitre,
  illustration,
  lien,
}: CartePrimaireTableauDeBordÉlèveProps) => {
  return (
    <div className="fr-enlarge-link grid h-full grid-rows-[1fr_auto] content-evenly border border-b-4 border-solid border-[--border-default-grey] border-b-[--border-action-high-blue-france] bg-white p-8 md:p-12">
      <div className="grid grid-flow-row content-center justify-items-center gap-6 text-center lg:grid-flow-col lg:text-left">
        <img
          alt=""
          className="w-[88px]"
          src={illustration}
        />
        <div className="pt-2">
          <LienInterne
            ariaLabel={titre}
            href={lien}
            variante="neutre"
          >
            <p className="fr-h3 mb-2 text-[--text-active-blue-france]">{titre}</p>
          </LienInterne>
          <p className="fr-text mb-0 text-[--text-default-grey]">{sousTitre}</p>
        </div>
      </div>
      <div className="text-right">
        <i className="fr-icon-arrow-right-line text-[--text-active-blue-france]" />
      </div>
    </div>
  );
};

export default CartePrimaireTableauDeBordÉlève;
