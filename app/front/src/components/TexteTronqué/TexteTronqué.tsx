import { type TexteTronquéProps } from "./TexteTronqué.interface";
import { i18n } from "@/configuration/i18n/i18n";
import parse from "html-react-parser";
import { useEffect, useRef, useState } from "react";
import remarkHtml from "remark-html";
import remarkParse from "remark-parse";
import { unified } from "unified";

const TexteTronqué = ({ texte }: TexteTronquéProps) => {
  const [afficherEnEntier, setAfficherEnEntier] = useState(false);
  const [afficherBoutonLireLaSuite, setAfficherBoutonLireLaSuite] = useState(false);

  const ref = useRef<HTMLParagraphElement | null>(null);

  const classEnFonctionDeAfficherEnEntier = () => {
    if (afficherEnEntier) return "";

    return "line-clamp-4";
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

  // Convertir le texte en HTML si nécessaire

  const texteHtml = unified().use(remarkParse).use(remarkHtml).processSync(texte).toString();

  return (
    <div className="justify-start">
      <div
        className={`${classEnFonctionDeAfficherEnEntier()} custom-ul mb-2 `}
        ref={ref}
      >
        {parse(texteHtml)}
      </div>
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
