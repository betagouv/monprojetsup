import { dependencies } from "@/configuration/dependencies/dependencies";
import { RécupérerArticlesUseCase } from "@/features/article/usecase/RécupérerArticles";
import { queryOptions } from "@tanstack/react-query";

export const articlesQueryOptions = queryOptions({
  queryKey: ["articles"],
  queryFn: async () => {
    const articles = await new RécupérerArticlesUseCase(dependencies.articleRepository).run();

    if (!articles) {
      throw new Error("Impossible de récupérer les articles");
    }

    return articles;
  },
});
