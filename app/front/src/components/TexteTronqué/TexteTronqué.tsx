import { type TexteTronquéProps } from "./TexteTronqué.interface";
import { useRef } from "react";

const TexteTronqué = ({ texte }: TexteTronquéProps) => {
  const ref = useRef<HTMLParagraphElement | null>(null);

  if (!texte || texte === "") return null;

  return (
    <div className="justify-start">
      <p
        className="mb-2 whitespace-pre-line"
        ref={ref}
      >
        {texte}
      </p>
    </div>
  );
};

export default TexteTronqué;
