package us.dexon.dexonbpm.infrastructure.enums;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public enum TicketFilter {

    ColaboratedTickets(1),
    CreatedTickets(2),
    RelatedTickets(3),
    AssignedTickets(4),
    None(5);

    private int code;

    private TicketFilter(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
