import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import { Outlet } from "@tanstack/react-router";

const MainLayout = () => {
  return (
    <>
      <Entête />
      <main>
        <Outlet />
      </main>
      <PiedDePage />
    </>
  );
};

export default MainLayout;
