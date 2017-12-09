package developers.sd.notes;

        import android.content.res.Configuration;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.app.AppCompatDelegate;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.Toast;

        import java.util.Calendar;

        import mehdi.sakout.aboutpage.AboutPage;
        import mehdi.sakout.aboutpage.Element;

/**
 * Created by sohamdeshmukh on 22/05/17.
 */

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        simulateDayNight(/* DAY */ 0);
//        Element adsElement = new Element();
//        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(this)
                .setDescription("Simply Elegant Note App.\n\n" +
                        "We would love if you could take out some of your precious time to give us valuable feedback.")
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle("Version 0.1"))
//                .addItem(adsElement)
//                .addGroup("Connect with us")
                .addEmail("developers.sd.com@gmail.com")
//                .addWebsite("https://pavedlife.blogspot.in")
//                .addFacebook("the.medy")
//                .addTwitter("medyo80")
//                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.ideashower.readitlater")
//                .addInstagram("medyo80")
//                .addGitHub("medyo")
//                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }


//    Element getCopyRightsElement() {
//        Element copyRightsElement = new Element();
//        final String copyrights = String.format(getString(R.string.about_contact_us), Calendar.getInstance().get(Calendar.YEAR));
//        copyRightsElement.setTitle(copyrights);
//        copyRightsElement.setIconDrawable(R.drawable.notes);
//        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
//        copyRightsElement.setIconNightTint(android.R.color.white);
//        copyRightsElement.setGravity(Gravity.CENTER);
//        copyRightsElement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AboutUs.this, copyrights, Toast.LENGTH_SHORT).show();
//            }
//        });
//        return copyRightsElement;
//    }

//    void simulateDayNight(int currentSetting) {
//        final int DAY = 0;
//        final int NIGHT = 1;
//        final int FOLLOW_SYSTEM = 3;
//
//        int currentNightMode = getResources().getConfiguration().uiMode
//                & Configuration.UI_MODE_NIGHT_MASK;
//        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_NO);
//        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_YES);
//        } else if (currentSetting == FOLLOW_SYSTEM) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }
//    }

}
