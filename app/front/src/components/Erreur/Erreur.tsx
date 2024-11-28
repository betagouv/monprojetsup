import { ErreurProps } from "./Erreur.interface";
import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import LienÉvitement from "@/components/LienÉvitement/LienÉvitement";
import Toast from "@/components/Toast/Toast";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import {
  NonAutoriséErreurHttp,
  NonIdentifiéErreurHttp,
  ServeurTemporairementIndisponibleErreurHttp,
} from "@/services/erreurs/erreursHttp";
import { ScrollRestoration } from "@tanstack/react-router";
import { useEffect } from "react";

const Erreur = ({ erreur }: ErreurProps) => {
  const utilisateur = useUtilisateur();

  useEffect(() => {
    const gérerErreur = async () => {
      if (erreur instanceof NonIdentifiéErreurHttp || erreur instanceof NonAutoriséErreurHttp) {
        await utilisateur.seDéconnecter();
      }
    };

    // eslint-disable-next-line promise/prefer-await-to-then
    gérerErreur().catch(() => {});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [erreur]);

  scrollTo({ top: 0 });

  if (erreur instanceof NonIdentifiéErreurHttp || erreur instanceof NonAutoriséErreurHttp) return null;

  return (
    <>
      <LienÉvitement />
      <Entête />
      <main id="contenu">
        <ScrollRestoration />
        <Toast />
        <div className="mx-auto my-60 grid gap-2 place-self-center text-center lg:w-[45%]">
          <div
            aria-hidden="true"
            className="fr-display--lg fr-mb-0"
          >
            {erreur instanceof ServeurTemporairementIndisponibleErreurHttp
              ? i18n.ERREURS.SERVEUR_INDISPONIBLE.EMOJI
              : i18n.ERREURS.GÉNÉRIQUE.EMOJI}
          </div>
          <p className="fr-display--xs mb-0">
            {erreur instanceof ServeurTemporairementIndisponibleErreurHttp
              ? i18n.ERREURS.SERVEUR_INDISPONIBLE.TITRE
              : i18n.ERREURS.GÉNÉRIQUE.TITRE}
          </p>
          <p className="fr-h3">
            {erreur instanceof ServeurTemporairementIndisponibleErreurHttp
              ? i18n.ERREURS.SERVEUR_INDISPONIBLE.SOUS_TITRE
              : i18n.ERREURS.GÉNÉRIQUE.SOUS_TITRE}
          </p>
        </div>
      </main>
      <PiedDePage />
    </>
  );
};

export default Erreur;
