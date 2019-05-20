package network.palace.parkmanager.autograph;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Signature {
    private final String signer;
    private final String message;
    private final long time;
}
