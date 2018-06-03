package com.example.dami.realm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dami.realm.Realm.Models.Entry;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.edit_text)
    EditText mUserInputEditText;
    @BindView(R.id.text_view)
    TextView mDataTextView;
    Realm mRealm;

    public void save() {
        if (mUserInputEditText.getText().length() > 0) {
            trySaveToDb();
        } else
            Toast.makeText(this, "Can't add empty text", Toast.LENGTH_LONG).show();
    }

    private void trySaveToDb() {
        try {
            SaveDataToDb();
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void SaveDataToDb() {
        Entry newEntry = new Entry(mUserInputEditText.getText().toString());
        mRealm.beginTransaction();
        mRealm.copyToRealm(newEntry);
        mRealm.commitTransaction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mDataTextView.setMovementMethod(new ScrollingMovementMethod());
        mUserInputEditText.setSelected(false);
        mUserInputEditText.setImeActionLabel("Save", KeyEvent.KEYCODE_ENTER);
        mUserInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                save();
                return false;
            }
        });
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfiguration);

        getDataFromDb();

        mRealm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                getDataFromDb();
            }
        });
    }

    private void getDataFromDb() {
        RealmResults<Entry> entryList = mRealm.where(Entry.class).findAll().sort("createdAt", Sort.DESCENDING);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry e : entryList) {
            stringBuilder.append(e.getText() + "\n");
        }
        mDataTextView.setText(stringBuilder.toString());
        mDataTextView.scrollTo(0, 0);
        mUserInputEditText.setText("");

        mUserInputEditText.clearFocus();
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
