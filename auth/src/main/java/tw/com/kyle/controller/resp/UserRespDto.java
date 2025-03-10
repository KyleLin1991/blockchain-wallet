package tw.com.kyle.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Timestamp crDateTime;
}
