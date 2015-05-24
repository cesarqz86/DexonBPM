package us.dexon.dexonbpm.infrastructure.enums;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public enum TicketFilter {

    ColaboratedTickets(1, "Colaborando"),
    CreatedTickets(2, "Creados"),
    RelatedTickets(3, "Relacionados"),
    AssignedTickets(4, "Asignados"),
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
