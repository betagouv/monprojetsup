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
    <div className="bg-[--background-alt-beige-gris-galet]">
      <div className="bg-[url('/images-de-fond/inscription.svg')] bg-no-repeat">
        <div className="fr-container--fluid">
          <div className="fr-grid-row fr-grid-row--center">
            <div className="bg-[--background-raised-grey] md:max-w-[840px] md:min-w-[740px] md:shadow-md fr-col-12 fr-col-md-8 fr-mt-md-7w fr-mb-md-10w">
              <div className="shadow-md fr-p-2w fr-py-md-3w fr-px-md-10w">
                <IndicateurÉtapes
                  étapeActuelle={etapeActuelle}
                  étapes={étapes}
                />
              </div>
              <div className="fr-py-3w fr-px-2w fr-py-md-6w fr-px-md-8w">
                <Outlet />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LayoutInscription;
