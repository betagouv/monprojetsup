import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import LienÉvitement from "@/components/LienÉvitement/LienÉvitement";
import useÉlèveRedirection from "@/features/élève/ui/hooks/useÉlèveRedirection/useÉlèveRedirection";
import { Outlet, ScrollRestoration } from "@tanstack/react-router";

const MainLayout = () => {
  const { estInitialisé } = useÉlèveRedirection();

  if (!estInitialisé) return null;

  return (
    <>
      <LienÉvitement />
      <Entête />
      <main id="contenu">
        <ScrollRestoration />
        <Outlet />
      </main>
      <PiedDePage />
    </>
  );
};

export default MainLayout;
