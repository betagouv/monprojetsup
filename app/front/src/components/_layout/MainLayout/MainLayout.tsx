import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import LienÉvitement from "@/components/LienÉvitement/LienÉvitement";
import Toast from "@/components/Toast/Toast";
import { constantes } from "@/configuration/constantes";
import useÉlèveRedirection from "@/features/élève/ui/hooks/useÉlèveRedirection/useÉlèveRedirection";
import { Outlet, ScrollRestoration } from "@tanstack/react-router";

const MainLayout = () => {
  const { estInitialisé } = useÉlèveRedirection();

  if (!estInitialisé) return null;

  return (
    <>
      <LienÉvitement />
      <Entête />
      <main
        id={constantes.ACCESSIBILITÉ.CONTENU_ID}
        tabIndex={-1}
      >
        <ScrollRestoration getKey={(location) => location.pathname} />
        <Toast />
        <Outlet />
      </main>
      <section
        id={constantes.ACCESSIBILITÉ.PIED_DE_PAGE_ID}
        tabIndex={-1}
      >
        <PiedDePage />
      </section>
    </>
  );
};

export default MainLayout;
