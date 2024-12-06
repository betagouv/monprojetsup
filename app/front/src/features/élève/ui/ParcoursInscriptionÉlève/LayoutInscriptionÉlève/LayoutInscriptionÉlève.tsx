import { actionsInscriptionÉlèveStore } from "@/features/élève/ui/ParcoursInscriptionÉlève/useInscriptionÉlèveStore/useInscriptionÉlèveStore";
import { Outlet, useRouterState } from "@tanstack/react-router";
import { useEffect } from "react";

const LayoutInscriptionÉlève = () => {
  const { définirÉtapeActuelle } = actionsInscriptionÉlèveStore();

  const router = useRouterState();

  useEffect(() => {
    définirÉtapeActuelle(router.location.pathname);
  }, [router.location.pathname, définirÉtapeActuelle]);

  return (
    <div className="h-full bg-[--background-alt-beige-gris-galet]">
      <div className="h-full bg-[url('/images-de-fond/inscription.svg')] bg-no-repeat">
        <div className="fr-container--fluid">
          <div className="fr-grid-row fr-grid-row--center">
            <div className="fr-col-12 fr-col-md-8 fr-mt-md-7w fr-mb-md-10w bg-[--background-raised-grey] md:min-w-[46.25rem] md:max-w-[52.5rem] md:shadow-md">
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LayoutInscriptionÉlève;
