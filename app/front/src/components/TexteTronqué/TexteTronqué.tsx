import { type TexteTronquéProps } from "./TexteTronqué.interface";
import parse from "html-react-parser";
import { useRef } from "react";
import remarkHtml from "remark-html";
import remarkParse from "remark-parse";
import { unified } from "unified";

const TexteTronqué = ({ texte }: TexteTronquéProps) => {
  const ref = useRef<HTMLDivElement | null>(null);

  if (!texte || texte === "") return null;

  const texteHtml = unified()
    .use(remarkParse)
    .use(remarkHtml)
    .processSync(texte)
    .toString()
    .replaceAll("<a ", '<a target="_blank" rel="noopener noreferrer" ');

  return (
    <div className="justify-start">
      <div
        className="mb-2"
        ref={ref}
      >
        {parse(texteHtml)}
      </div>
    </div>
  );
};

export default TexteTronqué;
