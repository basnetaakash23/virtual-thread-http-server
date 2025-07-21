package record;

import java.time.Instant;

public record AuthInfo(String token, Instant issuedAt) {

}
