package com.example.jwtangspr;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api.prontoitlabs.com/api/v1")
public class UserController {
	
	@Autowired
    private UserDAO personDao;

    private final Map<String, List<String>> userDb = new HashMap<>();

    public UserController() {
        userDb.put("tom", Arrays.asList("user"));
        userDb.put("sally", Arrays.asList("user", "admin"));
    }

    @RequestMapping(value = "user/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody final User login)
        throws ServletException {
    	
    	int a = personDao.checkuser(login);
        if (a==0) {
            throw new ServletException("Invalid login");
        }
        return new LoginResponse(Jwts.builder().setSubject(login.getUserName())
            .claim("roles", userDb.get(login.getUserName())).setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, "secretkey").compact());
    }

    @SuppressWarnings("unused")
    private static class UserLogin {
        public String name;
        public String password;
    }

    @SuppressWarnings("unused")
    private static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }
    
    
    
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public Map create(@ModelAttribute User usr, @RequestHeader HttpHeaders headers) {
    	Map m = new HashMap<>();
    	Map data = new HashMap<>();
    	m.put("errorMessage", null);
        try {           
            personDao.registerUser(usr);
            m.put("status", true);
            
            LoginResponse ll= new LoginResponse(Jwts.builder().setSubject(usr.getUserName())
                    .claim("roles", userDb.get(usr.getUserName())).setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS256, "secretkey").compact());
            data.put("token", ll.token);
            data.put("user", usr);
            m.put("data", data);
        } catch (Exception ex) {
        	m.put("errorMessage", ex.getMessage());
        }
        return m;
    }
    @RequestMapping(value = "/allusers")
    @ResponseBody
    public List getAllUsers(@RequestParam String pn,@RequestParam String nor) {
        try {
            return personDao.getAllUsers(pn, nor);
        } catch (Exception ex) {
            return null;
        }
    }
}
