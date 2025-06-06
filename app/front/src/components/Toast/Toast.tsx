import {
  actionsToastStore,
  descriptionToastStore,
  estOuvertToastStore,
  titreToastStore,
  typeToastStore,
} from "./useToastStore/useToastStore";
import { router } from "@/configuration/lib/tanstack-router";
import { Alert } from "@codegouvfr/react-dsfr/Alert";
import * as ToastRadix from "@radix-ui/react-toast";
import { useEffect } from "react";

const Toast = () => {
  const estOuvert = estOuvertToastStore();
  const titre = titreToastStore();
  const description = descriptionToastStore();
  const type = typeToastStore();
  const { fermerToast } = actionsToastStore();

  useEffect(() => {
    const unsub = router.subscribe("onLoad", ({ pathChanged }) => {
      if (pathChanged) {
        fermerToast();
      }
    });

    return () => unsub();
  }, [fermerToast]);

  return (
    <ToastRadix.Provider swipeDirection="right">
      <ToastRadix.Root
        aria-live="assertive"
        className="data-[state=closed]:animate-hide data-[state=open]:animate-slideIn data-[swipe=end]:animate-swipeOut p-0 marker:content-none data-[swipe=cancel]:translate-x-0 data-[swipe=move]:translate-x-[var(--radix-toast-swipe-move-x)] data-[swipe=cancel]:transition-[transform_200ms_ease-out]"
        onOpenChange={(ouvert) => {
          if (!ouvert) fermerToast();
        }}
        open={estOuvert}
      >
        <Alert
          closable
          description={description ?? ""}
          onClose={fermerToast}
          severity={type ?? "info"}
          title={titre ?? ""}
        />
      </ToastRadix.Root>
      <ToastRadix.Viewport className="fixed bottom-5 right-5 z-[1000] m-0 w-[400px] list-none bg-white p-0" />
    </ToastRadix.Provider>
  );
};

export default Toast;
