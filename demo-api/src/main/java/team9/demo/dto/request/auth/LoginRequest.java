package team9.demo.dto.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.notification.PushInfo;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String phoneNumber;
    private String countryCode;
    private String password;
    private String deviceId;
    private String provider;
    private String appToken;

    public LocalPhoneNumber toLocalPhoneNumber() {
        return LocalPhoneNumber.of(phoneNumber, countryCode);
    }

    public String toPassword() {
        return password;
    }

    public PushInfo.Device toDevice() {
        return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
    }
    public LoginRequest(String phoneNumber, String countryCode, String password,
                        String deviceId, String provider, String appToken) {
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.password = password;
        this.deviceId = deviceId;
        this.provider = provider;
        this.appToken = appToken;
    }


    public String toAppToken() {
        return appToken;
    }
}

