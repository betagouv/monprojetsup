import { i18n } from "@/configuration/i18n/i18n";

const AnimationChargement = () => {
  return (
    <div className="my-10 text-center">
      <div
        aria-label={i18n.ACCESSIBILITÉ.CHARGEMENT}
        className="inline-block size-10 animate-spin rounded-full border-[5px] border-solid border-[--text-action-high-blue-france] border-t-transparent text-[--text-action-high-blue-france]"
        role="status"
      >
        <span className="sr-only">{i18n.ACCESSIBILITÉ.CHARGEMENT}</span>
      </div>
    </div>
  );
};

export default AnimationChargement;
