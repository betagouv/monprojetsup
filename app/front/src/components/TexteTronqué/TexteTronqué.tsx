import { type TexteTronquéProps } from "./TexteTronqué.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useEffect, useRef, useState } from "react";

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

  return (
    <div className="justify-start">
      <p
        className={`mb-2 whitespace-pre-line`}
        ref={ref}
      >
        {texte}
      </p>
    </div>
  );
};

export default TexteTronqué;
