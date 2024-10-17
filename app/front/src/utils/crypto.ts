export const générerUUIDv4 = (): string => {
  const uuid = crypto.randomUUID();
  return uuid.replaceAll("-", "");
};

export const versBase64 = (valeur: ArrayBuffer): string => {
  return btoa([...new Uint8Array(valeur)].map((chr) => String.fromCodePoint(chr)).join(""));
};

export const générerPKCECodeVerifier = (): string => {
  return générerUUIDv4() + générerUUIDv4() + générerUUIDv4();
};

export const générerPKCECodeChallenge = async (codeVerifier: string): Promise<string> => {
  const encoder = new TextEncoder();
  const data = encoder.encode(codeVerifier);
  const hashed = await crypto.subtle.digest("SHA-256", data);
  return versBase64(hashed).replaceAll("+", "-").replaceAll("/", "_").replaceAll("=", "");
};
