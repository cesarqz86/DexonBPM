package us.dexon.dexonbpm.infrastructure.enums;

/**
 * Created by Cesar Quiroz on 8/17/15.
 */
public enum RenderControlType {

    DXControlsInt(0),
    DXControlsFloat(1),
    DXControlsLine(2),
    DXControlsPhone(3),
    DXControlsCellphone(4),
    DXControlsEmail(5),
    DXControlsMultiline(6),
    DXControlsPassword(7),
    DXControlsCurrency(8),
    DXControlsCharacter(9),
    DXControlsImage(10),
    DXControlsColor(11),
    DXControlsDate(12),
    DXControlsCheckbox(13),
    DXControlsGrid(14),
    DXControlsTree(15),
    DXControlsContainer(16),
    DXControlsTable(17),
    DXControlsNone(18),
    DXControlsLabel(19),
    DXControlsGridWithOptions(100);

    private int code;

    private RenderControlType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
