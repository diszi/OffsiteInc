package hu.d2.offsiteinc.ui.view.verifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hu.d2.offsiteinc.app.singleton.SettingsSingleton;
import hu.d2.offsiteinc.ui.model.Version;
import hu.d2.offsiteinc.ui.view.component.UpdateAppDialog;
import hu.d2.offsiteinc.ui.view.ticketlist.TicketListActivity;
import hu.d2.offsiteinc.util.EnvironmentTool;
import hu.d2.offsiteinc.util.UIConstans;

/**
 * This activity reviews application updates.
 */
public class UpdateActivity extends AppCompatActivity implements UpdateApp {

    private VerificationPresenter presenter;
    private String newAppVersion;
    private boolean updateAvailable;
    Date today = new Date();

    public void setUpdateAvailable(boolean update) {
        this.updateAvailable = update;
    }

    public boolean getUpdateAvailable() {
        return updateAvailable;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new VerificationPresenterImpl();
        presenter.setUpdateView(this);
        presenter.getUpdateVersion(EnvironmentTool.getAppPackageName(),this);

        setUpdateAvailable(false);
    }



    /**
     * @param updateVersionObj contains details of the official version of application (name, version number, etc)
     * @throws ParseException - check update details after login
     *                        - if no update available  -  loading ticket list
     *                        - if update is available - compare update version number with application version number
     *                        - update version nr > app version nr  => check difference between 2 date (actual date and date from property file)
     *                        - if the date in property file was older than 1 day  => will appear question dialog with new version
     *                        - if the difference between 2 date less than 1 day  => loading ticket list
     *                        - update version nr <= app version nr => loading ticket list
     */
    public void verificUpdateInformations(Version updateVersionObj) throws ParseException {
       // getUpdateAvailable();
        newAppVersion = updateVersionObj.getVersionNumber();

        if (updateVersionObj.getAppName() == null) {
            Intent intent = new Intent(this, TicketListActivity.class);
            startActivity(intent);
        } else {
            // if update is available - compare update version number with app version number
            if (compareVersionNames(EnvironmentTool.getVersionApp(), newAppVersion) == -1) {

                setUpdateAvailable(true);
                long difference = getDifferenceBetween2Date(SettingsSingleton.getInstance().getDate(), EnvironmentTool.convertDate(today, UIConstans.DATE_PATTERN_HU));

                //difference between 2 date more than 1 day OR in property file getDate() method return null ->  appear update dialog
                if (difference > 1 || difference == -1) {

                    android.app.FragmentManager fm = getFragmentManager();
                    UpdateAppDialog updateDialog = UpdateAppDialog.newInstance("UpdateActivity", newAppVersion);
                    updateDialog.show(fm, "update");

                } else {
                    //  loading ticket list
                    Intent intent = new Intent(this, TicketListActivity.class);
                    startActivity(intent);
                }

            } else {
                // update version nr <= app version nr -> loading ticket list
                Intent intent = new Intent(this, TicketListActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * @param pressedDate - date when user press NO button - user don't want to update
     * @param today       - actual date
     * @return - number (long type) = difference between @param pressDate and @param today
     * @throws ParseException
     */
    public long getDifferenceBetween2Date(String pressedDate, String  today) throws ParseException {
        long diffDays;
        DateFormat outFormat  = new SimpleDateFormat(UIConstans.DATE_PATTERN_HU);
        Date before, today2;

        if (pressedDate == null){
            diffDays = -1;
        }else{
            before = outFormat.parse(pressedDate);
            today2 = outFormat.parse(today);
            long diff = today2.getTime() - before.getTime();
            diffDays = (int) (diff / (24 * 60 * 60 *1000));
        }
        return diffDays;
    }

    /**
     * @param actualVersionName - the version of the running application
     * @param newVersionName    - new official application version number
     * @return an integer
     *          = 1 - if @param actualVersionName >  @param newVersionName
     *          = -1 - if @param actualVersionName < @param newVersionName
     *
     * The parameters are split into string array and compares the elements
     */
    public int compareVersionNames(String actualVersionName, String newVersionName) {
        int result = 0;

        String[] actualNr = actualVersionName.split("\\.");
        String[] newNr = newVersionName.split("\\.");

        int maxIndex = Math.min(actualNr.length, newNr.length);

        for (int i = 0; i < maxIndex; i++) {
            int actualVersionPart = Integer.valueOf(actualNr[i]);
            int newVersionPart = Integer.valueOf(newNr[i]);

            if (actualVersionPart < newVersionPart) {
                result = -1;
                break;
            } else if (actualVersionPart > newVersionPart) {
                result = 1;
                break;
            }
        }

        if (result == 0 && actualNr.length != newNr.length) {
            result = (actualNr.length > newNr.length) ? 1 : -1;
        }
        return result;
    }

    /**
     * Call a method SOAP
     */
    @Override
    public void downloadApp() {
        presenter.getNewApp(EnvironmentTool.getAppPackageName(), newAppVersion, this);
    }

    /**
     * @param versionAttachment - contains details of the new version
     * @param activity          - where the new application is downloaded
     */
    public void downloadNewAppAttachment(Version versionAttachment, Activity activity) {
        new DownloadUpdate(activity).execute(versionAttachment.getAppName(), versionAttachment.getNewAppDetails().get(0).getDocumentData());

    }
}


