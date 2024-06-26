import { type TexteTronquéProps } from "./TexteTronqué.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useEffect, useRef, useState } from "react";

const TexteTronqué = ({ texte, nombreDeLigneÀAfficher }: TexteTronquéProps) => {
  const [afficherEnEntier, setAfficherEnEntier] = useState(false);
  const [afficherBoutonLireLaSuite, setAfficherBoutonLireLaSuite] = useState(false);

  const ref = useRef<HTMLParagraphElement | null>(null);

  const classEnFonctionDeAfficherEnEntier = () => {
    if (afficherEnEntier) return "";

    return `line-clamp-${nombreDeLigneÀAfficher}`;
  };

  useEffect(() => {
    const doitAfficherBoutonLireLaSuite =
      ref?.current?.offsetHeight &&
      ref?.current?.scrollHeight &&
      ref?.current?.offsetWidth &&
      ref?.current?.scrollWidth &&
      (ref?.current?.offsetHeight < ref?.current?.scrollHeight ||
        ref?.current?.offsetWidth < ref?.current?.scrollWidth);

    setAfficherBoutonLireLaSuite(Boolean(doitAfficherBoutonLireLaSuite));
  }, []);

  if (!texte || texte === "") return null;

  return (
    <div className="justify-start">
      <p
        className={`${classEnFonctionDeAfficherEnEntier()} mb-2`}
        ref={ref}
      >
        {texte}
      </p>
      {afficherBoutonLireLaSuite && (
        <button
          className="fr-link inline border-0 border-b border-solid border-[--underline-img] text-sm hover:border-b-[1.5px] hover:!bg-inherit"
          onClick={() => setAfficherEnEntier((valeurActuelle) => !valeurActuelle)}
          type="button"
        >
          {afficherEnEntier ? i18n.COMMUN.MASQUER_SUITE : i18n.COMMUN.LIRE_SUITE}
        </button>
      )}
    </div>
  );
};

export default TexteTronqué;
