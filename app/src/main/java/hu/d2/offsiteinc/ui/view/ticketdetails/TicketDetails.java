package hu.d2.offsiteinc.ui.view.ticketdetails;


import hu.d2.offsiteinc.ui.model.IncidentEntity;

public interface TicketDetails {

    void showLoading();

    void hideLoading();

    void showErrorMessage(int messageID);

    void showSuccessMessage();

    void loadTicketDetails(IncidentEntity entity);

    String getLoggedInUser();

    void updateStatusRemote(String status);

    void updateTaskStatusRemote(String status,int pos,String wonum,String siteid);

    void updateStatus(String newStatus);

    void updateTaskStatus(String newStatus,int pos);

    void updateOwnerGroup(String newOwnerGroup);

    void updateOwnerGroupRemote(String ownerGroup);

    void updateOwner(String newOwner);

    void updateOwnerRemote(String owner);

    void updatePriorityRemote(String priority);

    void updatePriority(String newPriority);

    void addWorkLogRemote(String shortDesc, String longDesc);

    void addFile(String fileName , String pureFileName,String encode, String urlname );

    void setSyncDate();


}
