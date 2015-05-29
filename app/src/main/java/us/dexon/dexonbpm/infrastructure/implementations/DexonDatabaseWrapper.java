package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;

/**
 * Created by Cesar Quiroz on 5/16/15.
 */
public class DexonDatabaseWrapper implements IDexonDatabaseWrapper {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final DexonDatabaseWrapper fINSTANCE = new DexonDatabaseWrapper();
    private static Context context;
    private static DexonDatabaseHelper dexonDatabase;


    //endregion

    //region Properties
    public static DexonDatabaseWrapper getInstance() {
        return fINSTANCE;
    }

    public void setContext(Context contextData) {
        context = contextData;
        dexonDatabase = new DexonDatabaseHelper(context);
    }
    //endregion

    //region Constructor

    /**
     * Private constructor prevents construction outside this class.
     */
    private DexonDatabaseWrapper() {
    }
    //endregion

    //region Public Methods
    public void saveLoggedUser(LoginResponseDto loggedUser) {
        SQLiteDatabase database = dexonDatabase.getWritableDatabase();

        ContentValues loggedUserValues = new ContentValues();
        loggedUserValues.put("user_id", loggedUser.getUserID());
        loggedUserValues.put("person_name", loggedUser.getPersonName());
        loggedUserValues.put("user", loggedUser.getUserName());
        loggedUserValues.put("logged_on", loggedUser.isLogged());
        loggedUserValues.put("person_id", loggedUser.getPersonID());
        loggedUserValues.put("tech_id", loggedUser.getTechID());
        loggedUserValues.put("profile_id", loggedUser.getProfileID());
        loggedUserValues.put("preferred_lang_ID", loggedUser.getPreferLanguageID());
        loggedUserValues.put("last_error_code", loggedUser.getLastErrorCode());
        loggedUserValues.put("last_error_str", loggedUser.getLastErrorMessage());
        loggedUserValues.put("token", loggedUser.getSessionToken());
        loggedUserValues.put("sessionId", loggedUser.getSessionID());
        loggedUserValues.put("uniqueCode", loggedUser.getUniqueCode());

        LoginResponseDto isRecordSaved = this.getLoggedUser();

        if (isRecordSaved == null) {
            database.insert(dexonDatabase.USER_TABLE, null, loggedUserValues);
        } else {
            database.update(dexonDatabase.USER_TABLE, loggedUserValues, "user_id == " + isRecordSaved.getUserID(), null);
        }
        //dexonDatabase.close();
    }

    public LoginResponseDto getLoggedUser() {
        LoginResponseDto finalResult = null;

        SQLiteDatabase database = dexonDatabase.getReadableDatabase();
        SQLiteQueryBuilder userQuery = new SQLiteQueryBuilder();
        userQuery.setTables(dexonDatabase.USER_TABLE);
        Cursor cursor = userQuery.query(database, null, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            finalResult = new LoginResponseDto();
            finalResult.setUserID(cursor.getInt(cursor.getColumnIndex("user_id")));
            finalResult.setPersonName(cursor.getString(cursor.getColumnIndex("person_name")));
            finalResult.setUserName(cursor.getString(cursor.getColumnIndex("user")));
            finalResult.setIsLogged(cursor.getInt(cursor.getColumnIndex("logged_on")) > 0);
            finalResult.setPersonID(cursor.getInt(cursor.getColumnIndex("person_id")));
            finalResult.setTechID(cursor.getInt(cursor.getColumnIndex("tech_id")));
            finalResult.setProfileID(cursor.getInt(cursor.getColumnIndex("profile_id")));
            finalResult.setPreferLanguageID(cursor.getInt(cursor.getColumnIndex("preferred_lang_ID")));
            finalResult.setLastErrorCode(cursor.getInt(cursor.getColumnIndex("last_error_code")));
            finalResult.setLastErrorMessage(cursor.getString(cursor.getColumnIndex("last_error_str")));
            finalResult.setSessionToken(cursor.getString(cursor.getColumnIndex("token")));
            finalResult.setSessionID(cursor.getString(cursor.getColumnIndex("sessionId")));
            finalResult.setUniqueCode(cursor.getString(cursor.getColumnIndex("uniqueCode")));
            cursor.close();
        }

        //dexonDatabase.close();
        return finalResult;
    }

    public void deleteUserTable() {
        SQLiteDatabase database = dexonDatabase.getWritableDatabase();
        database.delete(dexonDatabase.USER_TABLE, null, null);
        //dexonDatabase.close();
    }

