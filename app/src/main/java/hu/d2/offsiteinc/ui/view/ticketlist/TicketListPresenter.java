package hu.d2.offsiteinc.ui.view.ticketlist;

import java.util.List;

import hu.d2.offsiteinc.ui.model.IncidentEntity;

import hu.d2.offsiteinc.ui.model.OwnerHolder;
import io.reactivex.Observable;



public interface TicketListPresenter {

     void setView(TicketList view);

     void onDestroy();

     void getTicketList();

     void getOwners(String owner);

     void getOwnerGroups(List<String> ownerGroupsList);

     Observable<List<IncidentEntity>> createObservable();

     Observable<OwnerHolder> createGetOwnerObservable(String owner);

     Observable<OwnerHolder> createGetOwnerGroupObservable(List<String> ownerGroupsList);
}
