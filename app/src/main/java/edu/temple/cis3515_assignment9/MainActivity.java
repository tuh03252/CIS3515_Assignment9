package edu.temple.cis3515_assignment9;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface, ControlFragment.ControlFragmentInterface
{
    FragmentManager fm;
    boolean twoPane;
    BookDetailsFragment bookDetailsFragment;
    Book selectedBook;
    private final String TAG_BOOKLIST = "booklist", TAG_BOOKDETAILS = "bookdetails";
    private final String KEY_SELECTED_BOOK = "selectedBook";
    private final String KEY_BOOKLIST = "searchedook";
    private final int BOOK_SEARCH_REQUEST_CODE = 123;
    BookList bookList;
    ControlFragment controlFragment;
    boolean isConnected;
    int duration;
    int progress;
    int index;
    Uri uri;
    SeekBar seek;
    boolean isPlayClicked;
    Intent intent;
    private static final String BOOK_PROGRESS = "bookProgress";
    private static final String BOOK_DURATION = "bookDuration";
    private static final String BOOK_LIST = "bookslisted";
    AudiobookService.MediaControlBinder mediaControlBinder;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mediaControlBinder = (AudiobookService.MediaControlBinder) iBinder;
            mediaControlBinder.setProgressHandler(mediaControlHandler);
            isConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            isConnected = false;
        }
    };

    Handler mediaControlHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message message)
        {
            final AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) message.obj;
            seek = findViewById(R.id.seekBar);
            seek.setMax(duration);
            if(mediaControlBinder.isPlaying())
            {
                seek.setProgress(bookProgress.getProgress());
                progress = selectedBook.getDuration();
                uri = bookProgress.getBookUri();
            }
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                {
                    if(b)
                    {
                        mediaControlBinder.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {

                }
            });

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
        {
            selectedBook = savedInstanceState.getParcelable(BOOK_LIST);
            duration = savedInstanceState.getInt(BOOK_DURATION);
            progress = savedInstanceState.getInt(BOOK_PROGRESS);
        }

        twoPane = findViewById(R.id.container_2) != null;
        seek = findViewById(R.id.seekBar);
        bookList = new BookList();

        Fragment fragment;
        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.container_1);
        if(fragment instanceof BookDetailsFragment)
        {
            fm.popBackStack();
        }
        else if(!(fragment instanceof BookListFragment))
        {
            fm.beginTransaction()
                    .add(R.id.container_1, BookListFragment.newInstance(bookList))
                    .commit();
        }

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
                startActivity(intent);
            }
        });

        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);
        controlFragment = (selectedBook == null) ? new ControlFragment() : ControlFragment.newInstance(selectedBook);

        intent = getIntent();
        if(twoPane){
            bookDetailsFragment = new BookDetailsFragment();
            controlFragment = new ControlFragment();

            if(intent.hasExtra("bookslisted")){
                Bundle extras = getIntent().getExtras();
                bookList = extras.getParcelable("bookslisted");
            }
            fm.beginTransaction().replace(R.id.container_1, BookListFragment.newInstance(bookList)).replace(R.id.container_2, BookDetailsFragment.newInstance(selectedBook)).replace(R.id.addition2, ControlFragment.newInstance(selectedBook)).commit();
        }
        else
        {
            if(intent.hasExtra("bookslisted")){
                Bundle extras = getIntent().getExtras();
                bookList = extras.getParcelable("bookslisted");
            }
            fm.beginTransaction().replace(R.id.container_1, BookListFragment.newInstance(bookList)).commit();
        }
        intent = new Intent(this, AudiobookService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void bookSelected(int position)
    {
        selectedBook = bookList.get(position);

        if(!twoPane) {
            fm.beginTransaction().replace(R.id.container_1, BookDetailsFragment.newInstance(selectedBook)).replace(R.id.addition1, ControlFragment.newInstance(selectedBook)).commit();
        }
        else{
            fm.beginTransaction().replace(R.id.container_2, BookDetailsFragment.newInstance(selectedBook)).replace(R.id.addition2, ControlFragment.newInstance(selectedBook)).addToBackStack(null).commit();
        }
        index = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BOOK_LIST, selectedBook);
        outState.putInt(BOOK_PROGRESS, progress);
        outState.putInt(BOOK_DURATION, duration);
    }

    @Override
    public void play(int id)
    {
        if(isConnected)
        {
            startService(intent);
            duration = selectedBook.getDuration();
            mediaControlBinder.setProgressHandler(mediaControlHandler);
            mediaControlBinder.play(id);
            isPlayClicked = true;
        }
    }
    @Override
    public void pause(int id)
    {
        mediaControlBinder.pause();
    }

    @Override
    public void stop(int id)
    {
        mediaControlBinder.stop();
        seek.setProgress(0);
    }

    @Override
    public void onBackPressed()
    {
        selectedBook = null;
        super.onBackPressed();
    }

}