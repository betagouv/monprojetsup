export type IHttpClient = {
  récupérer: <O extends {}>(options: HttpClientOptions) => Promise<O | undefined>;
};

export type HttpClientOptions = {
  body?: {};
  endpoint: string;
  méthode: "GET" | "POST";
  contentType?: string;
  headers?: {};
};
