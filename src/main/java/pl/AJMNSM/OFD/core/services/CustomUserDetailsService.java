package pl.AJMNSM.OFD.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.AJMNSM.OFD.core.users.CustomUserDetails;
import pl.AJMNSM.OFD.core.users.User;
import pl.AJMNSM.OFD.core.users.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("Nie znaleziono użytkownika");
        }
        return new CustomUserDetails(user);
    }
}
