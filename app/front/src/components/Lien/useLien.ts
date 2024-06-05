import { type useLienProps } from "./useLien.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useMemo } from "react";

const useLien = ({ href, ariaLabel, taille, variante, icône, estUnTéléchargement, estUnTag }: useLienProps) => {
  const estLienExterne = useMemo(() => /^https?:\/\/|www\./imu.test(href), [href]);
  const estLienEmail = useMemo(() => /^mailto:/imu.test(href), [href]);
  const estLienTéléphone = useMemo(() => /^tel:/imu.test(href), [href]);

  const classEnFonctionDeLaTaille = () => {
    if (taille === "grand") return "fr-link--lg";
    if (taille === "petit") return "fr-link--sm";

    return "";
  };

  const classEnFonctionDeLaVariante = () => {
    if (variante === "simple") return "fr-link";
    if (variante === "neutre") return "bg-none";

    return "";
  };

  const classEnFonctionDeLIcône = () => {
    if (icône?.position === "droite") return `fr-link--icon-right ${icône.classe}`;
    if (icône?.position === "gauche") return `fr-link--icon-left ${icône.classe}`;

    return "";
  };

  const classEnFonctionDeEstUnTéléchargement = () => {
    if (estUnTéléchargement) return "fr-link--download";

    return "";
  };

  const classEnFonctionDeEstUnTag = () => {
    if (estUnTag) return `fr-tag ${taille === "petit" ? "fr-tag--sm" : ""}`;

    return "";
  };

  const ariaLabelFormaté = () => {
    if (estLienExterne) return `${ariaLabel} - ${i18n.ACCESSIBILITÉ.LIEN_EXTERNE}`;

    if (estLienEmail) return `${ariaLabel} - ${i18n.ACCESSIBILITÉ.LIEN_EMAIL}`;

    if (estLienTéléphone) return `${ariaLabel} - ${i18n.ACCESSIBILITÉ.LIEN_TÉLÉPHONE}`;

    return ariaLabel;
  };

  return {
    ariaLabelFormaté: ariaLabelFormaté(),
    classesCSS: `${classEnFonctionDeLaTaille()} ${classEnFonctionDeLIcône()} ${classEnFonctionDeEstUnTéléchargement()} ${classEnFonctionDeLaVariante()} ${classEnFonctionDeEstUnTag()} has-[.fr-btn]:after:hidden`,
    target: estLienExterne ? "_blank" : "_self",
  };
};

export default useLien;
