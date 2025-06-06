export interface Logger {
  consigner: (
    niveau: "fatal" | "error" | "warning" | "log" | "info" | "debug",
    erreur: Error,
    contexte?: unknown,
  ) => void;
}
