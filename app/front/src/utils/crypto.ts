const versBase64 = (valeur: ArrayBuffer): string => {
  return btoa([...new Uint8Array(valeur)].map((chr) => String.fromCodePoint(chr)).join(""));
};

export const générerState = (): string => {
  const uuid = crypto.randomUUID();
  return uuid.replaceAll("-", "");
};

export const générerPKCECodeVerifier = (): string => {
  return générerState() + générerState() + générerState();
};

export const générerPKCECodeChallenge = async (codeVerifier: string): Promise<string> => {
  const encoder = new TextEncoder();
  const data = encoder.encode(codeVerifier);
  const hashed = await crypto.subtle.digest("SHA-256", data);
  return versBase64(hashed).replaceAll("+", "-").replaceAll("/", "_").replaceAll("=", "");
};
