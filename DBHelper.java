package com.example.myapplication11;


import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

class DBHelper extends SQLiteOpenHelper
{
    public int max_k;
    private String url_BD;
    private String db_name_create;


    public DBHelper(Context context, String db_name, int max, String url)
    {
        super(context, db_name, null, 1);
        max_k = max;
        url_BD = url;
        db_name_create = db_name;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table words (word text primary key, translate text, k integer, sentence text);");

        if (db_name_create.equals("BASE_10"))
        {
            String[] words = new String[] {"hello", "bye", "low", "temperatures", "affect", "processes", "heating", "mixture", "follows", "purification"};
            String[] translates = new String[] {"привет", "пока", "низкая", "температура", "влияет", "процессы", "нагревание", "микстуры", "следует", "очистка"};
            int[] ks = new int[] {0,0,0,0,0,0,0,0,0,0};
            String[] sentences = new String[] {"Hello from the inhabitants of planet Earth.",
                    "Bye to the inhabitants of planet Earth.",
                    "Low temperatures affect these processes.1",
                    "Low temperatures affect these processes.2",
                    "Low temperatures affect these processes.3",
                    "Low temperatures affect these processes.4",
                    "Heating of the mixture follows purification.5",
                    "Heating of the mixture follows purification.6",
                    "Heating of the mixture follows purification.7",
                    "Heating of the mixture follows purification.8" };
            ContentValues cv = new ContentValues();
            for (int i = 0; i < 10; i++)
            {
                cv.put("word", words[i]);
                cv.put("translate", translates[i]);
                cv.put("k", ks[i]);
                cv.put("sentence", sentences[i]);
                long rowID = db.insert("words", null, cv);
                //if (rowID == -1) str += "со словом " + words[i] + " что-то пошло не так";
                cv.clear();
            }
        }
        else if (db_name_create.equals("BASE_20"))
        {
            String[] words = new String[] {"hello", "bye", "low", "temperatures", "affect", "processes", "heating", "mixture", "follows", "purification"};
            String[] translates = new String[] {"привет", "пока", "низкая", "температура", "влияет", "процессы", "нагревание", "микстуры", "следует", "очистка"};
            int[] ks = new int[] {0,0,0,0,0,0,0,0,0,0};
            String[] sentences = new String[] {"Hello from the inhabitants of planet Earth.",
                    "Bye to the inhabitants of planet Earth.",
                    "Low temperatures affect these processes.1",
                    "Low temperatures affect these processes.2",
                    "Low temperatures affect these processes.3",
                    "Low temperatures affect these processes.4",
                    "Heating of the mixture follows purification.5",
                    "Heating of the mixture follows purification.6",
                    "Heating of the mixture follows purification.7",
                    "Heating of the mixture follows purification.8" };
            ContentValues cv = new ContentValues();
            for (int i = 0; i < 10; i++)
            {
                cv.put("word", words[i]);
                cv.put("translate", translates[i]);
                cv.put("k", ks[i]);
                cv.put("sentence", sentences[i]);
                long rowID = db.insert("words", null, cv);
                //if (rowID == -1) str += "со словом " + words[i] + " что-то пошло не так";
            }
            for (int i = 0; i < 10; i++)
            {
                cv.put("word", words[i] + "_copy");
                cv.put("translate", translates[i]);
                cv.put("k", ks[i]);
                cv.put("sentence", sentences[i]);
                long rowID = db.insert("words", null, cv);
                cv.clear();
            }



        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public int delete_bd(Context context, String name)
    {
        if (context.deleteDatabase(name)) return 0;
        else return -1;

    }
    public int increase(String word)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT k FROM words WHERE word = ?";
        String[] args = new String[] { word };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            int k = c.getInt(c.getColumnIndex("k"));
            c.close();
            ContentValues cv = new ContentValues();
            k++;
            cv.put("k", k);
            int updCount = db.update("words", cv, "word = ?", args);
            if (updCount != 1)
            {
                return updCount;
            }
            return (k >= max_k) ? 1 : 0;
        }
        else
        {
            return -1;
        }
    }
    public int check(String word, String translate)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT translate FROM words WHERE word = ?";
        String[] args = new String[] { word };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            String translate_correct = c.getString(c.getColumnIndex("translate"));
            c.close();
            return translate_correct.equals(translate) ? 1 : 0;
        }
        else
        {
            return -1;
        }
    }
    public String en2ru(String word)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT translate FROM words WHERE word = ?";
        String[] args = new String[] { word };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            String translate = c.getString(c.getColumnIndex("translate"));
            c.close();
            return translate;
        }
        else
        {
            return "";
        }
    }

    public String ru2en(String translate)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT word FROM words WHERE translate = ?";
        String[] args = new String[] { translate };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            String word = c.getString(c.getColumnIndex("word"));
            c.close();
            return word;
        }
        else
        {
            return "";
        }
    }
    public String get_sentence(String word)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT sentence FROM words WHERE word = ?";
        String[] args = new String[] { word };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            String sentence = c.getString(c.getColumnIndex("sentence"));
            c.close();
            return sentence;
        }
        else
        {
            return "";
        }
    }


    public int fill_up_list(List<String> for_return)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("words", null, null, null, null, null, null);
        if (c.moveToFirst())
        {
            int wordColIndex = c.getColumnIndex("word");
            int kColIndex = c.getColumnIndex("k");
            do {
                String word = c.getString(wordColIndex);
                int k = c.getInt(kColIndex);
                if (k < max_k)
                {
                    for_return.add(word);
                }
            }
            while (c.moveToNext());
            c.close();
            return 0;
        }
        else
        {
            c.close();
            return -1;
        }
    }

    public String view_all(Boolean find_x)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("words", null, null, null, null, null, null);
        int x = 0;
        int count = 0;
        if (c.moveToFirst())
        {
            String all_base = "";
            do {
                String word = c.getString(c.getColumnIndex("word"));
                String translate = c.getString(c.getColumnIndex("translate"));
                int k = c.getInt(c.getColumnIndex("k"));
                String sentence = c.getString(c.getColumnIndex("sentence"));
                if (k >= max_k) x++;
                count++;
                all_base += word + "---" + translate + "---" + Integer.toString(k) + "---" + sentence + "\n";
            } while (c.moveToNext());
            c.close();
            return find_x ? String.valueOf(x) + " из " + String.valueOf(count): all_base;
        }
        else
        {
            c.close();
            return "В базе ничего нет";
        }
    }
    public int nullify_progress()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("k", 0);
        return db.update("words", cv, null, null);
    }


}

