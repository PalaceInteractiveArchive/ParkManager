package network.palace.parkmanager.autograph;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Marc on 8/16/15
 */
@AllArgsConstructor
public class Signature {
    @Getter private final int id;
    @Getter private final String signer;
    @Getter private final String message;
}
