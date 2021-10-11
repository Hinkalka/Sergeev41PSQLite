package com.example.sergeev41psqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnRead, btnClear;
    EditText etName, etEmail;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();
    }
public void UpdateTable(){
    Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

    if (cursor.moveToFirst()) {int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
        int mailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
        TableLayout dbOutput = findViewById(R.id.dbOutput);
        dbOutput.removeAllViews();
        do {
            TableRow dbOutputRow = new TableRow(this);
            dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            TextView outputeID = new TextView(this);
            params.weight = 1.0f;
            outputeID.setLayoutParams(params);
            outputeID.setText(cursor.getString(idIndex));
            dbOutputRow.addView(outputeID);

            TextView outputeName = new TextView(this);
            params.weight = 3.0f;
            outputeName.setLayoutParams(params);
            outputeName.setText(cursor.getString(nameIndex));
            dbOutputRow.addView(outputeName);

            TextView outputeMail =  new TextView(this);
            params.weight = 3.0f;
            outputeMail.setLayoutParams(params);
            outputeMail.setText(cursor.getString(mailIndex));
            dbOutputRow.addView(outputeMail);

            Button deleteBtn = new Button(this);
            deleteBtn.setOnClickListener(this);
            params.weight = 1.0f;
            deleteBtn.setLayoutParams(params);
            deleteBtn.setText("Удалить запись");
            deleteBtn.setId(cursor.getInt(idIndex));
            dbOutputRow.addView(deleteBtn);

            dbOutput.addView(dbOutputRow);

        }
        while (cursor.moveToNext());
    }
    cursor.close();
}
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAdd:
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_MAIL, email);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                UpdateTable();
                break;


            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int mailIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MAIL);
                    int realId=1;
                    do{
                        if(cursorUpdater.getInt(idIndex)>realId)
                        {
                            contentValues.put(DBHelper.KEY_ID, realId);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_MAIL,cursorUpdater.getString(mailIndex));
                            database.replace(DBHelper.TABLE_CONTACTS,null,contentValues);
                        }
                        realId++;
                    }
                    while (cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast())
                    {
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID+" = ?",new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                break;
        }
        dbHelper.close();
    }
}}