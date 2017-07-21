import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    private MainActivityContract.View m_View;

    public MainActivityModule(MainActivityContract.View view){m_View = view;}

    @Provides
    public MainActivityContract.View provideView(){return m_View;}

    @Provides
    MainActivityContract.Presenter providePresenter(MainActivityPresenter presenter){return  presenter;}
}
