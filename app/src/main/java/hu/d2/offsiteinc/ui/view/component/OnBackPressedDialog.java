package hu.d2.offsiteinc.ui.view.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import hu.d2.offsiteinc.R;
import hu.d2.offsiteinc.ui.view.login.LoginActivity;

/**
 *  - this dialog will appear when user click on Back button in the ticket list page
 */

public class OnBackPressedDialog extends DialogFragment {

    /**
     *  Create custom Dialog object
     * @param savedInstanceState - the last saved instance state of the Fragment, or null if this is a freshly created Fragment
     * @return - a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setMessage(R.string.menuTicketList_logOut).setPositiveButton(R.string.buttonYES, new DialogInterface.OnClickListener() {

           //Log out
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.putExtra("backPressed","true");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).setNegativeButton(R.string.buttonNO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        }).create();
    }
}
