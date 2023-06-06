package xyz.ebidding.account.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import xyz.ebidding.common.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LoginResponse extends BaseResponse {
    private AccountDto accountDto;
    private String token;
}
