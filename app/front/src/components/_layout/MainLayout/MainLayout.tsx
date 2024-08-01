import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import useÉlèveRedirection from "@/features/élève/ui/hooks/useÉlèveRedirection/useÉlèveRedirection";
import { Outlet, ScrollRestoration } from "@tanstack/react-router";

const MainLayout = () => {
  const { estInitialisé } = useÉlèveRedirection();

  if (!estInitialisé) return null;

  return (
    <>
      <Entête />
      <main>
        <ScrollRestoration />
        <Outlet />
      </main>
      <PiedDePage />
    </>
  );
};

export default MainLayout;
