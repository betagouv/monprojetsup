export type IHttpClient = {
  récupérer: <O extends {}>(options: HttpClientOptions) => Promise<O | undefined>;
};

export type HttpClientOptions = {
  authorization?: string;
  body?: {};
  endpoint: string;
  méthode: "GET" | "POST";
  contentType?: string;
  headers?: {};
};
