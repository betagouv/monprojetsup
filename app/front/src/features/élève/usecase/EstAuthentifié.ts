import { IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface"

export class EstAuthentifiéUseCase {
  public constructor(private readonly _client: IMpsApiHttpClient) {}

  public run(): boolean {
    return this._client.estAuthentifié();
  }
  
}
