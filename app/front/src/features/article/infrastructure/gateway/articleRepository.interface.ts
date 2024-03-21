import { type Article } from "@/features/article/domain/article.interface";

export type ArticleRepository = {
  getAll: () => Promise<Article[] | undefined>;
  get: (id: number) => Promise<Article | undefined>;
};
