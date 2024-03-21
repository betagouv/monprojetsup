/* eslint-disable no-console */
import { type ILogger } from "./logger.interface";

export class Logger implements ILogger {
  public error(données: unknown) {
    console.error(données);
  }

  public warn(données: unknown) {
    console.warn(données);
  }

  public info(données: unknown) {
    console.info(données);
  }

  public debug(données: unknown) {
    console.debug(données);
  }

  public trace(données: unknown) {
    console.trace(données);
  }
}
