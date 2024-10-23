import { IllustrationBoutonRadioEmojiProps } from "./IllustrationBoutonRadioEmoji.interface";

const IllustrationBoutonRadioEmoji = ({ emoji }: IllustrationBoutonRadioEmojiProps) => {
  return (
    <p
      aria-hidden="true"
      className="mb-0 text-3xl"
    >
      {emoji}
    </p>
  );
};

export default IllustrationBoutonRadioEmoji;
