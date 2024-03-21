import { type ArticlesListProps } from "./ArticlesList.interface";

const ArticlesList = ({ articles }: ArticlesListProps) => {
  return (
    <ul>
      {articles.map((article) => (
        <li key={article.id}>{article.titre}</li>
      ))}
    </ul>
  );
};

export default ArticlesList;
