package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.github.cage.token.RandomTokenGenerator;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import main.api.response.CaptchaResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

  @Autowired
  private CaptchaRepository captchaRepository;

  public CaptchaResponse getAndAddCaptcha(){
    CaptchaResponse response = new CaptchaResponse();
    CaptchaCode captchaCode = new CaptchaCode();

    Cage cage = new GCage();
    String code = new RandomTokenGenerator(new Random(), 5, 1).next();

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      cage.draw(code, os);
    } catch (Exception ex){
      ex.printStackTrace();
    }

    byte[] bytes = os.toByteArray();
    try{
      os.close();
    } catch (Exception ex){
      ex.printStackTrace();
    }

    String image = "data:image/png;base64, ";
    image = image + Base64.getEncoder().encodeToString(bytes);

    String secretCode = UUID.randomUUID().toString().substring(0,32);
    captchaCode.setCode(code);
    captchaCode.setSecretCode(secretCode);
    captchaCode.setTime(new Date(System.currentTimeMillis()));
    captchaRepository.deleteOldCaptcha();
    captchaRepository.save(captchaCode);
    response.setSecret(secretCode);
    response.setImage(image);
    return response;
  }

  public boolean validateCaptcha(String code, String secret){
    return captchaRepository.findCaptcha(code, secret).size() != 0;
  }
}
