import { type Article } from "./article.interface";

export class ArticleBuilder {
  private article: Article;

  public constructor() {
    this.article = {
      id: 0,
      titre: "",
      contenu: "",
    };
  }

  public withId(id: number): ArticleBuilder {
    this.article.id = id;
    return this;
  }

  public withTitre(titre: string): ArticleBuilder {
    this.article.titre = titre;
    return this;
  }

  public withContenu(contenu: string): ArticleBuilder {
    this.article.contenu = contenu;
    return this;
  }

  public build(): Article {
    return this.article;
  }
}
