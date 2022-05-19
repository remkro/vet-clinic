package pl.kurs.vetclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.kurs.vetclinic.model.entity.User;
import pl.kurs.vetclinic.repository.UserRepository;

//oczywiscie, ze probowalem tutaj dac adnotacje Service/Component i wstrzykiwac UserRepository przez konstruktor, ale nie dziala
//ten sam case co przy FileListnerze, nie wiem dziwne, ale serio dziala tylko przez Autowired i zdefiniowanie Beana w klasie konfiguracji
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));
        return new MyUserDetails(user);
    }

}
