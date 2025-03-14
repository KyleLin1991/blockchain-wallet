package tw.com.kyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Kyle
 * @since 2025/3/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaultReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String walletAddress;
    private String password;
    private String privateKey;
    private String chainId;
    private String walletDirPath;
    private Timestamp crDatetime;
}
