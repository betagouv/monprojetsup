import { type TagFiltreAvecEmojiProps } from "./TagFiltreAvecEmoji.interface";
import { Tag } from "@codegouvfr/react-dsfr/Tag";
import { useState } from "react";

const TagFiltreAvecEmoji = ({ children, emoji, auClic, appuyéParDéfaut = false }: TagFiltreAvecEmojiProps) => {
  const [estAppuyé, setEstAppuyé] = useState(appuyéParDéfaut);

  return (
    <Tag
      nativeButtonProps={{
        onClick: () => {
          auClic(!estAppuyé);
          setEstAppuyé(!estAppuyé);
        },
        type: "button",
      }}
      pressed={estAppuyé}
    >
      {emoji && (
        <>
          <span
            aria-hidden
            className="pr-2"
          >
            {emoji}
          </span>{" "}
        </>
      )}
      <span className="capitalize">{children}</span>
    </Tag>
  );
};

export default TagFiltreAvecEmoji;
