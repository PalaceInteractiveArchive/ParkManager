package us.mcmagic.parkmanager.autograph;

import java.util.UUID;

/**
 * Created by Marc on 8/16/15
 */
public class Signature {
    private final int id;
    private final UUID signer;
    private final String message;

    public Signature(int id, UUID signer, String message) {
        this.id = id;
        this.signer = signer;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public UUID getSigner() {
        return signer;
    }

    public String getMessage() {
        return message;
    }
}