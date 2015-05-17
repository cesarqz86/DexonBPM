package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 5/16/15.
 */
public class DexonDatabaseHelper extends SQLiteOpenHelper {

    //region Attributes
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DexonData.db";
    private String UserTable;
    private String TicketTable;

    public static final String USER_TABLE = "LoggedUser";
    public static final String TICKET_TABLE = "TicketInfo";
    //endregion

    //region Properties
    //endregion

    //region Constructor
    public DexonDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.UserTable = context.getString(R.string.sql_table_user);
        this.TicketTable = context.getString(R.string.sql_table_ticket);
    }
    //endregion

    //region Public Methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder databaseCreate = new StringBuilder();
        databaseCreate.append(this.UserTable);
        databaseCreate.append(this.TicketTable);

        db.execSQL(databaseCreate.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //endregion

    //region Private Methods
    //endregion
}
