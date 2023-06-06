package xyz.ebidding.account.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.ebidding.account.dto.AccountDto;
import xyz.ebidding.account.props.AppProps;
import xyz.ebidding.account.repo.AccountRepo;
import xyz.ebidding.common.crypto.Hash;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AppProps appProps;



    public Optional<AccountDto> verifyAccount(String name, String password) {
        log.info("verify account: {}", name);
        return accountRepo.findByName(name)
                .filter(acc -> {
                    try {
                        log.info("filter account: {}", acc.getName());
                        return Objects.equals(acc.getPasswordHash(), Hash.encode(appProps.getHashKey(), password));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(acc -> modelMapper.map(acc, AccountDto.class));
    }
}
