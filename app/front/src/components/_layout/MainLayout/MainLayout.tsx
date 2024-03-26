import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import { Outlet } from "@tanstack/react-router";

const MainLayout = () => {
  return (
    <>
      <Entête />
      <main className="fr-m-6w">
        <Outlet />
      </main>
      <PiedDePage />
    </>
  );
};

export default MainLayout;
