package main.service;

import java.security.Principal;
import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.repository.GlobalSettingRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final GlobalSettingRepository globalSettingRepository;

  @Autowired
  public SettingsService(UserRepository userRepository,
      AuthenticationManager authenticationManager,
      GlobalSettingRepository globalSettingRepository) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.globalSettingRepository = globalSettingRepository;
  }

  public SettingsResponse getGlobalSettings(){
    SettingsResponse response = new SettingsResponse();
    for (GlobalSetting setting : globalSettingRepository.findAll()){
      if (setting.getCode().equals("MULTIUSER_MODE")){
        response.setMultiuserMode(setting.getValue().equals("YES"));
      }
      if (setting.getCode().equals("POST_PREMODERATION")){
        response.setPostPremoderation(setting.getValue().equals("YES"));
      }
      if (setting.getCode().equals("STATISTICS_IS_PUBLIC")){
        response.setStatisticsIsPublic(setting.getValue().equals("YES"));
      }
    }
    return response;
  }

  public void setGlobalSettings(boolean multiuserMode, boolean postPremoderation, boolean statisticsIsPublic){

    for (GlobalSetting setting : globalSettingRepository.findAll()){
      System.out.println(setting.getCode() + " " + setting.getValue() + "\n");
      if (setting.getCode().equals("MULTIUSER_MODE")){
        if (multiuserMode){
          setting.setValue("YES");
        }
        else{
          setting.setValue("NO");
        }
        continue;
      }
      if (setting.getCode().equals("POST_PREMODERATION")){
        if (postPremoderation){
          setting.setValue("YES");
        }
        else{
          setting.setValue("NO");
        }
        continue;
      }
      if (setting.getCode().equals("STATISTICS_IS_PUBLIC")){
        if (statisticsIsPublic){
          setting.setValue("YES");
        }
        else{
          setting.setValue("NO");
        }
      }
      globalSettingRepository.save(setting);
    }

  }
}
