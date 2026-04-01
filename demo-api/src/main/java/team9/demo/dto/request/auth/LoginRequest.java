package team9.demo.dto.request.auth;

import team9.demo.model.notification.PushInfo;

public record LoginRequest(
        String email,
        String password,
        String deviceId,
        String provider,
        String appToken
) {
    public String toEmail() { return email; }

    public String toPassword() { return password; }

    public PushInfo.Device toDevice() {
        return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
    }

    public String toAppToken() { return appToken; }
}
