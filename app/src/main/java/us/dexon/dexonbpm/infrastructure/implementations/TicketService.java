package us.dexon.dexonbpm.infrastructure.implementations;

import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class TicketService implements ITicketService {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final TicketService fINSTANCE =  new TicketService();

    private static String LOGIN_URL = "api/Logon/GetLoggedUser";
    //endregion

    //region Properties
    public static TicketService getInstance() {
        return fINSTANCE;
    }
    //endregion

    //region Constructor
    /**
     * Private constructor prevents construction outside this class.
     */
    private TicketService() {
    }
    //endregion

    //region Public Methods
    //endregion
}
