import { type BadgeProps } from "./Badge.interface";

const Badge = ({ titre, taille, type, afficherIcône = true }: BadgeProps) => {
  const classEnFonctionDeLaTaille = () => {
    if (taille === "sm") return "fr-badge--sm";
    return "";
  };

  const classEnFonctionDuType = () => {
    if (type === "succès") return "fr-badge--success";
    if (type === "erreur") return "fr-badge--error";
    if (type === "info") return "fr-badge--info";
    if (type === "alerte") return "fr-badge--warning";
    if (type === "nouveauté") return "fr-badge--new";
    return "";
  };

  const classEnFonctionDeLAffichageDeLIcône = () => {
    if (afficherIcône) return "fr-badge--no-icon";
    return "";
  };

  return (
    <p
      className={`fr-badge ${classEnFonctionDeLaTaille()} ${classEnFonctionDuType()} ${classEnFonctionDeLAffichageDeLIcône()}`}
    >
      {titre}
    </p>
  );
};

export default Badge;
