package tw.com.kyle.controller.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.kyle.enums.Role;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSystemUserReqDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 32, message = "長度不得超過32")
    @NotEmpty(message = "名字不得為空")
    private String name;

    @Email(message = "帳號需為email格式")
    @NotEmpty(message = "email不得為空")
    private String email;

    @Size(max = 20, message = "長度不得超過20")
    @NotEmpty(message = "電話不得為空")
    private String phoneNumber;

    private String address;
}
