package com.example.sbbTest.user;

import com.example.sbbTest.CommonUtil;
import com.example.sbbTest.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JavaMailSender mailSender;

    private static final String ADMIN_ADDRESS = "kimjaemin722@gmail.com";

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()){
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void modifyPassword(SiteUser siteUser,String password){
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    public boolean checkPassword(SiteUser siteUser, String password){
        return passwordEncoder.matches(password,siteUser.getPassword());
    }

    public SiteUser getUserByEmail(String email){
        Optional<SiteUser> os = this.userRepository.findByEmail(email);
        if (os.isPresent()){
            return os.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void delete(String email) {
        Optional<SiteUser> os = this.userRepository.findByEmail(email);
        os.ifPresent(this.userRepository::delete);
    }

    @Async
    public void sendEmail(String recipient, String subject, String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setFrom(ADMIN_ADDRESS);
            message.setSubject(subject);
            message.setText(body);
            this.mailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
