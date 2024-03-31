import { getFromSpringService } from "./api/call_service";

export function getStats(bac, key, onSuccess = null) {
  getFromSpringService(
    "stats",
    {
      bac: bac,
      key: key,
    },
    onSuccess,
    true
  );
}
