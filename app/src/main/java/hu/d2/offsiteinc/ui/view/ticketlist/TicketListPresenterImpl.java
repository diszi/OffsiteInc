package hu.d2.offsiteinc.ui.view.ticketlist;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import hu.d2.offsiteinc.R;
import hu.d2.offsiteinc.app.singleton.HolderSingleton;
import hu.d2.offsiteinc.app.singleton.SettingsSingleton;
import hu.d2.offsiteinc.remote.GetOwnerGroupListSOAP;
import hu.d2.offsiteinc.remote.GetOwnerListSOAP;
import hu.d2.offsiteinc.remote.GetTicketListSOAP;
import hu.d2.offsiteinc.ui.model.IncidentEntity;
import hu.d2.offsiteinc.ui.model.OwnerHolder;

import hu.d2.offsiteinc.util.EntityMapper;
import hu.d2.offsiteinc.util.NetworkTool;
import hu.d2.offsiteinc.util.UIThrowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class TicketListPresenterImpl implements TicketListPresenter {

    private TicketList view;
    private Disposable disposable;
    private Disposable disposable2;
    private Disposable disposable3;
    private Observable<List<IncidentEntity>> observable;
    private Observable<OwnerHolder> observable2;
    private Observable<OwnerHolder> observable3;


    @Override
    public void setView(TicketList view) {
        this.view = view;
    }

    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            Log.d("------------------>", " Dispose observer");
            disposable.dispose();
        }
        if (disposable2 != null && !disposable2.isDisposed()) {
            Log.d("------------------>", " Dispose observer2");
            disposable2.dispose();
        }
        if (disposable3 != null && !disposable3.isDisposed()){
            disposable3.dispose();
        }
    }


    @Override
    public void getTicketList() {

        view.showLoading();
        if (observable == null) {
            Log.d("------------------>", " Observable created");
            observable = createObservable();
        }
        Log.d("------------------>", " Subscribe to Observable");
        disposable = observable
                .subscribe((ticketList) -> { // onNext Consumer
                    Log.d("------------------>", " Get Data");
                    view.loadList(ticketList);
                    view.setSyncDate();
                }, (throwable) -> { // onError Consumer
                    int errorMessageCode = R.string.error_general;
                    if (throwable instanceof UIThrowable) {
                        UIThrowable uiThrowable = (UIThrowable) throwable;
                        errorMessageCode = uiThrowable.getMessageId();
                    }
                    view.showErrorMessage(errorMessageCode);
                    view.hideLoading();
                }, () -> { // onComplate Action

                    view.hideLoading();
                    getOwners(SettingsSingleton.getInstance().getUserName());
                });
    }

    @Override
    public void getOwners(String owner) {
        //if (observable2 == null){
        Log.d("------------------>", " Observable2 created");
        observable2 = createGetOwnerObservable(owner);
        //}

        Log.d("------------------>", " Subscribe to Observable2");
        disposable2 = observable2
                .subscribe((ownerData) -> { // onNext Consumer
                    Log.d("------------------>", " Get Data Owners  ");

                    getOwnerGroups(ownerData.getOwnerGroupList());

                }, (throwable) -> { // onError Consumer
//                    int errorMessageCode = R.string.error_general;
//                    if (throwable instanceof UIThrowable){
//                        UIThrowable uiThrowable = (UIThrowable) throwable;
//                        errorMessageCode = uiThrowable.getMessageId();
//                    }
                    Log.e("------------->", "Dont get data", throwable);
//                    view.showErrorMessage(errorMessageCode);
//                    view.hideLoading();
                }, () -> { // onComplate Action

//                    view.hideLoading();
                });


    }

    @Override
    public void getOwnerGroups(List<String> ownerGroupsList) {
        // if (observable3 == null){
        Log.d("------------------>", " Observable3 created");
        observable3 = createGetOwnerGroupObservable(ownerGroupsList);
        //}

        Log.d("------------------>", " Subscribe to Observable3");
        disposable3 = observable3
                .subscribe((ownerData) -> { // onNext Consumer
                    Log.d("------------------>", " Get Data Owner groups");
                    HolderSingleton.getInstance().setOwners(ownerData.getOwnerList());
                    HolderSingleton.getInstance().setOwnerGroups(ownerData.getOwnerGroupList());

                }, (throwable) -> { // onError Consumer
//                    int errorMessageCode = R.string.error_general;
//                    if (throwable instanceof UIThrowable){
//                        UIThrowable uiThrowable = (UIThrowable) throwable;
//                        errorMessageCode = uiThrowable.getMessageId();
//                    }
                    Log.e("------------->", "Dont get data", throwable);
//                    view.showErrorMessage(errorMessageCode);
//                    view.hideLoading();
                }, () -> { // onComplate Action

//                    view.hideLoading();
                });
    }


    @Override
    public Observable<List<IncidentEntity>> createObservable() {
        Observable<List<IncidentEntity>> result = Observable.create(emitter -> {
            try {
                Log.d("------------------>", " Start Remote SOAP Call ");
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    connection = NetworkTool.createSOAPConnection(NetworkTool.SOAP_INC_URL_GET, GetTicketListSOAP.SOAP_ACTION, String.format(GetTicketListSOAP.getSoapPayload(SettingsSingleton.getInstance().getSelectedStatus(), SettingsSingleton.getInstance().getMaxListValue(), SettingsSingleton.getInstance().getTicketSynchronization()), SettingsSingleton.getInstance().getUserName()));

                    int responseCode = connection.getResponseCode();
                    System.out.println(" RESPONSECODE = "+responseCode);
                    if (responseCode == 200) {
                        inputStream = connection.getInputStream();
                        List<IncidentEntity> ticketList = EntityMapper.transformTicketList(inputStream);
                        emitter.onNext(ticketList);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new UIThrowable(R.string.error_network));
                    }

                } finally {
                    if (connection != null) {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        connection.disconnect();
                    }
                }
            } catch (Exception ex) {
                Log.e("", "---------->", ex);
                emitter.onError(new UIThrowable(R.string.error_unknown));
            }

        });

        return result.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<OwnerHolder> createGetOwnerObservable(String owner) {
        Observable<OwnerHolder> result = Observable.create(emitter -> {
            try {
                Log.d("------------------>", " Start Remote SOAP Call ");
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    connection = NetworkTool.createSOAPGETConnection(NetworkTool.SOAP_OWNER_URL, GetOwnerListSOAP.SOAP_ACTION, String.format(GetOwnerListSOAP.getSoapPayload(owner), SettingsSingleton.getInstance().getUserName()));

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        inputStream = connection.getInputStream();
                        emitter.onNext(EntityMapper.transformOwnerDataList(inputStream));
                        emitter.onComplete();
                    } else {
                        emitter.onError(new UIThrowable(R.string.error_network));
                    }

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                        if (inputStream != null) {
                            inputStream.close();
                        }

                    }
                }
            } catch (Exception ex) {
                Log.e("", "---------->", ex);
                emitter.onError(new UIThrowable(R.string.error_unknown));
            }

        });

        return result.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<OwnerHolder> createGetOwnerGroupObservable(List<String> ownerGroupsList) {
        Observable<OwnerHolder> result = Observable.create(emitter -> {
            try {
                Log.d("------------------>", " Start Remote SOAP Call ");
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    connection = NetworkTool.createSOAPGETConnection(NetworkTool.SOAP_OWNER_URL, GetOwnerListSOAP.SOAP_ACTION, String.format(GetOwnerGroupListSOAP.getSoapPayload(ownerGroupsList), SettingsSingleton.getInstance().getUserName()));

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        inputStream = connection.getInputStream();
                        emitter.onNext(EntityMapper.transformOwnerDataList(inputStream));
                        emitter.onComplete();
                    } else {
                        emitter.onError(new UIThrowable(R.string.error_network));
                    }

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                        if (inputStream != null) {
                            inputStream.close();
                        }

                    }
                }
            } catch (Exception ex) {
                Log.e("", "---------->", ex);
                emitter.onError(new UIThrowable(R.string.error_unknown));
            }

        });

        return result.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }
}


