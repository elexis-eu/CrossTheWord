package it.uniroma1.lcl.crucy.login.social.utils;

public interface LoginSub
{
    void addObserver(LoginObs obs);
    void notifyObs(boolean login);
}
