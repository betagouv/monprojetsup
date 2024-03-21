export type ILogger = {
  debug: (object: {}) => void;
  error: (object: {}) => void;
  info: (object: {}) => void;
  trace: (object: {}) => void;
  warn: (object: {}) => void;
};
