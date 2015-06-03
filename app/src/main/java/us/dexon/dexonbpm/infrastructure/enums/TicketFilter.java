package us.dexon.dexonbpm.infrastructure.enums;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public enum TicketFilter {

    ColaboratedTickets(0, "Colaborando"),
    CreatedTickets(1, "Creados"),
    RelatedTickets(2, "Relacionados"),
    AssignedTickets(3, "Asignados"),
    WorkflowTickets (4, "Flujos"),
    None(5, "Ninguno");

    private int code;
    private String title;

    private TicketFilter(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean Compare(int enumCode) {
        return this.code == enumCode;
    }

    public boolean Compare(String titleName) {
        return this.title.equalsIgnoreCase(titleName);
    }

    public static TicketFilter GetValue(int enumCode) {
        TicketFilter finalResponse = TicketFilter.AssignedTickets;
        TicketFilter[] ticketFilters = TicketFilter.values();
        for (int i = 0; i < ticketFilters.length; i++) {
            if (ticketFilters[i].Compare(enumCode)) {
                finalResponse = ticketFilters[i];
                break;
            }
        }
        return finalResponse;
    }

    public static TicketFilter GetValue(String titleName) {
        TicketFilter finalResponse = TicketFilter.AssignedTickets;
        TicketFilter[] ticketFilters = TicketFilter.values();
        for (int i = 0; i < ticketFilters.length; i++) {
            if (ticketFilters[i].Compare(titleName)) {
                finalResponse = ticketFilters[i];
                break;
            }
        }
        return finalResponse;
    }
}
