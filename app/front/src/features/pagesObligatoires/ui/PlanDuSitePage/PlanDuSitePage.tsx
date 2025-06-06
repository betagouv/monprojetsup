import Head from "@/components/_layout/Head/Head";
import LienInterne from "@/components/Lien/LienInterne/LienInterne.tsx";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import { Paths } from "@/types/commons.ts";

const PlanDuSitePage = () => {
  const pages: { titre: string; url: Paths }[] = [
    {
      titre: i18n.NAVIGATION.TABLEAU_DE_BORD,
      url: "/",
    },
    {
      titre: i18n.NAVIGATION.FORMATIONS,
      url: "/formations",
    },
    {
      titre: i18n.NAVIGATION.FAVORIS,
      url: "/favoris",
    },
    {
      titre: i18n.PAGE_PROFIL.TITRE_PAGE,
      url: "/profil",
    },
  ];

  return (
    <>
      <Head titre={i18n.PAGE_PLAN_DU_SITE.TITRE_PAGE} />
      <div className="fr-container pb-20 pt-12">
        <Titre niveauDeTitre="h1">{i18n.PAGE_PLAN_DU_SITE.TITRE_PAGE}</Titre>
        <ul>
          {pages.map((page) => (
            <li
              className="p-1"
              key={page.url}
            >
              <LienInterne
                ariaLabel={page.titre}
                href={page.url}
                variante="simple"
              >
                {page.titre}
              </LienInterne>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
};

export default PlanDuSitePage;
