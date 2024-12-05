    package com.group2.papertrail.dao;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;

    import com.group2.papertrail.database.DatabaseManager;
    import com.group2.papertrail.model.User;
    import com.group2.papertrail.util.passwordUtil;

    import java.util.Collections;
    import java.util.List;

    public class UserDAO implements BaseDAO<User> {
        public static final String TABLE_NAME = "User";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "email TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL" +
                        ");";



        private final DatabaseManager dbManager;

        public UserDAO(Context context){
            this.dbManager = DatabaseManager.getInstance(context);
        }


        // function register
        @Override
        public long insert(User user) {
            SQLiteDatabase db = dbManager.getWritableDatabase();

            var values = new ContentValues();

            String encryptedPassword = passwordUtil.hashingPassword(user.getPassword());

            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("password", encryptedPassword);
            return db.insert(TABLE_NAME, null, values);
        }


        // function login
        public long loginUser(String username, String password){
            SQLiteDatabase db = dbManager.getReadableDatabase();
            String[] projection = {"userId" ,"password"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    if (passwordUtil.checkPassword(password, storedPassword)){
                        return cursor.getLong(cursor.getColumnIndexOrThrow("userId"));
                    }
                }
                return -1;
            }finally {
                cursor.close();
            }
        }




        @Override
        public int update(User model) {
            return 0;
        }

        @Override
        public int delete(User model) {
            return 0;
        }

        @Override
        public User findById(long id) {
            return null;
        }

        @Override
        public List<User> findAll() {
            return Collections.emptyList();
        }
    }
