import Head from "@/components/_layout/Head/Head";
import { i18n } from "@/configuration/i18n/i18n";
import ArticlesList from "@/features/article/ui/ArticlesList/ArticlesList";
import { articlesQueryOptions } from "@/features/article/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";

const ArticlePage = () => {
  const { data: articles } = useSuspenseQuery(articlesQueryOptions);

  return (
    <>
      <Head title={i18n.PAGE_ARTICLES.TITLE} />
      <div className="fr-container">
        <h1>{i18n.PAGE_ARTICLES.TITLE}</h1>
        <ArticlesList articles={articles} />
      </div>
    </>
  );
};

export default ArticlePage;
