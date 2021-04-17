package edu.temple.cis3515_assignment9;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Context;
import edu.temple.audiobookplayer.AudiobookService;

public class ControlFragment extends Fragment
{
    private Book book;
    TextView playing;
    Button playButton;
    Button pauseButton;
    Button stopButton;
    SeekBar seek;
    ControlFragmentInterface parentActivity;
    public static final String arg = "param1";

    interface ControlFragmentInterface
    {
        void play(int i);
        void pause(int i);
        void stop(int i);
    }

    public ControlFragment()
    {

    }

    public static ControlFragment newInstance(Book book)
    {
        ControlFragment fragment = new ControlFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(arg, book);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            book = getArguments().getParcelable(arg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        playButton = view.findViewById(R.id.playButton);
        pauseButton = view.findViewById(R.id.pauseButton);
        stopButton = view.findViewById(R.id.stopButton);
        playing = view.findViewById(R.id.playingText);
        seek = view.findViewById(R.id.seekBar);

        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                parentActivity.play(book.getId());
                if(book != null)
                {
                    changeBook(book);
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                parentActivity.pause(book.getId());
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                parentActivity.stop(book.getId());
            }
        });

        return view;
    }

    void changeBook(Book book)
    {
        playing.setText("Now Playing: " + book.getTitle());
    }

    public void updateStatus(BookDetailsFragment details, boolean isPlaying)
    {
        playing.setText("Now Playing: " + book.getTitle());
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof BookListFragment.BookSelectedInterface)
        {
            parentActivity = (ControlFragment.ControlFragmentInterface) context;
        }
        else
        {
            throw new RuntimeException("Please implement the required interface");
        }
    }
}