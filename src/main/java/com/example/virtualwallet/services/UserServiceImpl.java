package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

   /* @Override
    public Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable) {
        Page<Object[]> result = null; *//*userRepository.findFilteredUsers(
                userFilterOptions.getUsername().orElse(null),
                userFilterOptions.getEmail().orElse(null),
                userFilterOptions.getPhoneNumber().orElse(null),
                userFilterOptions.getAccount_type().orElse(null),
                userFilterOptions.getAccount_status().orElse(null),
                userFilterOptions.getMinNumberOfTransactions().orElse(0),
                userFilterOptions.getMaxNumberOfTransactions().orElse(Integer.MAX_VALUE),
                userFilterOptions.getOrderBy().orElse("username"), pageable);*//*
        return modelMapper.mapObjectPageToUserOutputPage(result);
    }*/

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with username: %s not found!", username)));
    }
}