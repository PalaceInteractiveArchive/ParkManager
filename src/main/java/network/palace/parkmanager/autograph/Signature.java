package network.palace.parkmanager.autograph;

import lombok.Getter;

@Getter
public class Signature {
    private final String signer;
    private final String message;
    private final long time;

    public Signature(String signer, String message, long time) {
        this.signer = signer.replaceAll("_", " ").replaceAll(" {2}", " ").trim();
        this.message = message;
        this.time = time;
    }
}
