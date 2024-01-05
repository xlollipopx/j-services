package com.authservice.service;

import com.authservice.enumeration.RoleEnum;
import com.authservice.model.Person;
import com.authservice.repository.PersonRepository;
import com.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleResult;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService implements UserDetailsService {
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public Optional<Person> getPersonByUserName(String username) {
        return personRepository.findByUsername(username);
    }

    public void savePerson(String username, String password) {
        Person person = new Person();
        person.setUsername(username);
        person.setPasswordHash(DigestUtils.sha256Hex(password));
        person.setRoles(List.of(roleRepository.findByValue(RoleEnum.USER.getAuthority()).get()));
        personRepository.save(person);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person
                = getPersonByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(person.getUsername(),
                person.getPasswordHash(),
                person.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getValue()))
                        .collect(Collectors.toList()));
    }

}
