package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.repository.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

  @Autowired
  private GlobalSettingRepository globalSettingRepository;

  public SettingsResponse getGlobalSettings(){
    SettingsResponse response = new SettingsResponse();
    for (GlobalSetting setting : globalSettingRepository.findAll()){
      if (setting.getCode().equals("MULTIUSER_MODE")){
        response.setMultiuserMode(setting.getValue().equals("NO"));
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
}
