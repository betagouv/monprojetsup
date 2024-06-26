import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";
import IndicateurÉtapes from "@/components/_dsfr/IndicateurÉtapes/IndicateurÉtapes";
import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
import FormationsForm from "@/features/élève/ui/formulaires/FormationsForm/FormationsForm";
import IntêretsForm from "@/features/élève/ui/formulaires/IntêretsForm/IntêretsForm";
import MétiersForm from "@/features/élève/ui/formulaires/MétiersForm/MétiersForm";
import ProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import {
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapePrécédenteÉtapesInscriptionÉlèveStore,
  étapesÉtapesInscriptionÉlèveStore,
  étapeSuivanteÉtapesInscriptionÉlèveStore,
  indexÉtapeActuelleÉtapesInscriptionÉlèveStore,
} from "@/features/élève/ui/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import { useSuspenseQuery } from "@tanstack/react-query";
import { ScrollRestoration, useNavigate } from "@tanstack/react-router";
import { Suspense } from "react";

const ÉlèveInscriptionPage = () => {
  useSuspenseQuery(élèveQueryOptions);

  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteÉtapesInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleÉtapesInscriptionÉlèveStore();
  const étapePrécédente = étapePrécédenteÉtapesInscriptionÉlèveStore();
  const indexÉtapeActuelle = indexÉtapeActuelleÉtapesInscriptionÉlèveStore();
  const étapes = étapesÉtapesInscriptionÉlèveStore();

  const àLaSoumissionDuFormulaireAvecSuccès = () => {
    navigate({ to: étapeSuivante?.url });
  };

  if (!étapeActuelle) return null;

  const propsFormulaire = {
    formId: étapeActuelle?.url ?? "",
    àLaSoumissionDuFormulaireAvecSuccès,
  };

  const composantÀAfficher = () => {
    switch (étapeActuelle?.url) {
      case "/eleve/inscription/projet":
        return <ProjetForm {...propsFormulaire} />;
      case "/eleve/inscription/scolarite":
        return <ScolaritéForm {...propsFormulaire} />;
      case "/eleve/inscription/domaines":
        return (
          <DomainesForm
            {...propsFormulaire}
            niveauDeTitreCatégories="h2"
          />
        );
      case "/eleve/inscription/interets":
        return (
          <IntêretsForm
            {...propsFormulaire}
            niveauDeTitreCatégories="h2"
          />
        );
      case "/eleve/inscription/metiers":
        return <MétiersForm {...propsFormulaire} />;
      case "/eleve/inscription/etude":
        return <ÉtudeForm {...propsFormulaire} />;
      case "/eleve/inscription/formations":
        return <FormationsForm {...propsFormulaire} />;
      default:
        return null;
    }
  };

  const préambuleÀAfficher = () => {
    switch (étapeActuelle?.url) {
      case "/eleve/inscription/domaines":
        return i18n.ÉLÈVE.DOMAINES.SÉLECTIONNE_AU_MOINS_UN;
      case "/eleve/inscription/interets":
        return i18n.ÉLÈVE.INTÊRETS.SÉLECTIONNE_AU_MOINS_UN;
      default:
        return (
          <>
            {i18n.COMMUN.CHAMPS_MARQUÉS_DU_SYMBOLE} <span className="text-[--artwork-minor-red-marianne]">*</span>{" "}
            {i18n.COMMUN.SONT_OBLIGATOIRES}
          </>
        );
    }
  };

  return (
    <>
      <Head title={étapeActuelle.titreÉtape} />
      <div className="fr-p-2w fr-py-md-3w fr-px-md-10w shadow-md">
        <IndicateurÉtapes
          indexÉtapeActuelle={indexÉtapeActuelle ?? 1}
          étapes={étapes.map((étape) => étape.titreÉtape)}
        />
      </div>
      <div className="fr-py-3w fr-px-2w fr-py-md-6w fr-px-md-8w">
        <div className="*:mb-2">
          <Titre
            niveauDeTitre="h1"
            styleDeTitre="h2"
          >
            {étapeActuelle?.titre}
          </Titre>
        </div>
        <p className="fr-text--lg mb-12">{préambuleÀAfficher()}</p>
        <Suspense fallback={<AnimationChargement />}>
          <ScrollRestoration />
          {composantÀAfficher()}
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
    </>
  );
};

export default ÉlèveInscriptionPage;
