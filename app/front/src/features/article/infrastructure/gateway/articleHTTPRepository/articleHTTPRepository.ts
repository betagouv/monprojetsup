import {
  type ArticleHTTP,
  type GetAllArticlesResponse,
  type GetArticleResponse,
} from "./articleHTTPRepository.interface";
import { type Article } from "@/features/article/domain/article.interface";
import { type ArticleRepository } from "@/features/article/infrastructure/gateway/articleRepository.interface";
import { type IHttpClient } from "@/services/httpClient/httpClient.interface";

export class ArticleHTTPRepository implements ArticleRepository {
  public constructor(
    private readonly _endpoint: string,
    private readonly _httpClient: IHttpClient,
  ) {}

  public async get(id: number) {
    const data = await this._httpClient.fetch<GetArticleResponse>({
      endpoint: `${this._endpoint}/${id}`,
      method: "GET",
    });

    if (!data) return undefined;

    return this._mapToDomain(data);
  }

  public async getAll() {
    const data = await this._httpClient.fetch<GetAllArticlesResponse>({
      endpoint: `${this._endpoint}`,
      method: "GET",
    });

    if (!data) return undefined;

    return data.map((article) => this._mapToDomain(article));
  }

  private _mapToDomain(article: ArticleHTTP): Article {
    return {
      id: article.id,
      titre: article.title,
      contenu: article.body,
    };
  }
}
