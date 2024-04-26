import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";
import IndicateurÉtapes from "@/components/_dsfr/IndicateurÉtapes/IndicateurÉtapes";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import {
  actionsÉtapesInscriptionÉlèveStore,
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapePrécédenteÉtapesInscriptionÉlèveStore,
  étapesÉtapesInscriptionÉlèveStore,
  indexÉtapeActuelleÉtapesInscriptionÉlèveStore,
} from "@/features/élève/ui/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import { Outlet, ScrollRestoration, useRouterState } from "@tanstack/react-router";
import { Suspense, useEffect } from "react";

const LayoutInscription = () => {
  const { définirÉtapeActuelle } = actionsÉtapesInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleÉtapesInscriptionÉlèveStore();
  const étapePrécédente = étapePrécédenteÉtapesInscriptionÉlèveStore();
  const indexÉtapeActuelle = indexÉtapeActuelleÉtapesInscriptionÉlèveStore();
  const étapes = étapesÉtapesInscriptionÉlèveStore();
  const router = useRouterState();

  useEffect(() => {
    définirÉtapeActuelle(router.location.pathname);
  }, [router.location.pathname, définirÉtapeActuelle]);

  return (
    <div className="h-full bg-[--background-alt-beige-gris-galet]">
      <div className="h-full bg-[url('/images-de-fond/inscription.svg')]  bg-no-repeat">
        <div className="fr-container--fluid">
          <div className="fr-grid-row fr-grid-row--center">
            <div className="fr-col-12 fr-col-md-8 fr-mt-md-7w fr-mb-md-10w bg-[--background-raised-grey] md:min-w-[740px] md:max-w-[840px] md:shadow-md">
              <div className="fr-p-2w fr-py-md-3w fr-px-md-10w shadow-md">
                <IndicateurÉtapes
                  indexÉtapeActuelle={indexÉtapeActuelle ?? 1}
                  étapes={étapes.map((étape) => étape.titreÉtape)}
                />
              </div>
              <div className="fr-py-3w fr-px-2w fr-py-md-6w fr-px-md-8w">
                <div className="*:mb-0">
                  <Titre
                    niveauDeTitre="h1"
                    styleDeTitre="h2"
                  >
                    {étapeActuelle?.titre}
                  </Titre>
                </div>
                <p className="fr-text--lg">{i18n.COMMUN.CHAMPS_OBLIGATOIRES}</p>
                <Suspense fallback={<AnimationChargement />}>
                  <ScrollRestoration />
                  <Outlet />
                  <hr className="mt-12" />
                  <div className={`fr-grid-row ${étapePrécédente ? "justify-between" : "justify-end"}`}>
                    {étapePrécédente && (
                      <LienInterne
                        ariaLabel={i18n.COMMUN.RETOUR}
                        href={étapePrécédente.url}
                        variante="neutre"
                      >
                        <BoutonSquelette
                          icône={{ position: "gauche", classe: "fr-icon-arrow-left-line" }}
                          label={i18n.COMMUN.RETOUR}
                          variante="secondaire"
                        />
                      </LienInterne>
                    )}
                    <Bouton
                      formId={étapeActuelle?.url}
                      icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
                      label={i18n.COMMUN.CONTINUER}
                      type="submit"
                    />
                  </div>
                </Suspense>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LayoutInscription;
