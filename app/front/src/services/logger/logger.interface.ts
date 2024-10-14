export type ILogger = {
  debug: (object: unknown) => void;
  error: (object: unknown) => void;
  info: (object: unknown) => void;
  trace: (object: unknown) => void;
  warn: (object: unknown) => void;
};
