import style from "./LayoutInscription.module.scss";
import IndicateurÉtapes from "@/components/_dsfr/IndicateurÉtapes/IndicateurÉtapes";
import { i18n } from "@/configuration/i18n/i18n";
import { Outlet } from "@tanstack/react-router";
import { useState } from "react";

const LayoutInscription = () => {
  const [etapeActuelle, setEtapeActuelle] = useState(1);
  const étapes = [
    i18n.INSCRIPTION.ÉTAPES.MON_PROJET,
    i18n.INSCRIPTION.ÉTAPES.MA_SCOLARITÉ,
    i18n.INSCRIPTION.ÉTAPES.MES_ASPIRATIONS,
    i18n.INSCRIPTION.ÉTAPES.MES_TALENTS,
    i18n.INSCRIPTION.ÉTAPES.MES_MÉTIERS,
    i18n.INSCRIPTION.ÉTAPES.MES_ÉTUDES,
  ];

  return (
    <div className={style.couleurDeFond}>
      <div className={style.imageDeFond}>
        <div className="fr-container--fluid">
          <div className="fr-grid-row fr-grid-row--center">
            <div className={`${style.carte} fr-col-12 fr-col-md-8 fr-mt-md-7w fr-mb-md-10w`}>
              <div className={`${style.etape} fr-p-2w fr-pt-md-3w fr-px-md-10w fr-pb-md-4w`}>
                <IndicateurÉtapes
                  étapeActuelle={etapeActuelle}
                  étapes={étapes}
                />
              </div>
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LayoutInscription;
