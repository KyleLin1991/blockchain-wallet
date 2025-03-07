package tw.com.kyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String pid;
    private String description;
}
