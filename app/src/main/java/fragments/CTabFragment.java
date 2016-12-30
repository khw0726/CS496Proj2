package fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs496.proj2.project2.R;

/**
 * Created by q on 2016-12-30.
 */

public class CTabFragment extends Fragment {
    public static CTabFragment newInstance(){
        return new CTabFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_c, container, false);

        return rootView;
    }
}
