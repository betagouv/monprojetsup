import Head from "@/components/_layout/Head/Head";
import { i18n } from "@/configuration/i18n/i18n";

const AccueilPage = () => {
  return (
    <>
      <Head title={i18n.PAGE_ACCUEIL.TITLE} />
      <div className="fr-container">
        <h1>{i18n.PAGE_ACCUEIL.TITLE}</h1>
      </div>
    </>
  );
};

export default AccueilPage;
