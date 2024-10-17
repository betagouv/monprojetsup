import { CarteSecondaireTableauDeBordÉlèveProps } from "./CarteSecondaireTableauDeBordÉlève.interface";

const CarteSecondaireTableauDeBordÉlève = ({
  titre,
  sousTitre,
  illustration,
  altIllustration,
  children,
}: CarteSecondaireTableauDeBordÉlèveProps) => {
  return (
    <div className="h-full border border-solid border-[--border-default-grey] bg-white p-8 md:px-[4.5rem] md:py-10">
      <div className="grid grid-flow-row items-center justify-items-center gap-6 text-center lg:justify-items-start lg:text-left">
        <img
          alt={altIllustration ?? ""}
          className="h-20 max-w-[85%]"
          src={illustration}
        />
        <div className="text-[--text-label-grey]">
          <p className="fr-h3 mb-2">{titre}</p>
          <p className="fr-text--lead mb-0">{sousTitre}</p>
        </div>
        <div className="pt-2">{children}</div>
      </div>
    </div>
  );
};

export default CarteSecondaireTableauDeBordÉlève;
