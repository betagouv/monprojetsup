export type ArticleHTTP = {
  userId: number;
  id: number;
  title: string;
  body: string;
};
export type GetAllArticlesResponse = ArticleHTTP[];
export type GetArticleResponse = ArticleHTTP;
