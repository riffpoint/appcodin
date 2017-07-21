import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.app.lexicontrainer.AppConstants;
import com.app.lexicontrainer.LexiconTrainerApplication;
import com.app.lexicontrainer.Model.Subunit;
import com.app.lexicontrainer.Model.Unit;
import com.app.lexicontrainer.R;
import com.app.lexicontrainer.ui.CustomViewPager;
import com.app.lexicontrainer.ui.SettingsFragment;
import com.app.lexicontrainer.ui.activitys.LoginActivity;
import com.app.lexicontrainer.ui.fragments.cardsfragment.CardsFragment;
import com.app.lexicontrainer.ui.fragments.InfoFragment;
import com.app.lexicontrainer.ui.fragments.loginfragment.LoginFragment;
import com.app.lexicontrainer.ui.fragments.RoomsFragment;
import com.app.lexicontrainer.ui.fragments.subunitsfragment.SubunitFragment;
import com.app.lexicontrainer.ui.fragments.unitsfragment.UnitsFragment;
import com.app.lexicontrainer.ui.fragments.unitsfragment.UnitsFragmentModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View
        , TabLayout.OnTabSelectedListener
{

    private static final int NUM_PAGES = 3;

    @BindView(R.id.vpPager) CustomViewPager mPager;
    @BindView(R.id.tabLayout) TabLayout m_TabLayout;
    @BindView(R.id.flContent)  FrameLayout m_FrameLayout;
    private ScreenSlidePagerAdapter mPagerAdapter;
    @Inject MainActivityContract.Presenter m_Presenter;
    @Inject UnitsFragment m_UnitsFragment;
    @Inject SubunitFragment m_SubunitFragment;
    @Inject CardsFragment m_CardsFragment;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String[] mPlanetTitles;
    private DrawerLayout m_DrawerLayout;
    private Toolbar toolbar;
    private NavigationView m_nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LexiconTrainerApplication) getApplication()).get(this).getComponent()
                .plus(new MainActivityModule(this))
                .inject(this);
        Log.d("Tag", "main activity onCreate");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
        setSupportActionBar(toolbar);
        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        m_nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(m_nvDrawer);
        Menu menu = m_nvDrawer.getMenu();
        MenuItem menuItem = menu.findItem(R.id.my_cards_navigation_drawer);
        selectDrawerItem(menuItem);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });



    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                m_DrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.synchronize:
                m_Presenter.synchronizeData();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        m_Presenter.onStart();
    }

    @Override
    protected void onStop() {
        m_Presenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void setSubunitsTab(Unit unit) {
        if(mPagerAdapter!=null){
            if(unit!=null){
                m_TabLayout.getTabAt(AppConstants.TAB_CARDS_INDEX).setText(R.string.tab_layout_cards);
                m_TabLayout.getTabAt(AppConstants.TAB_SUBUNITS_INDEX).setText(unit.getName());
                mPager.setCurrentItem(AppConstants.TAB_SUBUNITS_INDEX);
            }else {
                m_TabLayout.getTabAt(AppConstants.TAB_CARDS_INDEX).setText(R.string.tab_layout_cards);
                m_TabLayout.getTabAt(AppConstants.TAB_SUBUNITS_INDEX).setText(R.string.tab_layout_subunits);
            }
        }

    }

    @Override
    public void setCardsTab(Subunit subunit) {
        if(mPagerAdapter!=null){
            if(subunit!=null){
                m_TabLayout.getTabAt(AppConstants.TAB_CARDS_INDEX).setText(subunit.getName());
                mPager.setCurrentItem(AppConstants.TAB_CARDS_INDEX);
            }else {
                m_TabLayout.getTabAt(AppConstants.TAB_CARDS_INDEX).setText(R.string.tab_layout_cards);
            }
        }

    }


    @Override
    public void setMaxViewPagerPage(int position) {
        if(mPager!=null){
            mPager.setMaxViewingPage(position);
        }
    }

    @Override
    public void setPage(int position) {
        mPager.setCurrentItem(position);
    }

    @Override
    public Toast showToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.my_cards_navigation_drawer:
                setFragmentToFrameLayout(null);
                attachViewPager();
                break;
            case R.id.rooms_navigation_drawer:
                detachViewPager();
                setFragmentToFrameLayout(RoomsFragment.class);
                break;
            case R.id.settings_navigation_drawer:
                detachViewPager();
                setFragmentToFrameLayout(SettingsFragment.class);
                break;
            case R.id.info_navigation_drawer:
                detachViewPager();
                setFragmentToFrameLayout(InfoFragment.class);
                break;
            case R.id.login_navigation_drawer:
                detachViewPager();
                setFragmentToFrameLayout(LoginFragment.class);
                break;
        }
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        m_DrawerLayout.closeDrawers();
    }



    private void attachViewPager(){
        m_TabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        m_TabLayout.setVisibility(View.VISIBLE);
        m_TabLayout.addOnTabSelectedListener(this);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(m_UnitsFragment);
        fragments.add(m_SubunitFragment);
        fragments.add(m_CardsFragment);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        mPager.setAdapter(mPagerAdapter);
        m_TabLayout.setupWithViewPager(mPager);
        m_TabLayout.getTabAt(AppConstants.TAB_UNITS_INDEX).setText(R.string.tab_layout_units);
        m_TabLayout.getTabAt(AppConstants.TAB_SUBUNITS_INDEX).setText(R.string.tab_layout_subunits);
        m_TabLayout.getTabAt(AppConstants.TAB_CARDS_INDEX).setText(R.string.tab_layout_cards);
        mPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mPager.setCurrentItem(0);
    }

    private void detachViewPager(){
        m_TabLayout.removeAllTabs();
        mPagerAdapter = null;
        mPager.setAdapter(null);
        m_TabLayout.setVisibility(View.GONE);
    }

    private void setFragmentToFrameLayout(Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment == null){
            fragmentManager.beginTransaction().replace(R.id.flContent, new Fragment()).commit();
        }else {
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

    }

    public void setLoginEmail(String email){
        Menu menu = m_nvDrawer.getMenu();
        MenuItem menuItem = menu.findItem(R.id.login_navigation_drawer);
        menuItem.setTitle(email);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        m_Presenter.onTabSelected(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //Autogenerated
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //Autogenerated
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> m_lstFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            m_lstFragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return m_lstFragments.get(position);
        }



        @Override
        public int getCount() {
            return NUM_PAGES;
        }


    }

}
