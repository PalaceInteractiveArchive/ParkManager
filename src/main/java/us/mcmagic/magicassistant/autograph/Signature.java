package us.mcmagic.magicassistant.autograph;

import java.util.UUID;

/**
 * Created by Marc on 8/16/15
 */
public class Signature {
    private final UUID signer;
    private final String message;

    public Signature(UUID signer, String message) {
        this.signer = signer;
        this.message = message;
    }

    public UUID getSigner() {
        return signer;
    }

    public String getMessage() {
        return message;
    }
}