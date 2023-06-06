package xyz.ebidding.account.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ebidding.account.dto.LoginAccountDto;
import xyz.ebidding.account.dto.LoginRequest;
import xyz.ebidding.account.props.AppProps;
import xyz.ebidding.account.service.AccountService;
import xyz.ebidding.common.api.BaseResponse;
import xyz.ebidding.common.api.ResultCode;
import xyz.ebidding.common.crypto.Sign;
import xyz.ebidding.common.model.dto.GenericResponse;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/account")
@Validated
@Slf4j
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AppProps appProps;

    @PostMapping(value = "/login")
    public GenericResponse<LoginAccountDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("login request: {}", loginRequest.getUsername());
        Optional<LoginAccountDto> optionalAccount = accountService.verifyAccount(loginRequest.getUsername(),
                loginRequest.getPassword()).map(accountDto -> {
            LoginAccountDto loginAccountDto =
                    modelMapper.map(accountDto, LoginAccountDto.class);
            loginAccountDto.setToken(Sign.generateToken(
                    loginRequest.getUsername(), appProps.getSigningSecret(),
                    loginAccountDto.getRole(), 1000 * 3600 * 24));

            log.info("verified account: {}", loginAccountDto);
            return loginAccountDto;
        });

        GenericResponse<LoginAccountDto> failLogin = new GenericResponse<>();
        failLogin.setCode(ResultCode.UN_AUTHORIZED);

        return optionalAccount.map(GenericResponse::new).orElse(failLogin);
    }


    @PostMapping(value = "/logout")
    public BaseResponse logout() {
        return new BaseResponse();
    }
}
