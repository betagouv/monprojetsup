import { ArticleHTTPBuilder } from "./articleHTTP.builder";
import { ArticleHTTPRepository } from "./articleHTTPRepository";
import { ArticleBuilder } from "@/features/article/domain/article.builder";
import { type IHttpClient } from "@/services/httpClient/httpClient.interface";
import { mock } from "vitest-mock-extended";

describe("ArticleHTTPRepository", () => {
  let articleHTTPRepository: ArticleHTTPRepository;
  const httpClient = mock<IHttpClient>();
  const ENDPOINT = "http://example.com/articles";

  beforeEach(() => {
    articleHTTPRepository = new ArticleHTTPRepository(ENDPOINT, httpClient);
  });

  describe("get", () => {
    test("doit retourner l'article correspondant à l'id demandé", async () => {
      // GIVEN
      const articleId = 1;
      const fetchedArticle = new ArticleHTTPBuilder().withId(articleId).withTitle("titre").build();
      const expectedArticle = new ArticleBuilder().withId(articleId).withTitre("titre").build();
      httpClient.fetch.mockResolvedValueOnce(fetchedArticle);

      // WHEN
      const result = await articleHTTPRepository.get(articleId);

      // THEN
      expect(httpClient.fetch).toHaveBeenCalledWith({
        endpoint: `${ENDPOINT}/${articleId}`,
        method: "GET",
      });
      expect(result).toEqual(expectedArticle);
    });

    test("doit retourner undefined si il y a une erreur durant la récupération ou que l'id n'existe pas", async () => {
      // GIVEN
      const articleId = 1;
      httpClient.fetch.mockResolvedValueOnce(undefined);

      // WHEN
      const result = await articleHTTPRepository.get(articleId);

      // THEN
      expect(result).toBeUndefined();
      expect(httpClient.fetch).toHaveBeenCalledWith({
        endpoint: `${ENDPOINT}/${articleId}`,
        method: "GET",
      });
    });
  });

  describe("getAll", () => {
    test("doit retourner tous les articles", async () => {
      // GIVEN
      const fetchedArticles = [
        new ArticleHTTPBuilder().withId(1).withTitle("titre 1").build(),
        new ArticleHTTPBuilder().withId(2).withTitle("titre 2").build(),
      ];
      const expectedArticles = [
        new ArticleBuilder().withId(1).withTitre("titre 1").build(),
        new ArticleBuilder().withId(2).withTitre("titre 2").build(),
      ];
      httpClient.fetch.mockResolvedValueOnce(fetchedArticles);

      // WHEN
      const result = await articleHTTPRepository.getAll();

      // THEN
      expect(httpClient.fetch).toHaveBeenCalledWith({
        endpoint: ENDPOINT,
        method: "GET",
      });
      expect(result).toEqual(expectedArticles);
    });

    test("doit retourner undefined si il y a une erreur durant la récupération", async () => {
      // GIVEN
      httpClient.fetch.mockResolvedValueOnce(undefined);

      // WHEN
      const result = await articleHTTPRepository.getAll();

      // THEN
      expect(result).toBeUndefined();
      expect(httpClient.fetch).toHaveBeenCalledWith({
        endpoint: ENDPOINT,
        method: "GET",
      });
    });
  });
});
