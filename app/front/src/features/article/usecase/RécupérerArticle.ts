import { type Article } from "@/features/article/domain/article.interface";
import { type ArticleRepository } from "@/features/article/infrastructure/gateway/articleRepository.interface";

export class RécupérerArticleUseCase {
  public constructor(private readonly _articleRepository: ArticleRepository) {}

  public async run(id: number): Promise<Article | undefined> {
    return await this._articleRepository.get(id);
  }
}
