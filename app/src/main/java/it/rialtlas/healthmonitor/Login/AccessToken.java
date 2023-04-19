package it.rialtlas.healthmonitor.Login;

import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private Integer expiresIn;
    @SerializedName("refresh_expires_in")
    private Integer refreshExpiresIn;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("id_token")
    private String idToken;
    @SerializedName("not-before-policy")
    private Integer notBeforePolicy;
    @SerializedName("session_state")
    private String sessionState;
    @SerializedName("scope")
    private String scope;

    public AccessToken(String accessToken,Integer expiresIn,String idToken){
        this.accessToken =accessToken;
        this.expiresIn = expiresIn;
        this.idToken = idToken;
    }


}
