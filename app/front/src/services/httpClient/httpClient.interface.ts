export type HttpClientOptions = {
  body?: object;
  endpoint: string;
  méthode: "GET" | "POST";
  contentType?: string;
  headers?: object;
};

export type IHttpClient = {
  récupérer: <O extends object>(options: HttpClientOptions) => Promise<O | Error>;
};
