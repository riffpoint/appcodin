import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.app.lexicontrainer.Model.Subunit;
import com.app.lexicontrainer.Model.Unit;

public interface MainActivityContract {

    public interface View {
        void setSubunitsTab(Unit unit);
        void setCardsTab(Subunit subunit);
        void setMaxViewPagerPage(int position);
        void setPage(int position);
        Toast showToast(String text);
    }
    public interface Presenter{
        void onStart();
        void onStop();
        void synchronizeData();
        void onTabSelected(TabLayout.Tab tab);
        void onPageSelected(int position);
    }
}
