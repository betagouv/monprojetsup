import { type ListeEtAperçuLayoutProps } from "./ListeEtAperçuLayout.interface";
import { afficherBarreLatéraleEnMobileListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";

const ListeEtAperçuLayout = ({ variante, children, forcerMasquageBarreLatérale }: ListeEtAperçuLayoutProps) => {
  const afficherBarreLatéraleEnMobile =  !forcerMasquageBarreLatérale && afficherBarreLatéraleEnMobileListeEtAperçuStore();

  const classContent = () => {
    if(forcerMasquageBarreLatérale) {
      return "";
    } else {
      return "lg:grid lg:grid-cols-[450px_1fr] xl:grid-cols-[490px_1fr]";
    }
  }

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale = () => {
    if(forcerMasquageBarreLatérale) {
      return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-0% lg:to-white lg:to-0%";
    }
    
    if (afficherBarreLatéraleEnMobile && variante === "formations") {
      return "bg-[--background-contrast-beige-gris-galet] lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%";
    }

    if (afficherBarreLatéraleEnMobile && variante === "favoris") {
      return "bg-[--background-open-blue-france] lg:bg-gradient-to-r lg:from-[--background-open-blue-france] lg:from-50% lg:to-white lg:to-50%";
    }

    if (variante === "formations")
      return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%";

    return "bg-white lg:bg-gradient-to-r lg:from-[--background-open-blue-france] lg:from-50% lg:to-white lg:to-50%";
  };

  return (
    <div className={`h-full ${classContent()} ${classBackgroundEnFonctionDeAfficherLaBarreLatérale()}`}>
      <div className="fr-container h-full">{children}</div>
    </div>
  );
};

export default ListeEtAperçuLayout;
