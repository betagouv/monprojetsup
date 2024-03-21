import { env } from "@/configuration/environnement";
import { ArticleHTTPRepository } from "@/features/article/infrastructure/gateway/articleHTTPRepository/articleHTTPRepository";
import { type ArticleRepository } from "@/features/article/infrastructure/gateway/articleRepository.interface";
import { HttpClient } from "@/services/httpClient/httpClient";
import { type IHttpClient } from "@/services/httpClient/httpClient.interface";
import { Logger } from "@/services/logger/logger";
import { type ILogger } from "@/services/logger/logger.interface";

export class Dependencies {
  private static instance: Dependencies;

  public readonly logger: ILogger;

  public readonly httpClient: IHttpClient;

  public readonly articleRepository: ArticleRepository;

  private constructor() {
    this.logger = new Logger();
    this.httpClient = new HttpClient(this.logger);
    this.articleRepository = new ArticleHTTPRepository(`${env.VITE_API_URL}/posts`, this.httpClient);
  }

  public static getInstance(): Dependencies {
    if (!Dependencies.instance) {
      Dependencies.instance = new Dependencies();
    }

    return Dependencies.instance;
  }
}

export const dependencies = Dependencies.getInstance();
