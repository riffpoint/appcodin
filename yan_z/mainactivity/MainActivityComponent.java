import dagger.Subcomponent;

@Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject (MainActivity activity);
}
