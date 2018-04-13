package hu.d2.offsiteinc.ui.view.login;


public interface Login {

    void showLoading();

    void hideLoading();

    void showErrorMessage(int messageID);

    void launchListView();

    void setUserToContext(String userName);
}
