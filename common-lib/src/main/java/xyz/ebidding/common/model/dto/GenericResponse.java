package xyz.ebidding.common.model.dto;

import lombok.*;
import xyz.ebidding.common.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GenericResponse<T>  extends BaseResponse {
    private T data;
}