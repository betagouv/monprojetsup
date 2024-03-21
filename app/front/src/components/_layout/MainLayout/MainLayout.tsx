import Footer from "@/components/_layout/Footer/Footer";
import Header from "@/components/_layout/Header/Header";
import { Outlet } from "@tanstack/react-router";

const MainLayout = () => {
  return (
    <>
      <Header />
      <main className="fr-m-6w">
        <Outlet />
      </main>
      <Footer />
    </>
  );
};

export default MainLayout;
