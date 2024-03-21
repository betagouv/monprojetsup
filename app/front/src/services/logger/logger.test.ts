import { Logger } from "./logger";
import { type ILogger } from "./logger.interface";

describe("Logger", () => {
  let logger: ILogger;

  beforeEach(() => {
    logger = new Logger();
  });

  describe("error", () => {
    test("should log the error message to the console", () => {
      // GIVEN
      const errorMessage = "An error occurred";
      const consoleErrorSpy = vitest.spyOn(console, "error").mockImplementationOnce(() => {});

      // WHEN
      logger.error(errorMessage);

      // THEN
      expect(consoleErrorSpy).toHaveBeenCalledWith(errorMessage);
    });
  });

  describe("warn", () => {
    test("should log the warning message to the console", () => {
      // GIVEN
      const warningMessage = "A warning occurred";
      const consoleWarnSpy = vitest.spyOn(console, "warn").mockImplementationOnce(() => {});

      // WHEN
      logger.warn(warningMessage);

      // THEN
      expect(consoleWarnSpy).toHaveBeenCalledWith(warningMessage);
    });
  });

  describe("info", () => {
    test("should log the info message to the console", () => {
      // GIVEN
      const infoMessage = "An info message";
      const consoleInfoSpy = vitest.spyOn(console, "info").mockImplementationOnce(() => {});

      // WHEN
      logger.info(infoMessage);

      // THEN
      expect(consoleInfoSpy).toHaveBeenCalledWith(infoMessage);
    });
  });

  describe("debug", () => {
    test("should log the debug message to the console", () => {
      // GIVEN
      const debugMessage = "A debug message";
      const consoleDebugSpy = vitest.spyOn(console, "debug").mockImplementationOnce(() => {});

      // WHEN
      logger.debug(debugMessage);

      // THEN
      expect(consoleDebugSpy).toHaveBeenCalledWith(debugMessage);
    });
  });

  describe("trace", () => {
    test("should log the trace message to the console", () => {
      // GIVEN
      const traceMessage = "A trace message";
      const consoleTraceSpy = vitest.spyOn(console, "trace").mockImplementationOnce(() => {});

      // WHEN
      logger.trace(traceMessage);

      // THEN
      expect(consoleTraceSpy).toHaveBeenCalledWith(traceMessage);
    });
  });
});
