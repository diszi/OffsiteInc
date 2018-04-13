package hu.d2.offsiteinc.ui.view.verifications;

import java.text.ParseException;

import hu.d2.offsiteinc.ui.model.Version;

/**
 * This interface implemented in TicketListActivity and UpdateActivity.
 */
public interface UpdateApp {

    void downloadApp();

    void verificUpdateInformations(Version updateVersionObj) throws ParseException;

}
