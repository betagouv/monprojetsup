import { type Article } from "@/features/article/domain/article.interface";
import { type ArticleRepository } from "@/features/article/infrastructure/gateway/articleRepository.interface";

export class RécupérerArticlesUseCase {
  public constructor(private readonly _articleRepository: ArticleRepository) {}

  public async run(): Promise<Article[] | undefined> {
    return await this._articleRepository.getAll();
  }
}
