package hu.d2.offsiteinc.ui.view.ticketlist;

import java.util.List;

import hu.d2.offsiteinc.ui.model.IncidentEntity;

import hu.d2.offsiteinc.ui.model.TicketHolder;


public interface TicketList {

	void showLoading();

	void hideLoading();

	void showErrorMessage(int messageID);

	void launchDetailsView(TicketHolder entityHolder);

	String getLoggedInUser();

	void loadList(List<IncidentEntity> ticketList);

	void setSyncDate();
}