    public void saveTicketData(ArrayList<TicketsResponseDto> ticketDataList) {
        if (ticketDataList != null && !ticketDataList.isEmpty()) {
            SQLiteDatabase database = dexonDatabase.getWritableDatabase();

            for (TicketsResponseDto ticketData : ticketDataList) {

                TicketsResponseDto existingTicket = this.getTicketData(ticketData.getTicketID());

                for (String keysValues : ticketData.getTicketDataList().keySet()) {
                    ContentValues sqlTicketData = new ContentValues();
                    sqlTicketData.put("TicketID", ticketData.getTicketID());
                    sqlTicketData.put("ColumnKey", keysValues);
                    sqlTicketData.put("ColumnValue", String.valueOf(ticketData.getTicketDataList().get(keysValues)));

                    if (existingTicket != null && existingTicket.getTicketDataList() != null && existingTicket.getTicketDataList().containsKey(keysValues)) {
                        database.update(dexonDatabase.TICKET_TABLE, sqlTicketData, "TicketID == '" + ticketData.getTicketID() + "' AND ColumnKey == '" + keysValues + "'", null);

                    } else {
                        database.insert(dexonDatabase.TICKET_TABLE, null, sqlTicketData);
                    }
                }
            }
            //dexonDatabase.close();
        }
    }

    public void saveTicketData(String ticketID, String incidentID, HashMap<String, Object> dataToSave) {
        if (dataToSave != null && !dataToSave.isEmpty()) {
            SQLiteDatabase database = dexonDatabase.getWritableDatabase();

            for (String keyID : dataToSave.keySet()) {

                //TicketsResponseDto existingTicket = this.getTicketData(ticketID);

                ContentValues sqlTicketData = new ContentValues();
                sqlTicketData.put("TicketID", ticketID);
                sqlTicketData.put("IncidentID", incidentID);
                sqlTicketData.put("ColumnKey", keyID);
                sqlTicketData.put("ColumnValue", String.valueOf(dataToSave.get(keyID)));

                /*if (existingTicket != null && existingTicket.getTicketDataList() != null && existingTicket.getTicketDataList().containsKey(keysValues)) {
                    database.update(dexonDatabase.TICKET_TABLE, sqlTicketData, "TicketID == '" + ticketData.getTicketID() + "' AND ColumnKey == '" + keysValues + "'", null);

                } else {*/
                database.insert(dexonDatabase.TICKET_TABLE, null, sqlTicketData);
                //}

            }
            //dexonDatabase.close();
        }
    }

    public TicketsResponseDto getTicketData(String incidentID) {
        TicketsResponseDto finalResult = null;

        SQLiteDatabase database = dexonDatabase.getReadableDatabase();
        SQLiteQueryBuilder userQuery = new SQLiteQueryBuilder();
        userQuery.setTables(dexonDatabase.TICKET_TABLE);
        Cursor cursor = userQuery.query(database, null, "IncidentID == '" + incidentID + "'", null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            finalResult = new TicketsResponseDto();
            finalResult.setTicketID(cursor.getString(cursor.getColumnIndex("TicketID")));
            finalResult.setIncidentID(incidentID);
            HashMap<String, Object> ticketDataList = new HashMap<>();
            ticketDataList.put(cursor.getString(cursor.getColumnIndex("ColumnKey")), cursor.getString(cursor.getColumnIndex("ColumnValue")));

            while (cursor.moveToNext()) {
                ticketDataList.put(cursor.getString(cursor.getColumnIndex("ColumnKey")), cursor.getString(cursor.getColumnIndex("ColumnValue")));
            }
            finalResult.setTicketDataList(ticketDataList);
            cursor.close();
        }
        //dexonDatabase.close();
        return finalResult;
    }

    public ArrayList<TicketsResponseDto> getTicketData(String filterWord, TicketFilter ticketFilter) {
        ArrayList<TicketsResponseDto> finalResult = null;

        SQLiteDatabase database = dexonDatabase.getReadableDatabase();
        SQLiteQueryBuilder userQuery = new SQLiteQueryBuilder();
        userQuery.setTables(dexonDatabase.TICKET_TABLE);
        filterWord = filterWord == null ? "" : filterWord;
        userQuery.appendWhere("ColumnValue LIKE '%" + filterWord + "%'");
        Cursor cursor = userQuery.query(
                database,
                null,
                null,
                null,
                "IncidentID",
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int arraySize = cursor.getCount();
            finalResult = new ArrayList<>(arraySize);

            finalResult.add(this.getTicketData(cursor.getString(cursor.getColumnIndex("IncidentID"))));

            while (cursor.moveToNext()) {
                finalResult.add(this.getTicketData(cursor.getString(cursor.getColumnIndex("IncidentID"))));
            }
            cursor.close();
        }
        //dexonDatabase.close();
        return finalResult;
    }

    public void deleteTicketTable() {
        SQLiteDatabase database = dexonDatabase.getWritableDatabase();
        database.delete(dexonDatabase.TICKET_TABLE, null, null);
        //dexonDatabase.close();
    }
    //endregion

    //region Private Methods

    //endregion
}
