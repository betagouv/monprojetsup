import { type ListeEtAperçuLayoutProps } from "./ListeEtAperçuLayout.interface";
import { afficherBarreLatéraleEnMobileListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { useLocation } from "@tanstack/react-router";

const ListeEtAperçuLayout = ({ children }: ListeEtAperçuLayoutProps) => {
  const location = useLocation();
  const forcerMasquageBarreLatérale = location.pathname === "/formation";
  const variante = location.pathname.includes("favoris") ? "favoris" : "formations";
  const afficherBarreLatéraleEnMobile = afficherBarreLatéraleEnMobileListeEtAperçuStore();

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale = () => {
    if (forcerMasquageBarreLatérale) {
      return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-0% lg:to-white lg:to-50%";
    }

    if (afficherBarreLatéraleEnMobile && !forcerMasquageBarreLatérale && variante === "formations") {
      return "bg-[--background-contrast-beige-gris-galet] lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%";
    }

    if (afficherBarreLatéraleEnMobile && !forcerMasquageBarreLatérale && variante === "favoris") {
      return "bg-[--background-open-blue-france] lg:bg-gradient-to-r lg:from-[--background-open-blue-france] lg:from-50% lg:to-white lg:to-50%";
    }

    if (variante === "formations")
      return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%";

    return "bg-white lg:bg-gradient-to-r lg:from-[--background-open-blue-france] lg:from-50% lg:to-white lg:to-50%";
  };

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale2 = () => {
    if (forcerMasquageBarreLatérale) {
      return "";
    } else {
      return "lg:grid lg:grid-cols-[450px_1fr] xl:grid-cols-[490px_1fr]";
    }
  };

  return (
    <div
      className={`h-full ${classBackgroundEnFonctionDeAfficherLaBarreLatérale()}`}
      key={location.pathname}
    >
      <div className={`fr-container h-full ${classBackgroundEnFonctionDeAfficherLaBarreLatérale2()}`}>{children}</div>
    </div>
  );
};

export default ListeEtAperçuLayout;
