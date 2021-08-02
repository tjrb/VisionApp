package ai.vision.visionapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;

import ai.vision.visionapp.databaseHelper;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public HomeViewModel(Context context) {

        mText = new MutableLiveData<>();
        mText.setValue("Numero de carros:");
    }

    public LiveData<String> getText() {
        return mText;
    }
}