export type IHttpClient = {
  fetch: <O extends {}>(options: HttpClientOptions) => Promise<O | undefined>;
};

export type HttpClientOptions = {
  authorization?: string;
  body?: {};
  endpoint: string;
  method: "GET" | "POST";
  contentType?: string;
  headers?: {};
};
