import Entête from "@/components/_layout/Entête/Entête";
import Footer from "@/components/_layout/Footer/Footer";
import { Outlet } from "@tanstack/react-router";

const MainLayout = () => {
  return (
    <>
      <Entête />
      <main className="fr-m-6w">
        <Outlet />
      </main>
      <Footer />
    </>
  );
};

export default MainLayout;
