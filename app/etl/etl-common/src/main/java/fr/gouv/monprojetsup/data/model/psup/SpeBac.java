package fr.gouv.monprojetsup.data.model.psup;

public record SpeBac(
        String bMfCodNat,
        String bMfLibLon,
        String iSpCod,
        String iSpLib,
        String iClCod
) {

    public void checkAllFieldsAreNonNull() {
        if (bMfCodNat == null
                || bMfLibLon == null
                || iSpCod == null
                || iSpLib == null
                || iClCod == null
        ) throw new RuntimeException("All fields must be non-null");
    }
}
