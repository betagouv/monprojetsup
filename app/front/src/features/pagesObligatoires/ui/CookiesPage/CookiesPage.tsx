import Head from "@/components/_layout/Head/Head";
import Titre from "@/components/Titre/Titre.tsx";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { i18n } from "@/configuration/i18n/i18n.ts";
import { ToggleSwitch } from "@codegouvfr/react-dsfr/ToggleSwitch";
import { useLayoutEffect, useState } from "react";

const CookiesPage = () => {
  const [estOptOutMatomo, setEstOptOutMatomo] = useState<boolean>(false);

  useLayoutEffect(() => {
    const récupérerOptOutMatomo = async () => {
      setEstOptOutMatomo(await dépendances.analyticsRepository.estOptOut());
    };

    if (dépendances.analyticsRepository.estInitialisé()) {
      void récupérerOptOutMatomo();
    }
  }, []);

  return (
    <>
      <Head titre={i18n.PAGE_COOKIES.TITRE_PAGE} />
      <div className="fr-container pb-20 pt-12 [&_h2]:mb-2 [&_h2]:pt-5">
        <Titre niveauDeTitre="h1">{i18n.PAGE_COOKIES.TITRE_PAGE}</Titre>
        {dépendances.analyticsRepository.estInitialisé() ? (
          <>
            <p>
              Ce site dépose un petit fichier texte (un « cookie ») sur votre ordinateur lorsque vous le consultez. Cela
              nous permet de mesurer le nombre de visites et de comprendre quelles sont les pages les plus consultées.
            </p>
            <Titre niveauDeTitre="h2">Ce site n’affiche pas de bannière de consentement aux cookies, pourquoi ?</Titre>
            <p>
              C’est vrai, vous n’avez pas eu à cliquer sur un bloc qui recouvre la moitié de la page pour dire que vous
              êtes d’accord avec le dépôt de cookies !
            </p>
            <p>
              Nous respectons simplement la loi, qui dit que certains outils de suivi d’audience, correctement
              configurés pour respecter la vie privée, sont exemptés d’autorisation préalable. Nous utilisons pour cela
              Matomo, un outil libre, paramétré pour être en conformité avec la recommandation « Cookies » de la CNIL.
              Cela signifie que votre adresse IP, par exemple, est anonymisée avant d’être enregistrée. Il est donc
              impossible d’associer vos visites sur ce site à votre personne.
            </p>
            <p>Cependant vous pouvez toujours vous opposer au suivi dans la section ci-dessous.</p>
            <Titre niveauDeTitre="h2">Gestion des cookies</Titre>
            <div className="mt-4 pl-2">
              <ToggleSwitch
                checked={!estOptOutMatomo}
                label="Suivi des visites (Matomo)"
                onChange={async (checked) => {
                  await dépendances.analyticsRepository.changerConsentementMatomo();
                  setEstOptOutMatomo(!checked);
                }}
              />
            </div>
          </>
        ) : (
          <p>Ce site n'utilise pas de cookies.</p>
        )}
      </div>
    </>
  );
};

export default CookiesPage;
