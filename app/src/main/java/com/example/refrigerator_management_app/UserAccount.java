package com.example.refrigerator_management_app;

public class UserAccount {

    private String idToken; // firebase Uid (한 사용자의 고유 정보)
    private String userId; // 아이디
    private String userEmail; // 이메일 아이디
    private String userPw; // 비밀번호

    // firebase에서는 기본 생성자를 만들지 않으면(빈 생성자여도) 데이터 read시 오류가 날 수 있음.
    public UserAccount() {

    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setUserId(String userId) { this.userId = userId; }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPw() {
        return userPw;
    }

}
