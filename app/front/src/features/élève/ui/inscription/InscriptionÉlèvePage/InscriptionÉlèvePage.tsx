import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import FormationsForm from "@/features/élève/ui/formulaires/FormationsForm/FormationsForm";
import IntérêtsForm from "@/features/élève/ui/formulaires/IntérêtsForm/IntérêtsForm";
import MétiersForm from "@/features/élève/ui/formulaires/MétiersForm/MétiersForm";
import ProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
import {
  indexÉtapeActuelleInscriptionÉlèveStore,
  étapeActuelleInscriptionÉlèveStore,
  étapePrécédenteInscriptionÉlèveStore,
  étapesInscriptionÉlèveStore,
  étapeSuivanteInscriptionÉlèveStore,
} from "@/features/élève/ui/inscription/store/useInscriptionÉlève/useInscriptionÉlève";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { Stepper } from "@codegouvfr/react-dsfr/Stepper";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { ReactNode, Suspense } from "react";

const InscriptionÉlèvePage = () => {
  useSuspenseQuery(élèveQueryOptions);

  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleInscriptionÉlèveStore();
  const étapePrécédente = étapePrécédenteInscriptionÉlèveStore();
  const indexÉtapeActuelle = indexÉtapeActuelleInscriptionÉlèveStore();
  const étapes = étapesInscriptionÉlèveStore();

  const àLaSoumissionDuFormulaireAvecSuccès = async () => {
    await navigate({ to: étapeSuivante?.url });
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
          <IntérêtsForm
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

  const préambuleÀAfficher = (): ReactNode => {
    switch (étapeActuelle?.url) {
      case "/eleve/inscription/domaines":
        return i18n.ÉLÈVE.DOMAINES.SÉLECTIONNE_AU_MOINS_UN;
      case "/eleve/inscription/interets":
        return i18n.ÉLÈVE.INTÉRÊTS.SÉLECTIONNE_AU_MOINS_UN;
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
      <Head titre={étapeActuelle.titreÉtape} />
      <div className="fr-p-2w fr-py-md-3w fr-px-md-10w shadow-md">
        <Stepper
          currentStep={indexÉtapeActuelle + 1}
          nextTitle={étapes[indexÉtapeActuelle + 1].titreÉtape}
          stepCount={étapes.length}
          title={étapes[indexÉtapeActuelle].titreÉtape}
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

export default InscriptionÉlèvePage;
