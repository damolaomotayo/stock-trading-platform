package stock_trading.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRecord {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
}
