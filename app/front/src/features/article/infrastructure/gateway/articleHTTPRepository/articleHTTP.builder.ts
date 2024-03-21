import { type ArticleHTTP } from "./articleHTTPRepository.interface";

export class ArticleHTTPBuilder {
  private article: ArticleHTTP;

  public constructor() {
    this.article = {
      userId: 0,
      id: 0,
      title: "",
      body: "",
    };
  }

  public withUserId(userId: number): ArticleHTTPBuilder {
    this.article.userId = userId;
    return this;
  }

  public withId(id: number): ArticleHTTPBuilder {
    this.article.id = id;
    return this;
  }

  public withTitle(title: string): ArticleHTTPBuilder {
    this.article.title = title;
    return this;
  }

  public withBody(body: string): ArticleHTTPBuilder {
    this.article.body = body;
    return this;
  }

  public build(): ArticleHTTP {
    return this.article;
  }
}
